package de.shop.bestellverwaltung.domain;

import java.lang.invoke.MethodHandles;
import java.net.URI;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.PostPersist;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Min;
import javax.xml.bind.annotation.XmlTransient;

import org.jboss.logging.Logger;

import de.shop.artikelverwaltung.domain.Artikel;

@Entity
// TODO MySQL 5.7 kann einen Index nicht 2x anlegen --> War der Fehler das Komma vergessen?
@Table(indexes = { @Index(columnList = "bestellung_fk"),
				   @Index(columnList = "artikel_fk") })
/*
@NamedQueries({ @NamedQuery(name = Posten.FIND_LADENHUETER, query = "SELECT a"
		+ " FROM   Artikel a"
		+ " WHERE  a NOT IN (SELECT bp.artikel FROM Bestellposition bp)") })
		*/
public class Posten {

	private static final Logger LOGGER = Logger.getLogger(MethodHandles
			.lookup().lookupClass());

	private static final String PREFIX = "Bestellposition.";
	public static final String FIND_LADENHUETER = PREFIX + "findLadenhueter";
	private static final int ANZAHL_MIN = 1;
	
	@Id
	@GeneratedValue
	@Basic(optional = false)
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "artikel_fk", nullable = false)
	@XmlTransient
	private Artikel artikel;
	
	@Transient
	private URI artikelUri;
	
	@Min(value = ANZAHL_MIN, message = "{bestellposition.anzahl.min}")
	@Basic(optional = false)
	private int anzahl;

	public Artikel getArtikel() {
		return artikel;
	}

	public void setArtikel(Artikel artikel) {
		this.artikel = artikel;
	}

	public int getAnzahl() {
		return anzahl;
	}

	public void setAnzahl(int anzahl) {
		this.anzahl = anzahl;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + anzahl;
		result = prime * result + ((artikel == null) ? 0 : artikel.hashCode());
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
		final Posten other = (Posten) obj;
		if (anzahl != other.anzahl)
			return false;
		if (artikel == null) {
			if (other.artikel != null)
				return false;
		} else if (!artikel.equals(other.artikel))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Posten [artikel=" + artikel + ", anzahl=" + anzahl + "]";
	}
}
