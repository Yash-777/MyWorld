package com.github.yash777.myworld.api.exceptions.global;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public enum APIStatusCodes {
	SUCCESS("200", "Success."),
	HTTP_METHOD_NOT_SUPPORTED("400", "Http Method Not Supported."),
	INTERNAL_SERVER_ERROR("500", "Internal Server Error."), 
	
	GENERIC_OR_UNKNOWN_ERROR("4001", "Generic Or Unknown Error."),
	REQUIRED_FIELDS_VALIDATION_ERRORS("1001", "Some Required Fields are Blank.");
	
	private String code, message;
}