package service.rest;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import struts.LoggingInterceptor;

public class RestLoggingFilter implements Filter {
	private final transient Log log = LogFactory
			.getLog(RestLoggingFilter.class);

	FilterConfig fc;
	

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		
		if( request instanceof HttpServletRequest){
			HttpServletRequest httpRequest = (HttpServletRequest)request;
			log.info("Start rest request. Method:" + httpRequest.getMethod() + LoggingInterceptor.getParameters(httpRequest));
		}
		
		chain.doFilter(request, response);
		
	}

	@Override
	public void init(FilterConfig fc) throws ServletException {
		this.fc = fc;
	}
	
	@Override
	public void destroy() {
		this.fc = null;
	}

}
