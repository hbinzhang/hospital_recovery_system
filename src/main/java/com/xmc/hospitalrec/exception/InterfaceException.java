package com.xmc.hospitalrec.exception;

public class InterfaceException extends SktException {  
	private static final long serialVersionUID = 79780238977341990L;

	public InterfaceException(String code) {  
        super(code, null, code, null);  
    }  
  
    public InterfaceException(Throwable cause, String code) {  
        super(code, cause, code, null);  
    }  
  
    public InterfaceException(String code, Object[] values) {  
        super(code, null, code, values);  
    }  
  
    public InterfaceException(Throwable cause, String code, Object[] values) {  
        super(code, null, code, values);  
    }  
}  