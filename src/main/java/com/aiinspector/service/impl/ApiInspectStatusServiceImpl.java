package com.aiinspector.service.impl;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aiinspector.dao.mapper.ApiInspectStatusMapper;
import com.aiinspector.entity.ApiInspectStatus;
import com.aiinspector.service.ApiInspectStatusService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
@Slf4j
@Service
@Transactional
public class ApiInspectStatusServiceImpl extends ServiceImpl<ApiInspectStatusMapper, ApiInspectStatus> implements ApiInspectStatusService {
	
	@Resource
    private ApiInspectStatusMapper apiInspectStatusMapper;
	
	@Override
	public Flux<ApiInspectStatus> getAllStatusToday() {
		QueryWrapper<ApiInspectStatus> qryConditions = new QueryWrapper<>();
		Date today  = new java.sql.Date(ZonedDateTime.now(ZoneOffset.UTC).toInstant().toEpochMilli());
		qryConditions.eq("inspect_date", today);
		return Flux.fromIterable(apiInspectStatusMapper.selectList(qryConditions));
	}
	
	@Override
	public ApiInspectStatus findByUrlWithDate(String url, Date today) {
		Map<String, Object> queryMap = new LinkedHashMap<>();
		queryMap.put("inspect_url", url);
		queryMap.put("inspect_date", today);
		return apiInspectStatusMapper.selectOne(new QueryWrapper<ApiInspectStatus>().allEq(queryMap));
	}

}
