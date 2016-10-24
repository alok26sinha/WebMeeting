package subsystems.mail;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.mail.FetchProfile;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import org.apache.commons.logging.LogFactory;

import common.Config;
import common.UncheckedException;

public class InboundServer {
	private static org.apache.commons.logging.Log log = LogFactory
			.getLog(InboundServer.class);

	Store store;
	Folder folder;

	public void connect(String host, String user, String password) {
		try {
			log.debug("Connect to mail host:" + host + " user:" + user);
			// Get a Properties object
			Properties props = System.getProperties();

			props.setProperty("mail.store.protocol", "imaps");	
			props.setProperty("mail.imap.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.setProperty("mail.imap.socketFactory.fallback", "false");
			
			// Get a Session object
			Session session = Session.getDefaultInstance(props, null);
			session.setDebug(log.isDebugEnabled());

			//Used to use POP3 but IMAP look strong enough now
			Store store = session.getStore("imaps");
			store.connect(host, -1, user, password);
			
			folder = store.getDefaultFolder();
			folder = folder.getFolder("INBOX");
			folder.open(Folder.READ_WRITE);

		} catch (MessagingException e) {
			throw new UncheckedException("Failed to connect to to mail host:"
					+ host + " user:" + user, e);
		}
	}
	private static void setPropertyValue(Properties props, String key) {
		String value = Config.getInstance().getValue(key);
		props.put(key, value);
	}
	public List<InboundMessage> getAllMessages() {
		try {

			List<InboundMessage> messages = new ArrayList<InboundMessage>();
			
			Message[] msgs = folder.getMessages();

			// Use a suitable FetchProfile
			FetchProfile fp = new FetchProfile();
			fp.add(FetchProfile.Item.ENVELOPE);
			fp.add(FetchProfile.Item.FLAGS);
			fp.add(FetchProfile.Item.CONTENT_INFO);
			fp.add("X-Mailer");
			folder.fetch(msgs, fp);

			for (int i = 0; i < msgs.length; i++) {
				log.debug("Found message:" + msgs[i]);
				InboundMessage imapMessage = new InboundMessage();
				imapMessage.setMessage( msgs[i]);
				messages.add(imapMessage);
			}
			
			return messages;
			
		} catch (MessagingException e) {
			throw new UncheckedException("Failed to get messages", e);
		}
	}

	public void close() {
		log.debug("Closing connection to mail server");
		try {
			if (folder != null && folder.isOpen())
				folder.close(true);
			if (store != null)
				store.close();
		} catch (MessagingException e) {
			log.warn("Failed to disconnect." + e.getMessage());

		}
	}
}
