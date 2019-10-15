package com.aiinspector.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.aiinspector.constant.CheckConstant;
import com.aiinspector.exception.AIException;
import com.aiinspector.service.CheckSatusService;
import com.aiinspector.util.HttpUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CheckSatusServiceImp implements CheckSatusService {
	@Autowired
	HttpUtil gameListHttp;

	@Autowired
	HttpUtil epgsHttp;

	@Value("${inspector.check.login.project}")
	private String project;

	@Value("${inspector.check.login.secret}")
	private String secret;

	Map<String, String> loginMap = new HashMap<String, String>();

	public void checkGameList() {
		ResponseEntity responseEntity = gameListHttp.getHttp(CheckConstant.CHECKGAMELIST_URL);
		if (responseEntity.getStatusCodeValue() != 200) {
			throw new AIException(responseEntity.getBody().toString());
		}
	}

	public void checkEpgs() {
		if (checklogin(epgsHttp)) {
			MultiValueMap<String, String> valueMap = new LinkedMultiValueMap<String, String>();
			valueMap.add(CheckConstant.TOKEN, this.loginMap.get(CheckConstant.TOKEN));
			ResponseEntity responseEntity = epgsHttp.postHttp(CheckConstant.CHECKEPGS_URL, valueMap);
			if (responseEntity.getStatusCodeValue() != 200) {
				throw new AIException(responseEntity.getBody().toString());
			}
		}

	}

	public boolean checklogin(HttpUtil httpUtil) {
		MultiValueMap<String, String> valueMap = new LinkedMultiValueMap<String, String>();
		valueMap.add(CheckConstant.PROJECT, project);
		valueMap.add(CheckConstant.SECRET, secret);
		ResponseEntity responseEntity = httpUtil.postHttp(CheckConstant.CHECKEPGS_LOGIN_URL, valueMap);
		try {
			if (responseEntity.getStatusCodeValue() == 200) {
				ObjectMapper objectMapper = new ObjectMapper();
				Map<String, Object> loginmap = objectMapper.readValue(responseEntity.getBody().toString(), Map.class);
				this.loginMap.putAll((Map) loginmap.get(CheckConstant.DATA));
				log.info(this.loginMap.toString());
				return true;
			}
		} catch (Exception e) {
			throw new AIException(e.getMessage());
		}
		throw new AIException(responseEntity.getBody().toString());
	}

}
