package de.shop.kundenverwaltung.domain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Privatkunde extends Kunde {

	private static final long serialVersionUID = 870262362508397649L;
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
