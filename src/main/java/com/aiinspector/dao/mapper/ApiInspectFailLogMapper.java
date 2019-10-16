package com.aiinspector.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import com.aiinspector.entity.ApiInspectFailLog;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

@Mapper
public interface ApiInspectFailLogMapper extends BaseMapper<ApiInspectFailLog>{
	
	@Select(value = "select * from ai.api_inspect_fail_log")
	public List<ApiInspectFailLog> selectAll();
}
