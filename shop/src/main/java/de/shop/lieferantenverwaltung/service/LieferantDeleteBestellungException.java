package de.shop.lieferantenverwaltung.service;

import javax.ejb.ApplicationException;
import de.shop.lieferantenverwaltung.domain.Lieferant;


/**
 * Exception, die ausgel&ouml;st wird, wenn ein Lieferant gel&ouml;scht werden soll, aber mindestens eine Bestellung hat
 */
@ApplicationException(rollback = true)
public class LieferantDeleteBestellungException extends AbstractLieferantServiceException {
	private static final long serialVersionUID = 2237194289969083093L;
	
	private static final String MESSAGE_KEY = "lieferant.deleteMitBestellung";
	private final Long lieferantId;
	private final int anzahlBestellungen;
	
	public LieferantDeleteBestellungException(Lieferant lieferant) {
		super("Lieferant mit ID=" + lieferant.getId() + " kann nicht geloescht werden: "
			  + lieferant.getBestellungen().size() + " Bestellung(en)");
		this.lieferantId = lieferant.getId();
		this.anzahlBestellungen = lieferant.getBestellungen().size();
	}

	public Long getLieferantId() {
		return lieferantId;
	}
	public int getAnzahlBestellungen() {
		return anzahlBestellungen;
	}
	
	@Override
	public String getMessageKey() {
		return MESSAGE_KEY;
	}
}
	