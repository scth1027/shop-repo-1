package de.shop.bestellverwaltung.service;

import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Locale;

import javax.enterprise.context.Dependent;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.kundenverwaltung.domain.Kunde;


@Decorator
@Dependent
public abstract class BestellungServiceMitGeschenkverpackung implements BestellungService {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	@Inject
	@Delegate
	@Any
	private BestellungService bs;

	
	@Override
	public Bestellung findBestellungById(Long id) {
		return bs.findBestellungById(id);
	}

	
	@Override
	public List<Bestellung> findBestellungenByKunde(Kunde kunde) {
		return bs.findBestellungenByKunde(kunde);
	}

	/**
	 * {inheritDoc}
	 */
	@Override
	public Bestellung createBestellung(Bestellung bestellung, Kunde kunde, Locale locale) {
		LOGGER.warn("Geschenkverpackung noch nicht implementiert");
		
		return bs.createBestellung(bestellung, kunde, locale);
	}
}
