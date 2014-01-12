package de.shop.einkaufverwaltung.rest;

import static de.shop.einkaufverwaltung.service.EinkaufService.FetchType.NUR_EINKAUF;
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

import de.shop.einkaufverwaltung.domain.Einkauf;
import de.shop.einkaufverwaltung.service.EinkaufService;
import de.shop.lieferantenverwaltung.domain.Lieferant;
import de.shop.lieferantenverwaltung.rest.LieferantenResource;
import de.shop.lieferantenverwaltung.service.LieferantService;
import de.shop.lieferantenverwaltung.service.LieferantService.FetchType;
import de.shop.util.rest.UriHelper;

@Path("/einkaeufe")
@Produces({ APPLICATION_JSON, APPLICATION_XML + ";qs=0.75",
		TEXT_XML + ";qs=0.5" })
@Consumes
@RequestScoped
public class EinkaufResource {

	@Context
	private UriInfo uriInfo;

	@Inject
	private UriHelper uriHelper;

	@Inject
	private LieferantService ls;

	@Inject
	private LieferantenResource lieferantResource;

	@Inject
	private EinkaufService es;

	@GET
	@Path("{id:[1-9][0-9]*}")
	public Response findEinkaufById(@PathParam("id") Long id) {
		final Einkauf einkauf = es.findEinkaufById(id, NUR_EINKAUF);
		if (einkauf == null) {
			throw new NotFoundException("Keine Einkauf mit der ID " + id
					+ " gefunden.");
		}

		setStructuralLinks(einkauf, uriInfo);

		// Link-Header setzen
		final Response response = Response.ok(einkauf)
				.links(getTransitionalLinks(einkauf, uriInfo)).build();

		return response;
	}

	private Link[] setTransitionalLinksEinkaeufe(
			List<Einkauf> einkaeufe, UriInfo uriInfo) {
		if (einkaeufe == null || einkaeufe.isEmpty()) {
			return null;
		}

		final Link first = Link
				.fromUri(getEinkaeufeURI(einkaeufe.get(0), uriInfo))
				.rel(FIRST_LINK).build();
		final int lastPos = einkaeufe.size() - 1;
		final Link last = Link
				.fromUri(getEinkaeufeURI(einkaeufe.get(lastPos), uriInfo))
				.rel(LAST_LINK).build();

		return new Link[] {first, last};
	}

	private URI getEinkaeufeURI(Einkauf einkauf, UriInfo uriInfo2) {
		return uriHelper.getUri(EinkaufResource.class, "findEinkaufById",
				einkauf.getId(), uriInfo);

	}

	public void setStructuralLinks(Einkauf einkauf, UriInfo uriInfo) {
		// URI fuer Lieferant setzen
		final Lieferant lieferant = einkauf.getLieferant();
		if (lieferant != null) {
			final URI lieferantUri = lieferantResource.getUriLieferant(
					einkauf.getLieferant(), uriInfo);
			einkauf.setLieferantURI(lieferantUri);
		}
	}

	private Link[] getTransitionalLinks(Einkauf einkauf, UriInfo uriInfo) {
		final Link self = Link.fromUri(getUriEinkauf(einkauf, uriInfo))
				.rel(SELF_LINK).build();
		return new Link[] {self};
	}

	public URI getUriEinkauf(Einkauf einkauf, UriInfo uriInfo) {
		return uriHelper.getUri(EinkaufResource.class, "findEinkaufById",
				einkauf.getId(), uriInfo);
	}

	@POST
	@Consumes({ APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
	@Produces
	public Response createEinkauf(@Valid Einkauf einkauf,
			@Context HttpHeaders headers) {
		// TODO Anwendungskern statt Mock
		String lieferantenId = einkauf.getLieferantURI().toString();
		lieferantenId = lieferantenId.substring(lieferantenId.lastIndexOf("/") + 1);
		final Lieferant l = ls.findLieferantById(Long.valueOf(lieferantenId).longValue(), FetchType.NUR_LIEFERANT);
		System.out.println("Einkauf angekommen im Service");
		einkauf = es.createEinkauf(einkauf, l);
		System.out.println("Einkauf ist aus der Mock zurück");
		return Response.created(getUriEinkauf(einkauf, uriInfo)).build();
	}
}

