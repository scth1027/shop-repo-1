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

import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
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

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.util.Mock;
import de.shop.util.rest.UriHelper;

/*
 * ArtikelResource Klasse
 * enthaelt die RestServices fuer die Artikelverwaltung/Domain
 * Zugriff auf Anwendungslogik
 */

@Path("/artikel")
@Produces({ APPLICATION_JSON, APPLICATION_XML + ";qs=0.75", TEXT_XML + ";qs=0.5" })
@Consumes
@RequestScoped
public class ArtikelResource {
	public static final String ARTIKEL_ID_PATH_PARAM = "artikelId";
	public static final String ARTIKEL_BEZEICHNUNG_QUERY_PARAM = "bezeichnung";
	
	@Inject
	private ArtikelService as;
	
	@Context
	private UriInfo uriInfo;
	
	@Inject
	private UriHelper uriHelper;
	
	//Aktuelle Version ausgeben
	@GET
	@Produces({ TEXT_PLAIN, APPLICATION_JSON })
	@Path("version")
	public String getVersion() {
		
		return as.asd();
	}
	
	//Artikel mit ID finden
	@GET
	@Path("{" + ARTIKEL_ID_PATH_PARAM + ":[1-9][0-9]*}")
	public Response findArtikelById(@PathParam(ARTIKEL_ID_PATH_PARAM) Long id) {
		System.out.println("Marker1");
		final Artikel artikel = as.findArtikelById(id);
//		final Artikel artikel = Mock.findArtikelById(id);

		System.out.println("Marker2: " + artikel);
		//getTransitionalLinks(artikel, uriInfo);
		System.out.println("Marker3: " + artikel);
		
		return Response.ok(artikel)
                       //.links(getTransitionalLinks(artikel, uriInfo))
                       .build();
	}
	
	//Artikel mit Bezeichnung finden
	@GET
	public Response findArtikelByBezeichnung(@QueryParam(ARTIKEL_BEZEICHNUNG_QUERY_PARAM) String bezeichnung) {
		List<Artikel> artikelliste = null;
		if (bezeichnung != null) {
			System.out.println("RICHTIG");
			
			artikelliste = as.findArtikelByBezeichnung(bezeichnung);
			if (artikelliste.isEmpty()) {
				throw new NotFoundException("Kein Artikel mit Bezeichnung " + bezeichnung + " gefunden.");
			}
		}
		else {
			artikelliste = as.findAllArtikel();
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
		final List<Artikel> artikelliste = as.findAllArtikel();
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
		artikel = as.createArtikel(artikel);
		return Response.created(getUriArtikel(artikel, uriInfo))
			           .build();
	}
	
	//Artikel aendern
	@PUT
	@Consumes({APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
	@Produces
	public void updateArtikel(Artikel artikel) {
		as.updateArtikel(artikel);
	}
	
	//Artikel loeschen bzw. als nicht mehr aktiv makieren
	@DELETE
	@Path("{id:[1-9][0-9]*}")
	@Produces
	public void deleteArtikel(@PathParam("id") Long artikelId) {
		as.deleteArtikel(artikelId);
	}
	
	//Artikel URI erzeugen
	public URI getUriArtikel(Artikel artikel, UriInfo uriInfo) {
		System.out.println("Marker 2.1");
		URI test = uriHelper.getUri(ArtikelResource.class, "findArtikelById", artikel.getId(), uriInfo);
		System.out.println("Marker 2.1");
		//return uriHelper.getUri(ArtikelResource.class, "findArtikelById", artikel.getId(), uriInfo);
		return test;
	}
	
	//TODO Hinzuf�gen des LIST_LINKS
	//Verwaltungs URIs erzeugen
	public Link[] getTransitionalLinks(Artikel artikel, UriInfo uriInfo) {
		final Link self = Link.fromUri(getUriArtikel(artikel, uriInfo))
	                          .rel(SELF_LINK)
	                          .build();
		System.out.println("Self gesetzt");
		/*final Link list = Link.fromUri(uriHelper.getUri(ArtikelResource.class,
		 * "findAllArtikel", artikel.getId(), uriInfo))
							.rel(LIST_LINK)
							.build();*/
		
		final Link add = Link.fromUri(uriHelper.getUri(ArtikelResource.class, uriInfo))
                             .rel(ADD_LINK)
                             .build();
		System.out.println("Add gesetzt");
		final Link update = Link.fromUri(uriHelper.getUri(ArtikelResource.class, uriInfo))
                                .rel(UPDATE_LINK)
                                .build();
		System.out.println("Update gesetzt");
		final Link remove = Link.fromUri(uriHelper.getUri(ArtikelResource.class,
				"deleteArtikel", artikel.getId(), uriInfo))
                                .rel(REMOVE_LINK)
                                .build();
		System.out.println("Delete gesetzt");
		return new Link[] {self/*, list*/, add, update, remove };
		
//		final Link self = Link.fromUri(getKundenURI(kunde, uriInfo))
//				.rel(SELF_LINK)
//				.build();
//		System.out.println("Self gesetzt");
//		final Link add = Link.fromUri(uriHelper.getUri(KundenResource.class, uriInfo))
//				.rel(ADD_LINK)
//				.build();
//		System.out.println("Add gesetzt");
//		final Link update = Link.fromUri(uriHelper.getUri(KundenResource.class, uriInfo))
//				.rel(UPDATE_LINK)
//				.build();
//		System.out.println("Update gesetzt");
//
//		final Link remove = Link.fromUri(uriHelper.getUri(KundenResource.class, "deleteKunde", kunde.getId(), uriInfo))
//				.rel(REMOVE_LINK)
//				.build();
//		System.out.println("Delete gesetzt");
//		return new Link[] { self, add, update, remove };
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
