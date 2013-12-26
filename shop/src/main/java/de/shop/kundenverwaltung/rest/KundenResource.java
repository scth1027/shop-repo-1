package de.shop.kundenverwaltung.rest;

import static de.shop.util.Constants.ADD_LINK;
import static de.shop.util.Constants.FIRST_LINK;
import static de.shop.util.Constants.LAST_LINK;
import static de.shop.util.Constants.REMOVE_LINK;
import static de.shop.util.Constants.SELF_LINK;
import static de.shop.util.Constants.UPDATE_LINK;
import static de.shop.util.Constants.KEINE_ID;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.MediaType.TEXT_XML;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.hibernate.validator.constraints.Email;
import org.jboss.logging.Logger;

import com.google.common.base.Strings;

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.rest.BestellungResource;
import de.shop.bestellverwaltung.service.BestellungService;
import de.shop.kundenverwaltung.domain.Adresse;
import de.shop.kundenverwaltung.domain.AbstractKunde;
import de.shop.kundenverwaltung.service.KundeService;
import de.shop.kundenverwaltung.service.KundeService.FetchType;
import de.shop.kundenverwaltung.service.KundeService.OrderType;
import de.shop.util.Mock;
import de.shop.util.interceptor.Log;
import de.shop.util.rest.UriHelper;

/*
 * KundenResource Klasse
 * enthaelt die RestServices fuer die Kundenverwaltung/Domain
 * Zugriff auf den Mock später Anwendungslogik
 */
@RequestScoped
@Path("/kunden")
@Produces({ APPLICATION_JSON, APPLICATION_XML + ";qs=0.75",
		TEXT_XML + ";qs=0.5" })
@Consumes
@Transactional
@Log
public class KundenResource {

	private static final Logger LOGGER = Logger.getLogger(MethodHandles
			.lookup().lookupClass());
	private static final String VERSION = "1.0";

	public static final String KUNDEN_ID_PATH_PARAM = "kundeId";
	public static final String KUNDEN_NACHNAME_QUERY_PARAM = "nachname";
	public static final String KUNDEN_PLZ_QUERY_PARAM = "plz";
	public static final String KUNDEN_EMAIL_QUERY_PARAM = "email";

	@Context
	private UriInfo uriInfo;

	@Inject
	private UriHelper uriHelper;
	
	@Inject
	private BestellungResource bestellungResource;

	@Inject
	private BestellungService bs;

	@Inject
	private KundeService ks;

	@GET
	@Produces({ TEXT_PLAIN, APPLICATION_JSON })
	@Path("version")
	public String getVersion() {
		return VERSION;
	}

	// Alle Kunden ausgeben
	@GET
	public Response findAllKunden() {
		// Aufruf der Mock zur Erzeugung der Kunden
		final List<AbstractKunde> all = ks.findAllKunden(FetchType.NUR_KUNDE,
				OrderType.KEINE);
		// Plausibilitäsprüfung
		if (all.isEmpty())
			throw new NotFoundException(
					"Es konnten keine Kunden gefunden werden!");
		// Erstellen der Links für die jeweilign Kunden
		for (AbstractKunde kunde : all) {
			setStructuralLinks(kunde, uriInfo);
		}
		return Response.ok(new GenericEntity<List<AbstractKunde>>(all) {})
				.links(getTransitionalLinksKunden(all, uriInfo))
				.build();
	}

	// Kunde mit ID finden
	@GET
	@Path("{" + KUNDEN_ID_PATH_PARAM + ":[1-9][0-9]*}")
	public Response findKundeById(@PathParam(KUNDEN_ID_PATH_PARAM) Long id) {
		// TODO Anwendungskern statt Mock
		final AbstractKunde kunde = ks.findKundeById(id, FetchType.NUR_KUNDE);
		if (kunde == null) {
			throw new NotFoundException("Kein Kunde mit der ID " + id
					+ " gefunden.");
		}
		setStructuralLinks(kunde, uriInfo);
		return Response.ok(kunde).links(getTransitionalLinks(kunde, uriInfo))
				.build();
	}

	@GET
	public Response findKunden(
			@QueryParam(KUNDEN_NACHNAME_QUERY_PARAM) @Pattern(regexp = AbstractKunde.NACHNAME_PATTERN, message = "{kunde.nachname.pattern}") String nachname,
			@QueryParam(KUNDEN_PLZ_QUERY_PARAM) @Pattern(regexp = "\\d{5}", message = "{adresse.plz}") String plz,
			@QueryParam(KUNDEN_EMAIL_QUERY_PARAM) @Email(message = "{kunde.email}") String email) {
		List<? extends AbstractKunde> kunden = null;
		AbstractKunde kunde = null;
		// TODO Mehrere Query-Parameter koennen angegeben sein
		if (!Strings.isNullOrEmpty(nachname)) {
			kunden = ks.findKundenByNachname(nachname, FetchType.NUR_KUNDE);
		} else if (!Strings.isNullOrEmpty(plz)) {
			kunden = ks.findKundenByPLZ(plz);
		} else if (!Strings.isNullOrEmpty(email)) {
			kunde = ks.findKundeByEmail(email);
		} else {
			kunden = ks.findAllKunden(FetchType.NUR_KUNDE, OrderType.ID);
		}

		Object entity = null;
		Link[] links = null;
		if (kunden != null) {
			for (AbstractKunde k : kunden) {
				setStructuralLinks(k, uriInfo);
			}
			// FIXME JDK 8 hat Lambda-Ausdruecke
			// kunden.parallelStream()
			// .forEach(k -> setStructuralLinks(k, uriInfo));
			entity = new GenericEntity<List<? extends AbstractKunde>>(kunden) {
			};
			links = getTransitionalLinksKunden(kunden, uriInfo);
		} else if (kunde != null) {
			entity = kunde;
			links = getTransitionalLinks(kunde, uriInfo);
		}

		return Response.ok(entity).links(links).build();
	}
	
