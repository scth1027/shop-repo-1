package de.shop.lieferantenverwaltung.service;

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true)
public class EmailExistsException extends AbstractLieferantServiceException {
	private static final long serialVersionUID = 4867667611097919943L;

	private static final String MESSAGE_KEY = "lieferant.emailExists";
	private final String email;

	public EmailExistsException(String email) {
		super("Die Email-Adresse " + email + " existiert bereits");
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	@Override
	public String getMessageKey() {
		return MESSAGE_KEY;
	}
}
