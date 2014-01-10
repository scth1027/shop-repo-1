package de.shop.kundenverwaltung.domain;

/*
 * Ein Enum mit den vorhanden Hobbys
 * kann spaeter noch ergänzt werden
 * ---Sadrick---
 */
public enum HobbyType {
	FUSSBALL("F"), SCHWIMMEN("S"), REITEN("R"), HANDBALL("H"), LESEN("L");

	private String internal;

	private HobbyType(String internal) {
		this.internal = internal;
	}

	public String getInternal() {
		return internal;
	}

	public static HobbyType build(String internal) {
		if (internal == null) {
			return null;
		}

		switch (internal) {
		case "F":
			return FUSSBALL;
		case "S":
			return SCHWIMMEN;
		case "R":
			return REITEN;
		case "H":
			return HANDBALL;
		case "L":
			return LESEN;
		default:
			throw new RuntimeException(internal
					+ " ist kein gueltiger Wert fuer HobbyType");
		}
	}

}
