package de.shop.bestellverwaltung.domain;

import java.net.URI;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.bestellverwaltung.domain.Bestellstatus;


@XmlRootElement
public class Bestellung {
	
	private long id;
	private Kunde kunde;
	private Adresse lieferadresse;
	//private enum bestellstatus{angenommen, versendet, storniert, bezahlt};
	private Bestellstatus bestellstatus;
	
	@XmlTransient
	private List<Artikel> _artikel;
	
	private URI artikelURI;
	
	private URI kundeURI;
	
	public URI getKundeURI() {
		return kundeURI;
	}

	public void setKundeURI(URI kundeURI) {
		this.kundeURI = kundeURI;
	}

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

	public URI getArtikelURI() {
		return artikelURI;
	}

	public void setArtikelURI(URI artikelURI) {
		this.artikelURI = artikelURI;
	}

	public List<Artikel> get_artikel() {
		return _artikel;
	}

	public void set_artikel(List<Artikel> _artikel) {
		this._artikel = _artikel;
	}
		
	public Bestellstatus getBestellstatus() {
		return bestellstatus;
	}

	public void setBestellstatus(Bestellstatus bestellstatus) {
		this.bestellstatus = bestellstatus;
	}

	@Override
	public String toString() {
		return "Bestellung [id=" + id + ", kunde=" + kunde + ", lieferadresse="
				+ lieferadresse + ", bestellstatus=" + bestellstatus
				+ ", _artikel=" + _artikel + ", artikelURI=" + artikelURI + "]";
	}

	
	
	
	
	

}
