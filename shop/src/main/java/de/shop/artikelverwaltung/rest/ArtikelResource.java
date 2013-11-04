package de.shop.artikelverwaltung.rest;

import static de.shop.util.Constants.ADD_LINK;
import static de.shop.util.Constants.FIRST_LINK;
import static de.shop.util.Constants.LAST_LINK;
import static de.shop.util.Constants.REMOVE_LINK;
import static de.shop.util.Constants.SELF_LINK;
//import static de.shop.util.Constants.LIST_LINK;
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

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.util.Mock;
import de.shop.util.rest.NotFoundException;
import de.shop.util.rest.UriHelper;

/*
 * ArtikelResource Klasse
 * enthaelt die RestServices fuer die Artikelverwaltung/Domain
 * Zugriff auf den Mock spaeter Anwendungslogik
 */
@Path("/artikel")
@Produces({ APPLICATION_JSON, APPLICATION_XML + ";qs=0.75", TEXT_XML + ";qs=0.5" })
@Consumes
public class ArtikelResource {
	public static final String ARTIKEL_ID_PATH_PARAM = "artikelId";
	public static final String ARTIKEL_BEZEICHNUNG_QUERY_PARAM = "bezeichnung";
	
	//TODO Erweitern findAllArtikel, findArtikelByBezeichnung
	@Context
	private UriInfo uriInfo;
	
	@Inject
	private UriHelper uriHelper;
	
	//Aktuelle Version ausgeben
	@GET
	@Produces({ TEXT_PLAIN, APPLICATION_JSON })
	@Path("version")
	public String getVersion() {
		return "1.0";
	}
	
	//Artikel mit ID finden
	@GET
	@Path("{" + ARTIKEL_ID_PATH_PARAM + ":[1-9][0-9]*}")
	public Response findArtikelById(@PathParam(ARTIKEL_ID_PATH_PARAM) Long id) {
		// TODO Anwendungskern statt Mock, Verwendung von Locale
		final Artikel artikel = Mock.findArtikelById(id);
		if (artikel == null) {
			throw new NotFoundException("Der Artikel mit der ID:" + id + " konnte nicht gefunden werden.");
		}
		//System.out.println("Artikel ist nicht null");
		//System.out.println(artikel.getId() + ", " + artikel.getBezeichnung() + ", " + artikel.getPreis());
		return Response.ok(artikel)
                       .links(getTransitionalLinks(artikel, uriInfo))
                       .build();
	}
	
	//Artikel mit Bezeichnung finden
	@GET
	public Response findArtikelByBezeichnung(@QueryParam(ARTIKEL_BEZEICHNUNG_QUERY_PARAM) String bezeichnung) {
		List<Artikel> artikelliste = null;
		if (bezeichnung != null) {
			System.out.println("RICHTIG");
			// TODO Anwendungskern statt Mock, Verwendung von Locale
			artikelliste = Mock.findArtikelByBezeichnung(bezeichnung);
			if (artikelliste.isEmpty()) {
				throw new NotFoundException("Kein Artikel mit Bezeichnung " + bezeichnung + " gefunden.");
			}
		}
		else {
			// TODO Anwendungskern statt Mock, Verwendung von Locale
			artikelliste = Mock.findAllArtikel();
			if (artikelliste.isEmpty()) {
				throw new NotFoundException("Keine Artikel vorhanden.");
			}
		}

		return Response.ok(new GenericEntity<List<Artikel>>(artikelliste) { })
				.links(getTransitionalLinksArtikelListe(artikelliste, uriInfo))
				.build();
	}
	
	//Alle Artikel auflisten
	@GET
	public Response findAllArtikel() {
		// TODO Anwendungskern statt Mock, Verwendung von Locale
		final List<Artikel> artikelliste = Mock.findAllArtikel();
		if (artikelliste.isEmpty()) {
			throw new NotFoundException("Es konnte keine Artikel gefunden werden.");
		}

		return Response.ok(new GenericEntity<List<Artikel>>(artikelliste) { })
                       //.links(getTransitionalLinksArtikelListe(artikel_liste, uriInfo))
                       .build();
	}
	
	//Artikel erstellen
	@POST
	@Consumes({APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
	@Produces
	public Response createArtikel(Artikel artikel) {
		// TODO Anwendungskern statt Mock, Verwendung von Locale
		artikel = Mock.createArtikel(artikel);
		return Response.created(getUriArtikel(artikel, uriInfo))
			           .build();
	}
	
	//Artikel aendern
	@PUT
	@Consumes({APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
	@Produces
	public void updateArtikel(Artikel artikel) {
		// TODO Anwendungskern statt Mock, Verwendung von Locale
		Mock.updateArtikel(artikel);
	}
	
	//Artikel loeschen bzw. als nicht mehr aktiv makieren
	@DELETE
	@Path("{id:[1-9][0-9]*}")
	@Produces
	public void deleteArtikel(@PathParam("id") Long artikelId) {
		// TODO Anwendungskern statt Mock, Verwendung von Locale
		Mock.deleteArtikel(artikelId);
	}
	
	//Artikel URI erzeugen
	public URI getUriArtikel(Artikel artikel, UriInfo uriInfo) {
		return uriHelper.getUri(ArtikelResource.class, "findArtikelById", artikel.getId(), uriInfo);
	}
	
	//TODO Hinzufügen des LIST_LINKS
	//Verwaltungs URIs erzeugen
	public Link[] getTransitionalLinks(Artikel artikel, UriInfo uriInfo) {
		final Link self = Link.fromUri(getUriArtikel(artikel, uriInfo))
	                          .rel(SELF_LINK)
	                          .build();
		
		/*final Link list = Link.fromUri(uriHelper.getUri(ArtikelResource.class,
		 * "findAllArtikel", artikel.getId(), uriInfo))
							.rel(LIST_LINK)
							.build();*/
		
		final Link add = Link.fromUri(uriHelper.getUri(ArtikelResource.class, uriInfo))
                             .rel(ADD_LINK)
                             .build();

		final Link update = Link.fromUri(uriHelper.getUri(ArtikelResource.class, uriInfo))
                                .rel(UPDATE_LINK)
                                .build();

		final Link remove = Link.fromUri(uriHelper.getUri(ArtikelResource.class,
				"deleteArtikel", artikel.getId(), uriInfo))
                                .rel(REMOVE_LINK)
                                .build();
		
		return new Link[] {self/*, list*/, add, update, remove };
	}
	
	public Link[] getTransitionalLinksArtikelListe(List<Artikel> artikelliste, UriInfo uriInfo) {
		if (artikelliste == null || artikelliste.isEmpty()) {
			return null;
		}

		final Link first = Link.fromUri(getUriArtikel(artikelliste.get(0), uriInfo))
				.rel(FIRST_LINK)
				.build();
		final int lastPos = artikelliste.size() - 1;
		final Link last = Link.fromUri(getUriArtikel(artikelliste.get(lastPos), uriInfo))
				.rel(LAST_LINK)
				.build();

		return new Link[] {first, last };
	}
}
