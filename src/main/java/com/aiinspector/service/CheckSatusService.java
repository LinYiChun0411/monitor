package com.aiinspector.service;

import org.springframework.http.ResponseEntity;

public interface CheckSatusService {
	
	/**
	 * 赛事列表
	 */
	public void checkGameList();
	
	/**
	 * 赛事信息-启动
	 * @return login是否成功
	 */
	public boolean checklogin();

	/**
	 * 赛事信息
	 * @return response body
	 */
	public ResponseEntity checkEpgs();
	
	/**
	 * 赛事信息-各視頻
	 * @param jsonString: json response
	 */
	public void checkEpgPlayers(String jsonString);
	
	

}
