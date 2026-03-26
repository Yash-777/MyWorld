package com.github.yash777.myworld.api.exceptions.global;


public class APIResponseHelper {

	// This Base method will return success JSON response.
	public static BaseResponse getSuccessResponse(final Object object) {
		BaseResponse response = new BaseResponse();
		response.setError(Boolean.FALSE);
		response.setCode(APIStatusCodes.SUCCESS.getCode());
		response.setData((object instanceof APIStatusCodes) ? ((APIStatusCodes) object).getMessage() : object);
		return response;
	}
	// This Base method will only return success
	public static BaseResponse getSuccessResponse() {
		return getSuccessResponse(null);
	}

	// This Base method will Give Generic or Unknown error message, if any unhandle exception occurs.
	public static BaseResponse getErrorResponse() {
		return getErrorResponse(APIStatusCodes.GENERIC_OR_UNKNOWN_ERROR);
	}
	public static BaseResponse getErrorResponse(APIStatusCodes APIStatusCodes, String errorMessage) {
		BaseResponse response = new BaseResponse();
		response.setError(Boolean.TRUE);
		response.setCode(APIStatusCodes.getCode());
		response.setErrorMessage(errorMessage);
		return response;
	}
	public static BaseResponse getErrorResponse(APIStatusCodes APIStatusCodes, final Object object) {
		BaseResponse response = getErrorResponse(APIStatusCodes);
		response.setData(object);
		return response;
	}
	public static BaseResponse getErrorResponse(APIStatusCodes APIStatusCodes) {
		BaseResponse response = new BaseResponse();
		response.setError(Boolean.TRUE);
		response.setCode(APIStatusCodes.getCode());
		response.setErrorMessage(APIStatusCodes.getMessage());
		return response;
	}
	public static BaseResponse getErrorResponse(final Object object) {
		BaseResponse response = new BaseResponse();
		response.setError(Boolean.TRUE);
		response.setCode(APIStatusCodes.GENERIC_OR_UNKNOWN_ERROR.getCode());
		response.setData(object);
		return response;
	}
}
