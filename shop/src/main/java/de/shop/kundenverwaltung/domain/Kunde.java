package de.shop.kundenverwaltung.domain;

import java.io.Serializable;
import java.net.URI;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.validator.constraints.Email;
import org.codehaus.jackson.annotate.JsonSubTypes;
import org.codehaus.jackson.annotate.JsonSubTypes.Type;
import org.codehaus.jackson.annotate.JsonTypeInfo;

import de.shop.bestellverwaltung.domain.Bestellung;

/*
 * Abstrakte Kunden Klasse
 * enthält id, nachname, vorname, adresse(Adresse), email, List von Bestellungen und zugehörige URI
 * ist Serializiable aufgrund des Mappings notwendig
 * verweist auf die zwei Unterklassen, notwendig für Rest und JSON
 * List von Bestellunen wird nicht über den RestService bereitgestellt dafuer gibt es eine URI --> bestellungenURI
 * ---Sadrick---
 */
@XmlRootElement
@XmlSeeAlso({ Firmenkunde.class, Privatkunde.class })
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @Type(value = Privatkunde.class, name = Kunde.PRIVATKUNDE),
		@Type(value = Firmenkunde.class, name = Kunde.FIRMENKUNDE) })
@Entity
// Zu email wird unten ein UNIQUE Index definiert
@Table(name = "kunde", indexes = @Index(columnList = "nachname"))
@Inheritance
@DiscriminatorColumn(name = "art", length = 1)
public abstract class Kunde implements Serializable {

	private static final long serialVersionUID = 1430771599450877428L;
	public static final String PRIVATKUNDE = "P";
	public static final String FIRMENKUNDE = "F";

	private static final String NAME_PATTERN = "[A-Z\u00C4\u00D6\u00DC][a-z\u00E4\u00F6\u00FC\u00DF]+";
	private static final String NACHNAME_PREFIX = "(o'|von|von der|von und zu|van)?";

	public static final String NACHNAME_PATTERN = NACHNAME_PREFIX
			+ NAME_PATTERN + "(-" + NAME_PATTERN + ")?";
	private static final int NAME_LENGTH_MIN = 2;
	private static final int NAME_LENGTH_MAX = 32;
	private static final int EMAIL_LENGTH_MAX = 128;

	private Long id;
	@NotNull(message = "{kunde.nachname.notNull}")
	@Size(min = NAME_LENGTH_MIN, max = NAME_LENGTH_MAX, message = "{kunde.nachname.length}")
	@Pattern(regexp = NACHNAME_PATTERN, message = "{kunde.nachname.pattern}")
	private String nachname;
	@NotNull(message = "{kunde.vorname.notNull}")
	@Size(min = NAME_LENGTH_MIN, max = NAME_LENGTH_MAX, message = "{kunde.vorname.length}")
	@Pattern(regexp = NAME_PATTERN, message = "{kunde.vorname.pattern}")
	private String vorname;
	@Valid
	@NotNull(message = "{kunde.adresse.notNull}")
	private Adresse adresse;
	@Size(max = EMAIL_LENGTH_MAX, message = "{kunde.email.length}")
	@NotNull(message = "{kunde.email.notNull}")
	@Email(message = "{kunde.email.pattern}")
	private String email;
	@Past(message = "{kunde.seit.past}")
	private Date seit;

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
		return "Kunde [id=" + id + ", nachname=" + nachname + ", vorname="
				+ vorname + ", email=" + email + ", seit=" + seit + "]";
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

	public void setBestellungen(List<Bestellung> bestellungen) {
		this.bestellungen = bestellungen;
	}

	public Date getSeit() {
		return seit;
	}

	public void setSeit(Date seit) {
		this.seit = seit;
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
		final Kunde other = (Kunde) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		return true;
	}

}
