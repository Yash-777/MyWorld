package com.github.yash777.myworld.api.exceptions.global;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.github.yash777.myworld.api.exceptions.global.APIResponseHelper.getErrorResponse;

//@ControllerAdvice // @Component public @interface ControllerAdvice {  }
@RestControllerAdvice // @ControllerAdvice @ResponseBody public @interface RestControllerAdvice { }
public class GlobalExceptionHandlingControllerAdvice {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private MessageSource messageSource;

	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	@ExceptionHandler(Exception.class)
	public @ResponseBody BaseResponse handleGeneralError(Exception exception, HttpServletRequest request,
			HttpServletResponse response) {
		logger.error("General Exception {{}}. Exception Message {{}},Exception Details {{}} : ",
				request.getRequestURL(), exception.getMessage(), exception);
		return getErrorResponse(APIStatusCodes.INTERNAL_SERVER_ERROR);
	}

//	@ResponseStatus(value = HttpStatus.TOO_MANY_REQUESTS)
//	@ExceptionHandler(org.hibernate.StaleStateException.class)
//	public @ResponseBody BaseResponse handleStaleStateError(Exception exception, 
//			HttpServletRequest request, HttpServletResponse response) {
//		logger.error("StaleState Exception {{}}. Exception Message {{}},Exception Details {{}} : ",
//				request.getRequestURL(), exception.getMessage(), exception);
//		return getErrorResponse();
//	}

	// ExceptionHandlerExceptionResolver - Resolved [org.springframework.web.HttpRequestMethodNotSupportedException: Request method 'GET' not supported]
	@ExceptionHandler(HttpRequestMethodNotSupportedException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody BaseResponse processHttpRequestMethodNotSupportedException(
			HttpRequestMethodNotSupportedException ex, HttpServletRequest request, HttpServletResponse response) {
		logger.error("HTTP Request Method Not Supported Exception {{}}. Exception Message {{}},Exception Details {{}} : ",
				request.getRequestURL(), ex.getMessage(), ex);
		return getErrorResponse(APIStatusCodes.HTTP_METHOD_NOT_SUPPORTED);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public @ResponseBody BaseResponse processValidationError(MethodArgumentNotValidException ex,
			HttpServletRequest request, HttpServletResponse response) {
		BindingResult result = ex.getBindingResult();
		List<FieldError> fieldErrors = result.getFieldErrors();
		logger.error("Method Arguments Exception {{}}. Exception Message {{}},Exception Details {{}} : ",
				request.getRequestURL(), ex.getMessage(), ex);
		return processFieldErrors(fieldErrors);
	}

	@com.fasterxml.jackson.annotation.JsonInclude(com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL)
	@AllArgsConstructor @Data @NoArgsConstructor
	public class ErrorField {
		private String fieldName;
		private String message;
	}
	private BaseResponse processFieldErrors(List<FieldError> fieldErrors) {
		List<ErrorField> fieldErrorsList = new ArrayList<>();
		for (FieldError fieldError : fieldErrors) {
			fieldErrorsList.add(new ErrorField(fieldError.getField(), resolveLocalizedErrorMessage(fieldError)));
		}
		return getErrorResponse(APIStatusCodes.REQUIRED_FIELDS_VALIDATION_ERRORS, fieldErrorsList);
	}
	private String resolveLocalizedErrorMessage(FieldError fieldError) {
		Locale currentLocale = LocaleContextHolder.getLocale();
		return messageSource.getMessage(fieldError, currentLocale);
	}
	
	/*
    // 404 - Custom Exception
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    // 400 - Validation Errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                message,
                request.getRequestURI()
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // 500 - Generic Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                ex.getMessage(),
                request.getRequestURI()
        );

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
	 */
}