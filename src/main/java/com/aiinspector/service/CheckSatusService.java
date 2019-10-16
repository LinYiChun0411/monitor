package com.aiinspector.service;

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
	 */
	public void checkEpgs();
	
	

}
