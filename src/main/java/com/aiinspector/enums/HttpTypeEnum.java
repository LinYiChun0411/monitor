package com.aiinspector.enums;

public enum HttpTypeEnum {
	Get("get"),
	Post("post");
	
	private String type;
	
	HttpTypeEnum(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}
	
	

}
