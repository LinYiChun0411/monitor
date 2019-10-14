package com.aiinspector.service.impl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.aiinspector.entity.Employee;
import com.aiinspector.service.EmployeeService;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Slf4j
@Service
public class EmployeeServiceImpl implements EmployeeService {
	final List<Employee> employees = Stream.of(Employee.builder().id(1).name("joey").age(18).build()
			  ,Employee.builder().id(2).name("jeff").age(17).build())
			  .collect(Collectors.toList());
	@Override
	public Flux<List<Employee>> getAll() {
		log.info("EmployeeServiceImpl.getAll");
		  return Flux.just(employees);
	}
	
	@Override
	public Mono<Employee> findById(String id) {
		log.info("EmployeeServiceImpl.findById id:{}", id);
		return Mono.just(
	    		employees.stream().filter(
    					e->e.getId().toString().equals(id)
    					).findFirst().get()
    		);
	}

}
