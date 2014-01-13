package de.shop.bestellverwaltung.rest;

import static de.shop.bestellverwaltung.service.BestellungService.FetchType.NUR_BESTELLUNG;
import static de.shop.util.Constants.ADD_LINK;
import static de.shop.util.Constants.SELF_LINK;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.MediaType.TEXT_XML;
import static javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Link;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import de.shop.artikelverwaltung.domain.Artikel;
import de.shop.artikelverwaltung.rest.ArtikelResource;
import de.shop.artikelverwaltung.service.ArtikelService;
import de.shop.bestellverwaltung.domain.Posten;
import de.shop.bestellverwaltung.domain.Bestellung;
import de.shop.bestellverwaltung.service.BestellungService;
import de.shop.kundenverwaltung.domain.AbstractKunde;
import de.shop.kundenverwaltung.rest.KundenResource;
import de.shop.util.interceptor.Log;
import de.shop.util.rest.UriHelper;

/*
 * BestellungResource Klasse
 * enthaelt die RestServices fuer die Bestellverwaltung/Domain
 * Zugriff auf Anwendungslogik
 */

@Path("/bestellungen")
@Produces({APPLICATION_JSON, APPLICATION_XML + ";qs=0.75",
		TEXT_XML + ";qs=0.5" })
@Consumes
@RequestScoped
@Transactional
@Log
public class BestellungResource {
	@Context
	private UriInfo uriInfo;
	
	@Inject
	private BestellungService bs;
	
	@Inject
	private ArtikelService as;
	
	@Inject
	private ArtikelResource artikelResource;

	@Inject
	private KundenResource kundeResource;
	
	@Inject
	private UriHelper uriHelper;
	
	/**
	 * Suche die Bestellung zu gegebener ID.
	 * 
	 * @param id
	 *            ID der gesuchten Bestellung.
	 * @return Die gefundene Bestellung, sonst null.
	 */
	@GET
	@Path("{id:[1-9][0-9]*}")
	public Response findBestellungById(@PathParam("id") Long id) {
		final Bestellung bestellung = bs.findBestellungById(id, NUR_BESTELLUNG);

		// URIs innerhalb der gefundenen Bestellung anpassen
		setStructuralLinks(bestellung, uriInfo);
		
		// Link-Header setzen
		final Response response = Response.ok(bestellung)
                                          .links(getTransitionalLinks(bestellung, uriInfo))
                                          .build();
		return response;
	}
	
	//FIXME:Muss das nicht in die LieferantenResource?
	/**
	 * Suche nach Lieferung zu Bestellungs ID.
	 * 
	 * @param id
	 *            ID der Bestellung.
	 * @return Die gefundenen Lieferungen, sonst null.
	 */
	@GET
	@Path("{id:[1-9][0-9]*}/lieferungen")
	public Response findLieferungenByBestellungId(@PathParam("id") Long id) {
		// Diese Methode ist bewusst NICHT implementiert, um zu zeigen,
		// wie man Methodensignaturen an der Schnittstelle fuer andere
		// Teammitglieder schon mal bereitstellt, indem einfach ein "Internal
		// Server Error (500)" produziert wird.
		// Die Kolleg/inn/en koennen nun weiterarbeiten, waehrend man selbst
		// gerade keine Zeit hat, weil andere Aufgaben Vorrang haben.
		
		// TODO findLieferungenByBestellungId noch nicht implementiert
		return Response.status(INTERNAL_SERVER_ERROR)
			       .entity("findLieferungenByBestellungId: NOT YET IMPLEMENTED")
			       .type(TEXT_PLAIN)
			       .build();
	}

