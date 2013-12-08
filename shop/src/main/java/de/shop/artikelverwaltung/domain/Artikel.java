package de.shop.artikelverwaltung.domain;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonTypeInfo;

/*
 * Artikel Klasse
 * enthält id, bezeichnung und preis
 * ist Serializiable aufgrund des Mappings notwendig
 * ---Christian---
 */
@XmlRootElement
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class Artikel implements Serializable {
	
	private static final long serialVersionUID = 1430771599450877428L;
	
	private Long id;
	@NotNull(message="{artikel.bezeichnung.notnull}")
	@Size(max = 100, message="{artikel.bezeichnung.length}")
	@Pattern(regexp = "[A-ZÄÖÜ][a-z0-9äöüß_-]+", message="{artikel.bezeichnung.pattern}")
	private String bezeichnung;
	@NotNull(message="{artikel.preis.notnull}")
	@Digits(integer = 10, fraction = 2, message = "{artikel.preis.digits}")
	private BigDecimal preis;
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getBezeichnung() {
		return bezeichnung;
	}
	
	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}
	
	public BigDecimal getPreis() {
		return preis;
	}
	
	public void setPreis(BigDecimal preis) {
		this.preis = preis.setScale(2);
	}
	
	public Artikel() {
		super();
		this.id = null;
		this.bezeichnung = null;
		this.preis = null;
	}

	public Artikel(Long id, String bezeichnung, BigDecimal preis) {
		super();
		this.id = id;
		this.bezeichnung = bezeichnung;
		this.preis = preis.setScale(2);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((bezeichnung == null) ? 0 : bezeichnung.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((preis == null) ? 0 : preis.hashCode());
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
		final Artikel other = (Artikel) obj;
		if (bezeichnung == null) {
			if (other.bezeichnung != null)
				return false;
		}
		else if (!bezeichnung.equals(other.bezeichnung))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		if (preis == null) {
			if (other.preis != null)
				return false;
		}
		else if (!preis.equals(other.preis))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Artikel [id=" + id + ", bezeichnung=" + bezeichnung
				+ ", preis=" + preis + "]";
	}
}
