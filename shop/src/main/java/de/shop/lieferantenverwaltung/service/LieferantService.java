package de.shop.lieferantenverwaltung.service;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.Dependent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.shop.lieferantenverwaltung.domain.Lieferant;
import de.shop.util.Mock;
import de.shop.util.interceptor.Log;

@Dependent
@Log
public class LieferantService implements Serializable {

	private static final long serialVersionUID = 4360325837484294309L;
	
	@NotNull(message ="lieferant.notFound.id")
	public Lieferant findLieferantById(Long id) {
		if(id == null)
			return null;
		return Mock.findLieferantById(id);
	}
	
	@NotNull(message = "lieferant.notFound.email")
	/*Mock noch anpassen public Lieferant findLieferantByEmail(String email) {
		if (email == null)
			return null;
		return Mock.findLieferantByEmail(email);
	}*/
	
	@Size(min = 1, message = "lieferant.notFound.nachname")
	public List<Lieferant> findLieferantenByFirma(String firma) {
		return Mock.findLieferantenByFirma(firma);
	}
	
	public List<Lieferant> findAllLieferanten() {
		return Mock.findAllLieferanten();
	}
	
	public Lieferant createLieferant(Lieferant lieferant) {
		if (lieferant == null) {
			return lieferant;
		}

		/*Mock noch anpassenfinal Lieferant tmp = findLieferantByEmail(lieferant.getEmail()); 
		if (tmp != null) {
			// TODO: EmailException
			//throw new EmailExistsException(lieferant.getEmail());
		}*/
		lieferant = Mock.createLieferant(lieferant);

		return lieferant;
	} 
	
	public Lieferant updateLieferant(Lieferant lieferant) {
		if (lieferant == null) {
			return null;
		}

		/*Mock noch anpassenfinal Lieferant vorhandenerLieferant = findLieferantByEmail(lieferant.getEmail());
		if (vorhandenerLieferant != null) {
			if (vorhandenerLieferant.getId().longValue() != lieferant.getId().longValue()) {
				// TODO: EmailException
				//throw new EmailExistsException(lieferant.getEmail());
			}
		}*/

		// TODO Datenbanzugriffsschicht statt Mock
		Mock.updateLieferant(lieferant);
		
		return lieferant;
	}
	
	public void deleteLieferant(Long lieferantId) {
		Lieferant lieferant = findLieferantById(lieferantId);
		if (lieferant == null) {
			return;
		}
		
		if (!lieferant.getBestellungen().isEmpty()) {
			//TODO : LieferantDeleteBestellungException
			//throw new LieferantDeleteBestellungException(lieferant);
		}
		Mock.deleteLieferant(lieferant.getId());
	}
}
