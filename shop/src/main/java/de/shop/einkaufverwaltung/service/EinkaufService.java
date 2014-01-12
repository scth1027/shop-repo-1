package de.shop.einkaufverwaltung.service;

import java.util.List;
import java.util.Locale;

import javax.validation.constraints.NotNull;

import de.shop.einkaufverwaltung.domain.Einkauf;
import de.shop.lieferantenverwaltung.domain.Lieferant;

public interface EinkaufService {

	Einkauf createEinkauf(Einkauf einkauf, Lieferant lieferant, Locale locale);

	@NotNull(message = "einkauf.notFound.id")
	Einkauf findEinkaufById(Long id);

	@NotNull(message = "einkauf.notFound.lieferant")
	List<Einkauf> findEinkaeufeByLieferant(Lieferant lieferant);

	List<Einkauf> findAllEinkaeufe();

	void deleteEinkauf(Long einkaufId);

	void updateEinkauf(Einkauf einkauf);

}
