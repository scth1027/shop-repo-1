package de.shop.lieferantenverwaltung.domain;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.ArrayList;
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
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.validator.constraints.Email;
import org.jboss.logging.Logger;

import de.shop.einkaufverwaltung.domain.Einkauf;

/*
 * Lieferanten Klasse
 * enthält id, nachname, vorname, lieferantenadresse(Lieferantenadresse), email, List von Einkaeufe und zugehörige URI
 * ist Serializiable aufgrund des Mappings notwendig
 * List von Einkäufen wird nicht über den RestService bereitgestellt dafuer gibt es eine URI --> einkaeufeURI
 * ---Tim---
 */
/*
 @ScriptAssert(lang = "javascript", script = "_this.password != null && !_this.password.equals(\"\")"
 + " && _this.password.equals(_this.passwordWdh)", message = "{lieferant.password.notEqual}", groups = {
 Default.class, PasswordGroup.class })
 */
@Entity
// Zu email wird unten ein UNIQUE Index definiert
@Table(name = "lieferant", indexes = @Index(columnList = "firma"))
@Inheritance
@DiscriminatorColumn(name = "art", length = 1)
@NamedQueries({
		@NamedQuery(name = Lieferant.FIND_LIEFERANTEN, query = "SELECT l"
				+ " FROM   Lieferant l"),
		@NamedQuery(name = Lieferant.FIND_LIEFERANTEN_ORDER_BY_ID, query = "SELECT   l"
				+ " FROM  Lieferant l" + " ORDER BY l.id"),
		@NamedQuery(name = Lieferant.FIND_IDS_BY_PREFIX, query = "SELECT   l.id"
				+ " FROM  Lieferant l"
				+ " WHERE CONCAT('', l.id) LIKE :"
				+ Lieferant.PARAM_LIEFERANT_ID_PREFIX + " ORDER BY l.id"),
		@NamedQuery(name = Lieferant.FIND_LIEFERANTEN_BY_FIRMA, query = "SELECT l"
				+ " FROM   Lieferant l"
				+ " WHERE  UPPER(l.firma) = UPPER(:"
				+ Lieferant.PARAM_LIEFERANT_FIRMA + ")"),
		@NamedQuery(name = Lieferant.FIND_FIRMA_BY_PREFIX, query = "SELECT   DISTINCT l.firma"
				+ " FROM  Lieferant l "
				+ " WHERE UPPER(l.firma) LIKE UPPER(:"
				+ Lieferant.PARAM_LIEFERANT_FIRMA_PREFIX + ")"),
		@NamedQuery(name = Lieferant.FIND_LIEFERANT_BY_EMAIL, query = "SELECT DISTINCT l"
				+ " FROM   Lieferant l"
				+ " WHERE  l.email = :"
				+ Lieferant.PARAM_LIEFERANT_EMAIL),
		@NamedQuery(name = Lieferant.FIND_LIEFERANTEN_BY_PLZ, query = "SELECT l"
				+ " FROM  Lieferant l" + " WHERE l.lieferantenadresse.plz = :"
				+ Lieferant.PARAM_LIEFERANT_LIEFERANTENADRESSE_PLZ),
		@NamedQuery(name = Lieferant.FIND_LIEFERANTEN_BY_DATE, query = "SELECT l"
				+ " FROM  Lieferant l"
				+ " WHERE l.seit = :"
				+ Lieferant.PARAM_LIEFERANT_SEIT) })
@NamedEntityGraphs({ @NamedEntityGraph(name = Lieferant.GRAPH_EINKAEUFE,
				attributeNodes = @NamedAttributeNode("einkaeufe")) })
public class Lieferant implements Serializable {

