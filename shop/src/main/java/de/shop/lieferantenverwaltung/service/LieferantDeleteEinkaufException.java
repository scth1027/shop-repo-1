package de.shop.lieferantenverwaltung.service;

import javax.ejb.ApplicationException;
import de.shop.lieferantenverwaltung.domain.Lieferant;

/**
 * Exception, die ausgel&ouml;st wird, wenn ein Lieferant gel&ouml;scht werden
 * soll, aber mindestens eine Einkauf hat
 */
@ApplicationException(rollback = true)
public class LieferantDeleteEinkaufException extends
		LieferantServiceException {
	private static final long serialVersionUID = 2237194289969083093L;

	private static final String MESSAGE_KEY = "lieferant.deleteMitEinkauf";
	private final Long lieferantId;
	private final int anzahlEinkaeufe;

	public LieferantDeleteEinkaufException(Lieferant lieferant) {
		super("Lieferant mit ID=" + lieferant.getId()
				+ " kann nicht geloescht werden: "
				+ lieferant.getEinkaeufe().size() + " Einkauf(Einkaeufe)");
		this.lieferantId = lieferant.getId();
		this.anzahlEinkaeufe = lieferant.getEinkaeufe().size();
	}

	public Long getLieferantId() {
		return lieferantId;
	}

	public int getAnzahlEinkaeufe() {
		return anzahlEinkaeufe;
	}

	@Override
	public String getMessageKey() {
		return MESSAGE_KEY;
	}
}
