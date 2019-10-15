package com.aiinspector.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aiinspector.dao.mapper.EmployeeMapper;
import com.aiinspector.entity.Employee;
import com.aiinspector.service.EmployeeService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Slf4j
@Service
@Transactional
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
	
	@Resource
    private EmployeeMapper employeeMapper;
	
	@Override
	public Flux<List<Employee>> getAll() {
		log.info("EmployeeServiceImpl.getAll");
		  return Flux.just(employeeMapper.selectAll());
	}
	
	@Override
	public Mono<Employee> findById(String id) {
		log.info("EmployeeServiceImpl.findById id:{}", id);
		return Mono.just(employeeMapper.selectById(id));
	}

}
