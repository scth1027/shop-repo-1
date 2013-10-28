package de.shop.bestellverwaltung.domain;

import java.net.URI;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.domain.Kunde;

@XmlRootElement
public class Bestellung {
	
	private long id;
	private Kunde kunde;
	private Adresse lieferadresse;
	//private enum bestellstatus;
	//TODO wieso fehler?
	
	@XmlTransient
	private List<Artikel> _artikel;
	
	private URI artikelURI;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Kunde getKunde() {
		return kunde;
	}

	public void setKunde(Kunde kunde) {
		this.kunde = kunde;
	}

	public Adresse getLieferadresse() {
		return lieferadresse;
	}

	public void setLieferadresse(Adresse lieferadresse) {
		this.lieferadresse = lieferadresse;
	}

	@Override
	public String toString() {
		return "Bestellung [id=" + id + ", kunde=" + kunde + ", lieferadresse="
				+ lieferadresse + ", _artikel=" + _artikel + ", artikelURI="
				+ artikelURI + "]";
	}
	
	
	
	
	

}
