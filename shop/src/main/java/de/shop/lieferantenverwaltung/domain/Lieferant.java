package de.shop.lieferantenverwaltung.domain;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Past;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.validator.constraints.Email;
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
	@NotNull(message = "{lieferant.firma.notNull}")
	@Size(min = 2, max = 50, message = "{lieferant.firma.length}")
	private String firma;
	@Valid
	@NotNull(message = "{lieferant.adresse.notNull}")
	private Lieferantenadresse adresse;
	@Size(min = 4, message = "{lieferant.email.length}")
	@NotNull(message = "{lieferant.email.notNull}")
	@Email(message = "{lieferant.email.pattern}")
	private String email;
	@Past(message = "{lieferant.seit.past}")
	private Date seit;

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
	public void setFirma(String firma) {
		this.firma = firma;
	}
	
	public Lieferantenadresse getLieferantenadresse() {
		return adresse;
	}
	
	public void setLieferantenadresse(Lieferantenadresse adresse) {
		this.adresse = adresse;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public Date getSeit() {
		return seit;
	}

	public void setSeit(Date seit) {
		this.seit = seit;
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
		result = prime * result + ((email == null) ? 0 : email.hashCode());
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
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		return true;
	}

}
