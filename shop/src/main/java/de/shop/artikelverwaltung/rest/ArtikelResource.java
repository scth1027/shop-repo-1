package de.shop.artikelverwaltung.rest;


import static de.shop.util.Constants.ADD_LINK;
import static de.shop.util.Constants.REMOVE_LINK;
import static de.shop.util.Constants.SELF_LINK;
import static de.shop.util.Constants.LIST_LINK;
import static de.shop.util.Constants.UPDATE_LINK;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

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

@Path("/artikel")
@Produces({APPLICATION_JSON})
@Consumes
public class ArtikelResource {

	@Context
	private UriInfo uriInfo;
	
	@Inject
	private UriHelper uriHelper;
	
	/*
	@GET
	public Response findAllArtikel() {
		List<Artikel> a = Mock.findAllArtikel();
	}
	*/
	@GET
	@Path("{id:[1-9][0-9]*}")
	public Response findArtikelByID(@PathParam("id") Long id) {
		final Artikel artikel = Mock.findArtikelById(id);
		if(artikel == null)
			throw new NotFoundException("Die angegebene ID:" + id + "liefert keinen Artikel");
		return Response.ok(artikel).links(getTransitionalLinks(artikel, uriInfo)).build();
	}
	
	public Link[] getTransitionalLinks(Artikel artikel, UriInfo uriInfo) {
		final Link self = Link.fromUri(getUriArtikel(artikel, uriInfo))
	                          .rel(SELF_LINK)
	                          .build();
		
		final Link list = Link.fromUri(uriHelper.getUri(ArtikelResource.class, "findAllArtikel", artikel.getId(), uriInfo))
							.rel(LIST_LINK)
							.build();
		
		final Link add = Link.fromUri(uriHelper.getUri(ArtikelResource.class, uriInfo))
                             .rel(ADD_LINK)
                             .build();

		final Link update = Link.fromUri(uriHelper.getUri(ArtikelResource.class, uriInfo))
                                .rel(UPDATE_LINK)
                                .build();

		final Link remove = Link.fromUri(uriHelper.getUri(ArtikelResource.class, "deleteArtikel", artikel.getId(), uriInfo))
                                .rel(REMOVE_LINK)
                                .build();
		
		return new Link[] { self, list, add, update, remove };
	}
	
	public URI getUriArtikel(Artikel artikel, UriInfo uriInfo) {
		return uriHelper.getUri(ArtikelResource.class, "findArtikelById", artikel.getId(), uriInfo);
	}
}
