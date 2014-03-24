package org.openmrs.module.messaging.email;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.Flags.Flag;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.search.FlagTerm;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openmrs.Person;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.messaging.EncryptionService;
import org.openmrs.module.messaging.domain.Message;
import org.openmrs.module.messaging.domain.MessageRecipient;
import org.openmrs.module.messaging.domain.MessageStatus;
import org.openmrs.module.messaging.domain.gateway.MessagingGateway;
import org.openmrs.module.messaging.domain.gateway.Protocol;
import org.openmrs.module.messaging.util.MessagingConstants;
import org.openmrs.notification.MessageException;
import org.springframework.util.StringUtils;

/**
 * Email gateway for use with the Messaging Module
 */
public class EmailGateway extends MessagingGateway {

	/**
	 * logging facility
	 */
	private static Log log = LogFactory.getLog(EmailGateway.class);
	
	/**
	 * incoming mail session
	 */
	private Session inSession;
	
	/**
	 * outgoing mail session
	 */
	private Session outSession;

	/**
	 * send a message
	 */
	@Override
	public void sendMessage(Message message, MessageRecipient recipient) throws Exception {
		
		// fail when outSession does not exist
		if (outSession == null)
			throw new MessageException("outgoing server session is not turned on");

		// fail if there is no destination
		if (message.getTo() == null || message.getTo().size() < 1)
			throw new MessageException("Message must contain at least one recipient");

		// start a new mime message
		MimeMessage mimeMessage = new MimeMessage(outSession);
		
		// set sender
		if (message.getSender() != null) {
			InternetAddress sender = new InternetAddress();
			sender.setAddress(recipient.getOrigin());
			// TODO make the sender's name a global property / configurable
			sender.setPersonal("OpenMRS");
			mimeMessage.setSender(sender);
		}
		
		// set recipient
		mimeMessage.setRecipients(javax.mail.Message.RecipientType.TO,
				InternetAddress.parse(recipient.getRecipient().getAddress(), false));

		// set subject
		// TODO allow for text replacement in the subject (i.e. %U = sender's username)
		mimeMessage.setSubject(Context.getAdministrationService()
				.getGlobalProperty(MessagingConstants.GP_EMAIL_SUBJECT));
		
		// set content
		// TODO make this prettier
		StringBuilder content = new StringBuilder();
		content.append(message.getContent());
		String signature = Context.getAdministrationService().getGlobalProperty(MessagingConstants.GP_EMAIL_SIGNATURE); 
		if (StringUtils.hasText(signature)) {
			content.append("\n\n----------------------------------\n");
			content.append(signature);
		}
		mimeMessage.setContent(content.toString(), "text/plain");
 
		// send the message
		EncryptionService encryptionService = (EncryptionService) Context.getService(EncryptionService.class); 
		try {
			Transport t = outSession.getTransport(Context
					.getAdministrationService().getGlobalProperty(
							MessagingConstants.GP_EMAIL_OUT_PROTOCOL));

			t.connect(
					Context.getAdministrationService().getGlobalProperty(
							MessagingConstants.GP_EMAIL_OUT_HOST),
					Integer.valueOf(Context.getAdministrationService()
							.getGlobalProperty(
									MessagingConstants.GP_EMAIL_OUT_PORT)),
					Context.getAdministrationService().getGlobalProperty(
							MessagingConstants.GP_EMAIL_OUT_USERNAME),
					encryptionService.decrypt(Context
							.getAdministrationService().getGlobalProperty(
									MessagingConstants.GP_EMAIL_OUT_PASSWORD)));
			
			t.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
			
		} catch (MessagingException e) {
			log.error("failed to send message", e);			
			throw new MessageException(e);
		}
		
		log.debug("Email message sent to " + recipient.getRecipient().getAddress() + " successfully");
	}

