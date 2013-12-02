package de.shop.lieferantenverwaltung.rest;

import static de.shop.util.Constants.ADD_LINK;
import static de.shop.util.Constants.FIRST_LINK;
import static de.shop.util.Constants.LAST_LINK;
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

import de.shop.lieferantenverwaltung.domain.Lieferant;
import de.shop.util.Mock;
import de.shop.util.rest.UriHelper;

/*
 * LieferantenResource Klasse
 * enthaelt die RestServices fuer die Lieferantenverwaltung/Domain
 * Zugriff auf den Mock später Anwendungslogik
 */
@Path("/lieferanten")
@Produces({ APPLICATION_JSON, APPLICATION_XML + ";qs=0.75", TEXT_XML + ";qs=0.5" })
@Consumes
public class LieferantenResource {
	public static final String LIEFERANTEN_ID_PATH_PARAM = "lieferantId";
	public static final String LIEFERANTEN_FIRMA_QUERY_PARAM = "firma";

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

	// Lieferant mit ID finden
	@GET
	@Path("{" + LIEFERANTEN_ID_PATH_PARAM + ":[1-9][0-9]*}")
	public Response findLieferantById(@PathParam(LIEFERANTEN_ID_PATH_PARAM) Long id) {
		// TODO Anwendungskern statt Mock, Verwendung von Locale
		final Lieferant lieferant = Mock.findLieferantById(id);
		if (lieferant == null) {
			throw new NotFoundException("Kein Lieferant mit der ID " + id + " gefunden.");
		}
		System.out.println("Lieferant ist nicht null");
		setStructuralLinks(lieferant, uriInfo);
		System.out.println("Structural Links überstanden");
		return Response.ok(lieferant)
				.links(setTransitionalLinks(lieferant, uriInfo))
				.build();
	}

	// Lieferant mit Firma finden
	@GET
	public Response findLieferantenByFirma(@QueryParam(LIEFERANTEN_FIRMA_QUERY_PARAM) String firma) {
		List<? extends Lieferant> lieferanten = null;
		if (firma != null) {
			// TODO Anwendungskern statt Mock, Verwendung von Locale
			lieferanten = Mock.findLieferantenByFirma(firma);
			if (lieferanten.isEmpty()) {
				throw new NotFoundException("Kein Lieferant mit Firma " + firma + " gefunden.");
			}
		}
		else {
			// TODO Anwendungskern statt Mock, Verwendung von Locale
			lieferanten = Mock.findAllLieferanten();
			if (lieferanten.isEmpty()) {
				throw new NotFoundException("Keine Lieferanten vorhanden.");
			}
		}

		for (Lieferant l : lieferanten) {
			setStructuralLinks(l, uriInfo);
		}

		return Response.ok(new GenericEntity<List<? extends Lieferant>>(lieferanten){})
				.links(setTransitionalLinksLieferanten(lieferanten, uriInfo))
				.build();
	}

	/*
	@GET
	@Path("{id:[1-9][0-9]*}/bestellungen")
	public Response findBestellungenByLieferantId(@PathParam("id") Long lieferantenId) {
		// TODO Anwendungskern statt Mock, Verwendung von Locale
		final Lieferant lieferanten = Mock.findLieferantById(lieferantenId);
		final List<Bestellung> bestellungen = Mock.findBestellungenByLieferant(lieferanten);
		if (bestellungen.isEmpty()) {
			throw new NotFoundException("Zur ID " + lieferantenId + " wurden keine Bestellungen gefunden");
		}

		// URIs innerhalb der gefundenen Bestellungen anpassen
		for (Bestellung bestellung : bestellungen) {
			bestellungResource.setStructuralLinks(bestellung, uriInfo);
		}

		return Response.ok(new GenericEntity<List<Bestellung>>(bestellungen){})
                       .links(getTransitionalLinksBestellungen(bestellungen, lieferanten, uriInfo))
                       .build();
	}

	 */

