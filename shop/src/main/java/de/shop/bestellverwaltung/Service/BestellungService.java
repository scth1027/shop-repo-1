package de.shop.bestellverwaltung.Service;

import javax.validation.constraints.NotNull;

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.util.Mock;

public class BestellungService {

	
	@NotNull(message ="bestellung.notFound.id")
	public Bestellung findBestellungById(Long id) {
		if(id == null)
			return null;
		return Mock.findBestellungById(id);
	}
}
