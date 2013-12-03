package de.shop.kundenverwaltung.service;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.Mock;
import de.shop.util.interceptor.Log;

@Dependent
@Log
public class KundeService implements Serializable {

	private static final long serialVersionUID = 4360325837484294309L;
	
	@NotNull(message ="kunde.notFound.id")
	public Kunde findKundeById(Long id) {
		if(id == null)
			return null;
		return Mock.findKundeById(id);
	}
	
	@NotNull(message = "kunde.notFound.email")
	public Kunde findKundeByEmail(String email) {
		if (email == null)
			return null;
		return Mock.findKundeByEmail(email);
	}
	
	@Size(min = 1, message = "kunde.notFound.nachname")
	public List<Kunde> findKundenByNachname(String nachname) {
		return Mock.findKundenByNachname(nachname);
	}
	
	public List<Kunde> findAllKunden() {
		return Mock.findAllKunden();
	}
	
	public Kunde createKunde(Kunde kunde) {
		if (kunde == null) {
			return kunde;
		}

		final Kunde tmp = findKundeByEmail(kunde.getEmail()); 
		if (tmp != null) {
			// TODO: EmailException
			//throw new EmailExistsException(kunde.getEmail());
		}
		kunde = Mock.createKunde(kunde);

		return kunde;
	} 
	
	public Kunde updateKunde(Kunde kunde) {
		if (kunde == null) {
			return null;
		}

		final Kunde vorhandenerKunde = findKundeByEmail(kunde.getEmail());
		if (vorhandenerKunde != null) {
			if (vorhandenerKunde.getId().longValue() != kunde.getId().longValue()) {
				// TODO: EmailException
				//throw new EmailExistsException(kunde.getEmail());
			}
		}

		// TODO Datenbanzugriffsschicht statt Mock
		Mock.updateKunde(kunde);
		
		return kunde;
	}
	
	public void deleteKunde(Long kundeId) {
		Kunde kunde = findKundeById(kundeId);
		if (kunde == null) {
			return;
		}
		
		if (!kunde.getBestellungen().isEmpty()) {
			//TODO : KundeDeleteBestellungException
			//throw new KundeDeleteBestellungException(kunde);
		}
		Mock.deleteKunde(kunde.getId());
	}
}
