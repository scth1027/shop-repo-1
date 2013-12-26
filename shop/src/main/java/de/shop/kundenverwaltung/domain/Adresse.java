package de.shop.kundenverwaltung.domain;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;

import static de.shop.util.Constants.KEINE_ID;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.PostPersist;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;

import org.jboss.logging.Logger;

/*
 * Klasse Adresse
 * enthaelt id, ort, plz, strasse, hausnummer
 * ist Serializable
 * alle 3 Object Methoden überschrieben
 * ---Sadrick---
 */
@Entity
@Table(indexes = @Index(columnList = "plz")) 
public class Adresse implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	private static final int PLZ_LENGTH_MAX = 5;
	private static final int ORT_LENGTH_MIN = 2;
	private static final int ORT_LENGTH_MAX = 32;
	private static final int STRASSE_LENGTH_MIN = 2;
	private static final int STRASSE_LENGTH_MAX = 32;
	private static final int HAUSNR_LENGTH_MAX = 4;
	
	@Id
	@GeneratedValue
	@Basic(optional = false)
	private Long id = KEINE_ID;
	
	@NotNull(message="{adresse.ort.NotNull")
	@Size(min=ORT_LENGTH_MIN, max=ORT_LENGTH_MAX, message="{adresse.ort.length}")
	private String ort;
	@NotNull(message="{adresse.plz.NotNull}")
	@Pattern(regexp = "\\d{" + PLZ_LENGTH_MAX + "}", message = "{adresse.plz}")
	@Column(length=PLZ_LENGTH_MAX)
	private String plz;
	@NotNull(message="{adresse.strasse.NotNull}")
	@Size(min=STRASSE_LENGTH_MIN, max=STRASSE_LENGTH_MAX, message="{adresse.strasse.length}")
	private String strasse;
	@Size(max=HAUSNR_LENGTH_MAX, message="{adresse.hausnummer.length}")
	private String hausnummer;

	@OneToOne
	@JoinColumn(name = "kunde_fk", nullable = false, unique = true)
	@XmlTransient
	private AbstractKunde kunde;
	
	public Adresse() {
		super();
	}
	
	public Adresse(String plz, String ort, String strasse, String hausnr, AbstractKunde kunde) {
		super();
		this.plz = plz;
		this.ort = ort;
		this.strasse = strasse;
		this.hausnummer = hausnr;
		this.kunde = kunde;
	}
	
	@PostPersist
	private void postPersist() {
		LOGGER.debugf("Neue Adresse mit ID=%s", id);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getOrt() {
		return ort;
	}

	public void setOrt(String ort) {
		this.ort = ort;
	}

	public String getPlz() {
		return plz;
	}

	public void setPlz(String plz) {
		this.plz = plz;
	}

	public String getStrasse() {
		return strasse;
	}

	public void setStrasse(String strasse) {
		this.strasse = strasse;
	}

	public String getHausnummer() {
		return hausnummer;
	}

	public void setHausnummer(String hausnummer) {
		this.hausnummer = hausnummer;
	}

	public AbstractKunde getKunde() {
		return kunde;
	}

	public void setKunde(AbstractKunde kunde) {
		this.kunde = kunde;
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
		final Adresse other = (Adresse) obj;
		if (hausnummer == null) {
			if (other.hausnummer != null)
				return false;
		} 
		else if (!hausnummer.equals(other.hausnummer))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} 
		else if (!id.equals(other.id))
			return false;
		if (ort == null) {
			if (other.ort != null)
				return false;
		} 
		else if (!ort.equals(other.ort))
			return false;
		if (plz == null) {
			if (other.plz != null)
				return false;
		} 
		else if (!plz.equals(other.plz))
			return false;
		if (strasse == null) {
			if (other.strasse != null)
				return false;
		} 
		else if (!strasse.equals(other.strasse))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Adresse [id=" + id + ", ort=" + ort + ", plz=" + plz
				+ ", straße=" + strasse + ", hausnummer=" + hausnummer
				+ ", kunde=" + kunde + "]";
	}

}
