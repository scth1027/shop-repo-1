package de.shop.bestellverwaltung.service;

import static de.shop.util.Constants.KEINE_ID;
import static de.shop.util.Constants.LOADGRAPH;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.domain.Posten;
import de.shop.kundenverwaltung.domain.AbstractKunde;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.util.interceptor.Log;

@Dependent
@Log
public class BestellungServiceImpl implements Serializable, BestellungService {

	private static final long serialVersionUID = 2340151324738066967L;

	@Inject
	private transient EntityManager em;

	@Inject
	private KundeService ks;

	@Inject
	@NeueBestellung
	private transient Event<Bestellung> event;

	/**
	 * Suche die Bestellung zu gegebener ID.
	 * 
	 * @param id
	 *            ID der gesuchten Bestellung.
	 * @exception ConstraintViolationException
	 *                zu @NotNull, falls keine Bestellung gefunden wurde
	 * @return Die gefundene Bestellung, sonst null.
	 */
	@Override
	@NotNull(message = "{bestellung.notFound.id}")
	public Bestellung findBestellungById(Long id, FetchType fetch) {
		if (id == null) {
			return null;
		}

		Bestellung bestellung;
		switch (fetch) {
		case NUR_BESTELLUNG:
			bestellung = em.find(Bestellung.class, id);
			break;

		default:
			bestellung = em.find(Bestellung.class, id);
			break;
		}
		return bestellung;
	}
	
	/**
	 * {inheritDoc}
	 *
	 * Suche Bestellungen zu den gegebenen IDs.
	 * 
	 * @param ids
	 *            IDs den gesuchten Bestellungen.
	 * @return Die gefundenen Bestellungen, sonst null.
	 *
	 * @exception ConstraintViolationException
	 *                zu @Size, falls die Liste leer ist
	 */
	@Override
	@Size(min = 1, message = "{bestellung.notFound.ids}")
	public List<Bestellung> findBestellungenByIds(List<Long> ids,
			FetchType fetch) {
		if (ids == null || ids.isEmpty()) {
			return Collections.emptyList();
		}

		// SELECT b
		// FROM Bestellung b
		// WHERE b.id = <id> OR ...

		final CriteriaBuilder builder = em.getCriteriaBuilder();
		final CriteriaQuery<Bestellung> criteriaQuery = builder
				.createQuery(Bestellung.class);
		final Root<Bestellung> b = criteriaQuery.from(Bestellung.class);

		// Die Vergleichen mit "=" als Liste aufbauen
		final Path<Long> idPath = b.get("id");
		final List<Predicate> predList = new ArrayList<>();
		for (Long id : ids) {
			final Predicate equal = builder.equal(idPath, id);
			predList.add(equal);
		}
		// Die Vergleiche mit "=" durch "or" verknuepfen
		final Predicate[] predArray = new Predicate[predList.size()];
		final Predicate pred = builder.or(predList.toArray(predArray));
		criteriaQuery.where(pred).distinct(true);

		final TypedQuery<Bestellung> query = em.createQuery(criteriaQuery);
		if (FetchType.MIT_LIEFERUNGEN.equals(fetch)) {
			final EntityGraph<?> entityGraph = em
					.getEntityGraph(Bestellung.GRAPH_LIEFERUNGEN);
			query.setHint(LOADGRAPH, entityGraph);
		}

		return query.getResultList();
	}

	//TODO:findLieferungenByBestellungId
	
