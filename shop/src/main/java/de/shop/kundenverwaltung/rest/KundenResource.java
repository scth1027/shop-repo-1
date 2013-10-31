package de.shop.kundenverwaltung.rest;

import static de.shop.util.Constants.ADD_LINK;
import static de.shop.util.Constants.FIRST_LINK;
import static de.shop.util.Constants.LAST_LINK;
import static de.shop.util.Constants.REMOVE_LINK;
import static de.shop.util.Constants.SELF_LINK;
import static de.shop.util.Constants.UPDATE_LINK;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.MediaType.TEXT_XML;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
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

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.Mock;
import de.shop.util.rest.UriHelper;
import de.shop.util.rest.NotFoundException;

/*
 * KundenResource Klasse
 * enthaelt die RestServices fuer die Kundenverwaltung/Domain
 * Zugriff auf den Mock später Anwendungslogik
 */
@Path("/kunden")
@Produces({ APPLICATION_JSON, APPLICATION_XML + ";qs=0.75", TEXT_XML + ";qs=0.5" })
@Consumes
public class KundenResource {
	public static final String KUNDEN_ID_PATH_PARAM = "kundeId";
	public static final String KUNDEN_NACHNAME_QUERY_PARAM = "nachname";

	@Context
	private UriInfo uriInfo;

	@Inject
	private UriHelper uriHelper;

	@GET
	@Produces({ TEXT_PLAIN, APPLICATION_JSON })
	@Path("version")
	public String getVersion() {
		return "1.0";
	}

	// Kunde mit ID finden
	@GET
	@Path("{" + KUNDEN_ID_PATH_PARAM + ":[1-9][0-9]*}")
	public Response findKundeById(@PathParam(KUNDEN_ID_PATH_PARAM) Long id) {
		// TODO Anwendungskern statt Mock, Verwendung von Locale
		final Kunde kunde = Mock.findKundeById(id);
		if (kunde == null) {
			throw new NotFoundException("Kein Kunde mit der ID " + id + " gefunden.");
		}
		System.out.println("Kunde ist nicht null");
		setStructuralLinks(kunde, uriInfo);
		System.out.println("Structural Links überstanden");
		return Response.ok(kunde)
				.links(setTransitionalLinks(kunde, uriInfo))
				.build();
	}
	public URI getUriKunde(Kunde kunde, UriInfo uriInfo) {
		return uriHelper.getUri(KundenResource.class, "findKundeById", kunde.getId(), uriInfo);
	}

	// Kunden bei Nachname finden
	@GET
	public Response findKundenByNachname(@QueryParam(KUNDEN_NACHNAME_QUERY_PARAM) String nachname) {
		List<? extends Kunde> kunden = null;
		if (nachname != null) {
			// TODO Anwendungskern statt Mock, Verwendung von Locale
			kunden = Mock.findKundenByNachname(nachname);
			if (kunden.isEmpty()) {
				throw new NotFoundException("Kein Kunde mit Nachname " + nachname + " gefunden.");
			}
		}
		else {
			// TODO Anwendungskern statt Mock, Verwendung von Locale
			kunden = Mock.findAllKunden();
			if (kunden.isEmpty()) {
				throw new NotFoundException("Keine Kunden vorhanden.");
			}
		}

		for (Kunde k : kunden) {
			setStructuralLinks(k, uriInfo);
		}

		return Response.ok(new GenericEntity<List<? extends Kunde>>(kunden){})
				.links(setTransitionalLinksKunden(kunden, uriInfo))
				.build();
	}

	/*
	@GET
	@Path("{id:[1-9][0-9]*}/bestellungen")
	public Response findBestellungenByKundeId(@PathParam("id") Long kundeId) {
		// TODO Anwendungskern statt Mock, Verwendung von Locale
		final Kunde kunde = Mock.findKundeById(kundeId);
		final List<Bestellung> bestellungen = Mock.findBestellungenByKunde(kunde);
		if (bestellungen.isEmpty()) {
			throw new NotFoundException("Zur ID " + kundeId + " wurden keine Bestellungen gefunden");
		}

		// URIs innerhalb der gefundenen Bestellungen anpassen
		for (Bestellung bestellung : bestellungen) {
			bestellungResource.setStructuralLinks(bestellung, uriInfo);
		}

		return Response.ok(new GenericEntity<List<Bestellung>>(bestellungen){})
                       .links(getTransitionalLinksBestellungen(bestellungen, kunde, uriInfo))
                       .build();
	}

	 */

