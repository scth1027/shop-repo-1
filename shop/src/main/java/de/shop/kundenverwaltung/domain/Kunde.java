package de.shop.kundenverwaltung.domain;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import de.shop.bestellverwaltung.domain.Bestellung;

/*
 * Abstrakte Kunden Klasse
 * enth�lt id, nachname, vorname, adresse(Adresse), email, List von Bestellungen und zugeh�rige URI
 * ist Serializiable aufgrund des Mappings notwendig
 * verweist auf die zwei Unterklassen, notwendig f�r Rest und JSON
 * List von Bestellunen wird nicht �ber den RestService bereitgestellt dafuer gibt es eine URI --> bestellungenURI
 * ---Sadrick---
 */
@XmlRootElement
@XmlSeeAlso({ Firmenkunde.class, Privatkunde.class })
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
	@Type(value = Privatkunde.class, name = Kunde.PRIVATKUNDE),
	@Type(value = Firmenkunde.class, name = Kunde.FIRMENKUNDE) })
public abstract class Kunde implements Serializable {

	private static final long serialVersionUID = 1430771599450877428L;
	public static final String PRIVATKUNDE = "P";
	public static final String FIRMENKUNDE = "F";
	
	private Long id;
	@NotNull(message = "{kunde.nachname.notNull}")
	@Size(min = 2, max = 32, message = "{kunde.nachname.length}")
	private String nachname;
	@NotNull(message = "{kunde.vorname.notNull}")
	@Size(min = 2, max = 32, message = "{kunde.vorname.length}")
	private String vorname;
	@Valid
	@NotNull(message = "{kunde.adresse.notNull}")
	private Adresse adresse;
	@Size(min = 4, message = "{kunde.email.length}")
	@NotNull(message = "{kunde.email.notNull}")
	private String email;
	
	@XmlTransient
	private List<Bestellung> bestellungen;
	
	private URI bestellungenURI;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNachname() {
		return nachname;
	}
	public void setNachname(String nachname) {
		this.nachname = nachname;
	}
	public String getVorname() {
		return vorname;
	}
	public void setVorname(String vorname) {
		this.vorname = vorname;
	}
	public Adresse getAdresse() {
		return adresse;
	}
	public void setAdresse(Adresse adresse) {
		this.adresse = adresse;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	@Override
	public String toString() {
		return "Kunde [id=" + id + ", nachname=" + nachname + "vorname=" + vorname + ", email="
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
		result = prime * result
				+ ((bestellungen == null) ? 0 : bestellungen.hashCode());
		result = prime * result
				+ ((bestellungenURI == null) ? 0 : bestellungenURI.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((nachname == null) ? 0 : nachname.hashCode());
		result = prime * result + ((vorname == null) ? 0 : vorname.hashCode());
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
		final Kunde other = (Kunde) obj;
		if (adresse == null) {
			if (other.adresse != null)
				return false;
		} 
		else if (!adresse.equals(other.adresse))
			return false;
		if (bestellungen == null) {
			if (other.bestellungen != null)
				return false;
		} 
		else if (!bestellungen.equals(other.bestellungen))
			return false;
		if (bestellungenURI == null) {
			if (other.bestellungenURI != null)
				return false;
		}
		else if (!bestellungenURI.equals(other.bestellungenURI))
			return false;
		if (email == null) {
			if (other.email != null)
				return false;
		}
		else if (!email.equals(other.email))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		if (nachname == null) {
			if (other.nachname != null)
				return false;
		}
		else if (!nachname.equals(other.nachname))
			return false;
		if (vorname == null) {
			if (other.vorname != null)
				return false;
		}
		else if (!vorname.equals(other.vorname))
			return false;
		return true;
	}
	
}
