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
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
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
import de.shop.lieferantenverwaltung.service.LieferantService.OrderType;
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
	
	/**
	 * Suche den Einkauf zu gegebener ID.
	 * 
	 * @param id
	 *            ID des gesuchten Einkauf.
	 * @return Der gefundene Einkauf, sonst null.
	 */
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

	/**
	 * Suche nach allen Einkkaeufen.
	 * 
	 * @return Liste aller Einkaeufe.
	 */
	@GET
	public Response findAllEinkaeufe() {
		// Aufruf des Service zur Lieferantenerzeugung
		final List<Einkauf> all = es.findAllEinkaeufe(OrderType.KEINE);
		// Plausibilitäsprüfung
		if (all.isEmpty())
			throw new NotFoundException(
					"Es konnten keine Einkaeufe gefunden werden!");
		// Erstellen der Links für die jeweilign Lieferanten
		for (Einkauf einkauf : all) {
			setStructuralLinks(einkauf, uriInfo);
		}
		return Response.ok(new GenericEntity<List<Einkauf>>(all) {
		}).links(getTransitionalLinksEinkaeufe(all, uriInfo)).build();
	}
	
	/**
	 * Einen neuen Einkauf in der DB anlegen.
	 * 
	 * @param einkauf
	 *            Der anzulegende Einkauf.
	 * @return Der neue Einkauf einschliesslich generierter ID.
	 */
	@POST
	@Consumes({ APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
	@Produces
	public Response createEinkauf(@Valid Einkauf einkauf,
			@Context HttpHeaders headers) {
		String lieferantenId = einkauf.getLieferantURI().toString();
		lieferantenId = lieferantenId.substring(lieferantenId.lastIndexOf("/") + 1);
		final Lieferant l = ls.findLieferantById(Long.valueOf(lieferantenId).longValue(), FetchType.NUR_LIEFERANT);
		einkauf = es.createEinkauf(einkauf, l);
		return Response.created(getUriEinkauf(einkauf, uriInfo)).build();
	}

	/**
	 * Einkauf URI erzeugen.
	 * 
	 * @param einkauf
	 *            Der Einkauf zu dem eine URI erstellt wird.
	 * @param uriInfo
	 *            Die dazugehoerige UriInfo.     
	 */
	public URI getUriEinkauf(Einkauf einkauf, UriInfo uriInfo) {
		return uriHelper.getUri(EinkaufResource.class, "findEinkaufById",
				einkauf.getId(), uriInfo);
	}
	//FIXME:Löschbar?
	private URI getEinkaeufeURI(Einkauf einkauf, UriInfo uriInfo2) {
		return uriHelper.getUri(EinkaufResource.class, "findEinkaufById",
				einkauf.getId(), uriInfo);

	}
	
	/**
	 * Verwatungs URIs erzeugen.
	 * 
	 * @param einkauf
	 *            Der Einkauf zu dem die Verwaltungs URIs erzeugt werden sollen.
	 * @param uriInfo
	 *            Die dazugehoerige UriInfo.     
	 */
	private Link[] getTransitionalLinks(Einkauf einkauf, UriInfo uriInfo) {
		final Link self = Link.fromUri(getUriEinkauf(einkauf, uriInfo))
				.rel(SELF_LINK).build();
		return new Link[] {self};
	}
	
	/**
	 * Verwaltungs URIs fuer Liste von Einkaeufen erzeugen.
	 * 
	 * @param einkaeufe
	 *            Die Einkaeufe zu denen die Verwaltungs URIs erzeugt werden sollen.
	 * @param uriInfo
	 *            Die dazugehoerige UriInfo.     
	 */
	private Link[] getTransitionalLinksEinkaeufe(
			List<? extends Einkauf> einkaeufe, UriInfo uriInfo) {
		if (einkaeufe == null || einkaeufe.isEmpty()) {
			return null;
		}

		final Link first = Link.fromUri(getUriEinkauf(einkaeufe.get(0), uriInfo))
				.rel(FIRST_LINK).build();
		final int lastPos = einkaeufe.size() - 1;
		final Link last = Link
				.fromUri(getUriEinkauf(einkaeufe.get(lastPos), uriInfo))
				.rel(LAST_LINK).build();

		return new Link[] {first, last };
	}	
	
	//TODO:Kommentar --> Ich weiß nicht was erstellt wird
	public void setStructuralLinks(Einkauf einkauf, UriInfo uriInfo) {
		// URI fuer Lieferant setzen
		final Lieferant lieferant = einkauf.getLieferant();
		if (lieferant != null) {
			final URI lieferantUri = lieferantResource.getUriLieferant(
					einkauf.getLieferant(), uriInfo);
			einkauf.setLieferantURI(lieferantUri);
		}
	}

	/*private Link[] setTransitionalLinksEinkaeufe(
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
	}*/
}

