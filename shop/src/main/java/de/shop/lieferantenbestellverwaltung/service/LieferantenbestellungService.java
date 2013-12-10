package de.shop.lieferantenbestellverwaltung.service;

import java.util.List;
import java.util.Locale;

import javax.validation.constraints.NotNull;

import de.shop.lieferantenbestellverwaltung.domain.Lieferantenbestellung;
import de.shop.lieferantenverwaltung.domain.Lieferant;

public interface LieferantenbestellungService {

	Lieferantenbestellung createLieferantenbestellung(Lieferantenbestellung bestellung, Lieferant lieferant,Locale locale);

	@NotNull(message = "lieferantenbestellung.notFound.id")
	Lieferantenbestellung findLieferantenbestellungById(Long id);

	@NotNull(message = "lieferantenbestellung.notFound.lieferant")
	List<Lieferantenbestellung> findLieferantenbestellungenByLieferant(Lieferant lieferant);

	List<Lieferantenbestellung> findAllLieferantenbestellungen();

	void deleteLieferantenbestellung(Long bestellungId);

	void updateLieferantenbestellung(Lieferantenbestellung bestellung);

}
