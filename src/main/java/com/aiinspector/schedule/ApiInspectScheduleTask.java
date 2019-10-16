package com.aiinspector.schedule;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.aiinspector.service.CheckSatusService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ApiInspectScheduleTask {

	@Autowired
	CheckSatusService checkSatusServiceImp;

	@Scheduled(fixedRateString = "${inspector.scheduled}")
	public void checkGamelist() {
		checkSatusServiceImp.checkGameList();
	}

	@Scheduled(fixedRateString = "${inspector.scheduled}")
	public void checkEpgs() {
		if(checkSatusServiceImp.checklogin()) {
			checkSatusServiceImp.checkEpgs();			
		}
	}

}