	//FIXME:Muss das nicht in KundeService
	/**
	 * {inheritDoc}
	 *
	 * Suche nach Kunde zu Bestellungs ID.
	 * 
	 * @param id
	 *            ID des Kunden.
	 * @return Den gefundenen Kunden, sonst null.
	 *
	 * @exception ConstraintViolationException
	 *                zu @NotNull, falls kein Kunde gefunden wurde
	 */
	@Override
	@NotNull(message = "{bestellung.kunde.notFound.id}")
	public AbstractKunde findKundeById(Long id) {
		try {
			return em
					.createNamedQuery(Bestellung.FIND_KUNDE_BY_ID,
							AbstractKunde.class)
					.setParameter(Bestellung.PARAM_ID, id).getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	//FIXME:Nicht in der Resource vorhanden
	/**
	 * {inheritDoc}
	 *
	 * Suche Bestellungen zu den gegebenen Kunden.
	 * 
	 * @param kunde
	 *            AbstractKunde zu dem Bestellungen gesucht werden sollen.
	 * @return Die gefundenen Bestellungen, sonst null.
	 *
	 * @exception ConstraintViolationException
	 *                zu @Size, falls die Liste leer ist
	 */
	@Override
	@Size(min = 1, message = "{bestellung.notFound.kunde}")
	public List<Bestellung> findBestellungenByKunde(AbstractKunde kunde) {
		if (kunde == null) {
			return Collections.emptyList();
		}
		return em
				.createNamedQuery(Bestellung.FIND_BESTELLUNGEN_BY_KUNDE,
						Bestellung.class)
				.setParameter(Bestellung.PARAM_KUNDE, kunde).getResultList();
	}

	/**
	 * Zuordnung einer neuen, transienten Bestellung zu einem existierenden,
	 * persistenten Kunden. Der Kunde ist fuer den EntityManager bekannt, die
	 * Bestellung dagegen nicht. Das Zusammenbauen wird sowohl fuer einen Web
	 * Service aus auch fuer eine Webanwendung benoetigt.
	 */
	@Override
	public Bestellung createBestellung(Bestellung bestellung, Long kundeId) {
		if (bestellung == null) {
			return null;
		}

		// Den persistenten Kunden mit der transienten Bestellung verknuepfen
		final AbstractKunde kunde = ks.findKundeById(kundeId,
				KundeService.FetchType.MIT_BESTELLUNGEN);
		return createBestellung(bestellung, kunde);
	}

	/**
	 * Zuordnung einer neuen, transienten Bestellung zu einem existierenden,
	 * persistenten Kunden. Der Kunde ist fuer den EntityManager bekannt, die
	 * Bestellung dagegen nicht. Das Zusammenbauen wird sowohl fuer einen Web
	 * Service aus auch fuer eine Webanwendung benoetigt.
	 */
	@Override
	public Bestellung createBestellung(Bestellung bestellung,
			AbstractKunde kunde) {
		if (bestellung == null) {
			return null;
		}

		// Den persistenten Kunden mit der transienten Bestellung verknuepfen
		if (!em.contains(kunde)) {
			kunde = ks.findKundeById(kunde.getId(),
					KundeService.FetchType.MIT_BESTELLUNGEN);
		}
		kunde.addBestellung(bestellung);
		bestellung.setKunde(kunde);

		// Vor dem Abspeichern IDs zuruecksetzen:
		// IDs koennten einen Wert != null haben, wenn sie durch einen Web
		// Service uebertragen wurden
		bestellung.setId(KEINE_ID);
		for (Posten bp : bestellung.getPostenen()) {
			bp.setId(KEINE_ID);
		}
		// FIXME JDK 8 hat Lambda-Ausdruecke
		// bestellung.getPostenen()
		// .forEach(bp -> bp.setId(KEINE_ID));

		em.persist(bestellung);
		event.fire(bestellung);

		return bestellung;
	}

	//FIXME:Muss das nicht in din ArtikelService
	/**
	 * Suche Artikel die in keinen Posten vorkommen.
	 * 
	 * @param anzahl
	 *            Anzahl der maximal moeglichen Resultate.
	 * @return Die gefundenen Artikel, sonst null.
	 */
	@Override
	public List<Artikel> ladenhueter(int anzahl) {
		return em.createNamedQuery(Posten.FIND_LADENHUETER, Artikel.class)
				.setMaxResults(anzahl).getResultList();
	}
}
