package de.shop.artikelverwaltung.service;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.Dependent;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.util.Mock;

@Dependent
public class ArtikelService implements Serializable{
	
	private static final long serialVersionUID = -5105686816948437276L;
	
	//@NotNull(message = "{artikel.notFound.id}")
	public Artikel findArtikelById(Long id) {
		return Mock.findArtikelById(id);
	}
	
	//@Size(min = 1, message = "{artikel.notFound.bezeichnung}")
	public List<Artikel> findArtikelByBezeichnung(String bezeichnung) {
		return Mock.findArtikelByBezeichnung(bezeichnung);
	}
	
	//@Size(min = 1, message = "{artikel.notFound}")
	public List<Artikel> findAllArtikel(){
		return Mock.findAllArtikel();
	}

	public Artikel createArtikel(Artikel artikel) {
		return Mock.createArtikel(artikel);
	}

	public void updateArtikel(Artikel artikel) {
		Mock.updateArtikel(artikel);
	}

	public void deleteArtikel(Long id) {
		Mock.deleteArtikel(id);
	}
}
