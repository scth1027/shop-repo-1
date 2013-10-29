package de.shop.kundenverwaltung.domain;

import javax.xml.bind.annotation.XmlRootElement;

/*
 * Klasse Privatkunde
 * erbt von der abstrakten Klasse Kunde
 * enth�lt das zus�tzliche Attribut hobby --> HobbyType(Enum)
 * Hobby kann sp�ter zu einem HashSet ge�ndert werden
 * ist Serializable
 */
@XmlRootElement
public class Privatkunde extends Kunde {

	private static final long serialVersionUID = 870262362508397649L;
//	TODO: hobby sollte spaeter zu einem HashSet ge�ndert werden, sodass mehrere Hobbys aufgenommen werden koennen
	private HobbyType hobby;

	public HobbyType getHobby() {
		return hobby;
	}

	public void setHobby(HobbyType hobby) {
		this.hobby = hobby;
	}

	@Override
	public String toString() {
		return "Privatkunde[" + super.toString() + "] hobby=" + hobby;
	}

}
