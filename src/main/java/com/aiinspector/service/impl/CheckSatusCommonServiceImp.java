package com.aiinspector.service.impl;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.aiinspector.enums.HttpTypeEnum;
import com.aiinspector.service.CheckSatusCommonService;
import com.aiinspector.util.HttpUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CheckSatusCommonServiceImp implements CheckSatusCommonService{
	
	@Override
	public ResponseEntity checkCommonMethod(HttpUtil httpUtil, String url, MultiValueMap<String, String> valueMap,HttpTypeEnum httpTypeEnum) {
		ResponseEntity responseEntity = null;
		switch (httpTypeEnum) {
		case Get: {
			responseEntity = httpUtil.getHttp(url);
		}
			break;
		case Post: {
			responseEntity = httpUtil.postHttp(url, valueMap);
		}
			break;
		default:
			break;
		}
		return responseEntity;
	}

}
