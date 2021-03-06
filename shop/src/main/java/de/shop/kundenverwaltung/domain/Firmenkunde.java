package de.shop.kundenverwaltung.domain;

import static de.shop.kundenverwaltung.domain.AbstractKunde.FIRMENKUNDE;

import javax.persistence.Cacheable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/*
 * Klasse Firmenkunde
 * erbt von der abstrakten Kunden Klasse
 * erh�lt zus�tzlich das Attribut firmenname
 * ist Serializable
 * ---Sadrick---
 */
@XmlRootElement
@Entity
@Cacheable
@DiscriminatorValue(FIRMENKUNDE)
public class Firmenkunde extends AbstractKunde {

	private static final long serialVersionUID = -7720836132451815744L;

	@NotNull(message = "{kunde.firmenname.notNull}")
	@Size(min = 2, max = 50, message = "{kunde.firmenname.length}")
	private String firmenname;

	public String getFirmenname() {
		return firmenname;
	}

	public void setFirmenname(String firmenname) {
		this.firmenname = firmenname;
	}

	@Override
	public String toString() {
		return "Firmenkunde{" + super.toString() + "] firmenname=" + firmenname;
	}

}
