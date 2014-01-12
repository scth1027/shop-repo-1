package de.shop.einkaufverwaltung.service;

import java.util.List;

import javax.validation.constraints.NotNull;

import de.shop.einkaufverwaltung.domain.Einkauf;
import de.shop.lieferantenverwaltung.domain.Lieferant;

public interface EinkaufService {
	public enum FetchType { NUR_EINKAUF, MIT_LIEFERUNGEN }

	Einkauf createEinkauf(Einkauf einkauf, Lieferant lieferant);

	@NotNull(message = "einkauf.notFound.id")
	Einkauf findEinkaufById(Long id, FetchType fetch);

	@NotNull(message = "einkauf.notFound.lieferant")
	List<Einkauf> findEinkaeufeByLieferant(Lieferant lieferant);

}
