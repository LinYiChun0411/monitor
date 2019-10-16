package com.aiinspector.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aiinspector.config.YAMLConfig;
import com.aiinspector.entity.ApiInspectFailLog;
import com.aiinspector.service.ApiInspectFailLogService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@RestController
@RequestMapping("/apiinspect")
@Slf4j
public class ApiInspectController {
	
	@Autowired
	private ApiInspectFailLogService apiInspectFailLogService;
	
	@Autowired
	private  YAMLConfig myConfig;
	
//	@GetMapping("/{id}")
//	private Mono<Employee> getEmployeeById(@PathVariable String id) {
//		log.info("EmployeeController.getEmployeeById:{}, Environment:{}", id, myConfig.getEnvironment());
//	    return employeeService.findById(id);
//	   
//	}

	@GetMapping("/all")
	private Flux<List<ApiInspectFailLog>> getAllApiInspectFailLog() {
		log.info("ApiInspectController.getAllApiInspectFailLog, Environment:{}", myConfig.getEnvironment());
	    return apiInspectFailLogService.getAll().publishOn(Schedulers.parallel());
	}
}


