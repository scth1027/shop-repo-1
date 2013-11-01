package de.shop.lieferantenverwaltung.domain;


import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
/*
 * Klasse Lieferantenadresse
 * enthaelt id, ort, plz, strasse, hausnummer
 * ist Serializable
 * alle 3 Object Methoden �berschrieben
 * ---Tim---
 */
@XmlRootElement
public class Lieferantenadresse implements Serializable {

	private static final long serialVersionUID = 1L;
	private Long id;
	private String ort;
	private String plz;
	private String strasse;
	private Integer hausnummer;
	
	@XmlTransient
	private Lieferant lieferant;
	
	public Long getId() {
		return id;
	}
	public void setId(Long _id) {
		this.id = _id;
	}
	public String getOrt() {
		return ort;
	}
	public void setOrt(String _ort) {
		this.ort = _ort;
	}
	public String getPlz() {
		return plz;
	}
	public void setPlz(String _plz) {
		this.plz = _plz;
	}
	public String getStra�e() {
		return strasse;
	}
	public void setStra�e(String _stra�e) {
		this.strasse = _stra�e;
	}
	public Integer getHausnummer() {
		return hausnummer;
	}
	public void setHausnummer(Integer _hausnummer) {
		this.hausnummer = _hausnummer;
	}
	
	public Lieferant getLieferant() {
		return lieferant;
	}
	public void setLieferant(Lieferant _lieferant) {
		this.lieferant = _lieferant;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((hausnummer == null) ? 0 : hausnummer.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((ort == null) ? 0 : ort.hashCode());
		result = prime * result + ((plz == null) ? 0 : plz.hashCode());
		result = prime * result + ((strasse == null) ? 0 : strasse.hashCode());
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
		Lieferantenadresse other = (Lieferantenadresse) obj;
		if (hausnummer == null) {
			if (other.hausnummer != null)
				return false;
		} else if (!hausnummer.equals(other.hausnummer))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (ort == null) {
			if (other.ort != null)
				return false;
		} else if (!ort.equals(other.ort))
			return false;
		if (plz == null) {
			if (other.plz != null)
				return false;
		} else if (!plz.equals(other.plz))
			return false;
		if (strasse == null) {
			if (other.strasse != null)
				return false;
		} else if (!strasse.equals(other.strasse))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Adresse [id=" + id + ", ort=" + ort + ", plz=" + plz
				+ ", stra�e=" + strasse + ", hausnummer=" + hausnummer
				+ ", lieferant=" + lieferant + "]";
	}

}