	/**
	 * receive messages
	 */
	@Override
	public void receiveMessages() {
		log.info("Starting Recieving email messages");
		if (inSession == null) {
			log.error("cannot recieve messages; gateway is off");
			return;
		}
		
		Store store = null;
		Folder folder = null;
		
		try {
			// get the store
			store = inSession.getStore();
			if (store == null) {
				return;
			}
			
			// connect to the store
			EncryptionService encryptionService = (EncryptionService) Context.getService(EncryptionService.class); 
			store.connect(
					Context.getAdministrationService().getGlobalProperty(
							MessagingConstants.GP_EMAIL_IN_HOST),
					Context.getAdministrationService().getGlobalProperty(
							MessagingConstants.GP_EMAIL_IN_USERNAME),
					encryptionService.decrypt(Context
							.getAdministrationService().getGlobalProperty(
									MessagingConstants.GP_EMAIL_IN_PASSWORD)));

			// get the default folder
			folder = store.getDefaultFolder();
			if (folder == null) {
				log.error("No default folder in the incoming email store");
				return;
			}
			
			// open the inbox
			Folder inbox = folder.getFolder("INBOX");
			if (inbox == null) {
				log.error("No inbox folder in the incoming email store");
				return;
			}
			
			// process the unread mail
			inbox.open(Folder.READ_WRITE);
			javax.mail.Message[] messages = inbox.search(new FlagTerm(new Flags(Flag.SEEN), false));
			for (javax.mail.Message message: messages)
				processMessage(message);
			
			// close the store
			// TODO allow config setting for expunging mail
			inbox.close(false);
			store.close();

			log.debug("Successfully retrieved incoming email messages");
			
		} catch (NoSuchProviderException e) {
			log.error("No mail provider found", e);
			return;
		} catch (MessagingException e) {
			log.error("Error connecting to the incoming email store", e);
			return;
		}catch(Throwable t){
			log.error("Error Recieving email messages",t);
		}finally{
			log.info("In Finally block");
		}
		log.info("Done Recieving email messages");
	}

	private void processMessage(javax.mail.Message message) throws MessagingException {
		Person sender = null;
		String origin =null;
		try {
			// get the OpenMRS person for the sender
			Address[] senders = message.getFrom();
			if (senders.length == 0) {
				log.error("no senders found on incoming email message");
				return;
			}
			int i = 0;
			boolean found = false;
			while (!found && i < senders.length) {
				InternetAddress s = (InternetAddress) senders[i];
				sender = this.getAddressService().getPersonForAddress(s.getAddress());
				if (sender == null){
					log.error("no person could be found for sender: " + s.toString());
				}else{
					found = true;
					origin = s.getAddress();
				}
				i++;
			}
			if (sender == null) {
				log.error("no sender could be found for message #" + message.getMessageNumber());
				return;
			}
			
			// get the body of the message
			String content = null;
			if (StringUtils.startsWithIgnoreCase(message.getContentType(), "text/plain")) {
	            content = message.getContent().toString();
           
			} else if (StringUtils.startsWithIgnoreCase(message.getContentType(), "multipart/")) {
	            Multipart multipart = (Multipart) message.getContent();
	            List<String> contents = new ArrayList<String>();
	            
	            for (int x = 0; x < multipart.getCount(); x++) {
	                BodyPart bodyPart = multipart.getBodyPart(x);
	                String disposition = bodyPart.getDisposition();

	                // prefer text/plain but accept text/html
	                if (disposition == null || (disposition != null && !disposition.equals(BodyPart.ATTACHMENT)))
	        			if (StringUtils.startsWithIgnoreCase(bodyPart.getContentType(), "text/plain"))
	        				content = (String) bodyPart.getContent();
	        			else if (content == null && StringUtils.startsWithIgnoreCase(bodyPart.getContentType(), "text/html"))
	        				content = (String) bodyPart.getContent();
	            }
	            
	            if (!contents.isEmpty())
	            	content = StringUtils.arrayToDelimitedString(contents.toArray(), "\n");
	            	
			} else {
				log.error("Could not process message with content type: " + message.getContentType());
				return;
			}
			
			// post the message
			org.openmrs.module.messaging.domain.Message m = new org.openmrs.module.messaging.domain.Message(content,"",EmailProtocol.class);
			m.setSender(sender);
			m.setStatus(MessageStatus.RECEIVED);
			m.setOrigin(origin);
			m.setDate(message.getSentDate());
			this.getMessageService().saveMessage(m);
			
			// mark the message as seen
			message.setFlag(Flag.SEEN, true);			
		} catch (IOException e) {
			log.error("could not read message content due to an I/O error", e);
		} catch (MessagingException e) {
			log.error("could not read message content", e);
		}
	}

	/**
	 * whether this gateway can send email
	 */
	@Override
	public boolean canSend() {
		return outSession != null;
	}

