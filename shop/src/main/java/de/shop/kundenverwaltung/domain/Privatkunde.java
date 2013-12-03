package de.shop.kundenverwaltung.domain;

import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

/*
 * Klasse Privatkunde
 * erbt von der abstrakten Klasse Kunde
 * enthält das zusätzliche Attribut hobby --> HobbyType(Enum)
 * Hobby kann später zu einem HashSet geändert werden
 * ist Serializable
 */
@XmlRootElement
public class Privatkunde extends Kunde {

	private static final long serialVersionUID = 870262362508397649L;
	private Set<HobbyType> hobbies;

	public Set<HobbyType> getHobbies() {
		return hobbies;
	}
	public void setHobbies(Set<HobbyType> hobbies) {
		this.hobbies = hobbies;
	}
	@Override
	public String toString() {
		return "Privatkunde [" + super.toString() + ", hobbies=" + hobbies + "]";
	}

}
