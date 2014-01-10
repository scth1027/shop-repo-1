package de.shop.bestellverwaltung.domain;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;

import static de.shop.util.Constants.KEINE_ID;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;
import static javax.persistence.FetchType.EAGER;

import javax.persistence.Basic;
import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.NamedEntityGraphs;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.hibernate.validator.constraints.NotEmpty;
import org.jboss.logging.Logger;

import de.shop.kundenverwaltung.domain.AbstractKunde;

@XmlRootElement
@Entity
// TODO MySQL 5.7 kann einen Index nicht 2x anlegen
@Table(indexes = {
	@Index(columnList = "kunde_fk"),
	//@Index(columnList = "erzeugt")
})
@NamedQueries({
	@NamedQuery(name  = Bestellung.FIND_BESTELLUNGEN_BY_KUNDE,
                query = "SELECT b"
			            + " FROM   Bestellung b"
						+ " WHERE  b.kunde = :" + Bestellung.PARAM_KUNDE),
	@NamedQuery(name  = Bestellung.FIND_KUNDE_BY_ID,
 			    query = "SELECT b.kunde"
                        + " FROM   Bestellung b"
  			            + " WHERE  b.id = :" + Bestellung.PARAM_ID)
})
/*
@NamedEntityGraphs({
	@NamedEntityGraph(name = Bestellung.GRAPH_LIEFERUNGEN,
					  attributeNodes = @NamedAttributeNode("lieferungen"))
					  
})
*/
@Cacheable
public class Bestellung {
	
	private static final long serialVersionUID = 7560752199018702446L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	private static final String PREFIX = "Bestellung.";
	public static final String FIND_BESTELLUNGEN_BY_KUNDE = PREFIX + "findBestellungenByKunde";
	public static final String FIND_KUNDE_BY_ID = PREFIX + "findBestellungKundeById";
	
	public static final String PARAM_KUNDE = "kunde";
	public static final String PARAM_ID = "id";
	
	public static final String GRAPH_LIEFERUNGEN = PREFIX + "lieferungen";

	@Id
	@GeneratedValue
	@Basic(optional = false)
	private long id = KEINE_ID;

	@ManyToOne
	@JoinColumn(name = "kunde_fk", nullable = false, insertable = false, updatable = false)
	@XmlTransient
	private AbstractKunde kunde;

	private BigDecimal gesamtpreis;

	@NotNull(message = "{bestellung.bestellstatus.NotNull}")
	private Bestellstatus bestellstatus;

	@OneToMany(fetch = EAGER, cascade = { PERSIST, REMOVE })
	@JoinColumn(name = "bestellung_fk", nullable = false)
	@NotEmpty(message = "{bestellung.bestellpositionen.notEmpty}")
	@Valid
	private List<Posten> posten;

	@Transient
	private URI kundeURI;

	public URI getKundeURI() {
		return kundeURI;
	}

	public void setKundeURI(URI kundeURI) {
		this.kundeURI = kundeURI;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public AbstractKunde getKunde() {
		return kunde;
	}

	public void setKunde(AbstractKunde kunde) {
		this.kunde = kunde;
	}

	public Bestellstatus getBestellstatus() {
		return bestellstatus;
	}

	public void setBestellstatus(Bestellstatus bestellstatus) {
		this.bestellstatus = bestellstatus;
	}

	public List<Posten> getPosten() {
		return posten;
	}

	public void setPosten(List<Posten> posten) {
		this.posten = posten;
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
				+ ((bestellstatus == null) ? 0 : bestellstatus.hashCode());
		result = prime * result
				+ ((gesamtpreis == null) ? 0 : gesamtpreis.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result + ((kunde == null) ? 0 : kunde.hashCode());
		result = prime * result
				+ ((kundeURI == null) ? 0 : kundeURI.hashCode());
		result = prime * result + ((posten == null) ? 0 : posten.hashCode());
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
		final Bestellung other = (Bestellung) obj;
		if (bestellstatus != other.bestellstatus)
			return false;
		if (gesamtpreis == null) {
			if (other.gesamtpreis != null)
				return false;
		} 
		else if (!gesamtpreis.equals(other.gesamtpreis))
			return false;
		if (id != other.id)
			return false;
		if (kunde == null) {
			if (other.kunde != null)
				return false;
		} 
		else if (!kunde.equals(other.kunde))
			return false;
		if (kundeURI == null) {
			if (other.kundeURI != null)
				return false;
		} 
		else if (!kundeURI.equals(other.kundeURI))
			return false;
		if (posten == null) {
			if (other.posten != null)
				return false;
		} 
		else if (!posten.equals(other.posten))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Bestellung [id=" + id + ", kunde=" + kunde + ", gesamtpreis="
				+ gesamtpreis + ", bestellstatus=" + bestellstatus
				+ ", posten=" + posten + ", kundeURI=" + kundeURI + "]";
	}
}

