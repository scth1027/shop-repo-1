package de.shop.bestellverwaltung.rest;

import static de.shop.util.Constants.ADD_LINK;
import static de.shop.util.Constants.REMOVE_LINK;
import static de.shop.util.Constants.SELF_LINK;
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


import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.util.Mock;
import de.shop.util.rest.NotFoundException;
import de.shop.util.rest.UriHelper;

@Path("/Bestellung")
@Produces({ APPLICATION_JSON, APPLICATION_XML + ";qs=0.75", TEXT_XML + ";qs=0.5" })
@Consumes
public class BestellungResource {
	public static final String BESTELLUNG_ID_PATH_PARAM = "bestellunglId";
	
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
	@GET
	@Path("{" + BESTELLUNG_ID_PATH_PARAM + ":[1-9][0-9]*}")
	public Response findArtikelById(@PathParam(BESTELLUNG_ID_PATH_PARAM) Long id) {
		final Bestellung bestellung = Mock.findBestellungById(id);
		if(bestellung == null) {
			throw new NotFoundException("Die angegebene ID:" + id + "liefert keinen Artikel");
		}
		System.out.println("Bestellung ist nicht null");
		System.out.println(bestellung.getId());
		//setStructuralLinks(artikel, uriInfo);
		//System.out.println("Structural Links gebildet");
		return Response.ok(bestellung)
				       .links(getTransitionalLinks(bestellung, uriInfo))
                       .build();
	}
	
	//TODO:Hinzufügen des LIST_LINKS
		//Verwaltungs URIs erzeugen
		public Link[] getTransitionalLinks(Bestellung bestellung, UriInfo uriInfo) {
			final Link self = Link.fromUri(getUriBestellung(bestellung, uriInfo))
		                          .rel(SELF_LINK)
		                          .build();
			
			/*final Link list = Link.fromUri(uriHelper.getUri(BestellungResource.class, "findAllBestellungen", bestellung.getId(), uriInfo))
								.rel(LIST_LINK)
								.build();*/
			
			final Link add = Link.fromUri(uriHelper.getUri(BestellungResource.class, uriInfo))
	                             .rel(ADD_LINK)
	                             .build();

			final Link update = Link.fromUri(uriHelper.getUri(BestellungResource.class, uriInfo))
	                                .rel(UPDATE_LINK)
	                                .build();

			final Link remove = Link.fromUri(uriHelper.getUri(BestellungResource.class, "deleteBestellung", bestellung.getId(), uriInfo))
	                                .rel(REMOVE_LINK)
	                                .build();
			
			return new Link[] { self/*, list*/, add, update, remove };
		}
		
		//Bestellung URI erzeugen
		public URI getUriBestellung(Bestellung bestellung, UriInfo uriInfo) {
			return uriHelper.getUri(BestellungResource.class, "findBestellungById", bestellung.getId(), uriInfo);
		}

}
