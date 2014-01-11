package de.shop.lieferantenverwaltung.service;

import static de.shop.util.Constants.LOADGRAPH;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.google.common.collect.ImmutableMap;

import de.shop.lieferantenbestellverwaltung.domain.PostenLB;
import de.shop.lieferantenbestellverwaltung.domain.PostenLB_;
import de.shop.lieferantenbestellverwaltung.domain.Lieferantenbestellung;
import de.shop.lieferantenbestellverwaltung.domain.Lieferantenbestellung_;
import de.shop.lieferantenverwaltung.domain.AbstractLieferant;
import de.shop.lieferantenverwaltung.domain.AbstractLieferant_;
import de.shop.lieferantenverwaltung.domain.Lieferantenadresse_;
import de.shop.util.interceptor.Log;

@Dependent
@Log
public class LieferantService implements Serializable {

	public enum FetchType {
		NUR_LIEFERANT, MIT_LIEFERANTENBESTELLUNGEN
	}

	public enum OrderType {
		KEINE, ID
	}

	@Inject
	private transient EntityManager em;

	private static final long serialVersionUID = 4360325837484294309L;

	@NotNull(message = "lieferant.notFound.id")
	public AbstractLieferant findLieferantById(Long id, FetchType fetch) {
		if (id == null)
			return null;

		AbstractLieferant lieferant;
		EntityGraph<?> entityGraph;
		Map<String, Object> props;
		switch (fetch) {
		case NUR_LIEFERANT:
			lieferant = em.find(AbstractLieferant.class, id);
			break;

		case MIT_LIEFERANTENBESTELLUNGEN:
			entityGraph = em.getEntityGraph(AbstractLieferant.GRAPH_LIEFERANTENBESTELLUNGEN);
			props = ImmutableMap.of(LOADGRAPH, (Object) entityGraph);
			lieferant = em.find(AbstractLieferant.class, id, props);
			break;

		default:
			lieferant = em.find(AbstractLieferant.class, id);
			break;
		}

		return lieferant;
	}

	/**
	 * Suche nach IDs mit gleichem Praefix.
	 * 
	 * @param idPrefix
	 *            Der gemeinsame Praefix.
	 * @return Liste der passenden Praefixe.
	 */
	public List<Long> findIdsByPrefix(String idPrefix) {
		return em
				.createNamedQuery(AbstractLieferant.FIND_IDS_BY_PREFIX, Long.class)
				.setParameter(AbstractLieferant.PARAM_LIEFERANT_ID_PREFIX,
						idPrefix + '%').getResultList();
	}

