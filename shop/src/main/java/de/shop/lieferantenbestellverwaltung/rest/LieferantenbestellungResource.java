package de.shop.lieferantenbestellverwaltung.rest;

import static de.shop.util.Constants.FIRST_LINK;
import static de.shop.util.Constants.LAST_LINK;
import static de.shop.util.Constants.SELF_LINK;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_XML;

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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.shop.lieferantenbestellverwaltung.domain.Lieferantenbestellung;
import de.shop.lieferantenbestellverwaltung.service.LieferantenbestellungService;
import de.shop.lieferantenverwaltung.domain.AbstractLieferant;
import de.shop.lieferantenverwaltung.rest.LieferantenResource;
import de.shop.lieferantenverwaltung.service.LieferantService;
import de.shop.lieferantenverwaltung.service.LieferantService.FetchType;
import de.shop.util.rest.UriHelper;

@Path("/lieferantenbestellungen")
@Produces({ APPLICATION_JSON, APPLICATION_XML + ";qs=0.75",
		TEXT_XML + ";qs=0.5" })
@Consumes
@RequestScoped
public class LieferantenbestellungResource {
	@Context
	private UriInfo uriInfo;

	@Inject
	private UriHelper uriHelper;

	@Inject
	private LieferantService ls;

	@Inject
	private LieferantenResource lieferantResource;

	@Inject
	private LieferantenbestellungService lbs;

	@GET
	@Path("{id:[1-9][0-9]*}")
	public Response findBestellungById(@PathParam("id") Long id) {
		// TODO Anwendungskern statt Mock, Verwendung von Locale
		System.out.println("IN Methode");
		final Lieferantenbestellung lieferantenbestellung = lbs.findLieferantenbestellungById(id);
		if (lieferantenbestellung == null) {
			throw new NotFoundException("Keine Lieferantenbestellung mit der ID " + id
					+ " gefunden.");
		}

		setStructuralLinks(lieferantenbestellung, uriInfo);

		// Link-Header setzen
		final Response response = Response.ok(lieferantenbestellung)
				.links(getTransitionalLinks(lieferantenbestellung, uriInfo)).build();

		return response;
	}

	@GET
	public Response findAllBestellungen() {
		// Aufruf der Mock zur Erzeugung der Bestellungen
		final List<Lieferantenbestellung> all = lbs.findAllLieferantenbestellungen();
		// Plausibilitäsprüfung
		if (all.isEmpty())
			throw new NotFoundException(
					"Es konnten keine Lieferanten gefunden werden!");
		// Erstellen der Links für die jeweilign Bestellungen
		for (Lieferantenbestellung lieferantenbestellung : all) {
			setStructuralLinks(lieferantenbestellung, uriInfo);
		}
		return Response.ok(new GenericEntity<List<Lieferantenbestellung>>(all) {
		}).links(setTransitionalLinksBestellungen(all, uriInfo)).build();
	}

	private Link[] setTransitionalLinksBestellungen(
			List<Lieferantenbestellung> bestellungen, UriInfo uriInfo) {
		if (bestellungen == null || bestellungen.isEmpty()) {
			return null;
		}

		final Link first = Link
				.fromUri(getBestellungenURI(bestellungen.get(0), uriInfo))
				.rel(FIRST_LINK).build();
		final int lastPos = bestellungen.size() - 1;
		final Link last = Link
				.fromUri(getBestellungenURI(bestellungen.get(lastPos), uriInfo))
				.rel(LAST_LINK).build();

		return new Link[] {first, last};
	}

	private URI getBestellungenURI(Lieferantenbestellung lieferantenbestellung, UriInfo uriInfo2) {
		return uriHelper.getUri(LieferantenbestellungResource.class, "findLieferantenbestellungById",
				lieferantenbestellung.getId(), uriInfo);

	}

	public void setStructuralLinks(Lieferantenbestellung lieferantenbestellung, UriInfo uriInfo) {
		// URI fuer Lieferant setzen
		final AbstractLieferant lieferant = lieferantenbestellung.getLieferant();
		if (lieferant != null) {
			final URI lieferantUri = lieferantResource.getUriLieferant(
					lieferantenbestellung.getLieferant(), uriInfo);
			lieferantenbestellung.setLieferantURI(lieferantUri);
		}
	}

	private Link[] getTransitionalLinks(Lieferantenbestellung lieferantenbestellung, UriInfo uriInfo) {
		final Link self = Link.fromUri(getUriLieferantenbestellung(lieferantenbestellung, uriInfo))
				.rel(SELF_LINK).build();
		return new Link[] {self};
	}

	public URI getUriLieferantenbestellung(Lieferantenbestellung lieferantenbestellung, UriInfo uriInfo) {
		return uriHelper.getUri(LieferantenbestellungResource.class, "findLieferantenbestellungById",
				lieferantenbestellung.getId(), uriInfo);
	}

	@POST
	@Consumes({ APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
	@Produces
	public Response createBestellung(@Valid Lieferantenbestellung lieferantenbestellung,
			@Context HttpHeaders headers) {
		// TODO Anwendungskern statt Mock
		String lieferantenId = lieferantenbestellung.getLieferantURI().toString();
		lieferantenId = lieferantenId.substring(lieferantenId.lastIndexOf("/") + 1);
		final AbstractLieferant l = ls.findLieferantById(Long.valueOf(lieferantenId).longValue(), FetchType.NUR_LIEFERANT);
		System.out.println("Lieferantenbestellung angekommen im Service");
		lieferantenbestellung = lbs.createLieferantenbestellung(lieferantenbestellung, l, headers.getLanguage());
		System.out.println("Lieferantenbestellung ist aus der Mock zurück");
		return Response.created(getUriLieferantenbestellung(lieferantenbestellung, uriInfo)).build();
	}

	@DELETE
	@Path("{id:[1-9][0-9]*}")
	@Produces
	public void deleteBestellung(@PathParam("id") Long bestellungId) {
		lbs.deleteLieferantenbestellung(bestellungId);
	}

	@PUT
	@Consumes({ APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
	@Produces
	public void updateBestellung(@Valid Lieferantenbestellung lieferantenbestellung) {
		// TODO Anwendungskern statt Mock
		lbs.updateLieferantenbestellung(lieferantenbestellung);
	}

}

