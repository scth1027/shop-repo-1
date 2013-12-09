package de.shop.bestellverwaltung.Service;

import java.util.List;

import javax.validation.constraints.NotNull;

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.Mock;

public class BestellungService {

	
	public Bestellung createBestellung(Bestellung bestellung)
	{
		return Mock.createBestellung(bestellung);
	}
	
	@NotNull(message ="bestellung.notFound.id")
	public Bestellung findBestellungById(Long id) {
		if(id == null)
			return null;
		return Mock.findBestellungById(id);
	}
	
	@NotNull(message="Bestellung.notFound.Kunde")
	public List<Bestellung> findBestellungbyKunde(Kunde kunde)
	{
		return Mock.findBestellungenByKunde(kunde);
	}
	
	public List<Bestellung> findAllBestellungen()
	{
		return Mock.findAllBestellungen();
	}
	
	public void deleteBestellung(Long bestellungid) {
		Mock.deleteBestellung(bestellungid);	
	}
	
	public void updateBestellung(Bestellung bestellung) {
		Mock.updateBestellung(bestellung);
	}

}