	/**
	 * whether this gateway can receive email
	 */
	@Override
	public boolean canReceive() {
		return false;
	}

	/**
	 * provide whether the SMTP gateway is active
	 */
	@Override
	public boolean isActive() {
		return (inSession != null) && (outSession != null);
	}

	@Override
	public void startup() {
		log.info("Starting up Email Gateway");
		AdministrationService adminService = Context.getAdministrationService();
		
		// get in and out protocols
		String inProtocol = adminService.getGlobalProperty(MessagingConstants.GP_EMAIL_IN_PROTOCOL);
		String outProtocol = adminService.getGlobalProperty(MessagingConstants.GP_EMAIL_OUT_PROTOCOL);
		
		// get the incoming server settings
		Properties inProps = new Properties();
		inProps.setProperty("mail.debug", "false");
		inProps.setProperty("mail.store.protocol", inProtocol);
		inProps.setProperty("mail." + inProtocol + ".host",
				adminService.getGlobalProperty(MessagingConstants.GP_EMAIL_IN_HOST));
		inProps.setProperty("mail." + inProtocol + ".port",
				adminService.getGlobalProperty(MessagingConstants.GP_EMAIL_IN_PORT));
		inProps.setProperty("mail." + inProtocol + ".auth",
				adminService.getGlobalProperty(MessagingConstants.GP_EMAIL_IN_AUTH));
		inProps.setProperty("mail." + inProtocol + ".starttls.enable",
				adminService.getGlobalProperty(MessagingConstants.GP_EMAIL_IN_TLS));

		// get the outgoing server settings
		Properties outProps = new Properties();
		outProps.setProperty("mail.debug", "true");
		if (Boolean.valueOf(adminService.getGlobalProperty(MessagingConstants.GP_EMAIL_OUT_USEDEFAULT))) {
			// use OpenMRS default
			outProtocol = adminService.getGlobalProperty("mail.transport_protocol");
			outProps.setProperty("mail.transport.protocol", outProtocol);
			outProps.setProperty("mail." + outProtocol + ".host", 
					adminService.getGlobalProperty("mail.smtp_host"));
			outProps.setProperty("mail." + outProtocol + ".port", 
					adminService.getGlobalProperty("mail.smtp_port"));
			outProps.setProperty("mail." + outProtocol + ".from", 
					adminService.getGlobalProperty("mail.from"));
			outProps.setProperty("mail." + outProtocol + ".auth", 
					adminService.getGlobalProperty("mail.smtp_auth"));
			outProps.setProperty("mail." + outProtocol + ".starttls.enable", 
					adminService.getGlobalProperty("mail.use_tls", "false"));
		} else {
			// use ours
			outProps.setProperty("mail.transport.protocol", outProtocol);
			outProps.setProperty("mail." + outProtocol + ".host",
					adminService.getGlobalProperty(MessagingConstants.GP_EMAIL_OUT_HOST));
			outProps.setProperty("mail." + outProtocol + ".port",
					adminService.getGlobalProperty(MessagingConstants.GP_EMAIL_OUT_PORT));
			outProps.setProperty("mail." + outProtocol + ".from",
					adminService.getGlobalProperty(MessagingConstants.GP_EMAIL_OUT_FROM));
			outProps.setProperty("mail." + outProtocol + ".auth",
					adminService.getGlobalProperty(MessagingConstants.GP_EMAIL_OUT_AUTH));
			outProps.setProperty("mail." + outProtocol + ".starttls.enable",
					adminService.getGlobalProperty(MessagingConstants.GP_EMAIL_OUT_TLS));
		}
		
		// initialize sessions
		inSession = Session.getInstance(inProps, null);
		outSession = Session.getInstance(outProps, null);
		log.info("Done Starting up Email Gateway");
	}

	@Override
	public void shutdown() {
		// close sessions
		inSession = null;
		outSession = null;
	}

	/**
	 * provide the name of this gateway
	 */
	@Override
	public String getName() {
		return "Email";
	}

	/**
	 * provide the description of this gateway
	 */
	@Override
	public String getDescription() {
		return "A gateway for sending Email";
	}

	/**
	 * validate support of a given protocol
	 */
	@Override
	public boolean supportsProtocol(Class<? extends Protocol> p) {
		if (p == null)
			return false;
		return p.equals(EmailProtocol.class);
	}
}
