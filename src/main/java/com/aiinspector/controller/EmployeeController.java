package com.aiinspector.controller;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.aiinspector.config.YAMLConfig;
import com.aiinspector.entity.Employee;
import com.aiinspector.service.EmployeeService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/employees")
@Slf4j
public class EmployeeController {
	
	@Autowired
	private EmployeeService employeeService;
	
	@Autowired
	private  YAMLConfig myConfig;

	final AtomicInteger atomicInt = new AtomicInteger(0);
	
	@GetMapping("/{id}")
	private Mono<Employee> getEmployeeById(@PathVariable String id) {
		log.info("EmployeeController.getEmployeeById:{}", id);
		log.info("Environment:{}", myConfig.getEnvironment());
	    return employeeService.findById(id);
	   
	}

	@GetMapping("/all")
	private Flux<List<Employee>> getAllEmployees() {
		log.info("EmployeeController.getAllEmployees");
	    return employeeService.getAll();
	}
}


