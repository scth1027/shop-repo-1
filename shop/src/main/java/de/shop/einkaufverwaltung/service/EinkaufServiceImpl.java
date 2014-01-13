package de.shop.einkaufverwaltung.service;

import static de.shop.util.Constants.KEINE_ID;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import de.shop.einkaufverwaltung.domain.Einkauf;
import de.shop.einkaufverwaltung.domain.Einkaufposten;
import de.shop.lieferantenverwaltung.domain.Lieferant;
import de.shop.lieferantenverwaltung.service.LieferantService;
import de.shop.lieferantenverwaltung.service.LieferantService.OrderType;
import de.shop.util.interceptor.Log;

@Dependent
@Log
public class EinkaufServiceImpl implements EinkaufService, Serializable {
	
	private static final long serialVersionUID = 3222788597519982182L;

	@Inject
	@NeuerEinkauf
	private transient Event<Einkauf> event;

	@Inject
	private transient EntityManager em;

	@Inject
	private LieferantService ls;

	/**
	 * Suche den Einkauf zu gegebener ID.
	 * 
	 * @param id
	 *            ID des gesuchten Einkauf.
	 * @return Der gefundene Einkauf, sonst null.
	 */
	@Override
	@NotNull(message = "einkauf.notFound.id")
	public Einkauf findEinkaufById(Long id, FetchType fetch) {
		if (id == null) {
			return null;
		}

		Einkauf einkauf;
		switch (fetch) {
		case NUR_EINKAUF:
			einkauf = em.find(Einkauf.class, id);
			break;

		default:
			einkauf = em.find(Einkauf.class, id);
			break;
		}
		return einkauf;
	}

	/**
	 * Suche nach allen Einkkaeufen.
	 * 
	 * @return Liste aller Einkaeufe.
	 */
	@Override
	@NotNull(message = "einkauf.notFound.all")
	public List<Einkauf> findAllEinkaeufe(OrderType order) {
		final TypedQuery<Einkauf> query = OrderType.ID.equals(order) ? em
				.createNamedQuery(Einkauf.FIND_EINKAEUFE_ORDER_BY_ID,
					Einkauf.class) : em.createNamedQuery(
				Einkauf.FIND_EINKAEUFE, Einkauf.class);
		
		return query.getResultList();
	}
	
	

	//FIXME:Nicht in der AinkaufResource
	/**
	 * Suche nach allen Einkkaeufen zu gegebenm Lieferant.
	 * 
	 * @param lieferant
	 *            Der Lieferant zu dem die Einkaeufe gefunden werden sollen.
	 * @return Liste aller Einkaeufe zu dem gegebenem Lieferanten.
	 */
	@Override
	@NotNull(message = "einkauf.notFound.lieferant")
	public List<Einkauf> findEinkaeufeByLieferant(Lieferant lieferant) {
		if (lieferant == null) {
			return Collections.emptyList();
		}
		return em
				.createNamedQuery(Einkauf.FIND_EINKAEUFE_BY_LIEFERANT,
						Einkauf.class)
				.setParameter(Einkauf.PARAM_LIEFERANT, lieferant)
				.getResultList();
	}
	
	/**
	 * Einen neuen Einkauf in der DB anlegen.
	 * 
	 * @param einkauf
	 *            Der anzulegende Einkauf.
	 * @return Der neue Einkauf einschliesslich generierter ID.
	 */
	@Override
	public Einkauf createEinkauf(Einkauf einkauf, Lieferant lieferant) {
		if (einkauf == null) {
			return null;
		}

		// Den persistenten Kunden mit der transienten Bestellung verknuepfen
		if (!em.contains(lieferant)) {
			lieferant = ls.findLieferantById(lieferant.getId(),
					LieferantService.FetchType.NUR_LIEFERANT);
		}
		lieferant.addEinkauf(einkauf);
		einkauf.setLieferant(lieferant);

		// Vor dem Abspeichern IDs zuruecksetzen:
		// IDs koennten einen Wert != null haben, wenn sie durch einen Web
		// Service uebertragen wurden
		lieferant.setId(KEINE_ID);
		for (Einkaufposten bp : einkauf.getEinkaufposten()) {
			bp.setId(KEINE_ID);
		}
		return einkauf;
	}
}
