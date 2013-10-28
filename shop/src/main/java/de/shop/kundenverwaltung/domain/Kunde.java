package de.shop.kundenverwaltung.domain;

import java.io.Serializable;
import java.net.URI;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import de.shop.bestellverwaltung.domain.Bestellung;

@XmlRootElement
@XmlSeeAlso({ Firmenkunde.class, Privatkunde.class })
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
	@Type(value = Privatkunde.class, name = Kunde.PRIVATKUNDE),
	@Type(value = Firmenkunde.class, name = Kunde.FIRMENKUNDE) })
public class Kunde {
	
	public static final String PRIVATKUNDE = "P";
	public static final String FIRMENKUNDE = "F";
	
	private Long id;
	private String nachname;
	private String vorname;
	private Adresse adresse;
	private String email;
	
	@XmlTransient
	private List<Bestellung> _bestellungen;
	
	private URI bestellungURI;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNachname() {
		return nachname;
	}
	public void setNachname(String _nachname) {
		this.nachname = _nachname;
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
	public void setAdresse(Adresse _adresse) {
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
		return "Kunde [id=" + id + ", nachname=" + nachname + "vorname=" + vorname + ", email="
				+ email + ", bestellungURI=" + bestellungURI + "]";
	}
	
}
