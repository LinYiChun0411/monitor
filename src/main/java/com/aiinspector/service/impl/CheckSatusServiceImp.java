package com.aiinspector.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.aiinspector.constant.CheckConstant;
import com.aiinspector.exception.AIException;
import com.aiinspector.service.CheckSatusCommonService;
import com.aiinspector.service.CheckSatusService;
import com.aiinspector.util.HttpUtil;
import com.aiinspector.util.ObjectMapperUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CheckSatusServiceImp implements CheckSatusService {	
	@Autowired
	HttpUtil normalHttp;
	
	@Value("${inspector.check.check-gamelist-server}")
	private String gameListServerString;

	@Value("${inspector.check.check-epgs-server}")
	private String epgsServerString;

	@Value("${inspector.check.login.project}")
	private String project;

	@Value("${inspector.check.login.secret}")
	private String secret;
	
	@Autowired
	CheckSatusCommonService checkSatusCommonServiceImp;

	Map<String, String> loginMap = new HashMap<String, String>();

	public void checkGameList() {
		String url = gameListServerString+CheckConstant.CHECKGAMELIST_URL;
		ResponseEntity responseEntity = checkSatusCommonServiceImp.checkCommonMethod(normalHttp, url, null, HttpMethod.GET);		
	}

	public ResponseEntity checkEpgs() {
		String url = epgsServerString+CheckConstant.CHECKEPGS_URL;
		MultiValueMap<String, String> valueMap = new LinkedMultiValueMap<String, String>();
		valueMap.add(CheckConstant.TOKEN, this.loginMap.get(CheckConstant.TOKEN));
		valueMap.add(CheckConstant.FILTER, CheckConstant.EPG);	
		return checkSatusCommonServiceImp.checkCommonMethod(normalHttp, url, valueMap, HttpMethod.POST);
	}

	public boolean checklogin() {
		String url = epgsServerString+CheckConstant.CHECKEPGS_LOGIN_URL;
		MultiValueMap<String, String> valueMap = new LinkedMultiValueMap<String, String>();
		valueMap.add(CheckConstant.PROJECT, project);
		valueMap.add(CheckConstant.SECRET, secret);
		ResponseEntity responseEntity = null;
		try {
			responseEntity = checkSatusCommonServiceImp.checkCommonMethod(normalHttp, url, valueMap, HttpMethod.POST);
			if (responseEntity.getStatusCodeValue() == 200) {
				Map<String, Object> loginmap = ObjectMapperUtil.getObjectmapper().readValue(responseEntity.getBody().toString(), Map.class);
				this.loginMap.putAll((Map) loginmap.get(CheckConstant.DATA));
				log.info(this.loginMap.toString());
				return true;
			}
		} catch (Exception e) {
			throw new AIException(e.getMessage(),url);
		}
		return false;
	}
	
	public void checkEpgPlayers(String jsonString) {
		try {
			Map respMap = ObjectMapperUtil.getObjectmapper().readValue(jsonString, Map.class);
			List<Map> dataList = (List) respMap.get(CheckConstant.DATA);
			dataList.stream().forEach(dataMap->{
				List<Map> epgList = (List) dataMap.get(CheckConstant.EPG);
				epgList.stream().forEach(liveMap->{
					ResponseEntity responseEntity = checkSatusCommonServiceImp.checkCommonMethod(normalHttp,liveMap.get(CheckConstant.URL).toString(), null, HttpMethod.GET);
				});
			});			

		} catch (Exception e) {
			throw new AIException(e.getMessage(), jsonString);
		}

	}
	

}
