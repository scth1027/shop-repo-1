package de.shop.lieferantenverwaltung.service;

import de.shop.util.AbstractShopException;

public abstract class LieferantServiceException extends
		AbstractShopException {
	private static final long serialVersionUID = -2849585609393128387L;

	public LieferantServiceException(String msg) {
		super(msg);
	}

	public LieferantServiceException(String msg, Throwable t) {
		super(msg, t);
	}
}
