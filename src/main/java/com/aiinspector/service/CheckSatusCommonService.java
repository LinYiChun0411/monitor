package com.aiinspector.service;

import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import com.aiinspector.enums.HttpTypeEnum;
import com.aiinspector.util.HttpUtil;

public interface CheckSatusCommonService {
	
	public ResponseEntity checkCommonMethod(HttpUtil httpUtil, String url, MultiValueMap<String, String> valueMap,HttpTypeEnum httpTypeEnum);

}
