package com.ericsson.skt.exception;

public class SktException extends RuntimeException {  
	private static final long serialVersionUID = 49738065656951558L;
	private String code;  
    private Object[] values;  
  
    public String getCode() {  
        return code;  
    }  
  
    public void setCode(String code) {  
        this.code = code;  
    }  
  
    public Object[] getValues() {  
        return values;  
    }  
  
    public void setValues(Object[] values) {  
        this.values = values;  
    }  
  
    public SktException(String message, Throwable cause, String code, Object[] values) {  
        super(message, cause);  
        this.code = code;  
        this.values = values;  
    }  
}