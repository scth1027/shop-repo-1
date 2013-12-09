package de.shop.lieferantenverwaltung.service;

import de.shop.util.AbstractShopException;

public abstract class AbstractLieferantServiceException extends
		AbstractShopException {
	private static final long serialVersionUID = -2849585609393128387L;

	public AbstractLieferantServiceException(String msg) {
		super(msg);
	}

	public AbstractLieferantServiceException(String msg, Throwable t) {
		super(msg, t);
	}
}
