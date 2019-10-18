package com.aiinspector.aspect;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.PostConstruct;

import org.apache.http.HttpStatus;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.aiinspector.config.YAMLConfig;
import com.aiinspector.entity.ApiInspectFailLog;
import com.aiinspector.entity.ApiInspectStatus;
import com.aiinspector.service.ApiInspectFailLogService;
import com.aiinspector.service.ApiInspectStatusService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Jeffrey.hsiao
 * Date: 2019-10-16
 */
@Aspect
@Component
@Slf4j
public class ApiInspectScheduleAspect {
    @Autowired
    @Qualifier("threadPool")
    ThreadPoolTaskExecutor threadPool;

    //running environment
    private String env;

	@Autowired
	private YAMLConfig myConfig;

	@Autowired
	private ApiInspectFailLogService apiInspectFailLogService;
	
	@Autowired
	private ApiInspectStatusService apiInspectStatusService;
	
    /**
     * load all resources what can be used in here
     */
    @PostConstruct
    public void init() {
        log.info("init");
        env = myConfig.getEnvironment();
    }

    @Pointcut("execution(public * com.aiinspector.service.CheckSatusCommonService.checkCommonMethod(..))")
    public void inspect() {
    }

    @AfterReturning(returning = "resp", pointcut = "inspect()")
    public void doAfterReturning(JoinPoint joinPoint, ResponseEntity resp) throws Throwable {
    	log.debug("@doAfterReturning: joinPoint:{}, resp:{}", joinPoint, resp);
    	threadPool.execute(new InspectResultHandler(joinPoint, apiInspectStatusService, apiInspectFailLogService, resp));
    }

    @AfterThrowing(pointcut = "inspect()", throwing = "ex")
    public void addAfterThrowingLogger(JoinPoint joinPoint, Exception ex) {
    	Optional<ResponseEntity> respOpt = Optional.empty();
        threadPool.execute(new InspectFailHandler(joinPoint, apiInspectFailLogService, apiInspectStatusService, Optional.of(ex), respOpt));
    	log.error("@AfterThrowing: joinPoint:{}, ex:{}", joinPoint, ex);
    }
}


@Slf4j
@AllArgsConstructor
class InspectResultHandler implements Runnable{
	final private JoinPoint joinPoint;
	final private ApiInspectStatusService apiInspectStatusService;
	final private ApiInspectFailLogService apiInspectFailLogService;
	final private ResponseEntity resp;
	final private Predicate<Integer> isHttpStatusOK = status -> status == HttpStatus.SC_OK;
	final private Function<Integer, Integer> getCountBySuccess = status-> {return isHttpStatusOK.test(status)?1:0;};
	final private Function<Integer, Integer> getCountByFail = status-> {return isHttpStatusOK.test(status)?0:1;};
	@Override
	public void run() {
		try {
			final int respStatusCode = resp.getStatusCodeValue();
			String url  = (String) joinPoint.getArgs()[1];
			long currentUTCTimeMillis = ZonedDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli();
			Date today  = new java.sql.Date(currentUTCTimeMillis);
			Map<String, Object> queryMap = new LinkedHashMap<>();
			queryMap.put("inspect_url", url);
			queryMap.put("inspect_date", today);
			
			ApiInspectStatus exist_status = apiInspectStatusService.getOne(new QueryWrapper<ApiInspectStatus>().allEq(queryMap));
			
			if(null == exist_status) {
				apiInspectStatusService.save(ApiInspectStatus.builder()
															   .inspectUrl(url)
															   .inspect_date(today)
															   .successCount(getCountBySuccess.apply(respStatusCode))
															   .failCount(getCountByFail.apply(respStatusCode))
															   .lastRespStatus(respStatusCode)
															   .build());
			}else {
				exist_status = exist_status.toBuilder()
											.successCount(exist_status.getSuccessCount()+getCountBySuccess.apply(respStatusCode))
											.failCount(exist_status.getFailCount()+getCountByFail.apply(respStatusCode))
											.lastRespStatus(respStatusCode)
											.updateDatetime(new Date(currentUTCTimeMillis)).build();
				apiInspectStatusService.updateById(exist_status);
				log.info("{}",exist_status);
			}
			
			//To write failed response to ApiInspectFailLog
			if(!isHttpStatusOK.test(respStatusCode)) {
			    String reqClass = joinPoint.getSignature().getDeclaringTypeName();
	            String reqMethod = joinPoint.getSignature().getName();
	            String reqArgument = org.springframework.util.StringUtils.arrayToCommaDelimitedString(joinPoint.getArgs());
				ApiInspectFailLog apiInspectFailLog = ApiInspectFailLog.builder()
												     				   .reqClass(reqClass)
												     				   .reqMethod(reqMethod)
												     				   .reqArgument(reqArgument)
												     				   .reqUrl(url)
												     				   .failMsg(resp.getStatusCode().toString())
												     				   .stackTrace(resp.getStatusCode().getReasonPhrase())
												     				   .build();
				apiInspectFailLogService.save(apiInspectFailLog);
			}
		} catch (Exception e) {
			log.error("Class Name:{}, Exception:{}" + getClass().getName(), e);
		}
	}	
}

@Slf4j
@AllArgsConstructor
class InspectFailHandler implements Runnable {
	 final private JoinPoint joinPoint;
     private ApiInspectFailLogService apiInspectFailLogService;
	 private ApiInspectStatusService  apiInspectStatusService;
	 private Optional<Exception> exOpt;
	 private Optional<ResponseEntity> respOpt;
    @Override
    public void run() {
        try {
            String reqClass = joinPoint.getSignature().getDeclaringTypeName();
            String reqMethod = joinPoint.getSignature().getName();
            String reqArgument = org.springframework.util.StringUtils.arrayToCommaDelimitedString(joinPoint.getArgs());
            String reqUrl = (String) joinPoint.getArgs()[1];
            String failMsg =  exOpt.map(e->e.getMessage().toString()).orElse("");
            String stackTrace = exOpt.map(e->org.springframework.util.StringUtils.arrayToCommaDelimitedString(e.getStackTrace())) .orElse("");
            ApiInspectFailLog apiInspectFailLog = ApiInspectFailLog.builder()
									            				   .reqClass(reqClass)
									            				   .reqMethod(reqMethod)
									            				   .reqArgument(reqArgument)
									            				   .reqUrl(reqUrl)
									            				   .failMsg(failMsg)
									            				   .stackTrace(stackTrace).build();
            apiInspectFailLogService.save(apiInspectFailLog);
        } catch (Exception e) {
            log.error("Class Name:{}, Exception:{}" + getClass().getName(), e);
        }
    }
}

