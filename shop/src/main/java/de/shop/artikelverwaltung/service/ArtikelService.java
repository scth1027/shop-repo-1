package de.shop.artikelverwaltung.service;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.google.common.base.Strings;

import de.shop.artikelverwaltung.domain.Artikel;
//import de.shop.util.Mock;
import de.shop.util.interceptor.Log;

@Dependent
@Log
public class ArtikelService implements Serializable {
	private static final long serialVersionUID = 3076865030092242363L;

	@Inject
	private transient EntityManager em;

	// @NotNull(message = "{artikel.notFound.id}")
	// public Artikel findArtikelById(Long id) {
	// return Mock.findArtikelById(id);
	// }

	/**
	 * Suche den Artikel zu gegebener ID.
	 * 
	 * @param id
	 *            ID des gesuchten Artikels.
	 * @return Der gefundene Artikel, sonst null.
	 */
	@NotNull(message = "{artikel.notFound.id}")
	public Artikel findArtikelById(Long id) {
		return em.find(Artikel.class, id);
	}
	
	@Size(min = 1, message = "{artikel.notFound.ids}")
	public List<Artikel> findArtikelByIds(List<Long> ids) {
		if (ids == null || ids.isEmpty()) {
			return Collections.emptyList();
		}
		
		/*
		 * SELECT a
		 * FROM   Artikel a
		 * WHERE  a.id = ? OR a.id = ? OR ...
		 */
		final CriteriaBuilder builder = em.getCriteriaBuilder();
		final CriteriaQuery<Artikel> criteriaQuery = builder.createQuery(Artikel.class);
		final Root<Artikel> a = criteriaQuery.from(Artikel.class);

		final Path<Long> idPath = a.get("id");
		//final Path<String> idPath = a.get(Artikel_.id);   // Metamodel-Klassen funktionieren nicht mit Eclipse
		
		Predicate pred = null;
		if (ids.size() == 1) {
			// Genau 1 id: kein OR notwendig
			pred = builder.equal(idPath, ids.get(0));
		}
		else {
			// Mind. 2x id, durch OR verknuepft
			final Predicate[] equals = new Predicate[ids.size()];
			int i = 0;
			for (Long id : ids) {
				equals[i++] = builder.equal(idPath, id);
			}
			
			pred = builder.or(equals);
		}
		
		criteriaQuery.where(pred);
		
		return em.createQuery(criteriaQuery)
		         .getResultList();
	}

	// @Size(min = 1, message = "{artikel.notFound.bezeichnung}")
	// public List<Artikel> findArtikelByBezeichnung(String bezeichnung) {
	// return Mock.findArtikelByBezeichnung(bezeichnung);
	// }

	/**
	 * Suche die Artikel zu gegebener Bezeichnung
	 * 
	 * @param bezeichnung
	 *            Bezeichnung der gesuchten Artikel
	 * @return Liste der gefundenen Artikel
	 */
	@Size(min = 1, message = "{artikel.notFound.bezeichnung}")
	public List<Artikel> findArtikelByBezeichnung(String bezeichnung) {
		if (Strings.isNullOrEmpty(bezeichnung)) {
			return findAllArtikel();
		}

		return em
				.createNamedQuery(Artikel.FIND_ARTIKEL_BY_BEZEICHNUNG,
						Artikel.class)
				.setParameter(Artikel.PARAM_BEZEICHNUNG,
						"%" + bezeichnung + "%").getResultList();
	}

	// @Size(min = 1, message = "{artikel.notFound.all}")
	// public List<Artikel> findAllArtikel() {
	// return Mock.findAllArtikel();
	// }

	/**
	 * Suche nach allen Artikeln.
	 * 
	 * @return Liste aller Artikel.
	 */
	@Size(min = 1, message = "{artikel.notFound.all}")
	public List<Artikel> findAllArtikel() {
		return em.createNamedQuery(Artikel.FIND_ALL_ARTIKEL, Artikel.class)
				.getResultList();
	}

	// public Artikel createArtikel(Artikel artikel) {
	// return Mock.createArtikel(artikel);
	// }
	/**
	 * Einen neuen Artikel in der DB anlegen.
	 * 
	 * @param artikel
	 *            Der anzulegende Artikel.
	 * @return Der neue Artikel einschliesslich generierter ID.
	 */
	public Artikel createArtikel(Artikel artikel) {
		if (artikel == null)
			return null;

		em.persist(artikel);
		return artikel;
	}

	// public Artikel updateArtikel(Artikel artikel) {
	// Mock.updateArtikel(artikel);
	// return artikel;
	// }

	/**
	 * Einen vorhandenen Artikel aktualisieren
	 * 
	 * @param artikel
	 *            Der Artikel mit aktualisierten Attributwerten
	 * @return Der aktualisierte Artikel
	 */
	public Artikel updateArtikel(Artikel artikel) {
		if (artikel == null)
			return null;

		// kunde vom EntityManager trennen, weil anschliessend z.B. nach Id und
		// Email gesucht wird
		// FIXME:Noetig? Wird es Überschrieben oder nur hinzugefügt bei merge
		em.detach(artikel);
		em.merge(artikel);
		return artikel;
	}

	// public void deleteArtikel(Long id) {
	// Mock.deleteArtikel(id);
	// }

	/**
	 * Einen Artikel aus der DB loeschen, falls er existiert.
	 * 
	 * @param artikel
	 *            Der zu loeschende Artikel.
	 */
	public void deleteArtikel(Long id) {
		final Artikel artikel = findArtikelById(id);

		if (artikel == null)
			return;

		em.remove(artikel);
	}
}