	/**
	 * IDs mit gleichem Praefix suchen
	 * @param idPrefix Der gemeinsame Praefix
	 * @return Collection der IDs mit gleichem Praefix
	 */
	@GET
	@Path("/prefix/id/{id:[1-9][0-9]*}")
	public Collection<Long> findIdsByPrefix(@PathParam("id") String idPrefix) {
		final Collection<Long> ids = ks.findIdsByPrefix(idPrefix);
		return ids;
	}
	
	/**
	 * Nachnamen mit gleichem Praefix suchen
	 * @param nachnamePrefix Der gemeinsame Praefix
	 * @return Collection der Nachnamen mit gleichem Praefix
	 */
	@GET
	@Path("/prefix/nachname/{nachname}")
	@Produces({ APPLICATION_JSON, TEXT_PLAIN + ";qs=0.75" })
	public Collection<String> findNachnamenByPrefix(@PathParam("nachname") String nachnamePrefix) {
		final Collection<String> nachnamen = ks.findNachnamenByPrefix(nachnamePrefix);
		return nachnamen;
	}

	// Kunden bei Nachname finden
	@GET
	public Response findKundenByNachname(
			@QueryParam(KUNDEN_NACHNAME_QUERY_PARAM) String nachname) {
		List<? extends AbstractKunde> kunden = null;
		if (nachname != null) {
			// TODO Anwendungskern statt Mock
			kunden = ks.findKundenByNachname(nachname, FetchType.NUR_KUNDE);
			if (kunden.isEmpty()) {
				throw new NotFoundException("Kein Kunde mit Nachname "
						+ nachname + " gefunden.");
			}
		} else {
			// TODO Anwendungskern statt Mock
			kunden = Mock.findAllKunden();
			if (kunden.isEmpty()) {
				throw new NotFoundException("Keine Kunden vorhanden.");
			}
		}

		for (AbstractKunde k : kunden) {
			setStructuralLinks(k, uriInfo);
		}

		return Response
				.ok(new GenericEntity<List<? extends AbstractKunde>>(kunden) {
				}).links(getTransitionalLinksKunden(kunden, uriInfo)).build();
	}

	@GET
	@Path("{" + KUNDEN_ID_PATH_PARAM + ":[1-9][0-9]*}/bestellungen")
	public Response findBestellungenByKundeId(
			@PathParam(KUNDEN_ID_PATH_PARAM) Long kundeId) {
		// TODO Anwendungskern statt Mock
		final AbstractKunde kunde = ks.findKundeById(kundeId, FetchType.NUR_KUNDE);
		final List<Bestellung> bestellungen = Mock
				.findBestellungenByKunde(kunde);
		if (bestellungen.isEmpty()) {
			throw new NotFoundException("Zur ID " + kundeId
					+ " wurden keine Bestellungen gefunden");
		}

		// URIs innerhalb der gefundenen Bestellungen anpassen
		for (Bestellung bestellung : bestellungen) {
			bestellungResource.setStructuralLinks(bestellung, uriInfo);
		}

		// Rückgabe einer Generische Liste von Bestellungen
		return Response
				.ok(new GenericEntity<List<Bestellung>>(bestellungen) {
				})
				.links(getTransitionalLinksBestellungen(bestellungen, kunde,
						uriInfo)).build();
	}
	
	/**
	 * Bestellung-IDs zu einem Kunden suchen
	 * @param kundeId ID des Kunden
	 * @return Liste der Bestellung-IDs
	 */
	@GET
	@Path("{id:[1-9][0-9]*}/bestellungenIds")
	@Produces({ APPLICATION_JSON, TEXT_PLAIN + ";qs=0.75", APPLICATION_XML + ";qs=0.5" })
	public Response findBestellungenIdsByKundeId(@PathParam("id") Long kundeId) {
		final AbstractKunde kunde = ks.findKundeById(kundeId, FetchType.MIT_BESTELLUNGEN);

		final Collection<Bestellung> bestellungen = bs.findBestellungenByKunde(kunde);		
		final int anzahl = bestellungen.size();
		final Collection<Long> bestellungenIds = new ArrayList<>(anzahl);
		for (Bestellung bestellung : bestellungen) {
			bestellungenIds.add(bestellung.getId());
		}
		// FIXME JDK 8 hat Lambda-Ausdruecke
		//bestellungen.parallelStream()
		//            .map(Bestellung::getId)
		//            .forEach(id -> bestellungenIds.add(id));
		
		return Response.ok(new GenericEntity<Collection<Long>>(bestellungenIds) {})
				       .build();
	}

