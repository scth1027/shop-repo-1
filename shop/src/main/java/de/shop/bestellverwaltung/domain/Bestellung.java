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
		return "Bestellung [id=" + id + ", kunde=" + kunde + ", lieferant="
				+ lieferant + ", bestellstatus=" + bestellstatus
				+ ", _artikel=" + _artikel + ", artikelURI=" + artikelURI
				+ ", kundeURI=" + kundeURI + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((_artikel == null) ? 0 : _artikel.hashCode());
		result = prime * result
				+ ((artikelURI == null) ? 0 : artikelURI.hashCode());
		result = prime * result
				+ ((bestellstatus == null) ? 0 : bestellstatus.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((kunde == null) ? 0 : kunde.hashCode());
		result = prime * result
				+ ((kundeURI == null) ? 0 : kundeURI.hashCode());
		result = prime * result
				+ ((lieferant == null) ? 0 : lieferant.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Bestellung other = (Bestellung) obj;
		if (_artikel == null) {
			if (other._artikel != null)
				return false;
		} else if (!_artikel.equals(other._artikel))
			return false;
		if (artikelURI == null) {
			if (other.artikelURI != null)
				return false;
		} else if (!artikelURI.equals(other.artikelURI))
			return false;
		if (bestellstatus != other.bestellstatus)
			return false;
		if (id != other.id)
			return false;
		if (kunde == null) {
			if (other.kunde != null)
				return false;
		} else if (!kunde.equals(other.kunde))
			return false;
		if (kundeURI == null) {
			if (other.kundeURI != null)
				return false;
		} else if (!kundeURI.equals(other.kundeURI))
			return false;
		if (lieferant == null) {
			if (other.lieferant != null)
				return false;
		} else if (!lieferant.equals(other.lieferant))
			return false;
		return true;
	}

	

	
	
	
	
	

}
