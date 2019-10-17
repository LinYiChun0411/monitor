package com.aiinspector.aspect;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
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
     * load all resource what can be used in here
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
    	log.info("@doAfterReturning: joinPoint:{}, resp:{}", joinPoint, resp);
    	
    	if(resp.getStatusCodeValue()==HttpStatus.SC_OK) {
    		threadPool.execute(new InspectSuccessHandler(joinPoint, resp, apiInspectStatusService));
    	}else {
    		threadPool.execute(()->{});
    	}
    }

    @AfterThrowing(pointcut = "inspect()", throwing = "ex")
    public void addAfterThrowingLogger(JoinPoint joinPoint, Exception ex) {
//        threadPool.execute(new InspectFailHandler(apiInspectFailLogService, joinPoint, ex));
        log.error("@AfterThrowing: {}", ex);
    }
}


@Slf4j
@AllArgsConstructor
class InspectSuccessHandler implements Runnable{
	final private JoinPoint joinPoint;
	final private ResponseEntity resp;
	final private ApiInspectStatusService apiInspectStatusService;
	@Override
	public void run() {
		String url  = (String) joinPoint.getArgs()[1];
		Date today  = new java.sql.Date(System.currentTimeMillis());
		Integer respStatus = resp.getStatusCodeValue();
		Map<String, Object> queryMap = new LinkedHashMap<>();
		queryMap.put("inspect_url", url);
		queryMap.put("inspect_date", today);
		
		ApiInspectStatus exist_status = apiInspectStatusService.getOne(new QueryWrapper<ApiInspectStatus>().allEq(queryMap));
		if(null == exist_status) {
			ApiInspectStatus new_status = ApiInspectStatus.builder()
											.inspectUrl(url)
											.inspect_date(today)
											.successCount(1)
											.failCount(0)
											.lastRespStatus(respStatus)
											.build();
			apiInspectStatusService.save(new_status);
		}else {
			exist_status.setSuccessCount(exist_status.getSuccessCount()+1);
			exist_status.setLastRespStatus(respStatus);
			exist_status.setUpdateDatetime( new Date(ZonedDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli()));
			apiInspectStatusService.updateById(exist_status);
			log.info("{}",exist_status);
		}
	}	
}

@Slf4j
@AllArgsConstructor
class InspectFailHandler implements Runnable {
    final private ApiInspectFailLogService apiInspectFailLogService;
    final private JoinPoint joinPoint;
    final private Exception ex;

    @Override
    public void run() {
        try {
            String reqClass = joinPoint.getSignature().getDeclaringTypeName();
            String reqMethod = joinPoint.getSignature().getName();
            String reqArgument = "";
            String reqUrl = "";
            String failMsg = StringUtils.defaultIfEmpty(ex.getMessage(), "");
            String cause = ex.getCause().toString();
            String stackTrace = org.springframework.util.StringUtils.arrayToCommaDelimitedString(ex.getStackTrace());
            ApiInspectFailLog apiInspectFailLog = ApiInspectFailLog.builder()
									            				   .reqClass(reqClass)
									            				   .reqMethod(reqMethod)
									            				   .reqArgument(reqArgument)
									            				   .reqUrl(reqUrl)
									            				   .failMsg(failMsg)
									            				   .cause(cause)
									            				   .stackTrace(stackTrace).build();
            apiInspectFailLogService.save(apiInspectFailLog);
        } catch (Exception e) {
            log.error("Class Name:{}, Exception:{}" + getClass().getName(), e);
        }
    }
}

