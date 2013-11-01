package de.shop.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.domain.Bestellstatus;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.domain.Firmenkunde;
import de.shop.kundenverwaltung.domain.HobbyType;
import de.shop.kundenverwaltung.domain.Privatkunde;
import de.shop.lieferantenverwaltung.domain.Lieferant;
import de.shop.lieferantenverwaltung.domain.Lieferantenadresse;

/**
 * Emulation des Anwendungskerns
 */
public final class Mock {
	private static final int MAX_ID = 99;
	private static final int MAX_KUNDEN = 8;
	private static final int MAX_ARTIKEL = 10;
	private static final int MAX_BESTELLUNGEN = 4;
	private static final int MAX_LIEFERANTEN = 8;
	
	//Kundenteil
	public static Kunde findKundeById(Long id) {
		if (id > MAX_ID) {
			return null;
		}
		
		final Kunde kunde = id % 2 == 1 ? new Privatkunde() : new Firmenkunde();
		kunde.setId(id);
		kunde.setNachname("Nachname" + id);
		kunde.setVorname("Vorname" + id);
		kunde.setEmail("" + id + "@hska.de");
		
		final Adresse adresse = new Adresse();
		adresse.setId(id + 1);        // andere ID fuer die Adresse
		adresse.setPlz("12345");
		adresse.setOrt("Testort");
		adresse.setKunde(kunde);
		kunde.setAdresse(adresse);
		
		if (kunde.getClass().equals(Privatkunde.class)) {
			final Privatkunde privatkunde = (Privatkunde) kunde;
			/*
			final Set<HobbyType> hobbies = new HashSet<>();
			hobbies.add(HobbyType.LESEN);
			hobbies.add(HobbyType.REISEN);
			privatkunde.setHobbies(hobbies);
			*/
			privatkunde.setHobby(HobbyType.FUSSBALL);
		}
		
		return kunde;
	}

	public static List<Kunde> findAllKunden() {
		final int anzahl = MAX_KUNDEN;
		final List<Kunde> kunden = new ArrayList<>(anzahl);
		for (int i = 1; i <= anzahl; i++) {
			final Kunde kunde = findKundeById(Long.valueOf(i));
			kunden.add(kunde);			
		}
		return kunden;
	}

	public static List<Kunde> findKundenByNachname(String nachname) {
		final int anzahl = nachname.length();
		final List<Kunde> kunden = new ArrayList<>(anzahl);
		for (int i = 1; i <= anzahl; i++) {
			final Kunde kunde = findKundeById(Long.valueOf(i));
			kunde.setNachname(nachname);
			kunden.add(kunde);			
		}
		return kunden;
	}
	

	public static List<Bestellung> findBestellungenByKunde(Kunde kunde) {
		// Beziehungsgeflecht zwischen Kunde und Bestellungen aufbauen
		final int anzahl = kunde.getId().intValue() % MAX_BESTELLUNGEN + 1;  // 1, 2, 3 oder 4 Bestellungen
		final List<Bestellung> bestellungen = new ArrayList<>(anzahl);
		for (int i = 1; i <= anzahl; i++) {
			final Bestellung bestellung = findBestellungById(Long.valueOf(i));
			bestellung.setKunde(kunde);
			bestellungen.add(bestellung);			
		}
		kunde.setBestellungen(bestellungen);
		
		return bestellungen;
	}

	public static Bestellung findBestellungById(Long id) {
		if (id > MAX_ID) {
			return null;
		}

		final Kunde kunde = findKundeById(id + 1);  // andere ID fuer den Kunden

		final Bestellung bestellung = new Bestellung();
		bestellung.setId(id);
		//bestellung.setBestellstatus("bestellung" + id);
		//TODO setBestellstatus in Mock überarbeiten
		bestellung.setKunde(kunde);
		
		
		
		return bestellung;
	}

	public static Kunde createKunde(Kunde kunde) {
		// Neue IDs fuer Kunde und zugehoerige Adresse
		// Ein neuer Kunde hat auch keine Bestellungen
		final String nachname = kunde.getNachname();
		kunde.setId(Long.valueOf(nachname.length()));
		final Adresse adresse = kunde.getAdresse();
		adresse.setId((Long.valueOf(nachname.length())) + 1);
		adresse.setKunde(kunde);
		kunde.setBestellungen(null);
		System.out.println("Neuer Kunde: " + kunde);
		return kunde;
	}

	public static void updateKunde(Kunde kunde) {
		System.out.println("Aktualisierter Kunde: " + kunde);
	}

	public static void deleteKunde(Long kundeId) {
		System.out.println("Kunde mit ID=" + kundeId + " geloescht");
	}

