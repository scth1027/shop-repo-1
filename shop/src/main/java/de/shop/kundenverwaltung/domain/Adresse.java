package de.shop.kundenverwaltung.domain;


import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
public class Adresse {
	
	private Long id;
	private String ort;
	private String plz;
	private String straﬂe;
	private Integer hausnummer;
	
	@XmlTransient
	private Kunde kunde;
	
	public Long getId() {
		return id;
	}
	public void setId(Long _id) {
		this.id = _id;
	}
	public String getOrt() {
		return ort;
	}
	public void setOrt(String _ort) {
		this.ort = _ort;
	}
	public String getPlz() {
		return plz;
	}
	public void setPlz(String _plz) {
		this.plz = _plz;
	}
	public String getStraﬂe() {
		return straﬂe;
	}
	public void setStraﬂe(String _straﬂe) {
		this.straﬂe = _straﬂe;
	}
	public Integer getHausnummer() {
		return hausnummer;
	}
	public void setHausnummer(Integer _hausnummer) {
		this.hausnummer = _hausnummer;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((hausnummer == null) ? 0 : hausnummer.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((ort == null) ? 0 : ort.hashCode());
		result = prime * result + ((plz == null) ? 0 : plz.hashCode());
		result = prime * result + ((straﬂe == null) ? 0 : straﬂe.hashCode());
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
		Adresse other = (Adresse) obj;
		if (hausnummer == null) {
			if (other.hausnummer != null)
				return false;
		} else if (!hausnummer.equals(other.hausnummer))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (ort == null) {
			if (other.ort != null)
				return false;
		} else if (!ort.equals(other.ort))
			return false;
		if (plz == null) {
			if (other.plz != null)
				return false;
		} else if (!plz.equals(other.plz))
			return false;
		if (straﬂe == null) {
			if (other.straﬂe != null)
				return false;
		} else if (!straﬂe.equals(other.straﬂe))
			return false;
		return true;
	}
	public Kunde getKunde() {
		return kunde;
	}
	public void setKunde(Kunde _kunde) {
		this.kunde = _kunde;
	}
	@Override
	public String toString() {
		return "Adresse [id=" + id + ", ort=" + ort + ", plz=" + plz
				+ ", straﬂe=" + straﬂe + ", hausnummer=" + hausnummer
				+ ", kunde=" + kunde + "]";
	}

}
