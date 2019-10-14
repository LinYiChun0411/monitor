package com.aiinspector.service;

import java.util.List;

import com.aiinspector.entity.Employee;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface EmployeeService {
	public Flux<List<Employee>> getAll();
	public Mono<Employee> findById(String id);
}
