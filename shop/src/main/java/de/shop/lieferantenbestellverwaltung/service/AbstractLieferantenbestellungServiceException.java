package de.shop.lieferantenbestellverwaltung.service;

import de.shop.util.AbstractShopException;

public abstract class AbstractLieferantenbestellungServiceException extends AbstractShopException {
	private static final long serialVersionUID = -2849585609393128387L;

	public AbstractLieferantenbestellungServiceException(String msg) {
		super(msg);
	}

	public AbstractLieferantenbestellungServiceException(String msg, Throwable t) {
		super(msg, t);
	}
}
