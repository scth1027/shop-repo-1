package de.shop.lieferantenverwaltung.rest;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import de.shop.lieferantenverwaltung.service.LieferantDeleteEinkaufException;
import de.shop.util.interceptor.Log;
import de.shop.util.rest.Messages;

@Provider
@ApplicationScoped
@Log
public class LieferantDeleteEinkaufExceptionMapper implements
		ExceptionMapper<LieferantDeleteEinkaufException> {
	@Context
	private HttpHeaders headers;

	@Inject
	private Messages messages;

	@Override
	public Response toResponse(LieferantDeleteEinkaufException e) {
		final String msg = messages.getMessage(headers, e.getMessageKey(),
				e.getLieferantId(), e.getAnzahlEinkaeufe());
		return Response.status(BAD_REQUEST).type(TEXT_PLAIN).entity(msg)
				.build();
	}
}
