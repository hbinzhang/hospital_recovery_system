package com.xmc.hospitalrec.exception;

public class RestException extends SktException {  
	private static final long serialVersionUID = 79780238977341990L;

	public RestException(String code) {  
        super(code, null, code, null);  
    }  
  
    public RestException(Throwable cause, String code) {  
        super(code, cause, code, null);  
    }  
  
    public RestException(String code, Object[] values) {  
        super(code, null, code, values);  
    }  
  
    public RestException(Throwable cause, String code, Object[] values) {  
        super(code, null, code, values);  
    }  
}  