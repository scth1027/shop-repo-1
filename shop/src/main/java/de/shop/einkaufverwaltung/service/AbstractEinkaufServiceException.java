package de.shop.einkaufverwaltung.service;

import de.shop.util.AbstractShopException;

public abstract class AbstractEinkaufServiceException extends AbstractShopException {
	private static final long serialVersionUID = -2849585609393128387L;

	public AbstractEinkaufServiceException(String msg) {
		super(msg);
	}

	public AbstractEinkaufServiceException(String msg, Throwable t) {
		super(msg, t);
	}
}