	private static final long serialVersionUID = 1430771599450877428L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles
			.lookup().lookupClass());

	private static final String PREFIX = "Lieferant.";
	public static final String FIND_LIEFERANTEN = PREFIX + "findLieferanten";
	public static final String FIND_LIEFERANTEN_ORDER_BY_ID = PREFIX
			+ "findLieferantenOrderById";
	public static final String FIND_IDS_BY_PREFIX = PREFIX + "findIdsByPrefix";
	public static final String FIND_LIEFERANTEN_BY_FIRMA = PREFIX
			+ "findLieferantenByFirma";
	public static final String FIND_FIRMA_BY_PREFIX = PREFIX
			+ "findFirmaByPrefix";
	public static final String FIND_LIEFERANT_BY_EMAIL = PREFIX
			+ "findLieferantByEmail";
	public static final String FIND_LIEFERANTEN_BY_PLZ = PREFIX + "findLieferantenByPlz";
	public static final String FIND_LIEFERANTEN_BY_DATE = PREFIX
			+ "findLieferantenByDate";

	public static final String PARAM_LIEFERANT_ID = "lieferantId";
	public static final String PARAM_LIEFERANT_ID_PREFIX = "idPrefix";
	public static final String PARAM_LIEFERANT_FIRMA = "firma";
	public static final String PARAM_LIEFERANT_FIRMA_PREFIX = "firmaPrefix";
	public static final String PARAM_LIEFERANT_LIEFERANTENADRESSE_PLZ = "plz";
	public static final String PARAM_LIEFERANT_SEIT = "seit";
	public static final String PARAM_LIEFERANT_EMAIL = "email";

	public static final String GRAPH_EINKAEUFE = PREFIX + "einkaeufe";

	private static final String FIRMA_PATTERN = "[A-Z\u00C4\u00D6\u00DC][a-z\u00E4\u00F6\u00FC\u00DF]+";
	public static final String FIRMA_PATTERN_PUB = FIRMA_PATTERN;
	
	private static final int FIRMA_LENGTH_MIN = 2;
	private static final int FIRMA_LENGTH_MAX = 64;
	private static final int EMAIL_LENGTH_MAX = 128;
	private static final int PASSWORD_LENGTH_MAX = 256;

	@Id
	@GeneratedValue
	@Basic(optional = false)
	private Long id = KEINE_ID;

	@NotNull(message = "{lieferant.firma.notNull}")
	@Size(min = FIRMA_LENGTH_MIN, max = FIRMA_LENGTH_MAX, message = "{lieferant.firma.length}")
	@Pattern(regexp = FIRMA_PATTERN, message = "{lieferant.firma.pattern}")
	private String firma;

	@OneToOne(cascade = { PERSIST, REMOVE }, mappedBy = "lieferant")
	@Valid
	@NotNull(message = "{lieferant.lieferantenadresse.notNull}")
	private Lieferantenadresse lieferantenadresse;

	@Email(message = "{lieferant.email.pattern}")
	@NotNull(message = "{lieferant.email.notNull}")
	@Size(max = EMAIL_LENGTH_MAX, message = "{lieferant.email.length}")
	@Column(unique = true)
	private String email;

	@Temporal(DATE)
	@Past(message = "{lieferant.seit.past}")
	private Date seit;

	@Size(max = PASSWORD_LENGTH_MAX, message = "{lieferant.password.length}")
	private String password;

	@Transient
	private String passwordWdh;

	@OneToMany
	@JoinColumn(name = "lieferant_fk", nullable = false)
	@OrderColumn(name = "idx", nullable = false)
	@XmlTransient
	private List<Einkauf> einkaeufe;

	@Transient
	private URI einkaeufeURI;

	@PostPersist
	protected void postPersist() {
		LOGGER.debugf("Neuer Lieferant mit ID=%d", id);
	}

	@PostLoad
	protected void postLoad() {
		passwordWdh = password;
	}

	public void setValues(Lieferant l) {
		firma = l.firma;
		seit = l.seit;
		email = l.email;
		password = l.password;
		passwordWdh = l.password;
	}

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
		return lieferantenadresse;
	}

	public void setLieferantenadresse(Lieferantenadresse lieferantenadresse) {
		this.lieferantenadresse = lieferantenadresse;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "Lieferant [id=" + id + ", firma=" + firma + ", email=" + email + ", seit=" + seit + "]";
	}

	public URI getEinkaeufeURI() {
		return einkaeufeURI;
	}

	public void setEinkaeufeURI(URI einkaeufeURI) {
		this.einkaeufeURI = einkaeufeURI;
	}

	public List<Einkauf> getEinkaeufe() {
		return einkaeufe;
	}

	public void setEinkaeufe(List<Einkauf> einkaeufe) {
		this.einkaeufe = einkaeufe;
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
		final Lieferant other = (Lieferant) obj;
		if (email == null) {
			if (other.email != null)
				return false;
		}
		else if (!email.equals(other.email))
			return false;
		return true;
	}

	public void addEinkauf(Einkauf einkauf) {
		if (einkaeufe == null) {
			einkaeufe = new ArrayList<>();
		}
		einkaeufe.add(einkauf);
	}

}
