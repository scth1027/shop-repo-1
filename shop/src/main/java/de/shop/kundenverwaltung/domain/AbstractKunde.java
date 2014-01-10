package de.shop.kundenverwaltung.domain;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.Date;
import java.util.List;

import static de.shop.util.Constants.KEINE_ID;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.TemporalType.DATE;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.OrderColumn;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.groups.Default;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.ScriptAssert;
import org.jboss.logging.Logger;
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
/*
 @ScriptAssert(lang = "javascript", script = "_this.password != null && !_this.password.equals(\"\")"
 + " && _this.password.equals(_this.passwordWdh)", message = "{kunde.password.notEqual}", groups = {
 Default.class, PasswordGroup.class })
 */
@XmlRootElement
@XmlSeeAlso({ Firmenkunde.class, Privatkunde.class })
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
		@Type(value = Privatkunde.class, name = AbstractKunde.PRIVATKUNDE),
		@Type(value = Firmenkunde.class, name = AbstractKunde.FIRMENKUNDE) })
@Entity
// Zu email wird unten ein UNIQUE Index definiert
@Table(name = "kunde", indexes = @Index(columnList = "nachname"))
@Inheritance
@DiscriminatorColumn(name = "art", length = 1)
@NamedQueries({
		@NamedQuery(name = AbstractKunde.FIND_KUNDEN, query = "SELECT k"
				+ " FROM   AbstractKunde k"),
		@NamedQuery(name = AbstractKunde.FIND_KUNDEN_ORDER_BY_ID, query = "SELECT   k"
				+ " FROM  AbstractKunde k" + " ORDER BY k.id"),
		@NamedQuery(name = AbstractKunde.FIND_IDS_BY_PREFIX, query = "SELECT   k.id"
				+ " FROM  AbstractKunde k"
				+ " WHERE CONCAT('', k.id) LIKE :"
				+ AbstractKunde.PARAM_KUNDE_ID_PREFIX + " ORDER BY k.id"),
		@NamedQuery(name = AbstractKunde.FIND_KUNDEN_BY_NACHNAME, query = "SELECT k"
				+ " FROM   AbstractKunde k"
				+ " WHERE  UPPER(k.nachname) = UPPER(:"
				+ AbstractKunde.PARAM_KUNDE_NACHNAME + ")"),
		@NamedQuery(name = AbstractKunde.FIND_NACHNAMEN_BY_PREFIX, query = "SELECT   DISTINCT k.nachname"
				+ " FROM  AbstractKunde k "
				+ " WHERE UPPER(k.nachname) LIKE UPPER(:"
				+ AbstractKunde.PARAM_KUNDE_NACHNAME_PREFIX + ")"),
		@NamedQuery(name = AbstractKunde.FIND_KUNDE_BY_EMAIL, query = "SELECT DISTINCT k"
				+ " FROM   AbstractKunde k"
				+ " WHERE  k.email = :"
				+ AbstractKunde.PARAM_KUNDE_EMAIL),
		@NamedQuery(name = AbstractKunde.FIND_KUNDEN_BY_PLZ, query = "SELECT k"
				+ " FROM  AbstractKunde k" + " WHERE k.adresse.plz = :"
				+ AbstractKunde.PARAM_KUNDE_ADRESSE_PLZ),
		@NamedQuery(name = AbstractKunde.FIND_KUNDEN_BY_DATE, query = "SELECT k"
				+ " FROM  AbstractKunde k"
				+ " WHERE k.seit = :"
				+ AbstractKunde.PARAM_KUNDE_SEIT),
		@NamedQuery(name = AbstractKunde.FIND_PRIVATKUNDEN_FIRMENKUNDEN, query = "SELECT k"
				+ " FROM  AbstractKunde k"
				+ " WHERE TYPE(k) IN (Privatkunde, Firmenkunde)") })
@NamedEntityGraphs({ @NamedEntityGraph(name = AbstractKunde.GRAPH_BESTELLUNGEN, attributeNodes = @NamedAttributeNode("bestellungen")) })
public abstract class AbstractKunde implements Serializable {

