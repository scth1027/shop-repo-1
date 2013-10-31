package de.shop.lieferantenverwaltung.domain;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonTypeInfo;

import de.shop.bestellverwaltung.domain.Bestellung;

/*
 * Abstrakte Lieferanten Klasse
 * enthält id, firma, adresse(Adresse), email, List von Bestellungen und zugehörige URI
 * ist Serializiable aufgrund des Mappings notwendig
 * verweist auf die zwei Unterklassen, notwendig für Rest und JSON
 * List von Bestellungen wird nicht über den RestService bereitgestellt dafuer gibt es eine URI --> bestellungenURI
 * ---Tim---
 */
@XmlRootElement
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
public class Lieferant implements Serializable{

	private static final long serialVersionUID = 1430771599450877428L;
	
	private Long id;
	private String firma;
	private Lieferantenadresse adresse;
	private String email;
	
	@XmlTransient
	// Bestellungsklasse mitbenutzen oder eigene Klasse für Lieferantenbestellungen?
	private List<Bestellung> bestellungen;
	
	private URI bestellungenURI;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFirma() {
		return firma;
	}
	public void setFirma(String _firma) {
		this.firma = _firma;
	}
	public Lieferantenadresse getLieferantenadresse() {
		return adresse;
	}
	public void setLieferantenadresse(Lieferantenadresse _adresse) {
		this.adresse = _adresse;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String _email) {
		this.email = _email;
	}
	
	@Override
	public String toString() {
		return "Lieferant [id=" + id + ", firma=" + firma + ", email="
				+ email + ", bestellungURI=" + bestellungenURI + "]";
	}
	public URI getBestellungenURI() {
		return bestellungenURI;
	}
	public void setBestellungenURI(URI bestellungenURI) {
		this.bestellungenURI = bestellungenURI;
	}
	public List<Bestellung> getBestellungen() {
		return bestellungen;
	}
	public void setBestellungen(List<Bestellung> _bestellungen) {
		this.bestellungen = _bestellungen;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((adresse == null) ? 0 : adresse.hashCode());
		result = prime * result	+ ((bestellungen == null) ? 0 : bestellungen.hashCode());
		result = prime * result	+ ((bestellungenURI == null) ? 0 : bestellungenURI.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result	+ ((firma == null) ? 0 : firma.hashCode());
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
		Lieferant other = (Lieferant) obj;
		if (adresse == null) {
			if (other.adresse != null)
				return false;
		} else if (!adresse.equals(other.adresse))
			return false;
		if (bestellungen == null) {
			if (other.bestellungen != null)
				return false;
		} else if (!bestellungen.equals(other.bestellungen))
			return false;
		if (bestellungenURI == null) {
			if (other.bestellungenURI != null)
				return false;
		} else if (!bestellungenURI.equals(other.bestellungenURI))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (firma == null) {
			if (other.firma != null)
				return false;
		} else if (!firma.equals(other.firma))
			return false;
		return true;
	}
	
}
