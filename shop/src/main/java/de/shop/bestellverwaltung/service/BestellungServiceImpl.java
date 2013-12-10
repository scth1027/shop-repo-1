package de.shop.bestellverwaltung.service;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.Mock;
import de.shop.util.interceptor.Log;

@Dependent
@Log
public class BestellungServiceImpl implements BestellungService, Serializable {

	private static final long serialVersionUID = 3222788597519982182L;

	@Inject
	@NeueBestellung
	private transient Event<Bestellung> event;

	@Override
	public Bestellung createBestellung(Bestellung bestellung, Kunde kunde,
			Locale locale) {
		bestellung = Mock.createBestellung(bestellung, kunde);
		event.fire(bestellung);
		System.out.println("da");
		return bestellung;
	}

	@Override
	@NotNull(message = "bestellung.notFound.id")
	public Bestellung findBestellungById(Long id) {
		return Mock.findBestellungById(id);
	}

	@Override
	@NotNull(message = "bestellung.notFound.kunde")
	public List<Bestellung> findBestellungenByKunde(Kunde kunde) {
		return Mock.findBestellungenByKunde(kunde);
	}

	@Override
	public List<Bestellung> findAllBestellungen() {
		return Mock.findAllBestellungen();
	}

	@Override
	public void deleteBestellung(Long bestellungId) {
		Mock.deleteBestellung(bestellungId);
	}

	@Override
	public void updateBestellung(Bestellung bestellung) {
		Mock.updateBestellung(bestellung);
	}

}