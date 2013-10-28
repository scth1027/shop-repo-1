package de.shop.util.rest;

public class NotFoundException extends RuntimeException {
	private static final long serialVersionUID = -866705588853138386L;

	public NotFoundException(String message) {
		super(message);
	}
	
	public NotFoundException(String message, Throwable t) {
		super(message, t);
	}
}
