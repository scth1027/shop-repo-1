package de.shop.einkaufverwaltung.domain;

import static de.shop.util.Constants.KEINE_ID;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.EAGER;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.validator.constraints.NotEmpty;
import org.jboss.logging.Logger;

import de.shop.lieferantenverwaltung.domain.Lieferant;
import de.shop.util.persistence.AbstractAuditable;

@XmlRootElement
@Entity
// TODO MySQL 5.7 kann einen Index nicht 2x anlegen
@Table(indexes = { @Index(columnList = "lieferant_fk"),
		@Index(columnList = "erzeugt") })
@NamedQueries({
		@NamedQuery(name = Einkauf.FIND_EINKAEUFE_BY_LIEFERANT, query = "SELECT l"
				+ " FROM   Einkauf l"
				+ " WHERE  l.lieferant = :"
				+ Einkauf.PARAM_LIEFERANT),
		@NamedQuery(name = Einkauf.FIND_LIEFERANT_BY_ID, query = "SELECT l.lieferant"
				+ " FROM   Einkauf l"
				+ " WHERE  l.id = :"
				+ Einkauf.PARAM_ID) })
@Cacheable
public class Einkauf extends AbstractAuditable {

	private static final long serialVersionUID = -9110571232439282099L;

	private static final Logger LOGGER = Logger.getLogger(MethodHandles
			.lookup().lookupClass());

	private static final String PREFIX = "Einkauf.";
	public static final String FIND_EINKAEUFE_BY_LIEFERANT = PREFIX
			+ "findEinkaufenByLieferant";
	public static final String FIND_LIEFERANT_BY_ID = PREFIX
			+ "findEinkaufLieferantById";

	public static final String PARAM_LIEFERANT = "lieferant";
	public static final String PARAM_ID = "id";

	public static final String GRAPH_LIEFERUNGEN = PREFIX + "lieferungen";

	@Id
	@GeneratedValue
	@Basic(optional = false)
	private Long id = KEINE_ID;

	@ManyToOne
	@JoinColumn(name = "lieferant_fk", nullable = false, insertable = false, updatable = false)
	@XmlTransient
	private Lieferant lieferant;

	@Digits(integer = 10, fraction = 2, message = "{einkauf.preis.digits}")
	private BigDecimal gesamtpreis;

	@NotNull(message = "{einkauf.einkaufstatus.NotNull}")
	private Einkaufstatus einkaufstatus;

	@OneToMany(fetch = EAGER, cascade = { PERSIST, REMOVE })
	@JoinColumn(name = "einkauf_fk", nullable = false)
	@NotEmpty(message = "{einkauf.einkaufpositionen.notEmpty}")
	@Valid
	private List<Einkaufposten> einkaufposten;

	@Transient
	private URI lieferantURI;

	public URI getLieferantURI() {
		return lieferantURI;
	}

	public void setLieferantURI(URI lieferantURI) {
		this.lieferantURI = lieferantURI;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Lieferant getLieferant() {
		return lieferant;
	}

	public void setLieferant(Lieferant lieferant) {
		this.lieferant = lieferant;
	}

	public Einkaufstatus getEinkaufstatus() {
		return einkaufstatus;
	}

	public void setEinkaufstatus(Einkaufstatus einkaufstatus) {
		this.einkaufstatus = einkaufstatus;
	}

	public List<Einkaufposten> getEinkaufposten() {
		return einkaufposten;
	}

	public void setEinkaufposten(List<Einkaufposten> einkaufposten) {
		this.einkaufposten = einkaufposten;
	}

	public BigDecimal getGesamtpreis() {
		return gesamtpreis;
	}

	public void setGesamtpreis(BigDecimal gesamtpreis) {
		this.gesamtpreis = gesamtpreis.setScale(2);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((einkaufstatus == null) ? 0 : einkaufstatus.hashCode());
		result = prime * result
				+ ((gesamtpreis == null) ? 0 : gesamtpreis.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((lieferant == null) ? 0 : lieferant.hashCode());
		result = prime * result
				+ ((lieferantURI == null) ? 0 : lieferantURI.hashCode());
		result = prime * result + ((einkaufposten == null) ? 0 : einkaufposten.hashCode());
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
		final Einkauf other = (Einkauf) obj;
		if (einkaufstatus != other.einkaufstatus)
			return false;
		if (gesamtpreis == null) {
			if (other.gesamtpreis != null)
				return false;
		}
		else if (!gesamtpreis.equals(other.gesamtpreis))
			return false;
		if (id != other.id)
			return false;
		if (lieferant == null) {
			if (other.lieferant != null)
				return false;
		}
		else if (!lieferant.equals(other.lieferant))
			return false;
		if (lieferantURI == null) {
			if (other.lieferantURI != null)
				return false;
		}
		else if (!lieferantURI.equals(other.lieferantURI))
			return false;
		if (einkaufposten == null) {
			if (other.einkaufposten != null)
				return false;
		}
		else if (!einkaufposten.equals(other.einkaufposten))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Einkauf [id=" + id + ", lieferant=" + lieferant + ", gesamtpreis="
				+ gesamtpreis + ", einkaufstatus=" + einkaufstatus
				+ ", einkaufposten=" + einkaufposten + ", lieferantURI=" + lieferantURI + "]";
	}
}
