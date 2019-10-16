package com.aiinspector.service;

import java.util.List;

import com.aiinspector.entity.ApiInspectFailLog;
import com.baomidou.mybatisplus.extension.service.IService;

import reactor.core.publisher.Flux;


public interface ApiInspectFailLogService extends IService<ApiInspectFailLog> {
	public Flux<List<ApiInspectFailLog>> getAll();
}