//@Slf4j
//class VendorUrlErrHandler implements Runnable {
//    //如果為Mx3Exception 符合下列RespEnum不做告警處理
//    final static RespErrEnum[] ByPassRespErrEnums = {RespErrEnum.MAINTENANCE_OF_PLATFORM, RespErrEnum.OUT_OF_PLATFORM, RespErrEnum.DATA_NOT_FOUND};
//    final private FeGameVendorUrlErrLogService errLogService;
//    final private FeGameVendorUrlErrStateService errStateService;
//    final private JoinPoint joinPoint;
//    final private Exception ex;
//    final private CopyOnWriteArrayList<FeGameVendorUrlErrState> stateList;
//    final private RegisterListener listener;
//    final private Predicate<Exception> isMx3Exception = e -> e instanceof Mx3Exception;
//    final private Predicate<Exception> hasDataInMx3Exception = e -> isMx3Exception.test(e) && null != Mx3Exception.class.cast(e).getData();
//    final private Predicate<Object> isConnectTypeErr = e -> e instanceof IOException || e instanceof RestClientException;  //認定為連線異常的Exception種類
//    final private Function<Exception, Exception> getOriginalExecption = e -> hasDataInMx3Exception.test(e) ? (Exception) Mx3Exception.class.cast(e).getData() : e;
//    final private Function<Object, String> getExecptionTypeDesc = e -> isConnectTypeErr.test(e) ? "连线异常" : "程式异常";
//    final private Predicate<Exception> isByPassStatus = e -> Stream.of(ByPassRespErrEnums)
//            .anyMatch(x -> isMx3Exception.test(getOriginalExecption.apply(e)) && x.equals(Mx3Exception.class.cast(getOriginalExecption.apply(e)).getBaseEnum()));
//
//    public VendorUrlErrHandler(RegisterListener registerListener,
//                               FeGameVendorUrlErrStateService feGameVendorUrlErrStateService,
//                               FeGameVendorUrlErrLogService errLogService, JoinPoint joinPoint, Exception ex
//            , CopyOnWriteArrayList<FeGameVendorUrlErrState> stateCopyOnWriteList) {
//        this.errLogService = errLogService;
//        this.errStateService = feGameVendorUrlErrStateService;
//        this.joinPoint = joinPoint;
//        this.ex = ex;
//        this.stateList = stateCopyOnWriteList;
//        listener = registerListener;
//    }
//
//    public void run() {
//        try {
//            /*檢查Pass的列外*/
//            if (isByPassStatus.test(ex)) {
//                log.warn("Don't handle the status ex:{} ", ex);
//                return;
//            }
//
//            String reqArgument = StringUtils.arrayToCommaDelimitedString(joinPoint.getArgs());//來源方法的參數
//            final String reqClass = joinPoint.getSignature().getDeclaringTypeName();//被呼叫的class
//            final String reqMethod = joinPoint.getSignature().getName();//被呼叫的method
//            String reqAddress = "";
//            String reqUrl = "";
//            String gpId = "";
//            String exceptionMsg = "";
//            String cause = "";
//            String detail = "";
//            String stackTrace = "";
//
//            //從被呼叫的來源方法中的參數取得gpId (可在refactor code)
//            if (org.apache.commons.lang3.StringUtils.containsIgnoreCase(reqArgument, ApiInspectScheduleAspect.GpId)) {
//                gpId = org.apache.commons.lang3.StringUtils.substringBetween(reqArgument, ApiInspectScheduleAspect.GpId + "=", ",");
//                if (StringUtils.isEmpty(gpId)) {//上方的patter沒找到則再找一次下方的patter
//                    gpId = org.apache.commons.lang3.StringUtils.substringBetween(reqArgument, ApiInspectScheduleAspect.GpId + ":", ",");
//                }
//            }
//
//            /**
//             *
//             * 取得Exception  產生的錯誤訊息，寫入fe_game_vendor_url_err_log
//             */
//            exceptionMsg = org.apache.commons.lang3.StringUtils.defaultIfEmpty(ex.getMessage(), "");
//            /**
//             * 取得异常种类
//             */
//            cause = getExecptionTypeDesc.compose(getOriginalExecption).apply(ex);
//            /**
//             * 取得异常细项说明
//             */
//            detail = getOriginalExecption.apply(ex).toString();
//
//            stackTrace = StringUtils.arrayToCommaDelimitedString(ex.getStackTrace());
//            errLogService.insert(new FeGameVendorUrlErrLog(gpId, reqUrl, reqClass, reqMethod, reqArgument, reqAddress, exceptionMsg, cause, detail, stackTrace));
//
//            /**
//             * 找出對應的service class跟method，並且COUNT 加1， 寫入fe_game_vendor_url_err_state
//             */
//            Optional<FeGameVendorUrlErrState> resultOpt = stateList.stream().filter(
//                    state -> reqClass.contains(state.getServiceName())
//                            && reqMethod.equalsIgnoreCase(state.getMethodName())
//            ).findFirst();
//
//            if (resultOpt.isPresent()) {
//                FeGameVendorUrlErrState state = resultOpt.get();
//                state.setUrlMissCount(state.getUrlMissCount() + 1);
//                state.setUpdateDatetime(new Date(System.currentTimeMillis()));
//                //累計次數大於Telegram通知設定值，則發送通知並將累計值歸0
//                if (state.getUrlMissCount() > ApiInspectScheduleAspect.UrlMissMaxCount) {
//                    state.setUrlMissCount(0);
//                    Optional<GameProviderEnum> serviceOpt = Stream.of(GameProviderEnum.values()).filter(
//                            seviceEnum -> state.getServiceName().equalsIgnoreCase(seviceEnum.getServiceName())
//                    ).findFirst();
//                    Optional<GameTandemServiceMethodEnum> methodOpt = Stream.of(GameTandemServiceMethodEnum.values()).filter(
//                            methodEnum -> state.getMethodName().equalsIgnoreCase(methodEnum.getName())
//                    ).findFirst();
//
//                    String platformNa = serviceOpt.isPresent() ? serviceOpt.get().name() : state.getServiceName();
//                    String urlFunNa = methodOpt.isPresent() ? methodOpt.get().getValue() : state.getMethodName();
//                    String env = ApiInspectScheduleAspect.ENV;
//                    String message = String.format("  \n\t\t\t運行環境:  %s \n\t\t\t遊戲商:  %s  \n\t\t\t功能:  %s   \n\t\t\t異常種類:  %s   \n\t\t\t異常原因:  %s  ，錯誤超過設定次數 ", env, platformNa, urlFunNa, cause, exceptionMsg);
//                    listener.setSendMsgListener(new TaskEvent(this, "WARN", message, new Exception(detail)));
//                }
//                errStateService.updateById(state);
//
//            } else {
//                log.warn("reqClass:{}, reqMethod:{}, can not be found in stateList:{}", reqClass, reqMethod, stateList);
//            }
//        } catch (Exception e) {
//            log.error("Class Name:{}, Exception:{}" + getClass().getName(), e);
//        }
//    }
//}