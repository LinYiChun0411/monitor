package com.aiinspector.aspect;


import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
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
        threadPool.execute(new InspectFailHandler(joinPoint, apiInspectFailLogService, apiInspectStatusService, Optional.of(ex)));
    	log.error("@AfterThrowing: joinPoint:{}, ex:{}", joinPoint, ex);
    }
}

@Slf4j
abstract class InspectHandle implements Runnable{
	final Predicate<Integer> isHttpStatusOK = status -> status == HttpStatus.SC_OK;
	final Function<Integer, Integer> getCountBySuccess = status-> {return isHttpStatusOK.test(status)?1:0;};
	final Function<Integer, Integer> getCountByFail = status-> {return isHttpStatusOK.test(status)?0:1;};
	
	protected void toSaveApiInspectStatus(final String url,final Date today,final int statusCode, ApiInspectStatusService apiInspectStatusService) {
		ApiInspectStatus status = apiInspectStatusService.findByUrlWithDate( url, today);
		if(null == status) {
			apiInspectStatusService.save(ApiInspectStatus.builder()
														   .inspectUrl(url)
														   .inspect_date(today)
														   .successCount(getCountBySuccess.apply(statusCode))
														   .failCount(getCountByFail.apply(statusCode))
														   .lastRespStatus(statusCode)
														   .build());
		}else {
			status = status.toBuilder()
										.successCount(status.getSuccessCount()+getCountBySuccess.apply(statusCode))
										.failCount(status.getFailCount()+getCountByFail.apply(statusCode))
										.lastRespStatus(statusCode)
										.updateDatetime(new Date(ZonedDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli())).build();
			apiInspectStatusService.updateById(status);
			log.info("{}",status);
		}
	}
	
	
	protected void toSaveApiInspectFailLog(final JoinPoint joinPoint ,final String url, String failMsg, String stackTrace, ApiInspectFailLogService apiInspectFailLogService) {
		  String reqClass = joinPoint.getSignature().getDeclaringTypeName();
          String reqMethod = joinPoint.getSignature().getName();
          String reqArgument = org.springframework.util.StringUtils.arrayToCommaDelimitedString(joinPoint.getArgs());
		  ApiInspectFailLog apiInspectFailLog = ApiInspectFailLog.builder()
											     				   .reqClass(reqClass)
											     				   .reqMethod(reqMethod)
											     				   .reqArgument(reqArgument)
											     				   .reqUrl(url)
											     				   .failMsg(failMsg)
											     				   .stackTrace(stackTrace)
											     				   .build();
		apiInspectFailLogService.toSaveWithMap(apiInspectFailLog);
	}
}

@Slf4j
@AllArgsConstructor
class InspectResultHandler extends InspectHandle{
	final private JoinPoint joinPoint;
	final private ApiInspectStatusService apiInspectStatusService;
	final private ApiInspectFailLogService apiInspectFailLogService;
	final private ResponseEntity resp;

	@Override
	public void run() {
		try {
			final int respStatusCode = resp.getStatusCodeValue();
			String url  = (String) joinPoint.getArgs()[1];
			long currentUTCTimeMillis = ZonedDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli();
			Date today  = new java.sql.Date(currentUTCTimeMillis);
			//To write status response to ApiInspectStatus
			toSaveApiInspectStatus(url, today, respStatusCode, apiInspectStatusService);
			//To write failed response to ApiInspectFailLog
			if(!isHttpStatusOK.test(respStatusCode)) {
				toSaveApiInspectFailLog(joinPoint, url, resp.getStatusCode().toString(), resp.getStatusCode().getReasonPhrase(), apiInspectFailLogService);
			}
		} catch (Exception e) {
			log.error("Class Name:{}, Exception:{}" + getClass().getName(), e);
		}
	}	
}

@Slf4j
@AllArgsConstructor
class InspectFailHandler extends InspectHandle {
	 final private JoinPoint joinPoint;
	 final private ApiInspectFailLogService apiInspectFailLogService;
	 final private ApiInspectStatusService  apiInspectStatusService;
	 final private Optional<Exception> exOpt;
    @Override
    public void run() {
        try {
        	long currentUTCTimeMillis = ZonedDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli();
        	Date today  = new java.sql.Date(currentUTCTimeMillis);
        	String url = (String) joinPoint.getArgs()[1];
        	String failMsg =  exOpt.map(e->e.getMessage().toString()).orElse("");
            String stackTrace = exOpt.map(e->org.springframework.util.StringUtils.arrayToCommaDelimitedString(e.getStackTrace())) .orElse("");
        	 
          //To write failed response to ApiInspectFailLog
        	toSaveApiInspectFailLog(joinPoint, url, failMsg, stackTrace, apiInspectFailLogService);
            
           //To write failed response to ApiInspectStatus
            toSaveApiInspectStatus(url, today, HttpStatus.SC_BAD_REQUEST, apiInspectStatusService);
			
        } catch (Exception e) {
            log.error("Class Name:{}, Exception:{}" + getClass().getName(), e);
        }
    }
}

