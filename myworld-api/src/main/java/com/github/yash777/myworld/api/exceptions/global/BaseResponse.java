package com.github.yash777.myworld.api.exceptions.global;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor @NoArgsConstructor @Data
public class BaseResponse implements Serializable {
	private static final long serialVersionUID = 3414309655411886020L;

	private boolean error;
	private String code;
	private transient Object data;
	private String errorMessage;
}

//public class ErrorResponse {
//    private LocalDateTime timestamp;
//    private int status;
//    private String error;
//    private String message;
//    private String path;
//}