	@POST
	@Consumes({APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
	@Produces
	public Response createKunde(@Valid AbstractKunde kunde) {
		kunde.setId(KEINE_ID);
		
		final Adresse adresse = kunde.getAdresse();
		if (adresse != null) {
			adresse.setKunde(kunde);
		}
		
		kunde = ks.createKunde(kunde);
		LOGGER.tracef("Kunde: %s", kunde);
		
		return Response.created(getUriKunde(kunde, uriInfo))
				       .build();
	}

	@PUT
	@Consumes({ APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
	@Produces
	public void updateKunde(@Valid AbstractKunde kunde) {
		// Vorhandenen Kunden ermitteln
		final AbstractKunde origKunde = ks.findKundeById(kunde.getId(), FetchType.NUR_KUNDE);
		LOGGER.tracef("Kunde vorher: %s", origKunde);
	
		// Daten des vorhandenen Kunden ueberschreiben
		origKunde.setValues(kunde);
		LOGGER.tracef("Kunde nachher: %s", origKunde);
		
		// Update durchfuehren
		ks.updateKunde(origKunde);
	}

	@Path("{id:[0-9]+}")
	@DELETE
	@Produces
	public void deleteKunde(@PathParam("id") Long kundeId) {
		final AbstractKunde kunde = ks.findKundeById(kundeId, FetchType.NUR_KUNDE);
		ks.deleteKunde(kunde);
	}

	private Link[] getTransitionalLinksBestellungen(
			List<Bestellung> bestellungen, AbstractKunde kunde, UriInfo uriInfo) {
		if (bestellungen == null || bestellungen.isEmpty()) {
			return new Link[0];
		}

		final Link self = Link.fromUri(getUriBestellungen(kunde, uriInfo))
				.rel(SELF_LINK).build();

		final Link first = Link
				.fromUri(
						bestellungResource.getUriBestellung(
								bestellungen.get(0), uriInfo)).rel(FIRST_LINK)
				.build();
		final int lastPos = bestellungen.size() - 1;

		final Link last = Link
				.fromUri(
						bestellungResource.getUriBestellung(
								bestellungen.get(lastPos), uriInfo))
				.rel(LAST_LINK).build();

		return new Link[] { self, first, last };
	}

	// Bestellungslink setzen
	private void setStructuralLinks(AbstractKunde kunde, UriInfo uriInfo) {
		// URI fuer Bestellungen setzen
		final URI uri = getUriBestellungen(kunde, uriInfo);
		kunde.setBestellungenURI(uri);
	}

	// BestellungURI erzeugen
	private URI getUriBestellungen(AbstractKunde kunde, UriInfo uriInfo) {
		// return URI.create("http://localhost:8080/shop/rest/");
		return uriHelper.getUri(KundenResource.class,
				"findBestellungenByKundeId", kunde.getId(), uriInfo);
	}

	public URI getUriKunde(AbstractKunde kunde, UriInfo uriInfo) {
		return uriHelper.getUri(KundenResource.class, "findKundeById",
				kunde.getId(), uriInfo);
	}

	// VerwaltungsURIs erzeugen
	private Link[] getTransitionalLinks(AbstractKunde kunde, UriInfo uriInfo) {
		final Link self = Link.fromUri(getUriKunde(kunde, uriInfo))
				.rel(SELF_LINK).build();
		System.out.println("Self gesetzt");
		final Link add = Link
				.fromUri(uriHelper.getUri(KundenResource.class, uriInfo))
				.rel(ADD_LINK).build();
		System.out.println("Add gesetzt");
		final Link update = Link
				.fromUri(uriHelper.getUri(KundenResource.class, uriInfo))
				.rel(UPDATE_LINK).build();
		System.out.println("Update gesetzt");

		final Link remove = Link
				.fromUri(
						uriHelper.getUri(KundenResource.class, "deleteKunde",
								kunde.getId(), uriInfo)).rel(REMOVE_LINK)
				.build();
		System.out.println("Delete gesetzt");
		return new Link[] { self, add, update, remove };
	}

	private Link[] getTransitionalLinksKunden(
			List<? extends AbstractKunde> kunden, UriInfo uriInfo) {
		if (kunden == null || kunden.isEmpty()) {
			return null;
		}

		final Link first = Link.fromUri(getUriKunde(kunden.get(0), uriInfo))
				.rel(FIRST_LINK).build();
		final int lastPos = kunden.size() - 1;
		final Link last = Link
				.fromUri(getUriKunde(kunden.get(lastPos), uriInfo))
				.rel(LAST_LINK).build();

		return new Link[] { first, last };
	}

}
