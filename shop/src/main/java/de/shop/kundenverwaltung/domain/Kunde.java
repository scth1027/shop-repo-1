package de.shop.kundenverwaltung.domain;

import java.net.URI;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import de.shop.bestellverwaltung.domain.Bestellung;

@XmlRootElement
public class Kunde {
	
	private Long id;
	private String nachname;
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
		return "Kunde [id=" + id + ", nachname=" + nachname + ", email="
				+ email + ", bestellungURI=" + bestellungURI + "]";
	}
	
	
}
