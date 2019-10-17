package com.aiinspector.service.impl;

import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;

import com.aiinspector.service.CheckSatusCommonService;
import com.aiinspector.util.HttpUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CheckSatusCommonServiceImp implements CheckSatusCommonService{
	
	@Override
	public ResponseEntity checkCommonMethod(HttpUtil httpUtil, String url, MultiValueMap<String, String> valueMap,HttpMethod httpMethod) {
		ResponseEntity responseEntity = null;
		switch (httpMethod) {
		case GET: {
			responseEntity = httpUtil.getHttp(url);
		}
			break;
		case POST: {
			responseEntity = httpUtil.postHttp(url, valueMap);
		}
			break;
		default:
			break;
		}
		return responseEntity;
	}

}
