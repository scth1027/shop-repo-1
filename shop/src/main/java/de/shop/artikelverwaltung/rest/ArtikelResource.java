package de.shop.artikelverwaltung.rest;

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

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
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

import org.jboss.logging.Logger;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.util.interceptor.Log;
import de.shop.util.rest.UriHelper;

/*
 * ArtikelResource Klasse
 * enthaelt die RestServices fuer die Artikelverwaltung/Domain
 * Zugriff auf Anwendungslogik
 */

@Path("/artikel")
@Produces({ APPLICATION_JSON, APPLICATION_XML + ";qs=0.75",
		TEXT_XML + ";qs=0.5" })
@Consumes
@RequestScoped
@Log
public class ArtikelResource {
	private static final Logger LOGGER = Logger.getLogger(MethodHandles.lookup().lookupClass());
	
	public static final String ARTIKEL_ID_PATH_PARAM = "artikelId";
	public static final String ARTIKEL_BEZEICHNUNG_QUERY_PARAM = "bezeichnung";

	@Inject
	private ArtikelService as;

	@Context
	private UriInfo uriInfo;

	@Inject
	private UriHelper uriHelper;

	// Aktuelle Version ausgeben
	@GET
	@Produces({ TEXT_PLAIN, APPLICATION_JSON })
	@Path("version")
	public String getVersion() {
		return "1.0";
	}

	// Artikel mit ID finden
	@GET
	@Path("{" + ARTIKEL_ID_PATH_PARAM + ":[1-9][0-9]*}")
	public Response findArtikelById(@PathParam(ARTIKEL_ID_PATH_PARAM) Long id) {
		final Artikel artikel = as.findArtikelById(id);

		return Response.ok(artikel)
				.links(getTransitionalLinks(artikel, uriInfo)).build();
	}

	// Artikel mit Bezeichnung finden
	@GET
	public Response findArtikelByBezeichnung(
			@QueryParam(ARTIKEL_BEZEICHNUNG_QUERY_PARAM) String bezeichnung) {
		List<Artikel> artikelliste = null;
		if (bezeichnung != null) {
			artikelliste = as.findArtikelByBezeichnung(bezeichnung);
			if (artikelliste.isEmpty()) {
				throw new NotFoundException("Kein Artikel mit Bezeichnung "
						+ bezeichnung + " gefunden.");
			}
		} 
		else {
			artikelliste = as.findAllArtikel();
			if (artikelliste.isEmpty()) {
				throw new NotFoundException("Keine Artikel vorhanden.");
			}
		}

		return Response.ok(new GenericEntity<List<Artikel>>(artikelliste) {
		}).links(getTransitionalLinksArtikelListe(artikelliste, uriInfo))
				.build();
	}

	// Alle Artikel auflisten
	@GET
	public Response findAllArtikel() {
		final List<Artikel> artikelliste = as.findAllArtikel();
		if (artikelliste.isEmpty()) {
			throw new NotFoundException(
					"Es konnte keine Artikel gefunden werden.");
		}

		return Response.ok(new GenericEntity<List<Artikel>>(artikelliste) {
		}).links(getTransitionalLinksArtikelListe(artikelliste, uriInfo))
				.build();
	}

	// Artikel erstellen
	@POST
	@Consumes({ APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
	@Produces
	public Response createArtikel(@Valid Artikel artikel) {
		artikel.setId(null);
		
		artikel = as.createArtikel(artikel);
		LOGGER.tracef("Artikel: %s", artikel);
		
		return Response.created(getUriArtikel(artikel, uriInfo)).build();
	}

	// Artikel aendern
	@PUT
	@Consumes({ APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
	@Produces
	public void updateArtikel(@Valid Artikel artikel) {
		final Artikel orgArtikel = as.findArtikelById(artikel.getId());
		LOGGER.tracef("Artikel vorher: %s", orgArtikel);
		
		LOGGER.tracef("Artikel nachher: %s", artikel);
		
		as.updateArtikel(artikel);
	}

	// Artikel loeschen bzw. als nicht mehr aktiv makieren
	@DELETE
	@Path("{id:[1-9][0-9]*}")
	@Produces
	public void deleteArtikel(@PathParam("id") Long artikelId) {
		as.deleteArtikel(artikelId);
	}

	// Artikel URI erzeugen
	public URI getUriArtikel(Artikel artikel, UriInfo uriInfo) {
		return uriHelper.getUri(ArtikelResource.class, "findArtikelById",
				artikel.getId(), uriInfo);
	}

	// Verwaltungs URIs erzeugen
	public Link[] getTransitionalLinks(Artikel artikel, UriInfo uriInfo) {
		final Link self = Link.fromUri(getUriArtikel(artikel, uriInfo))
				.rel(SELF_LINK).build();
		final Link add = Link
				.fromUri(uriHelper.getUri(ArtikelResource.class, uriInfo))
				.rel(ADD_LINK).build();
		final Link update = Link
				.fromUri(uriHelper.getUri(ArtikelResource.class, uriInfo))
				.rel(UPDATE_LINK).build();
		final Link remove = Link
				.fromUri(
						uriHelper.getUri(ArtikelResource.class,
								"deleteArtikel", artikel.getId(), uriInfo))
				.rel(REMOVE_LINK).build();
		return new Link[] {self, add, update, remove};
	}

	public Link[] getTransitionalLinksArtikelListe(List<Artikel> artikelliste,
			UriInfo uriInfo) {
		if (artikelliste == null || artikelliste.isEmpty()) {
			return null;
		}

		final Link first = Link
				.fromUri(getUriArtikel(artikelliste.get(0), uriInfo))
				.rel(FIRST_LINK).build();
		final int lastPos = artikelliste.size() - 1;
		final Link last = Link
				.fromUri(getUriArtikel(artikelliste.get(lastPos), uriInfo))
				.rel(LAST_LINK).build();

		return new Link[] {first, last};
	}
}