	private static final long serialVersionUID = 1430771599450877428L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles
			.lookup().lookupClass());

	private static final String PREFIX = "Kunde.";
	public static final String FIND_KUNDEN = PREFIX + "findKunden";
	public static final String FIND_KUNDEN_ORDER_BY_ID = PREFIX
			+ "findKundenOrderById";
	public static final String FIND_IDS_BY_PREFIX = PREFIX + "findIdsByPrefix";
	public static final String FIND_KUNDEN_BY_NACHNAME = PREFIX
			+ "findKundenByNachname";
	public static final String FIND_NACHNAMEN_BY_PREFIX = PREFIX
			+ "findNachnamenByPrefix";
	public static final String FIND_KUNDE_BY_EMAIL = PREFIX
			+ "findKundeByEmail";
	public static final String FIND_KUNDEN_BY_PLZ = PREFIX + "findKundenByPlz";
	public static final String FIND_KUNDEN_BY_DATE = PREFIX
			+ "findKundenByDate";
	public static final String FIND_PRIVATKUNDEN_FIRMENKUNDEN = PREFIX
			+ "findPrivatkundenFirmenkunden";

	public static final String PARAM_KUNDE_ID = "kundeId";
	public static final String PARAM_KUNDE_ID_PREFIX = "idPrefix";
	public static final String PARAM_KUNDE_NACHNAME = "nachname";
	public static final String PARAM_KUNDE_NACHNAME_PREFIX = "nachnamePrefix";
	public static final String PARAM_KUNDE_ADRESSE_PLZ = "plz";
	public static final String PARAM_KUNDE_SEIT = "seit";
	public static final String PARAM_KUNDE_EMAIL = "email";

	public static final String GRAPH_BESTELLUNGEN = PREFIX + "bestellungen";

	public static final String PRIVATKUNDE = "P";
	public static final String FIRMENKUNDE = "F";

	private static final String NAME_PATTERN = "[A-Z\u00C4\u00D6\u00DC][a-z\u00E4\u00F6\u00FC\u00DF]+";
	private static final String NACHNAME_PREFIX = "(o'|von|von der|von und zu|van)?";

	public static final String NACHNAME_PATTERN = NACHNAME_PREFIX
			+ NAME_PATTERN + "(-" + NAME_PATTERN + ")?";
	private static final int NAME_LENGTH_MIN = 2;
	private static final int NAME_LENGTH_MAX = 32;
	private static final int EMAIL_LENGTH_MAX = 128;
	private static final int PASSWORD_LENGTH_MAX = 256;

	@Id
	@GeneratedValue
	@Basic(optional = false)
	private Long id = KEINE_ID;

	@NotNull(message = "{kunde.nachname.notNull}")
	@Size(min = NAME_LENGTH_MIN, max = NAME_LENGTH_MAX, message = "{kunde.nachname.length}")
	@Pattern(regexp = NACHNAME_PATTERN, message = "{kunde.nachname.pattern}")
	private String nachname;

	@NotNull(message = "{kunde.vorname.notNull}")
	@Size(min = NAME_LENGTH_MIN, max = NAME_LENGTH_MAX, message = "{kunde.vorname.length}")
	@Pattern(regexp = NAME_PATTERN, message = "{kunde.vorname.pattern}")
	private String vorname;

	@OneToOne(cascade = { PERSIST, REMOVE }, mappedBy = "kunde")
	@Valid
	@NotNull(message = "{kunde.adresse.notNull}")
	private Adresse adresse;

	@Email(message = "{kunde.email.pattern}")
	@NotNull(message = "{kunde.email.notNull}")
	@Size(max = EMAIL_LENGTH_MAX, message = "{kunde.email.length}")
	@Column(unique = true)
	private String email;

	@Temporal(DATE)
	@Past(message = "{kunde.seit.past}")
	private Date seit;

	@Size(max = PASSWORD_LENGTH_MAX, message = "{kunde.password.length}")
	private String password;

	@Transient
	private String passwordWdh;

	@OneToMany
	@JoinColumn(name = "kunde_fk", nullable = false)
	@OrderColumn(name = "idx", nullable = false)
	@XmlTransient
	private List<Bestellung> bestellungen;

	@Transient
	private URI bestellungenURI;

	@PostPersist
	protected void postPersist() {
		LOGGER.debugf("Neuer Kunde mit ID=%d", id);
	}

	@PostLoad
	protected void postLoad() {
		passwordWdh = password;
	}

	public void setValues(AbstractKunde k) {
		nachname = k.nachname;
		vorname = k.vorname;
		seit = k.seit;
		email = k.email;
		password = k.password;
		passwordWdh = k.password;
	}

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
		final AbstractKunde other = (AbstractKunde) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		return true;
	}

}
