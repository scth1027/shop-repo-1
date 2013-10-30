package de.shop.artikelverwaltung.rest;

import static de.shop.util.Constants.ADD_LINK;
import static de.shop.util.Constants.REMOVE_LINK;
import static de.shop.util.Constants.SELF_LINK;
import static de.shop.util.Constants.LIST_LINK;
import static de.shop.util.Constants.UPDATE_LINK;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.MediaType.TEXT_XML;

import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
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
 * Zugriff auf den Mock später Anwendungslogik
 */
@Path("/artikel")
@Produces({ APPLICATION_JSON, APPLICATION_XML + ";qs=0.75", TEXT_XML + ";qs=0.5" })
@Consumes
public class ArtikelResource {
	public static final String ARTIKEL_ID_PATH_PARAM = "artikelId";
	public static final String ARTIKEL_BEZEICHNUNG_QUERY_PARAM = "bezeichnung";
	//TODO: Erweitern um POST, PUT, DELETE, findByBezeichnung
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
		final Artikel artikel = Mock.findArtikelById(id);
		if(artikel == null) {
			throw new NotFoundException("Die angegebene ID:" + id + "liefert keinen Artikel");
		}
		//return Response.ok(artikel).links(getTransitionalLinks(artikel, uriInfo)).build();
		System.out.println("Artikel ist nicht null");
		//setStructuralLinks(artikel, uriInfo);
		//System.out.println("Structural Links gebildet");
		return Response.ok(artikel)
                       .links(getTransitionalLinks(artikel, uriInfo))
                       .build();
	}
	
	//TODO:Hinzufügen des LIST_LINKS
	//Verwaltungs URIs erzeugen
	public Link[] getTransitionalLinks(Artikel artikel, UriInfo uriInfo) {
		final Link self = Link.fromUri(getUriArtikel(artikel, uriInfo))
	                          .rel(SELF_LINK)
	                          .build();
		
		/*final Link list = Link.fromUri(uriHelper.getUri(ArtikelResource.class, "findAllArtikel", artikel.getId(), uriInfo))
							.rel(LIST_LINK)
							.build();*/
		
		final Link add = Link.fromUri(uriHelper.getUri(ArtikelResource.class, uriInfo))
                             .rel(ADD_LINK)
                             .build();

		final Link update = Link.fromUri(uriHelper.getUri(ArtikelResource.class, uriInfo))
                                .rel(UPDATE_LINK)
                                .build();

		final Link remove = Link.fromUri(uriHelper.getUri(ArtikelResource.class, "deleteArtikel", artikel.getId(), uriInfo))
                                .rel(REMOVE_LINK)
                                .build();
		
		return new Link[] { self/*, list*/, add, update, remove };
	}
	
	//Artikel URI erzeugen
	public URI getUriArtikel(Artikel artikel, UriInfo uriInfo) {
		return uriHelper.getUri(ArtikelResource.class, "findArtikelById", artikel.getId(), uriInfo);
	}
}
