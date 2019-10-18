package com.aiinspector.service;

import java.util.List;

import com.aiinspector.entity.ApiInspectFailLog;
import com.aiinspector.entity.ApiInspectStatus;
import com.baomidou.mybatisplus.extension.service.IService;

import reactor.core.publisher.Flux;


public interface ApiInspectStatusService extends IService<ApiInspectStatus> {
	public Flux<ApiInspectStatus> getAllStatusToday();
	
}
