package de.shop.einkaufverwaltung.service;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import de.shop.einkaufverwaltung.domain.Einkauf;
import de.shop.lieferantenverwaltung.domain.Lieferant;
import de.shop.util.Mock;
import de.shop.util.interceptor.Log;

@Dependent
@Log
public class EinkaufServiceImpl implements EinkaufService, Serializable {

	private static final long serialVersionUID = 3222788597519982182L;

	@Inject
	@NeuerEinkauf
	private transient Event<Einkauf> event;

	@Override
	public Einkauf createEinkauf(Einkauf einkauf, Lieferant lieferant, Locale locale) {
		einkauf = Mock.createEinkauf(einkauf, lieferant);
		event.fire(einkauf);
		System.out.println("da");
		return einkauf;
	}

	@Override
	@NotNull(message = "einkauf.notFound.id")
	public Einkauf findEinkaufById(Long id) {
		return Mock.findEinkaufById(id);
	}

	@Override
	@NotNull(message = "einkauf.notFound.lieferant")
	public List<Einkauf> findEinkaeufeByLieferant(Lieferant lieferant) {
		return Mock.findEinkaeufeByLieferant(lieferant);
	}

	@Override
	public List<Einkauf> findAllEinkaeufe() {
		return Mock.findAllEinkaeufe();
	}

	@Override
	public void deleteEinkauf(Long einkaufId) {
		Mock.deleteEinkauf(einkaufId);
	}

	@Override
	public void updateEinkauf(Einkauf einkauf) {
		Mock.updateEinkauf(einkauf);
	}

}
