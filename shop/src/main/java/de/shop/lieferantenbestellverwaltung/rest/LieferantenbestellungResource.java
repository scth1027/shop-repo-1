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
import de.shop.lieferantenverwaltung.service.LieferantService;
import de.shop.lieferantenverwaltung.domain.Lieferant;
import de.shop.lieferantenverwaltung.rest.LieferantenResource;
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
	public Response findLieferantenbestellungById(@PathParam("id") Long id) {
		// TODO Anwendungskern statt Mock
		System.out.println("IN Methode");
		final Lieferantenbestellung bestellung = lbs.findLieferantenbestellungById(id);
		if (bestellung == null) {
			throw new NotFoundException("Keine Bestellung mit der ID " + id
					+ " gefunden.");
		}

		setStructuralLinks(bestellung, uriInfo);

		// Link-Header setzen
		final Response response = Response.ok(bestellung)
				.links(getTransitionalLinks(bestellung, uriInfo)).build();

		return response;
	}

	@GET
	public Response findAllBestellungen() {
		// Aufruf der Mock zur Erzeugung der Bestellungen
		final List<Lieferantenbestellung> all = lbs.findAllLieferantenbestellungen();
		// Plausibilitätsprüfung
		if (all.isEmpty())
			throw new NotFoundException(
					"Es konnten keine Lieferanten gefunden werden!");
		// Erstellen der Links für die jeweiligen Bestellungen
		for (Lieferantenbestellung bestellung : all) {
			setStructuralLinks(bestellung, uriInfo);
		}
		return Response.ok(new GenericEntity<List<Lieferantenbestellung>>(all) {
		}).links(setTransitionalLinksLieferantenbestellungen(all, uriInfo))
				.build();
	}

	private Link[] setTransitionalLinksLieferantenbestellungen(
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

		return new Link[] { first, last };
	}

	private URI getBestellungenURI(Lieferantenbestellung bestellung, UriInfo uriInfo2) {
		return uriHelper.getUri(LieferantenbestellungResource.class,"findLieferantenbestellungById",
				bestellung.getId(), uriInfo);

	}

	public void setStructuralLinks(Lieferantenbestellung bestellung, UriInfo uriInfo) {
		// URI fuer Kunde setzen
		final Lieferant lieferant = bestellung.getLieferant();
		if (lieferant != null) {
			final URI lieferantUri = lieferantResource.getLieferantenURI(
					bestellung.getLieferant(), uriInfo);
			bestellung.setLieferantURI(lieferantUri);
		}
	}

	private Link[] getTransitionalLinks(Lieferantenbestellung bestellung, UriInfo uriInfo) {
		final Link self = Link.fromUri(getUriLieferantenbestellung(bestellung, uriInfo))
				.rel(SELF_LINK).build();
		return new Link[] { self };
	}

	public URI getUriLieferantenbestellung(Lieferantenbestellung bestellung, UriInfo uriInfo) {
		return uriHelper.getUri(LieferantenbestellungResource.class, "findLieferantenbestellungById",
				bestellung.getId(), uriInfo);
	}

	@POST
	@Consumes({ APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
	@Produces
	public Response createBestellung(@Valid Lieferantenbestellung bestellung,
			@Context HttpHeaders headers) {
		// TODO Anwendungskern statt Mock
		String lieferantenId = bestellung.getLieferantURI().toString();
		lieferantenId = lieferantenId.substring(lieferantenId.lastIndexOf("/") + 1);
		final Lieferant l = ls.findLieferantById(Long.valueOf(lieferantenId).longValue());
		System.out.println("Bestellung angekommen im Service");
		bestellung = lbs.createLieferantenbestellung(bestellung, l, headers.getLanguage());
		System.out.println("Bestellung ist aus der Mock zurück");
		return Response.created(getUriLieferantenbestellung(bestellung, uriInfo)).build();
	}

	@DELETE
	@Path("{id:[1-9][0-9]*}")
	@Produces
	public void deleteLieferantenbestellung(@PathParam("id") Long bestellungId) {
		lbs.deleteLieferantenbestellung(bestellungId);
	}

	@PUT
	@Consumes({ APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
	@Produces
	public void updateBestellung(@Valid Lieferantenbestellung bestellung) {
		// TODO Anwendungskern statt Mock
		lbs.updateLieferantenbestellung(bestellung);
	}

}
