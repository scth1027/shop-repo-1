package de.shop.kundenverwaltung.domain;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Firmenkunde extends Kunde {
	
	private static final long serialVersionUID = -7720836132451815744L;
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
