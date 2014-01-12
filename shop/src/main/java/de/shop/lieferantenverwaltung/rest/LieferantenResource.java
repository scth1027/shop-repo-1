package de.shop.lieferantenverwaltung.rest;

import static de.shop.util.Constants.ADD_LINK;
import static de.shop.util.Constants.FIRST_LINK;
import static de.shop.util.Constants.LAST_LINK;
import static de.shop.util.Constants.REMOVE_LINK;
import static de.shop.util.Constants.SELF_LINK;
import static de.shop.util.Constants.UPDATE_LINK;
import static de.shop.util.Constants.KEINE_ID;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static javax.ws.rs.core.MediaType.APPLICATION_XML;
import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.MediaType.TEXT_XML;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
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

import org.hibernate.validator.constraints.Email;
import org.jboss.logging.Logger;

import com.google.common.base.Strings;

import de.shop.einkaufverwaltung.domain.Einkauf;
import de.shop.einkaufverwaltung.rest.EinkaufResource;
import de.shop.einkaufverwaltung.service.EinkaufService;
import de.shop.lieferantenverwaltung.domain.Lieferantenadresse;
import de.shop.lieferantenverwaltung.domain.Lieferant;
import de.shop.lieferantenverwaltung.service.LieferantService;
import de.shop.lieferantenverwaltung.service.LieferantService.FetchType;
import de.shop.lieferantenverwaltung.service.LieferantService.OrderType;
import de.shop.util.interceptor.Log;
import de.shop.util.rest.UriHelper;

/*
 * LieferantenResource Klasse
 * enthaelt die RestServices fuer die Lieferantenverwaltung/Domain
 * Zugriff auf den Mock später Anwendungslogik
 */
@RequestScoped
@Path("/lieferanten")
@Produces({ APPLICATION_JSON, APPLICATION_XML + ";qs=0.75",
		TEXT_XML + ";qs=0.5" })
@Consumes
@Transactional
@Log
public class LieferantenResource {

	private static final Logger LOGGER = Logger.getLogger(MethodHandles
			.lookup().lookupClass());
	private static final String VERSION = "1.0";

	public static final String LIEFERANTEN_ID_PATH_PARAM = "lieferantId";
	public static final String LIEFERANTEN_FIRMA_QUERY_PARAM = "firma";
	public static final String LIEFERANTEN_PLZ_QUERY_PARAM = "plz";
	public static final String LIEFERANTEN_EMAIL_QUERY_PARAM = "email";

	@Context
	private UriInfo uriInfo;

	@Inject
	private UriHelper uriHelper;

	@Inject
	private EinkaufResource einkaufResource;

	@Inject
	private EinkaufService es;

	@Inject
	private LieferantService ls;

	@GET
	@Produces({ TEXT_PLAIN, APPLICATION_JSON })
	@Path("version")
	public String getVersion() {
		return VERSION;
	}

	// Alle Lieferanten ausgeben
	@GET
	public Response findAllLieferanten() {
		// Aufruf der Mock zur Erzeugung der Lieferanten
		final List<Lieferant> all = ls.findAllLieferanten(FetchType.NUR_LIEFERANT,
				OrderType.KEINE);
		// Plausibilitäsprüfung
		if (all.isEmpty())
			throw new NotFoundException(
					"Es konnten keine Lieferanten gefunden werden!");
		// Erstellen der Links für die jeweilign Lieferanten
		for (Lieferant lieferant : all) {
			setStructuralLinks(lieferant, uriInfo);
		}
		return Response.ok(new GenericEntity<List<Lieferant>>(all) {
		}).links(getTransitionalLinksLieferanten(all, uriInfo)).build();
	}

