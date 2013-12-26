package de.shop.kundenverwaltung.domain;

import java.util.Set;

import static de.shop.kundenverwaltung.domain.AbstractKunde.PRIVATKUNDE;
import static javax.persistence.FetchType.EAGER;

import javax.persistence.Cacheable;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.ElementCollection;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.UniqueConstraint;
import javax.xml.bind.annotation.XmlRootElement;

/*
 * Klasse Privatkunde
 * erbt von der abstrakten Klasse Kunde
 * enthält das zusätzliche Attribut hobby --> HobbyType(Enum)
 * Hobby kann später zu einem HashSet geändert werden
 * ist Serializable
 */
@XmlRootElement
@Entity
@DiscriminatorValue(PRIVATKUNDE)
@Cacheable
public class Privatkunde extends AbstractKunde {

	private static final long serialVersionUID = 870262362508397649L;

	@ElementCollection(fetch = EAGER)
	@CollectionTable(name = "kunde_hobby",
	                 joinColumns = @JoinColumn(name = "kunde_fk", nullable = false),
	                 uniqueConstraints =  @UniqueConstraint(columnNames = { "kunde_fk", "hobby" }),
	                 indexes = @Index(columnList = "kunde_fk"))
	@Column(table = "kunde_hobby", name = "hobby", length = 2, nullable = false)
	private Set<HobbyType> hobbies;

	public Set<HobbyType> getHobbies() {
		return hobbies;
	}

	public void setHobbies(Set<HobbyType> hobbies) {
		this.hobbies = hobbies;
	}

	@Override
	public String toString() {
		return "Privatkunde [" + super.toString() + ", hobbies=" + hobbies
				+ "]";
	}

}
