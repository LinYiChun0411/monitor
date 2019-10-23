package com.aiinspector.schedule;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import com.aiinspector.entity.ApiInspectFailLog;
import com.aiinspector.service.AlertPushService;
import com.aiinspector.service.ApiInspectFailLogService;
import com.aiinspector.service.CheckSatusService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ApiInspectScheduleTask {

	@Autowired
	CheckSatusService checkSatusServiceImp;

	@Autowired
	AlertPushService mailAlertPushServiceImp;

	@Autowired
	private ApiInspectFailLogService apiInspectFailLogService;
	
    @Autowired
    ThreadPoolTaskExecutor threadPool;
	
    final String msgFormat = "failMsg:%s URL:%s <br>";
    
    @Scheduled(fixedRateString = "${inspector.scheduled}")
	public void checkAPI() {
		final Function<CheckSatusService, Void> checkGmaiList = s->{
			s.checkGameList();
			return null;
		};
		
		final Function<CheckSatusService, Void> checkEpgs = s->{
			if(checkSatusServiceImp.checklogin()) {
				ResponseEntity responseEntity = checkSatusServiceImp.checkEpgs();
				if (responseEntity != null && responseEntity.getStatusCodeValue() == 200) {
					checkSatusServiceImp.checkEpgPlayers(responseEntity.getBody().toString());
				}						
			}
			return null;
		};
		
		Runnable r1 = ()->{checkGmaiList.apply(checkSatusServiceImp);};
		Runnable r2 = ()->{checkEpgs.apply(checkSatusServiceImp);};
		CompletableFuture<Void> cf = CompletableFuture.runAsync(r1, threadPool).thenRunAsync( r2, threadPool);
		
		try {
			cf.get(10, TimeUnit.SECONDS);
			StringBuffer msgContext = new StringBuffer("");
			Set<String> keySet = apiInspectFailLogService.FailLogMap.keySet();
			keySet.forEach(key->{
					ApiInspectFailLog fLog = apiInspectFailLogService.FailLogMap.remove(key);
					String msg = String.format(msgFormat, fLog.getFailMsg(), fLog.getReqUrl());
					msgContext.append(msg);
				});
			
			if(!StringUtils.isEmpty(msgContext.toString())) {
				mailAlertPushServiceImp.sendAlertMessage("API Inpected Err Report", msgContext.toString());
			}
		} catch (Exception e) {
			log.error("ApiInspectScheduleTask.checkAPI occurred Exception:{}",  e);
		}
		
	}
	
}
