package de.shop.artikelverwaltung.service;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.google.common.base.Strings;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.util.Mock;
import de.shop.util.interceptor.Log;

@Dependent
@Log
public class ArtikelService implements Serializable {
	
	private static final long serialVersionUID = 3076865030092242363L;
	
	@Inject
	private transient EntityManager em;
	
//	@NotNull(message = "{artikel.notFound.id}")
//	public Artikel findArtikelById(Long id) {
//		return Mock.findArtikelById(id);
//	}
	
	@NotNull(message = "{artikel.notFound.id}")
	public Artikel findArtikelById(Long id) {
		return em.find(Artikel.class, id);
	}
	
//	@Size(min = 1, message = "{artikel.notFound.bezeichnung}")
//	public List<Artikel> findArtikelByBezeichnung(String bezeichnung) {
//		return Mock.findArtikelByBezeichnung(bezeichnung);
//	}
	
	@Size(min = 1, message = "{artikel.notFound.bezeichnung}")
	public List<Artikel> findArtikelByBezeichnung(String bezeichnung) {
		if (Strings.isNullOrEmpty(bezeichnung)) {
			return findAllArtikel();
		}
		
		return em.createNamedQuery(Artikel.FIND_ARTIKEL_BY_BEZEICHNUNG, Artikel.class)
				 .setParameter(Artikel.PARAM_BEZEICHNUNG, "%" + bezeichnung + "%")
				 .getResultList();
	}
	
//	@Size(min = 1, message = "{artikel.notFound.all}")
//	public List<Artikel> findAllArtikel() {
//		return Mock.findAllArtikel();
//	}
	
	@Size(min = 1, message = "{artikel.notFound.all}")
	public List<Artikel> findAllArtikel() {
		return em.createNamedQuery(Artikel.FIND_ALL_ARTIKEL, Artikel.class)
				 .getResultList();
	}

//	public Artikel createArtikel(Artikel artikel) {
//		return Mock.createArtikel(artikel);
//	}
	
	public Artikel createArtikel(Artikel artikel) {
		if (artikel == null)
			return null;
		
		em.persist(artikel);
		return artikel;
	}

//	public Artikel updateArtikel(Artikel artikel) {
//		Mock.updateArtikel(artikel);
//		return artikel;
//	}
	
	public Artikel updateArtikel(Artikel artikel) {
		if (artikel == null)
			return null;
		
		// kunde vom EntityManager trennen, weil anschliessend z.B. nach Id und Email gesucht wird
		//FIXME:Noetig? Wird es Überschrieben oder nur hinzugefügt bei merge
		em.detach(artikel);
		em.merge(artikel);
		return artikel;
	}

//	public void deleteArtikel(Long id) {
//		Mock.deleteArtikel(id);
//	}
	
	public void deleteArtikel(Long id) {
		final Artikel artikel = findArtikelById(id);
		
		if (artikel == null)
			return;
		
		em.remove(artikel);
	}
}
