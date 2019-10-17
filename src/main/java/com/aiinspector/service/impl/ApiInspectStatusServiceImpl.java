package com.aiinspector.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.aiinspector.dao.mapper.ApiInspectStatusMapper;
import com.aiinspector.entity.ApiInspectStatus;
import com.aiinspector.service.ApiInspectStatusService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
@Transactional
public class ApiInspectStatusServiceImpl extends ServiceImpl<ApiInspectStatusMapper, ApiInspectStatus> implements ApiInspectStatusService {
	
	@Resource
    private ApiInspectStatusMapper apiInspectStatusMapper;
	

}