	//FIXME:Muss das nicht in KundeService
	/**
	 * Suche nach Kunde zu Bestellungs ID.
	 * 
	 * @param id
	 *            ID der Bestellung.
	 * @return Den gefundenen Kunden, sonst null.
	 */
	@GET
	@Path("{id:[1-9][0-9]*}/kunde")
	public Response findKundeByBestellungId(@PathParam("id") Long id) {
		final AbstractKunde kunde = bs.findKundeById(id);
		kundeResource.setStructuralLinks(kunde, uriInfo);
		
		// Link Header setzen
		final Response response = Response.ok(kunde)
                                          .links(kundeResource.getTransitionalLinks(kunde, uriInfo))
                                          .build();
		return response;
	}

	
	/**
	 * Eine neue Bestellung in der DB anlegen.
	 * 
	 * @param bestellung
	 *            Die anzulegende Bestellung.
	 * @return Die neue Bestellung einschliesslich generierter ID.
	 */
	@POST
	@Consumes({ APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
	@Produces
	public Response createBestellung(@Valid Bestellung bestellung) {
		// TODO eingeloggter Kunde wird durch die URI im Attribut "kundeUri" emuliert
		final String kundeUriStr = bestellung.getKundeUri().toString();
		int startPos = kundeUriStr.lastIndexOf('/') + 1;
		final String kundeIdStr = kundeUriStr.substring(startPos);
		Long kundeId = null;
		try {
			kundeId = Long.valueOf(kundeIdStr);
		}
		catch (NumberFormatException e) {
			kundeIdInvalid();
		}
		
		// IDs der (persistenten) Artikel ermitteln
		final Collection<Posten> postenen = bestellung.getPostenen();
		final List<Long> artikelIds = new ArrayList<>(postenen.size());
		for (Posten bp : postenen) {
			final URI artikelUri = bp.getArtikelUri();
			if (artikelUri == null) {
				continue;
			}
			final String artikelUriStr = artikelUri.toString();
			startPos = artikelUriStr.lastIndexOf('/') + 1;
			final String artikelIdStr = artikelUriStr.substring(startPos);
			Long artikelId = null;
			try {
				artikelId = Long.valueOf(artikelIdStr);
			}
			catch (NumberFormatException e) {
				// Ungueltige Artikel-ID: wird nicht beruecksichtigt
				continue;
			}
			artikelIds.add(artikelId);
		}
		
		if (artikelIds.isEmpty()) {
			// keine einzige Artikel-ID als gueltige Zahl
			artikelIdsInvalid();
		}

		final Collection<Artikel> gefundeneArtikel = as.findArtikelByIds(artikelIds);
		
		// Postenen haben URLs fuer persistente Artikel.
		// Diese persistenten Artikel wurden in einem DB-Zugriff ermittelt (s.o.)
		// Fuer jede Posten wird der Artikel passend zur Artikel-URL bzw. Artikel-ID gesetzt.
		// Postenen mit nicht-gefundene Artikel werden eliminiert.
		int i = 0;
		final Set<Posten> neuePostenen = new HashSet<>();
		for (Posten bp : postenen) {
			// Artikel-ID der aktuellen Posten (s.o.):
			// artikelIds haben gleiche Reihenfolge wie Postenen
			final long artikelId = artikelIds.get(i++);
			
			// Wurde der Artikel beim DB-Zugriff gefunden?
			for (Artikel artikel : gefundeneArtikel) {
				if (artikel.getId().longValue() == artikelId) {
					// Der Artikel wurde gefunden
					bp.setArtikel(artikel);
					neuePostenen.add(bp);
					break;					
				}
			}
			// FIXME JDK 8 hat Lambda-Ausdruecke
			//final Artikel artikel = gefundeneArtikel.stream()
			//                                        .filter(a -> a.getId() == artikelId)
			//                                        .findAny();
			//if (artikel != null) {
			//	bp.setArtikel(artikel);
			//	neuePostenen.add(bp);				
			//}
		}
		bestellung.setPostenen(neuePostenen);
		
		bestellung = bs.createBestellung(bestellung, kundeId);

		final URI bestellungUri = getUriBestellung(bestellung, uriInfo);
		return Response.created(bestellungUri).build();
	}
	
	/**
	 * Ausloesen der BeanValidation fuer eine Artikelliste von IDs die leer ist
	 * 
	 * @NotNull verletzen, um die entsprechende Meldung zu verursachen, weil keine einzige Artikel-ID
	 *          eine gueltige Zahl war.
	 * @return null
	 */
	@NotNull(message = "{bestellung.artikelIds.invalid}")
	public List<Long> artikelIdsInvalid() {
		return null;
	}
	
	/**
	 * Ausloesen der BeanValidation fuer eine Kundenliste von IDs die leer ist
	 * 
	 * @NotNull verletzen, um die entsprechende Meldung zu verursachen, weil keine einzige Kudnden-ID
	 *          eine gueltige Zahl war.
	 * @return null
	 */
	@NotNull(message = "{bestellung.kunde.id.invalid}")
	public Long kundeIdInvalid() {
		return null;
	}
	
	/**
	 * Bestellungs URI erzeugen.
	 * 
	 * @param bestellung
	 *            Die Bestellungl zu dem eine URI erstellt wird.
	 * @param uriInfo
	 *            Die dazugehoerige UriInfo.     
	 */
	public URI getUriBestellung(Bestellung bestellung, UriInfo uriInfo) {
		return uriHelper.getUri(BestellungResource.class, "findBestellungById", bestellung.getId(), uriInfo);
	}
	
	/**
	 * Verwatungs URIs erzeugen.
	 * 
	 * @param bestellung
	 *            Die Bestellung zu dem die Verwaltungs URIs erzeugt werden sollen.
	 * @param uriInfo
	 *            Die dazugehoerige UriInfo.     
	 */
	public Link[] getTransitionalLinks(Bestellung bestellung, UriInfo uriInfo) {
		final Link self = Link.fromUri(getUriBestellung(bestellung, uriInfo))
                              .rel(SELF_LINK)
                              .build();
		final Link add = Link.fromUri(uriHelper.getUri(BestellungResource.class, uriInfo))
                             .rel(ADD_LINK)
                             .build();
		return new Link[] { self, add };
	}
	
	//TODO:Kommentar --> Ich weiﬂ nicht was hier genau erzeugt wird
	public void setStructuralLinks(Bestellung bestellung, UriInfo uriInfo) {
		// URI fuer Kunde setzen
		final AbstractKunde kunde = bestellung.getKunde();
		if (kunde != null) {
			final URI kundeUri = kundeResource.getUriKunde(bestellung.getKunde(), uriInfo);
			bestellung.setKundeUri(kundeUri);
		}
		
		// URIs fuer Artikel in den Postenen setzen
		final Collection<Posten> postenen = bestellung.getPostenen();
		if (postenen != null && !postenen.isEmpty()) {
			for (Posten bp : postenen) {
				final URI artikelUri = artikelResource.getUriArtikel(bp.getArtikel(), uriInfo);
				bp.setArtikelUri(artikelUri);
			}
		}
	}
}
