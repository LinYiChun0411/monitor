package com.aiinspector.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aiinspector.dao.mapper.ApiInspectFailLogMapper;
import com.aiinspector.entity.ApiInspectFailLog;
import com.aiinspector.service.ApiInspectFailLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
@Slf4j
@Service
@Transactional
public class ApiInspectFailLogServiceImpl extends ServiceImpl<ApiInspectFailLogMapper, ApiInspectFailLog> implements ApiInspectFailLogService {
	
	@Resource
    private ApiInspectFailLogMapper apiInspectFailLogMapper;
	
	@Override
	public Flux<List<ApiInspectFailLog>> getAll() {
		log.info("ApiInspectFailLogServiceImpl.getAll");
		  return Flux.just(apiInspectFailLogMapper.selectAll());
	}

	@Override
	public void toSaveWithMap(ApiInspectFailLog apiInspectFailLog) {
		FailLogMap.putIfAbsent(apiInspectFailLog.getReqUrl(), apiInspectFailLog);
		apiInspectFailLogMapper.insert(apiInspectFailLog);
	}

}