	// Lieferant mit ID finden
	@GET
	@Path("{" + LIEFERANTEN_ID_PATH_PARAM + ":[1-9][0-9]*}")
	public Response findLieferantById(@PathParam(LIEFERANTEN_ID_PATH_PARAM) Long id) {
		final Lieferant lieferant = ls.findLieferantById(id, FetchType.NUR_LIEFERANT);
		if (lieferant == null) {
			throw new NotFoundException("Kein Lieferant mit der ID " + id
					+ " gefunden.");
		}
		setStructuralLinks(lieferant, uriInfo);
		return Response.ok(lieferant).links(getTransitionalLinks(lieferant, uriInfo))
				.build();
	}

	@GET
	public Response findLieferanten(
			@QueryParam(LIEFERANTEN_FIRMA_QUERY_PARAM) @Pattern(regexp = Lieferant.
					FIRMA_PATTERN_PUB, message = "{lieferant.firma.pattern}") String firma,
			@QueryParam(LIEFERANTEN_PLZ_QUERY_PARAM) @Pattern(regexp = "\\d{5}",
					message = "{lieferantenadresse.plz}") String plz,
			@QueryParam(LIEFERANTEN_EMAIL_QUERY_PARAM) @Email(message = "{lieferant.email}")
					String email) {
		List<? extends Lieferant> lieferanten = null;
		Lieferant lieferant = null;
		if (!Strings.isNullOrEmpty(firma)) {
			lieferanten = ls.findLieferantenByFirma(firma, FetchType.NUR_LIEFERANT);
		} 
		else if (!Strings.isNullOrEmpty(plz)) {
			lieferanten = ls.findLieferantenByPLZ(plz);
		}
		else if (!Strings.isNullOrEmpty(email)) {
			lieferant = ls.findLieferantByEmail(email);
		}
		else {
			lieferanten = ls.findAllLieferanten(FetchType.NUR_LIEFERANT, OrderType.ID);
		}

		Object entity = null;
		Link[] links = null;
		if (lieferanten != null) {
			for (Lieferant k : lieferanten) {
				setStructuralLinks(k, uriInfo);
			}
			// FIXME JDK 8 hat Lambda-Ausdruecke
			// lieferanten.parallelStream()
			// .forEach(k -> setStructuralLinks(k, uriInfo));
			entity = new GenericEntity<List<? extends Lieferant>>(lieferanten) { };
			links = getTransitionalLinksLieferanten(lieferanten, uriInfo);
		}
		else if (lieferant != null) {
			entity = lieferant;
			links = getTransitionalLinks(lieferant, uriInfo);
		}

		return Response.ok(entity).links(links).build();
	}

	/**
	 * IDs mit gleichem Praefix suchen
	 * 
	 * @param idPrefix
	 *            Der gemeinsame Praefix
	 * @return Collection der IDs mit gleichem Praefix
	 */
	@GET
	@Path("/prefix/id/{id:[1-9][0-9]*}")
	public Collection<Long> findIdsByPrefix(@PathParam("id") String idPrefix) {
		final Collection<Long> ids = ls.findIdsByPrefix(idPrefix);
		return ids;
	}

	/**
	 * Firman mit gleichem Praefix suchen
	 * 
	 * @param nachnamePrefix
	 *            Der gemeinsame Praefix
	 * @return Collection der Firman mit gleichem Praefix
	 */
	@GET
	@Path("/prefix/firma/{firma}")
	@Produces({ APPLICATION_JSON, TEXT_PLAIN + ";qs=0.75" })
	public Collection<String> findFirmanByPrefix(
			@PathParam("firma") String nachnamePrefix) {
		final Collection<String> nachnamen = ls
				.findFirmaByPrefix(nachnamePrefix);
		return nachnamen;
	}

	// Lieferanten bei Firma finden
	@GET
	public Response findLieferantenByFirma(
			@QueryParam(LIEFERANTEN_FIRMA_QUERY_PARAM) String firma) {
		List<? extends Lieferant> lieferanten = null;
		if (firma != null) {
			lieferanten = ls.findLieferantenByFirma(firma, FetchType.NUR_LIEFERANT);
			if (lieferanten.isEmpty()) {
				throw new NotFoundException("Kein Lieferant mit Firma "
						+ firma + " gefunden.");
			}
		}
		else {
			lieferanten = ls.findAllLieferanten(FetchType.NUR_LIEFERANT, OrderType.KEINE);
			if (lieferanten.isEmpty()) {
				throw new NotFoundException("Keine Lieferanten vorhanden.");
			}
		}

		for (Lieferant k : lieferanten) {
			setStructuralLinks(k, uriInfo);
		}

		return Response
				.ok(new GenericEntity<List<? extends Lieferant>>(lieferanten) {
				}).links(getTransitionalLinksLieferanten(lieferanten, uriInfo)).build();
	}

