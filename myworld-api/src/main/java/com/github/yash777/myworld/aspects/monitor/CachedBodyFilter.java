package com.github.yash777.myworld.aspects.monitor;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

@Component
public class CachedBodyFilter implements Filter {
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (request instanceof HttpServletRequest httpRequest) {
			CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(httpRequest);
			wrappedRequest.setAttribute("cachedRequestBody", wrappedRequest.getCachedBodyAsString());
			chain.doFilter(wrappedRequest, response);
		} else {
			chain.doFilter(request, response);
		}
	}
}