	//Artikelteil
	public static Artikel findArtikelById(Long id){
		if (id > MAX_ID){
			return null;
		}
		
		Artikel artikel = new Artikel();
		artikel.setId(id);
		artikel.setBezeichnung("Bezeichnung" + id);
		artikel.setPreis(0.0 + id);
		
		return artikel;
	}

	public static List<Artikel> findAllArtikel(){
		final int anzahl = MAX_ARTIKEL;
		final List<Artikel> artikel_liste = new ArrayList<>(anzahl);
		for(int i = 1; i <= anzahl; i++) {
			final Artikel artikel = findArtikelById(Long.valueOf(i));
			artikel_liste.add(artikel);
		}
		return null;
	}

	public static Artikel createArtikel(Artikel artikel) {
		//Nur neue ID zugewiesen
		final String bezeichnung = artikel.getBezeichnung();
		artikel.setId(Long.valueOf(bezeichnung.length()));
		
		System.out.println("Neuer Artikel: " + artikel);
		return artikel;
	}

	public static void updateArtikel(Artikel artikel) {
		System.out.println("Aktualisierter Artikel: " + artikel);
	}

	public static void deleteArtikel(Long id) {
		System.out.println("Artikel mit ID=" + id + " geloescht");
	}

	//Lieferantenteil
	public static Lieferant findLieferantById(Long id) {
		if (id > MAX_ID) {
			return null;
		}
		
		final Lieferant lieferant = new Lieferant();
		lieferant.setId(id);
		lieferant.setFirma("Firma" + id);
		lieferant.setEmail("" + id + "@hska.de");
		
		final Lieferantenadresse adresse = new Lieferantenadresse();
		adresse.setId(id + 1);        // andere ID fuer die Adresse
		adresse.setPlz("12345");
		adresse.setOrt("Testort");
		adresse.setLieferant(lieferant);
		lieferant.setLieferantenadresse(adresse);
		
		return lieferant;
	}

	public static List<Lieferant> findAllLieferanten() {
		final int anzahl = MAX_KUNDEN;
		final List<Lieferant> lieferanten = new ArrayList<>(anzahl);
		for (int i = 1; i <= anzahl; i++) {
			final Lieferant lieferant = findLieferantById(Long.valueOf(i));
			lieferanten.add(lieferant);			
		}
		return lieferanten;
	}

	public static List<Lieferant> findLieferantenByFirma(String firma) {
		final int anzahl = firma.length();
		final List<Lieferant> lieferanten = new ArrayList<>(anzahl);
		for (int i = 1; i <= anzahl; i++) {
			final Lieferant lieferant = findLieferantById(Long.valueOf(i));
			lieferant.setFirma(firma);
			lieferanten.add(lieferant);			
		}
		return lieferanten;
	}

	public static List<Bestellung> findBestellungenByLieferant(Lieferant lieferant) {
		// Beziehungsgeflecht zwischen Lieferant und Bestellungen aufbauen
		final int anzahl = lieferant.getId().intValue() % MAX_BESTELLUNGEN + 1;  // 1, 2, 3 oder 4 Bestellungen
		final List<Bestellung> bestellungen = new ArrayList<>(anzahl);
		for (int i = 1; i <= anzahl; i++) {
			final Bestellung bestellung = findBestellungById(Long.valueOf(i));
			bestellung.setLieferant(lieferant);
			bestellungen.add(bestellung);			
		}
		lieferant.setBestellungen(bestellungen);
		
		return bestellungen;
	}

	/*public static Bestellung findBestellungById(Long id) {
		if (id > MAX_ID) {
			return null;
		}
		
		final Lieferant lieferant = findLieferantById(id + 1);  // andere ID fuer den Lieferanten
		final Bestellung bestellung = new Bestellung();
		bestellung.setId(id);
		//bestellung.setBestellstatus("bestellung" + id);
		//TODO setBestellstatus in Mock überarbeiten
		bestellung.setLieferant(lieferant);
		
		return bestellung;
	}*/

	public static Lieferant createLieferant(Lieferant lieferant) {
		// Neue IDs fuer Lieferant und zugehoerige Adresse
		// Ein neuer Lieferant hat auch keine Bestellungen
		final String firma = lieferant.getFirma();
		lieferant.setId(Long.valueOf(firma.length()));
		final Lieferantenadresse adresse = lieferant.getLieferantenadresse();
		adresse.setId((Long.valueOf(firma.length())) + 1);
		adresse.setLieferant(lieferant);
		lieferant.setBestellungen(null);
		System.out.println("Neuer Lieferant: " + lieferant);
		return lieferant;
	}

	public static void updateLieferant(Lieferant lieferant) {
		System.out.println("Aktualisierter Lieferant: " + lieferant);
	}

	public static void deleteLieferant(Long lieferantId) {
		System.out.println("Lieferant mit ID=" + lieferantId + " geloescht");
	}

	private Mock() { /**/ }
}