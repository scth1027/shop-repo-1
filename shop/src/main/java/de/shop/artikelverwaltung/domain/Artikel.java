package de.shop.artikelverwaltung.domain;

import java.io.Serializable;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PostPersist;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.jboss.logging.Logger;

/*
 * Artikel Klasse
 * enthält id, bezeichnung und preis
 * ist Serializiable aufgrund des Mappings notwendig
 * ---Christian---
 */
@XmlRootElement
@Entity
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@Table(indexes = @Index(columnList = "bezeichnung"))
@NamedQueries({
	@NamedQuery(name  = Artikel.FIND_ARTIKEL_BY_ID,
        	query = "SELECT      a"
                    + " FROM     Artikel a"
					+ " WHERE    a.id = :" + Artikel.PARAM_ID
		 	        + " ORDER BY a.id ASC"),
	@NamedQuery(name  = Artikel.FIND_ARTIKEL_BY_BEZEICHNUNG,
            	query = "SELECT      a"
                        + " FROM     Artikel a"
						+ " WHERE    a.bezeichnung LIKE :" + Artikel.PARAM_BEZEICHNUNG
			 	        + " ORDER BY a.id ASC"),
	@NamedQuery(name  = Artikel.FIND_ALL_ARTIKEL,
            	query = "SELECT      a"
            	        + " FROM     Artikel a"
                        + " ORDER BY a.id ASC")
})
public class Artikel implements Serializable {
	
	private static final long serialVersionUID = -7855084950143201920L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles
			.lookup().lookupClass());
	
	private static final int BEZEICHNUNG_LENGTH_MAX = 100;
	private static final String BEZEICHNUNG_PATTERN = "[A-Z\u00C4\u00D6\u00DC][a-z0-9\u00E4\u00F6\u00FC\u00DF_-'\"/ ]+";
	private static final int PREIS_INTEGER = 10;
	private static final int PREIS_FRACTION = 2;
	
	private static final String PREFIX = "Artikel.";

	public static final String FIND_ARTIKEL_BY_ID = PREFIX + "findArtikelById";
	public static final String FIND_ARTIKEL_BY_BEZEICHNUNG = PREFIX + "findArtikelByBezeichnung";
	public static final String FIND_ALL_ARTIKEL = PREFIX + "findAllArtikel";

	public static final String PARAM_ID = "id";
	public static final String PARAM_BEZEICHNUNG = "bezeichnung";
	
	@Id
	@GeneratedValue
	@Basic(optional = false)
	private Long id;
	@NotNull(message = "{artikel.bezeichnung.notnull}")
	@Size(max = BEZEICHNUNG_LENGTH_MAX, message = "{artikel.bezeichnung.length}")
	@Pattern(regexp = BEZEICHNUNG_PATTERN, message = "{artikel.bezeichnung.pattern}")
	private String bezeichnung;
	@NotNull(message = "{artikel.preis.notnull}")
	@Digits(integer = PREIS_INTEGER, fraction = PREIS_FRACTION, message = "{artikel.preis.digits}")
	private BigDecimal preis;
	
	@PostPersist
	protected void postPersist() {
		LOGGER.debugf("Neuer Artikel mit ID=%d", id);
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getBezeichnung() {
		return bezeichnung;
	}
	
	public void setBezeichnung(String bezeichnung) {
		this.bezeichnung = bezeichnung;
	}
	
	public BigDecimal getPreis() {
		return preis;
	}
	
	public void setPreis(BigDecimal preis) {
		this.preis = preis.setScale(2);
	}
	
	public Artikel() {
		super();
		this.id = null;
		this.bezeichnung = null;
		this.preis = null;
	}

	public Artikel(Long id, String bezeichnung, BigDecimal preis) {
		super();
		this.id = id;
		this.bezeichnung = bezeichnung;
		this.preis = preis.setScale(2);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((bezeichnung == null) ? 0 : bezeichnung.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((preis == null) ? 0 : preis.hashCode());
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
		Artikel other = (Artikel) obj;
		if (bezeichnung == null) {
			if (other.bezeichnung != null)
				return false;
		} else if (!bezeichnung.equals(other.bezeichnung))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (preis == null) {
			if (other.preis != null)
				return false;
		} else if (!preis.equals(other.preis))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Artikel [id=" + id + ", bezeichnung=" + bezeichnung
				+ ", preis=" + preis + "]";
	}
}
