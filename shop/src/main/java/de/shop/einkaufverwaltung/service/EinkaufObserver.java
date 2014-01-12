package de.shop.einkaufverwaltung.service;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.invoke.MethodHandles;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.jboss.logging.Logger;

import de.shop.einkaufverwaltung.domain.Einkauf;
import de.shop.lieferantenverwaltung.domain.Lieferant;
import de.shop.util.interceptor.Log;
import de.shop.util.mail.AbsenderMail;
import de.shop.util.mail.AbsenderName;

@Log
@ApplicationScoped
public class EinkaufObserver implements Serializable {
	private static final long serialVersionUID = 6676220131351311744L;
	private static final Logger LOGGER = Logger.getLogger(MethodHandles
			.lookup().lookupClass());

	@Inject
	private transient Session session;

	@Inject
	@AbsenderMail
	private String absenderMail;

	@Inject
	@AbsenderName
	private String absenderName;

	@PostConstruct
	private void postConstruct() {
		if (absenderMail == null) {
			LOGGER.warn("Der Absender fuer Einkaufs-Emails ist nicht gesetzt.");
			return;
		}
	}

	public void onCreateEinkauf(
			@Observes @NeuerEinkauf Einkauf einkauf) {
		final Lieferant lieferant = einkauf.getLieferant();
		final String empfaengerMail = lieferant.getEmail();
		if (absenderMail == null || empfaengerMail == null) {
			return;
		}
		final String empfaengerName = lieferant.getFirma();

		final MimeMessage message = new MimeMessage(session);

		try {
			// Absender setzen
			final InternetAddress absenderObj = new InternetAddress(absenderMail, absenderName);
			message.setFrom(absenderObj);

			// Empfaenger setzen
			final InternetAddress empfaenger = new InternetAddress(empfaengerMail, empfaengerName);
			message.setRecipient(RecipientType.TO, empfaenger); // RecipientType:
																// TO, CC, BCC

			// Subject setzen
			message.setSubject("Neue Einkauf Nr. " + einkauf.getId());

			// Text setzen mit MIME Type "text/plain"
			final String text = "<h3>Neue Einkauf Nr. <b>"
					+ einkauf.getId() + "</b></h3>";
			LOGGER.trace(text);
			message.setContent(text, "text/html;charset=iso-8859-1");

			// Hohe Prioritaet einstellen
			// message.setHeader("Importance", "high");
			// message.setHeader("Priority", "urgent");
			// message.setHeader("X-Priority", "1");

			Transport.send(message);
		} catch (MessagingException | UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage());
			return;
		}
	}
}