	@GET
	@Path("{" + LIEFERANTEN_ID_PATH_PARAM + ":[1-9][0-9]*}/einkaeufe")
	public Response findEinkaeufeByLieferantId(
			@PathParam(LIEFERANTEN_ID_PATH_PARAM) Long lieferantId) {
		final Lieferant lieferant = ls.findLieferantById(lieferantId,
				FetchType.NUR_LIEFERANT);
		final List<Einkauf> einkaeufe = es.findEinkaeufeByLieferant(lieferant);
		if (einkaeufe.isEmpty()) {
			throw new NotFoundException("Zur ID " + lieferantId
					+ " wurden keine Einkaeufe gefunden");
		}

		// URIs innerhalb der gefundenen Einkaeufe anpassen
		for (Einkauf einkauf : einkaeufe) {
			einkaufResource.setStructuralLinks(einkauf, uriInfo);
		}

		// Rückgabe einer Generische Liste von Einkaeufe
		return Response
				.ok(new GenericEntity<List<Einkauf>>(einkaeufe) {
				})
				.links(getTransitionalLinksEinkaeufe(einkaeufe, lieferant,
						uriInfo)).build();
	}

	/**
	 * Einkauf-IDs zu einem Lieferanten suchen
	 * 
	 * @param lieferantId
	 *            ID des Lieferanten
	 * @return Liste der Einkauf-IDs
	 */
	@GET
	@Path("{id:[1-9][0-9]*}/bestellungenIds")
	@Produces({ APPLICATION_JSON, TEXT_PLAIN + ";qs=0.75",
			APPLICATION_XML + ";qs=0.5" })
	public Response findEinkaeufeIdsByLieferantId(@PathParam("id") Long lieferantId) {
		final Lieferant lieferant = ls.findLieferantById(lieferantId,
				FetchType.MIT_EINKAEUFEN);

		final Collection<Einkauf> einkaeufe = es.findEinkaeufeByLieferant(lieferant);
		final int anzahl = einkaeufe.size();
		final Collection<Long> bestellungenIds = new ArrayList<>(anzahl);
		for (Einkauf einkauf : einkaeufe) {
			bestellungenIds.add(einkauf.getId());
		}
		// FIXME JDK 8 hat Lambda-Ausdruecke
		// einkaeufe.parallelStream()
		// .map(Einkauf::getId)
		// .forEach(id -> bestellungenIds.add(id));

		return Response.ok(
				new GenericEntity<Collection<Long>>(bestellungenIds) {
				}).build();
	}

