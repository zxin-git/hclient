package com.zxin.java.common.http.exception;


public class HCBException  extends Exception {

	private static final long serialVersionUID = -728737161798695346L;

	public HCBException(Exception e){
		super(e);
	}

	/**
	 * @param msg	消息
	 */
	public HCBException(String msg) {
		super(msg);
	}
	
	/**
	 * @param message	异常消息
	 * @param e			异常
	 */
	public HCBException(String message, Exception e) {
		super(message, e);
	}
	
}