package de.shop.lieferantenbestellverwaltung.domain;

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

import de.shop.lieferantenverwaltung.domain.AbstractLieferant;
import de.shop.util.persistence.AbstractAuditable;

@XmlRootElement
@Entity
// TODO MySQL 5.7 kann einen Index nicht 2x anlegen
@Table(indexes = { @Index(columnList = "lieferant_fk"),
		@Index(columnList = "erzeugt") })
@NamedQueries({
		@NamedQuery(name = Lieferantenbestellung.FIND_BESTELLUNGEN_BY_KUNDE, query = "SELECT b"
				+ " FROM   Lieferantenbestellung b"
				+ " WHERE  b.lieferant = :"
				+ Lieferantenbestellung.PARAM_KUNDE),
		@NamedQuery(name = Lieferantenbestellung.FIND_KUNDE_BY_ID, query = "SELECT b.lieferant"
				+ " FROM   Lieferantenbestellung b"
				+ " WHERE  b.id = :"
				+ Lieferantenbestellung.PARAM_ID) })
/*
 * @NamedEntityGraphs({
 * 
 * @NamedEntityGraph(name = Lieferantenbestellung.GRAPH_LIEFERUNGEN, attributeNodes =
 * 
 * @NamedAttributeNode("lieferungen"))
 * 
 * })
 */
@Cacheable
public class Lieferantenbestellung extends AbstractAuditable {

	private static final long serialVersionUID = -9110571232439282099L;

	private static final Logger LOGGER = Logger.getLogger(MethodHandles
			.lookup().lookupClass());

	private static final String PREFIX = "Lieferantenbestellung.";
	public static final String FIND_BESTELLUNGEN_BY_KUNDE = PREFIX
			+ "findLieferantenbestellungenByLieferant";
	public static final String FIND_KUNDE_BY_ID = PREFIX
			+ "findLieferantenbestellungLieferantById";

	public static final String PARAM_KUNDE = "lieferant";
	public static final String PARAM_ID = "id";

	public static final String GRAPH_LIEFERUNGEN = PREFIX + "lieferungen";

	@Id
	@GeneratedValue
	@Basic(optional = false)
	private Long id = KEINE_ID;

	@ManyToOne
	@JoinColumn(name = "lieferant_fk", nullable = false, insertable = false, updatable = false)
	@XmlTransient
	private AbstractLieferant lieferant;

	@Digits(integer = 10, fraction = 2, message = "{lieferantenbestellung.preis.digits}")
	private BigDecimal gesamtpreis;

	@NotNull(message = "{lieferantenbestellung.bestellstatusLB.NotNull}")
	private BestellstatusLB bestellstatusLB;

	@OneToMany(fetch = EAGER, cascade = { PERSIST, REMOVE })
	@JoinColumn(name = "bestellung_fk", nullable = false)
	@NotEmpty(message = "{lieferantenbestellung.bestellpositionen.notEmpty}")
	@Valid
	private List<PostenLB> postenLB;

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

	public AbstractLieferant getLieferant() {
		return lieferant;
	}

	public void setLieferant(AbstractLieferant lieferant) {
		this.lieferant = lieferant;
	}

	public BestellstatusLB getBestellstatusLB() {
		return bestellstatusLB;
	}

	public void setBestellstatusLB(BestellstatusLB bestellstatusLB) {
		this.bestellstatusLB = bestellstatusLB;
	}

	public List<PostenLB> getPostenLB() {
		return postenLB;
	}

	public void setPostenLB(List<PostenLB> postenLB) {
		this.postenLB = postenLB;
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
				+ ((bestellstatusLB == null) ? 0 : bestellstatusLB.hashCode());
		result = prime * result
				+ ((gesamtpreis == null) ? 0 : gesamtpreis.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((lieferant == null) ? 0 : lieferant.hashCode());
		result = prime * result
				+ ((lieferantURI == null) ? 0 : lieferantURI.hashCode());
		result = prime * result + ((postenLB == null) ? 0 : postenLB.hashCode());
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
		final Lieferantenbestellung other = (Lieferantenbestellung) obj;
		if (bestellstatusLB != other.bestellstatusLB)
			return false;
		if (gesamtpreis == null) {
			if (other.gesamtpreis != null)
				return false;
		} else if (!gesamtpreis.equals(other.gesamtpreis))
			return false;
		if (id != other.id)
			return false;
		if (lieferant == null) {
			if (other.lieferant != null)
				return false;
		} else if (!lieferant.equals(other.lieferant))
			return false;
		if (lieferantURI == null) {
			if (other.lieferantURI != null)
				return false;
		} else if (!lieferantURI.equals(other.lieferantURI))
			return false;
		if (postenLB == null) {
			if (other.postenLB != null)
				return false;
		} else if (!postenLB.equals(other.postenLB))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Lieferantenbestellung [id=" + id + ", lieferant=" + lieferant + ", gesamtpreis="
				+ gesamtpreis + ", bestellstatusLB=" + bestellstatusLB
				+ ", postenLB=" + postenLB + ", lieferantURI=" + lieferantURI + "]";
	}
}