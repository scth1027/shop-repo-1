package de.shop.bestellverwaltung.domain;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.shop.kundenverwaltung.domain.Kunde;

@XmlRootElement
public class Bestellung {

	private long id;

	@XmlTransient
	private Kunde kunde;

	private BigDecimal gesamtpreis;

	@NotNull(message = "{bestellung.bestellstatus.NotNull}")
	private Bestellstatus bestellstatus;

	@NotNull(message = "{bestellung.posten.NotNull}")
	private List<Posten> posten;

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

	public Bestellstatus getBestellstatus() {
		return bestellstatus;
	}

	public void setBestellstatus(Bestellstatus bestellstatus) {
		this.bestellstatus = bestellstatus;
	}

	public List<Posten> getPosten() {
		return posten;
	}

	public void setPosten(List<Posten> posten) {
		this.posten = posten;
	}

	public BigDecimal getGesamtpreis() {
		return gesamtpreis;
	}

	public void setGesamtpreis(BigDecimal gesamtpreis) {
		this.gesamtpreis = gesamtpreis.setScale(2);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((bestellstatus == null) ? 0 : bestellstatus.hashCode());
		result = prime * result
				+ ((gesamtpreis == null) ? 0 : gesamtpreis.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((kunde == null) ? 0 : kunde.hashCode());
		result = prime * result
				+ ((kundeURI == null) ? 0 : kundeURI.hashCode());
		result = prime * result + ((posten == null) ? 0 : posten.hashCode());
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
		final Bestellung other = (Bestellung) obj;
		if (bestellstatus != other.bestellstatus)
			return false;
		if (gesamtpreis == null) {
			if (other.gesamtpreis != null)
				return false;
		} else if (!gesamtpreis.equals(other.gesamtpreis))
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
		if (posten == null) {
			if (other.posten != null)
				return false;
		} else if (!posten.equals(other.posten))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Bestellung [id=" + id + ", kunde=" + kunde + ", gesamtpreis="
				+ gesamtpreis + ", bestellstatus=" + bestellstatus
				+ ", posten=" + posten + ", kundeURI=" + kundeURI + "]";
	}
}
