package de.shop.lieferantenverwaltung.service;

import javax.ejb.ApplicationException;
import de.shop.lieferantenverwaltung.domain.AbstractLieferant;

/**
 * Exception, die ausgel&ouml;st wird, wenn ein Lieferant gel&ouml;scht werden
 * soll, aber mindestens eine Bestellung hat
 */
@ApplicationException(rollback = true)
public class LieferantDeleteLieferantenbestellungException extends
		AbstractLieferantServiceException {
	private static final long serialVersionUID = 2237194289969083093L;

	private static final String MESSAGE_KEY = "lieferant.deleteMitBestellung";
	private final Long lieferantId;
	private final int anzahlLieferantenbestellungen;

	public LieferantDeleteLieferantenbestellungException(AbstractLieferant lieferant) {
		super("Lieferant mit ID=" + lieferant.getId()
				+ " kann nicht geloescht werden: "
				+ lieferant.getLieferantenbestellungen().size() + " Bestellung(en)");
		this.lieferantId = lieferant.getId();
		this.anzahlLieferantenbestellungen = lieferant.getLieferantenbestellungen().size();
	}

	public Long getLieferantId() {
		return lieferantId;
	}

	public int getAnzahlLieferantenbestellungen() {
		return anzahlLieferantenbestellungen;
	}

	@Override
	public String getMessageKey() {
		return MESSAGE_KEY;
	}
}
