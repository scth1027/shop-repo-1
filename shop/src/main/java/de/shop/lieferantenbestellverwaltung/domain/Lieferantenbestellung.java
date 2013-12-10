package de.shop.lieferantenbestellverwaltung.domain;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.shop.lieferantenbestellverwaltung.domain.LieferantenBestellstatus;
import de.shop.lieferantenbestellverwaltung.domain.PostenLB;
import de.shop.lieferantenverwaltung.domain.Lieferant;

@XmlRootElement
public class Lieferantenbestellung {

	private long id;

	@XmlTransient
	private Lieferant lieferant;

	private BigDecimal gesamtpreis;

	@NotNull(message = "{lieferantenbestellung.lieferantenbestellstatus.NotNull}")
	private LieferantenBestellstatus bestellstatus;

	@NotNull(message = "{lieferantenbestellung.posten.NotNull}")
	private List<PostenLB> posten;
	
	private URI lieferantURI;

	public URI getLieferantURI() {
		return lieferantURI;
	}

	public void setLieferantURI(URI lieferantURI) {
		this.lieferantURI = lieferantURI;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Lieferant getLieferant() {
		return lieferant;
	}

	public void setLieferant(Lieferant lieferant) {
		this.lieferant = lieferant;
	}

	public LieferantenBestellstatus getBestellstatus() {
		return bestellstatus;
	}

	public void setBestellstatus(LieferantenBestellstatus bestellstatus) {
		this.bestellstatus = bestellstatus;
	}

	public List<PostenLB> getPostenLB() {
		return posten;
	}

	public void setPostenLB(List<PostenLB> posten) {
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
		result = prime * result
				+ ((lieferant == null) ? 0 : lieferant.hashCode());
		result = prime * result
				+ ((lieferantURI == null) ? 0 : lieferantURI.hashCode());
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
		final Lieferantenbestellung other = (Lieferantenbestellung) obj;
		if (bestellstatus != other.bestellstatus)
			return false;
		if (gesamtpreis == null) {
			if (other.gesamtpreis != null)
				return false;
		} 
		else if (!gesamtpreis.equals(other.gesamtpreis))
			return false;
		if (id != other.id)
			return false;
		if (lieferant == null) {
			if (other.lieferant != null)
				return false;
		} 
		else if (!lieferant.equals(other.lieferant))
			return false;
		if (lieferantURI == null) {
			if (other.lieferantURI != null)
				return false;
		} 
		else if (!lieferantURI.equals(other.lieferantURI))
			return false;
		if (posten == null) {
			if (other.posten != null)
				return false;
		} 
		else if (!posten.equals(other.posten))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Bestellung [id=" + id + ", lieferant=" + lieferant
				+ ", gesamtpreis=" + gesamtpreis + ", bestellstatus="
				+ bestellstatus + ", posten=" + posten + ", lieferantURI="
				+ lieferantURI + "]";
	}
}