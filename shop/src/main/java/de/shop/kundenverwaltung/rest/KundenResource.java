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

import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.util.Mock;
import de.shop.util.rest.NotFoundException;
import de.shop.util.rest.UriHelper;

@Path("/kunden")
@Produces({APPLICATION_JSON})
@Consumes
public class KundenResource {
	
	@Context
	private UriInfo uriInfo;
	
	@Inject
	private UriHelper uriHelper;
	
	/*
	@GET
	public Response findAllKunden() {
		List<Kunde> k = Mock.findAllKunden();
	}
	*/
	@GET
	@Path("{id:[1-9][0-9]*}")
	public Response findKundeByID(@PathParam("id") Long id) {
		final Kunde kunde = Mock.findKundeById(id);
		if(kunde == null)
			throw new NotFoundException("Die angegebene ID:" + id + "liefert keinen Kunde");
		setStructuralLinks(kunde, uriInfo);
		return Response.ok(kunde).links(getTransitionalLinks(kunde, uriInfo)).build();
	}
	

	public Link[] getTransitionalLinks(Kunde kunde, UriInfo uriInfo) {
		final Link self = Link.fromUri(getUriKunde(kunde, uriInfo))
	                          .rel(SELF_LINK)
	                          .build();
		
		final Link add = Link.fromUri(uriHelper.getUri(KundenResource.class, uriInfo))
                             .rel(ADD_LINK)
                             .build();

		final Link update = Link.fromUri(uriHelper.getUri(KundenResource.class, uriInfo))
                                .rel(UPDATE_LINK)
                                .build();

		final Link remove = Link.fromUri(uriHelper.getUri(KundenResource.class, "deleteKunde", kunde.getId(), uriInfo))
                                .rel(REMOVE_LINK)
                                .build();
		
		return new Link[] { self, add, update, remove };
	}
	
	public void setStructuralLinks(Kunde kunde, UriInfo uriInfo) {
		final URI uri = getUriBestellungen(kunde, uriInfo);
		kunde.setBestellungenURI(uri);
	}
	
	private URI getUriBestellungen(Kunde kunde, UriInfo uriInfo) {
		return uriHelper.getUri(KundenResource.class, "findBestellungenByKundeId", kunde.getId(), uriInfo);
	}
	
	public URI getUriKunde(Kunde kunde, UriInfo uriInfo) {
		return uriHelper.getUri(KundenResource.class, "findKundeById", kunde.getId(), uriInfo);
	}
	
	@GET
	public Response findKundenByNachname(@QueryParam("nachname") String nachname) {
		List<? extends Kunde> kunden = null;
		if (nachname != null) {
			kunden = Mock.findKundenByNachname(nachname);
			if (kunden.isEmpty()) {
				throw new NotFoundException("Kein Kunde mit Nachname " + nachname + " gefunden.");
			}
		}
		else {
			kunden = Mock.findAllKunden();
			if (kunden.isEmpty()) {
				throw new NotFoundException("Keine Kunden vorhanden.");
			}
		}
		
		for (Kunde k : kunden) {
			setStructuralLinks(k, uriInfo);
		}
		
		return Response.ok(new GenericEntity<List<? extends Kunde>>(kunden){})
                       .links(getTransitionalLinksKunden(kunden, uriInfo))
                       .build();
	}
	
	private Link[] getTransitionalLinksKunden(List<? extends Kunde> kunden, UriInfo uriInfo) {
		if (kunden == null || kunden.isEmpty()) {
			return null;
		}
		
		final Link first = Link.fromUri(getUriKunde(kunden.get(0), uriInfo))
	                           .rel(FIRST_LINK)
	                           .build();
		final int lastPos = kunden.size() - 1;
		final Link last = Link.fromUri(getUriKunde(kunden.get(lastPos), uriInfo))
                              .rel(LAST_LINK)
                              .build();
		
		return new Link[] { first, last };
	}
	
	@POST
	@Consumes({APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
	@Produces
	public Response createKunde(Kunde kunde) {
		kunde = Mock.createKunde(kunde);
		return Response.created(getUriKunde(kunde, uriInfo))
			           .build();
	}
	
	@PUT
	@Consumes({APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
	@Produces
	public void updateKunde(Kunde kunde) {
		Mock.updateKunde(kunde);
	}
	
	@DELETE
	@Path("{id:[1-9][0-9]*}")
	@Produces
	public void deleteKunde(@PathParam("id") Long kundeId) {
		Mock.deleteKunde(kundeId);
	}

}