	@POST
	@Consumes({ APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
	@Produces
	public Response createLieferant(@Valid Lieferant lieferant) {
		lieferant.setId(KEINE_ID);

		final Lieferantenadresse lieferantenadresse = lieferant.getLieferantenadresse();
		if (lieferantenadresse != null) {
			lieferantenadresse.setLieferant(lieferant);
		}

		lieferant = ls.createLieferant(lieferant);
		LOGGER.tracef("Lieferant: %s", lieferant);

		return Response.created(getUriLieferant(lieferant, uriInfo)).build();
	}

	@PUT
	@Consumes({ APPLICATION_JSON, APPLICATION_XML, TEXT_XML })
	@Produces
	public void updateLieferant(@Valid Lieferant lieferant) {
		// Vorhandenen Lieferanten ermitteln
		final Lieferant origLieferant = ls.findLieferantById(lieferant.getId(),
				FetchType.NUR_LIEFERANT);
		LOGGER.tracef("Lieferant vorher: %s", origLieferant);

		// Daten des vorhandenen Lieferanten ueberschreiben
		origLieferant.setValues(lieferant);
		LOGGER.tracef("Lieferant nachher: %s", origLieferant);

		// Update durchfuehren
		ls.updateLieferant(origLieferant);
	}

	@Path("{id:[0-9]+}")
	@DELETE
	@Produces
	public void deleteLieferant(@PathParam("id") Long lieferantId) {
		final Lieferant lieferant = ls.findLieferantById(lieferantId,
				FetchType.NUR_LIEFERANT);
		ls.deleteLieferant(lieferant);
	}

	private Link[] getTransitionalLinksEinkaeufe(
			List<Einkauf> einkaeufe, Lieferant lieferant, UriInfo uriInfo) {
		if (einkaeufe == null || einkaeufe.isEmpty()) {
			return new Link[0];
		}

		final Link self = Link.fromUri(getUriEinkaeufe(lieferant, uriInfo))
				.rel(SELF_LINK).build();

		final Link first = Link
				.fromUri(
						einkaufResource.getUriEinkauf(
								einkaeufe.get(0), uriInfo)).rel(FIRST_LINK)
				.build();
		final int lastPos = einkaeufe.size() - 1;

		final Link last = Link
				.fromUri(
						einkaufResource.getUriEinkauf(
								einkaeufe.get(lastPos), uriInfo))
				.rel(LAST_LINK).build();

		return new Link[] {self, first, last };
	}

	// Einkaufslink setzen
	private void setStructuralLinks(Lieferant lieferant, UriInfo uriInfo) {
		// URI fuer Einkaeufe setzen
		final URI uri = getUriEinkaeufe(lieferant, uriInfo);
		lieferant.setEinkaeufeURI(uri);
	}

	// EinkaufURI erzeugen
	private URI getUriEinkaeufe(Lieferant lieferant, UriInfo uriInfo) {
		// return URI.create("http://localhost:8080/shop/rest/");
		return uriHelper.getUri(LieferantenResource.class,
				"findEinkaeufeByLieferantId", lieferant.getId(), uriInfo);
	}

	public URI getUriLieferant(Lieferant lieferant, UriInfo uriInfo) {
		return uriHelper.getUri(LieferantenResource.class, "findLieferantById",
				lieferant.getId(), uriInfo);
	}

	// VerwaltungsURIs erzeugen
	private Link[] getTransitionalLinks(Lieferant lieferant, UriInfo uriInfo) {
		final Link self = Link.fromUri(getUriLieferant(lieferant, uriInfo))
				.rel(SELF_LINK).build();
		System.out.println("Self gesetzt");
		final Link add = Link
				.fromUri(uriHelper.getUri(LieferantenResource.class, uriInfo))
				.rel(ADD_LINK).build();
		System.out.println("Add gesetzt");
		final Link update = Link
				.fromUri(uriHelper.getUri(LieferantenResource.class, uriInfo))
				.rel(UPDATE_LINK).build();
		System.out.println("Update gesetzt");

		final Link remove = Link
				.fromUri(
						uriHelper.getUri(LieferantenResource.class, "deleteLieferant",
								lieferant.getId(), uriInfo)).rel(REMOVE_LINK)
				.build();
		System.out.println("Delete gesetzt");
		return new Link[] {self, add, update, remove };
	}

	private Link[] getTransitionalLinksLieferanten(
			List<? extends Lieferant> lieferanten, UriInfo uriInfo) {
		if (lieferanten == null || lieferanten.isEmpty()) {
			return null;
		}

		final Link first = Link.fromUri(getUriLieferant(lieferanten.get(0), uriInfo))
				.rel(FIRST_LINK).build();
		final int lastPos = lieferanten.size() - 1;
		final Link last = Link
				.fromUri(getUriLieferant(lieferanten.get(lastPos), uriInfo))
				.rel(LAST_LINK).build();

		return new Link[] {first, last };
	}

}
