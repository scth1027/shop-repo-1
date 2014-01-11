package de.shop.lieferantenbestellverwaltung.service;

import java.util.List;
import java.util.Locale;

import javax.validation.constraints.NotNull;

import de.shop.lieferantenbestellverwaltung.domain.Lieferantenbestellung;
import de.shop.lieferantenverwaltung.domain.AbstractLieferant;

public interface LieferantenbestellungService {

	Lieferantenbestellung createLieferantenbestellung(Lieferantenbestellung bestellung, AbstractLieferant lieferant,Locale locale);

	@NotNull(message = "lieferantenbestellung.notFound.id")
	Lieferantenbestellung findLieferantenbestellungById(Long id);

	@NotNull(message = "lieferantenbestellung.notFound.lieferant")
	List<Lieferantenbestellung> findLieferantenbestellungenByLieferant(AbstractLieferant lieferant);

	List<Lieferantenbestellung> findAllLieferantenbestellungen();

	void deleteLieferantenbestellung(Long bestellungId);

	void updateLieferantenbestellung(Lieferantenbestellung bestellung);

}