	/**
	 * Suche einen Lieferanten zu gegebener Email-Lieferantenadresse.
	 * 
	 * @param email
	 *            Die gegebene Email-Lieferantenadresse.
	 * @return Der gefundene Lieferant.
	 * @exception ConstraintViolationException
	 *                zu @NotNull, falls kein Lieferant gefunden wurde
	 */
	@NotNull(message = "{lieferant.notFound.email}")
	public AbstractLieferant findLieferantByEmail(String email) {
		try {
			return em
					.createNamedQuery(AbstractLieferant.FIND_LIEFERANT_BY_EMAIL,
							AbstractLieferant.class)
					.setParameter(AbstractLieferant.PARAM_LIEFERANT_EMAIL, email)
					.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	@Size(min = 1, message = "lieferant.notFound.firma")
	public List<AbstractLieferant> findLieferantenByFirma(String firma,
			FetchType fetch) {
		final TypedQuery<AbstractLieferant> query = em.createNamedQuery(
				AbstractLieferant.FIND_LIEFERANTEN_BY_FIRMA, AbstractLieferant.class)
				.setParameter(AbstractLieferant.PARAM_LIEFERANT_FIRMA, firma);

		EntityGraph<?> entityGraph;
		switch (fetch) {
		case NUR_LIEFERANT:
			break;

		case MIT_LIEFERANTENBESTELLUNGEN:
			entityGraph = em.getEntityGraph(AbstractLieferant.GRAPH_LIEFERANTENBESTELLUNGEN);
			query.setHint(LOADGRAPH, entityGraph);
			break;

		default:
			break;
		}

		return query.getResultList();
	}

	public List<AbstractLieferant> findAllLieferanten(FetchType fetch, OrderType order) {
		final TypedQuery<AbstractLieferant> query = OrderType.ID.equals(order) ? em
				.createNamedQuery(AbstractLieferant.FIND_LIEFERANTEN_ORDER_BY_ID,
						AbstractLieferant.class) : em.createNamedQuery(
				AbstractLieferant.FIND_LIEFERANTEN, AbstractLieferant.class);

		EntityGraph<?> entityGraph;
		switch (fetch) {
		case NUR_LIEFERANT:
			break;

		case MIT_LIEFERANTENBESTELLUNGEN:
			entityGraph = em.getEntityGraph(AbstractLieferant.GRAPH_LIEFERANTENBESTELLUNGEN);
			query.setHint(LOADGRAPH, entityGraph);
			break;

		default:
			break;
		}

		return query.getResultList();
	}

	/**
	 * Suche alle Firmen mit gleichem Praefix
	 * 
	 * @param firmaPrefix
	 *            Der gemeinsame Praefix
	 * @return Liste der passenden Firmen
	 */
	public List<String> findFirmaByPrefix(String firmaPrefix) {
		return em
				.createNamedQuery(AbstractLieferant.FIND_FIRMA_BY_PREFIX,
						String.class)
				.setParameter(AbstractLieferant.PARAM_LIEFERANT_FIRMA_PREFIX,
						firmaPrefix + '%').getResultList();
	}

	/**
	 * Die Lieferanten mit gleicher Postleitzahl suchen.
	 * 
	 * @param plz
	 *            Die Postleitzahl
	 * @return Liste der passenden Lieferanten.
	 * @exception ConstraintViolationException
	 *                zu @Size, falls die Liste leer ist
	 */
	@Size(min = 1, message = "{lieferant.notFound.plz}")
	public List<AbstractLieferant> findLieferantenByPLZ(String plz) {
		return em
				.createNamedQuery(AbstractLieferant.FIND_LIEFERANTEN_BY_PLZ,
						AbstractLieferant.class)
				.setParameter(AbstractLieferant.PARAM_LIEFERANT_LIEFERANTENADRESSE_PLZ, plz)
				.getResultList();
	}

	/**
	 * Diejenigen Lieferanten suchen, die seit einem bestimmten Datum registriert
	 * sind.
	 * 
	 * @param seit
	 *            Das zu vergleichende Datum
	 * @return Die Liste der passenden Lieferanten
	 * @exception ConstraintViolationException
	 *                zu @Size, falls die Liste leer ist
	 */
	@Size(min = 1, message = "{lieferant.notFound.seit}")
	public List<AbstractLieferant> findLieferantenBySeit(Date seit) {
		return em
				.createNamedQuery(AbstractLieferant.FIND_LIEFERANTEN_BY_DATE,
						AbstractLieferant.class)
				.setParameter(AbstractLieferant.PARAM_LIEFERANT_SEIT, seit)
				.getResultList();
	}

	/**
	 * Lieferanten mit gleichem Firmen durch eine Criteria-Query suchen.
	 * 
	 * @param firma
	 *            Die gemeinsame Firma.
	 * @return Liste der passenden Lieferanten
	 * @exception ConstraintViolationException
	 *                zu @Size, falls die Liste leer ist
	 */
	@Size(min = 1, message = "{lieferant.notFound.firma}")
	public List<AbstractLieferant> findLieferantenByFirmaCriteria(String firma) {
		final CriteriaBuilder builder = em.getCriteriaBuilder();
		final CriteriaQuery<AbstractLieferant> criteriaQuery = builder
				.createQuery(AbstractLieferant.class);
		final Root<AbstractLieferant> l = criteriaQuery.from(AbstractLieferant.class);

		final Path<String> firmaPath = l.get(AbstractLieferant_.firma);

		final Predicate pred = builder.equal(firmaPath, firma);
		criteriaQuery.where(pred);

		// Ausgabe des komponierten Query-Strings. Voraussetzung: das Modul
		// "org.hibernate" ist aktiviert
		// LOGGER.tracef("",
		// em.createQuery(criteriaQuery).unwrap(org.hibernate.Query.class).getQueryString());
		return em.createQuery(criteriaQuery).getResultList();
	}

	/**
	 * Die Lieferanten mit einer bestimmten Mindestbestellmenge suchen.
	 * 
	 * @param minMenge
	 *            Die Mindestbestellmenge
	 * @return Liste der passenden Lieferanten
	 * @exception ConstraintViolationException
	 *                zu @Size, falls die Liste leer ist
	 */
	@Size(min = 1, message = "{lieferant.notFound.minBestMenge}")
	public List<AbstractLieferant> findLieferantenMitMinBestMenge(short minMenge) {
		final CriteriaBuilder builder = em.getCriteriaBuilder();
		final CriteriaQuery<AbstractLieferant> criteriaQuery = builder
				.createQuery(AbstractLieferant.class);
		final Root<AbstractLieferant> k = criteriaQuery.from(AbstractLieferant.class);

		final Join<AbstractLieferant, Lieferantenbestellung> b = k
				.join(AbstractLieferant_.lieferantenbestellungen);
		final Join<Lieferantenbestellung, PostenLB> bp = b.join(Lieferantenbestellung_.postenLB);
		criteriaQuery.where(
				builder.gt(bp.<Integer> get(PostenLB_.anzahl), minMenge))
				.distinct(true);

		return em.createQuery(criteriaQuery).getResultList();
	}

	/**
	 * Lieferanten zu den Suchkriterien suchen
	 * 
	 * @param email
	 *            Email-Lieferantenadresse
	 * @param firma
	 *            Firma
	 * @param plz
	 *            Postleitzahl
	 * @¸aram seit Datum seit
	 * @param minBestMenge
	 *            Mindestbestellmenge
	 * @return Die gefundenen Lieferanten
	 * @exception ConstraintViolationException
	 *                zu @Size, falls die Liste leer ist
	 */
	@NotNull(message = "{lieferant.notFound.criteria}")
	public List<AbstractLieferant> findLieferantenByCriteria(String email,
			String firma, String plz, Date seit, Short minBestMenge) {
		// SELECT DISTINCT k
		// FROM AbstractLieferant k
		// WHERE email = ? AND firma = ? AND k.adresse.plz = ? and seit = ?

		final CriteriaBuilder builder = em.getCriteriaBuilder();
		final CriteriaQuery<AbstractLieferant> criteriaQuery = builder
				.createQuery(AbstractLieferant.class);
		final Root<? extends AbstractLieferant> k = criteriaQuery
				.from(AbstractLieferant.class);

		Predicate pred = null;
		if (email != null) {
			final Path<String> emailPath = k.get(AbstractLieferant_.email);
			pred = builder.equal(emailPath, email);
		}
		if (firma != null) {
			final Path<String> firmaPath = k.get(AbstractLieferant_.firma);
			final Predicate tmpPred = builder.equal(firmaPath, firma);
			pred = pred == null ? tmpPred : builder.and(pred, tmpPred);
		}
		if (plz != null) {
			final Path<String> plzPath = k.get(AbstractLieferant_.lieferantenadresse).get(
					Lieferantenadresse_.plz);
			final Predicate tmpPred = builder.equal(plzPath, plz);
			pred = pred == null ? tmpPred : builder.and(pred, tmpPred);
		}
		if (seit != null) {
			final Path<Date> seitPath = k.get(AbstractLieferant_.seit);
			final Predicate tmpPred = builder.equal(seitPath, seit);
			pred = pred == null ? tmpPred : builder.and(pred, tmpPred);
		}
		if (minBestMenge != null) {
			final Path<Integer> anzahlPath = k
					.join(AbstractLieferant_.lieferantenbestellungen).join(Lieferantenbestellung_.postenLB)
					.get(PostenLB_.anzahl);
			final Predicate tmpPred = builder.gt(anzahlPath, minBestMenge);
			pred = pred == null ? tmpPred : builder.and(pred, tmpPred);
		}

		criteriaQuery.where(pred).distinct(true);
		return em.createQuery(criteriaQuery).getResultList();
	}

	public <T extends AbstractLieferant> T createLieferant(T lieferant) {
		if (lieferant == null) {
			return lieferant;
		}

		// Pruefung, ob die Email-Lieferantenadresse schon existiert
		final AbstractLieferant tmp = findLieferantByEmail(lieferant.getEmail()); // Kein
																		// Aufruf
																		// als
																		// Business-Methode
		if (tmp != null) {
			throw new EmailExistsException(lieferant.getEmail());
		}

		em.persist(lieferant);
		return lieferant;
	}

	public <T extends AbstractLieferant> T updateLieferant(T lieferant) {
		if (lieferant == null) {
			return null;
		}

		// lieferant vom EntityManager trennen, weil anschliessend z.B. nach Id und
		// Email gesucht wird
		em.detach(lieferant);

		// Gibt es ein anderes Objekt mit gleicher Email-Lieferantenadresse?
		final AbstractLieferant tmp = findLieferantByEmail(lieferant.getEmail()); // Kein
																		// Aufruf
																		// als
																		// Business-Methode
		if (tmp != null) {
			em.detach(tmp);
			if (tmp.getId().longValue() != lieferant.getId().longValue()) {
				// anderes Objekt mit gleichem Attributwert fuer email
				throw new EmailExistsException(lieferant.getEmail());
			}
		}

		em.merge(lieferant);
		return lieferant;
	}

	/**
	 * Einen Lieferanten aus der DB loeschen, falls er existiert.
	 * 
	 * @param lieferant
	 *            Der zu loeschende Lieferant.
	 */
	public void deleteLieferant(AbstractLieferant lieferant) {
		if (lieferant == null) {
			return;
		}

		// Lieferantenbestellungen laden, damit sie anschl. ueberprueft werden koennen
		lieferant = findLieferantById(lieferant.getId(), FetchType.MIT_LIEFERANTENBESTELLUNGEN); // Kein
																			// Aufruf
																			// als
																			// Business-Methode
		if (lieferant == null) {
			return;
		}

		// Gibt es Lieferantenbestellungen?
		if (!lieferant.getLieferantenbestellungen().isEmpty()) {
			throw new LieferantDeleteLieferantenbestellungException(lieferant);
		}

		em.remove(lieferant);
	}
}
