package com.aiinspector.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.aiinspector.entity.Employee;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

public interface EmployeeMapper extends BaseMapper<Employee>{
	
	@Select(value = "select id, name, age from ai.employee")
	public List<Employee> selectAll();
}
