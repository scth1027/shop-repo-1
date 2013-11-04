package de.shop.bestellverwaltung.rest;

import static de.shop.util.Constants.FIRST_LINK;
import static de.shop.util.Constants.LAST_LINK;
import static de.shop.util.Constants.SELF_LINK;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_XML;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.kundenverwaltung.domain.Kunde;
import de.shop.kundenverwaltung.rest.KundenResource;
import de.shop.util.Mock;
import de.shop.util.rest.UriHelper;
import de.shop.util.rest.NotFoundException;


@Path("/bestellungen")
@Produces({ APPLICATION_JSON, APPLICATION_XML + ";qs=0.75", TEXT_XML + ";qs=0.5" })
@Consumes
public class BestellungResource {
	@Context
	private UriInfo uriInfo;
	
	@Inject
	private UriHelper uriHelper;
	
	@Inject
	private KundenResource kundeResource;
	
	@GET
	@Path("{id:[1-9][0-9]*}")
	public Response findBestellungById(@PathParam("id") Long id) {
		// TODO Anwendungskern statt Mock, Verwendung von Locale
		System.out.println("IN Methode");
		final Bestellung bestellung = Mock.findBestellungById(id);
		if (bestellung == null) {
			throw new NotFoundException("Keine Bestellung mit der ID " + id + " gefunden.");
		}
		
		setStructuralLinks(bestellung, uriInfo);
		
		// Link-Header setzen
		final Response response = Response.ok(bestellung)
                                          .links(getTransitionalLinks(bestellung, uriInfo))
                                          .build();
		
		return response;
	}
	
	@GET
	public Response findBestellungen() {
		// Aufruf der Mock zur Erzeugung der Bestellungen
		List<Bestellung> all = Mock.findAllBestellungen();
		// Plausibilitäsprüfung
		if(all.isEmpty())
			throw new NotFoundException("Es konnten keine Kunden gefunden werden!");
		// Erstellen der Links für die jeweilign Bestellungen
		for (Bestellung bestellung : all) {
			setStructuralLinks(bestellung, uriInfo);
		}
		return Response.ok(new GenericEntity<List<Bestellung>>(all){})
		        .links(setTransitionalLinksBestellungen(all, uriInfo))
		        .build();
	}
	
	
	private Link[] setTransitionalLinksBestellungen(List<Bestellung> bestellungen, UriInfo uriInfo) {
		if (bestellungen == null || bestellungen.isEmpty()) {
			return null;
		}

		final Link first = Link.fromUri(getBestellungenURI(bestellungen.get(0), uriInfo))
				.rel(FIRST_LINK)
				.build();
		final int lastPos = bestellungen.size() - 1;
		final Link last = Link.fromUri(getBestellungenURI(bestellungen.get(lastPos), uriInfo))
				.rel(LAST_LINK)
				.build();

		return new Link[] { first, last };
	}

	private URI getBestellungenURI(Bestellung bestellung, UriInfo uriInfo2) {
		return uriHelper.getUri(BestellungResource.class, "findBestellungById", bestellung.getId(), uriInfo);
		
	}

	public void setStructuralLinks(Bestellung bestellung, UriInfo uriInfo) {
		// URI fuer Kunde setzen
		final Kunde kunde = bestellung.getKunde();
		if (kunde != null) {
			final URI kundeUri = kundeResource.getKundenURI(bestellung.getKunde(), uriInfo);
			bestellung.setKundeURI(kundeUri);
		}
	}
	
	private Link[] getTransitionalLinks(Bestellung bestellung, UriInfo uriInfo) {
		final Link self = Link.fromUri(getUriBestellung(bestellung, uriInfo))
                              .rel(SELF_LINK)
                              .build();
		return new Link[] { self };
	}
	
	
	public URI getUriBestellung(Bestellung bestellung, UriInfo uriInfo) {
		return uriHelper.getUri(BestellungResource.class, "findBestellungById", bestellung.getId(), uriInfo);
	}
}
