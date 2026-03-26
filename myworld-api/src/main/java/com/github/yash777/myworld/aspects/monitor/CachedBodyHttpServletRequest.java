package com.github.yash777.myworld.aspects.monitor;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

public class CachedBodyHttpServletRequest extends HttpServletRequestWrapper {
	
	private final byte[] cachedBody;
	
	public CachedBodyHttpServletRequest(HttpServletRequest request) throws IOException {
		super(request);
		InputStream requestInputStream = request.getInputStream();
		this.cachedBody = requestInputStream.readAllBytes();
	}
	
	@Override
	public ServletInputStream getInputStream() {
		return new CachedServletInputStream(this.cachedBody);
	}
	
	@Override
	public BufferedReader getReader() {
		return new BufferedReader(new InputStreamReader(getInputStream()));
	}
	
	public String getCachedBodyAsString() {
		return new String(this.cachedBody);
	}
	
	private static class CachedServletInputStream extends ServletInputStream {
		private final ByteArrayInputStream buffer;
		
		public CachedServletInputStream(byte[] body) {
			this.buffer = new ByteArrayInputStream(body);
		}
		
		@Override
		public boolean isFinished() {
			return buffer.available() == 0;
		}
		
		@Override
		public boolean isReady() {
			return true;
		}
		
		@Override
		public void setReadListener(ReadListener readListener) {
		}
		
		@Override
		public int read() {
			return buffer.read();
		}
	}
}