	@POST
	@Consumes({APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
	@Produces
	public Response createLieferanten(Lieferant lieferant) {
		// TODO Anwendungskern statt Mock, Verwendung von Locale
		System.out.println("Lieferant angekommen im Service");
		lieferant = Mock.createLieferant(lieferant);
		System.out.println("Lieferant ist aus der Mock zurück");
		return Response.created(getLieferantenURI(lieferant, uriInfo))
				.build();
	}

	@PUT
	@Consumes({APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
	@Produces
	public void updateLieferant(Lieferant lieferant) {
		// TODO Anwendungskern statt Mock, Verwendung von Locale
		Mock.updateLieferant(lieferant);
	}
	/*
	@DELETE
	@Path("{id:[1-9][0-9]*}")
	@Produces
	public void deleteLieferant(@PathParam("id") Long lieferantenId) {
		// TODO Anwendungskern statt Mock, Verwendung von Locale
		Mock.deleteLieferant(lieferantenId);
	}

	private Link[] getTransitionalLinksBestellungen(List<Bestellung> bestellungen, Lieferant lieferanten, UriInfo uriInfo) {
		if (bestellungen == null || bestellungen.isEmpty()) {
			return new Link[0];
		}

		final Link self = Link.fromUri(getUriBestellungen(lieferanten, uriInfo))
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
	private void setStructuralLinks(Lieferant lieferant, UriInfo uriInfo) {
		// URI fuer Bestellungen setzen
		System.out.println("Lieferant angekommen");
		final URI uri = getUriBestellungen(lieferant, uriInfo);
		System.out.println("Kein Fehler bei der Erzeugung der URI");
		lieferant.setBestellungenURI(uri);
	}

	// BestellungURI erzeugen
	private URI getUriBestellungen(Lieferant lieferant, UriInfo uriInfo) {
		return URI.create("http://localhost:8080/shop/rest/");
		// TODO: URI für Bestellungen wird später erstellt sobald BestellungenResource bereit steht
		//return uriHelper.getUri(LieferantenResource.class, "findBestellungenByLieferantId", lieferanten.getId(), uriInfo);
	}

	// LieferantenURI erzeugen
	private URI getLieferantenURI(Lieferant lieferant, UriInfo uriInfo) {
		return uriHelper.getUri(LieferantenResource.class, "findLieferantById", lieferant.getId(), uriInfo);
	}

	// VerwaltungsURIs erzeugen
	private Link[] setTransitionalLinks(Lieferant lieferant, UriInfo uriInfo) {
		final Link self = Link.fromUri(getLieferantenURI(lieferant, uriInfo))
				.rel(SELF_LINK)
				.build();
		System.out.println("Self gesetzt");
		final Link add = Link.fromUri(uriHelper.getUri(LieferantenResource.class, uriInfo))
				.rel(ADD_LINK)
				.build();
		System.out.println("Add gesetzt");
		final Link update = Link.fromUri(uriHelper.getUri(LieferantenResource.class, uriInfo))
				.rel(UPDATE_LINK)
				.build();
		System.out.println("Update gesetzt");
		/*
		final Link remove = Link.fromUri(uriHelper.getUri(LieferantenResource.class, "deleteLieferant", lieferanten.getId(), uriInfo))
				.rel(REMOVE_LINK)
				.build();
		System.out.println("Delete gesetzt");
		 */
		return new Link[] { self, add, update };
	}

	private Link[] setTransitionalLinksLieferanten(List<? extends Lieferant> lieferanten, UriInfo uriInfo) {
		if (lieferanten == null || lieferanten.isEmpty()) {
			return null;
		}

		final Link first = Link.fromUri(getLieferantenURI(lieferanten.get(0), uriInfo))
				.rel(FIRST_LINK)
				.build();
		final int lastPos = lieferanten.size() - 1;
		final Link last = Link.fromUri(getLieferantenURI(lieferanten.get(lastPos), uriInfo))
				.rel(LAST_LINK)
				.build();

		return new Link[] { first, last };
	}

}
