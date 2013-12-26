package de.shop.bestellverwaltung.service;

import java.util.List;
import java.util.Locale;

import javax.validation.constraints.NotNull;

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.kundenverwaltung.domain.AbstractKunde;

public interface BestellungService {

	Bestellung createBestellung(Bestellung bestellung, AbstractKunde kunde,
			Locale locale);

	@NotNull(message = "bestellung.notFound.id")
	Bestellung findBestellungById(Long id);

	@NotNull(message = "Bestellung.notFound.Kunde")
	List<Bestellung> findBestellungenByKunde(AbstractKunde kunde);

	List<Bestellung> findAllBestellungen();

	void deleteBestellung(Long bestellungId);

	void updateBestellung(Bestellung bestellung);

}
