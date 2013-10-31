package de.shop.bestellverwaltung.domain;

import java.net.URI;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.bestellverwaltung.domain.Bestellstatus;
import de.shop.lieferantenverwaltung.domain.Lieferant;


@XmlRootElement
public class Bestellung {
	
	private long id;
	private Kunde kunde;
	private Lieferant lieferant;
	
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
	
	public Lieferant getLieferant() {
		return lieferant;
	}

	public void setLieferant(Lieferant _lieferant) {
		this.lieferant = _lieferant;
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
		return "Bestellung [id=" + id + ", kunde=" + kunde + "," + ", bestellstatus=" + bestellstatus
				+ ", _artikel=" + _artikel + ", artikelURI=" + artikelURI + "]";
	}

	
	
	
	
	

}