	@POST
	@Consumes({APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
	@Produces
	public Response createKunde(Kunde kunde) {
		// TODO Anwendungskern statt Mock, Verwendung von Locale
		System.out.println("Kunde angekommen im Service");
		kunde = Mock.createKunde(kunde);
		System.out.println("Kunde ist aus der Mock zurück");
		return Response.created(getKundenURI(kunde, uriInfo))
				.build();
	}

	@PUT
	@Consumes({APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
	@Produces
	public void updateKunde(Kunde kunde) {
		// TODO Anwendungskern statt Mock, Verwendung von Locale
		Mock.updateKunde(kunde);
	}
	/*
	@DELETE
	@Path("{id:[1-9][0-9]*}")
	@Produces
	public void deleteKunde(@PathParam("id") Long kundeId) {
		// TODO Anwendungskern statt Mock, Verwendung von Locale
		Mock.deleteKunde(kundeId);
	}

	private Link[] getTransitionalLinksBestellungen(List<Bestellung> bestellungen, Kunde kunde, UriInfo uriInfo) {
		if (bestellungen == null || bestellungen.isEmpty()) {
			return new Link[0];
		}

		final Link self = Link.fromUri(getUriBestellungen(kunde, uriInfo))
                              .rel(SELF_LINK)
                              .build();

		final Link first = Link.fromUri(bestellungResource.getUriBestellung(bestellungen.get(0), uriInfo))
	                           .rel(FIRST_LINK)
	                           .build();
		final int lastPos = bestellungen.size() - 1;

		final Link last = Link.fromUri(bestellungResource.getUriBestellung(bestellungen.get(lastPos), uriInfo))
                              .rel(LAST_LINK)
                              .build();

		return new Link[] { self, first, last };
	}
	 */

	// Bestellungslink setzen
	private void setStructuralLinks(Kunde kunde, UriInfo uriInfo) {
		// URI fuer Bestellungen setzen
		System.out.println("Kunde angekommen");
		final URI uri = getUriBestellungen(kunde, uriInfo);
		System.out.println("Kein Fehler bei der Erzeugung der URI");
		kunde.setBestellungenURI(uri);
	}

	// BestellungURI erzeugen
	private URI getUriBestellungen(Kunde kunde, UriInfo uriInfo) {
		return URI.create("http://localhost:8080/shop/rest/");
		// TODO: URI für Bestellungen wird später erstellt sobald BestellungenResource bereit steht
		//return uriHelper.getUri(KundenResource.class, "findBestellungenByKundeId", kunde.getId(), uriInfo);
	}

	// KundenURI erzeugen
	private URI getKundenURI(Kunde kunde, UriInfo uriInfo) {
		return uriHelper.getUri(KundenResource.class, "findKundeById", kunde.getId(), uriInfo);
	}

	// VerwaltungsURIs erzeugen
	private Link[] setTransitionalLinks(Kunde kunde, UriInfo uriInfo) {
		final Link self = Link.fromUri(getKundenURI(kunde, uriInfo))
				.rel(SELF_LINK)
				.build();
		System.out.println("Self gesetzt");
		final Link add = Link.fromUri(uriHelper.getUri(KundenResource.class, uriInfo))
				.rel(ADD_LINK)
				.build();
		System.out.println("Add gesetzt");
		final Link update = Link.fromUri(uriHelper.getUri(KundenResource.class, uriInfo))
				.rel(UPDATE_LINK)
				.build();
		System.out.println("Update gesetzt");
		/*
		final Link remove = Link.fromUri(uriHelper.getUri(KundenResource.class, "deleteKunde", kunde.getId(), uriInfo))
				.rel(REMOVE_LINK)
				.build();
		System.out.println("Delete gesetzt");
		*/
		return new Link[] { self, add, update };
	}

	private Link[] setTransitionalLinksKunden(List<? extends Kunde> kunden, UriInfo uriInfo) {
		if (kunden == null || kunden.isEmpty()) {
			return null;
		}

		final Link first = Link.fromUri(getKundenURI(kunden.get(0), uriInfo))
				.rel(FIRST_LINK)
				.build();
		final int lastPos = kunden.size() - 1;
		final Link last = Link.fromUri(getKundenURI(kunden.get(lastPos), uriInfo))
				.rel(LAST_LINK)
				.build();

		return new Link[] { first, last };
	}

}
