package com.ericsson.skt.exception;

import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;

import com.ericsson.skt.rest.util.Message;

@Provider
public class SktExceptionMapperSupport implements ExceptionMapper<SktException> {
	private static final Logger LOGGER = LoggerFactory.getLogger(SktExceptionMapperSupport.class);

	private static final String CONTEXT_ATTRIBUTE = WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE;

	@Context
	private HttpServletRequest request;

	@Context
	private ServletContext servletContext;

	public Response toResponse(SktException exception) {
		WebApplicationContext context = (WebApplicationContext) servletContext.getAttribute(CONTEXT_ATTRIBUTE);

		Locale locale = Locale.CHINA;// request.getLocale()
		String message = context.getMessage(exception.getCode(), exception.getValues(), exception.getMessage(), locale);
		LOGGER.error(message, exception);

		return Response.ok(new Message(message), MediaType.APPLICATION_JSON).status(Status.EXPECTATION_FAILED).build();
	}
}