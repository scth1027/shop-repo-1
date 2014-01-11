package de.shop.lieferantenbestellverwaltung.service;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import de.shop.lieferantenbestellverwaltung.domain.Lieferantenbestellung;
import de.shop.lieferantenverwaltung.domain.AbstractLieferant;
import de.shop.util.Mock;
import de.shop.util.interceptor.Log;

@Dependent
@Log
public class LieferantenbestellungServiceImpl implements LieferantenbestellungService, Serializable {

	private static final long serialVersionUID = 3222788597519982182L;

	@Inject
	@NeueLieferantenbestellung
	private transient Event<Lieferantenbestellung> event;

	@Override
	public Lieferantenbestellung createLieferantenbestellung(Lieferantenbestellung bestellung, AbstractLieferant lieferant, Locale locale) {
		bestellung = Mock.createLieferantenbestellung(bestellung, lieferant);
		event.fire(bestellung);
		System.out.println("da");
		return bestellung;
	}

	@Override
	@NotNull(message = "lieferantenbestellung.notFound.id")
	public Lieferantenbestellung findLieferantenbestellungById(Long id) {
		return Mock.findLieferantenbestellungById(id);
	}

	@Override
	@NotNull(message = "lieferantenbestellung.notFound.lieferant")
	public List<Lieferantenbestellung> findLieferantenbestellungenByLieferant(AbstractLieferant lieferant) {
		return Mock.findBestellungenByLieferant(lieferant);
	}

	@Override
	public List<Lieferantenbestellung> findAllLieferantenbestellungen() {
		return Mock.findAllLieferantenbestellungen();
	}

	@Override
	public void deleteLieferantenbestellung(Long bestellungId) {
		Mock.deleteBestellung(bestellungId);
	}

	@Override
	public void updateLieferantenbestellung(Lieferantenbestellung bestellung) {
		Mock.updateLieferantenbestellung(bestellung);
	}

}
