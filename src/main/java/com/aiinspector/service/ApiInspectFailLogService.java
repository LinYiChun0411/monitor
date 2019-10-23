package com.aiinspector.service;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Generated;

import com.aiinspector.entity.ApiInspectFailLog;
import com.baomidou.mybatisplus.extension.service.IService;

import lombok.Getter;
import reactor.core.publisher.Flux;


public interface ApiInspectFailLogService extends IService<ApiInspectFailLog> {
	
	final static ConcurrentHashMap<String, ApiInspectFailLog> FailLogMap = new ConcurrentHashMap<>();
	
	public void toSaveWithMap(ApiInspectFailLog apiInspectFailLog);
	
	public Flux<List<ApiInspectFailLog>> getAll();
}
