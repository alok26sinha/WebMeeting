package security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import model.Guest;
import model.Person;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Repository;

import security.passwordhash.PasswordHash;
import security.passwordhash.RandomGenerator;
import service.EventService;
import subsystems.mail.MailMessage;
import subsystems.mail.OutboundService;
import type.StringUtils;

import common.Config;
import common.UncheckedException;

import dao.RecordNotFoundException;
import dao.SecurityDao;

@Repository
public class SecurityService {
	private static Log log = LogFactory.getLog(SecurityService.class);

	static OutboundService mailService = new OutboundService();
	static RandomGenerator randomGenerator = new RandomGenerator();
	static PasswordHash passwordHash = new PasswordHash();

	private static final int SIGNIFICANT_BYTES = 64; // 512 bits

	private SecurityDao securityDao;
	@Resource
	private EventService eventService;

	// private MailMessageDao mailMessageDao;

	/**
	 * Does this password token exist
	 */
	public boolean passwordTokenExists(String passwordToken) {
		try {
			securityDao.getPersonForPasswordToken(passwordToken);
			return true;
		} catch (RecordNotFoundException e) {
			return false;
		}

	}

	/**
	 * Log this person on.
	 * 
	 * The userToken will be updated to a new value.
	 * 
	 * @param email
	 * @param password
	 *            clear text password
	 * @return the person record for this email address
	 * @throws RecordNotFoundException
	 *             if the email and password do not match a person
	 */

	public Person login(String email, String password) {
		if (email != null)
			email = email.trim();

		Person person = securityDao.getPersonForEmail(email);
		if (person.salt != null) {
			if (log.isDebugEnabled())
				log.debug("Running new security");
			return newLogin(email, password);
		} else {
			if (log.isDebugEnabled())
				log.debug("Running old security");
			return oldLogin(email, password);
		}
	}

	private Person newLogin(String email, String password) {
		Person person = securityDao.getPersonForEmail(email);

		String salt = person.salt;
		String calculatedHash = passwordHash.hash(password, salt);

		if (StringUtils
				.nullSafeEquals(calculatedHash, person.encryptedPassword)) {

			log.info("Successfully login for:" + email);

			// Do we need to set a user token
			if (person.userToken == null) {
				String userToken = generateNewUserToken();
				person.userToken = userToken;
			}
			
			return person;
		} else
			throw new RecordNotFoundException(
					"Could not validate user and password");
	}

	private Person oldLogin(String email, String password) {
		String encryptedPassword = encryptPassword(password);
		Person person = securityDao.getPersonForEmailAndPassword(email,
				encryptedPassword);

		log.info("Successfully login for:" + email);

		// Do we need to set a user token
		if (person.userToken == null) {
			String userToken = generateNewUserToken();
			person.userToken = userToken;
		}

		return person;
	}

	/**
	 * Log a guest on.
	 * 
	 * @param guest
	 *            The guest to log on
	 * @return The guest person
	 */
	public Person loginGuest(Guest guest) {
		Person person = guest.person;
		log.info("Guest login for:" + person.email);

		if (person.userToken == null) {
			String userToken = generateNewUserToken();
			person.userToken = userToken;
		}

		return person;
	}

	/**
	 * Authenticate the user and password
	 */
	public Person authenticate(String email, String password) {
		String encryptedPassword = encryptPassword(password);
		Person person = securityDao.authenticate(email, encryptedPassword);
		return person;
	}

	/**
	 * Send this person a new set password link.
	 * 
	 * The userToken will be nulled and a new passwordToken will be created.
	 * 
	 * @param email
	 */

	public void sendLinkEmail(String email) {
		log.info("Sending a new email link to email");
		try {
			if(email != null)
				email = email.trim().toLowerCase();
			
			Person person = securityDao.getPersonForEmail(email);
			person.userToken = null;
			person.salt = null;
			String passwordToken = generateNewPasswordToken();
			person.passwordToken = passwordToken;
			
			eventService.logEvent(person, "Sending Welcm Email");

			MailMessage mailMessage = new MailMessage();
			mailMessage.setTo(email);
			String adminUser = Config.getInstance().getValue("mail.smtp.user");
			mailMessage.setFrom(adminUser);
			mailMessage.setSubject("Welcome to Meeteorite, the world's No. 1 meeting productivity software.");

			StringBuffer content = new StringBuffer();
			content.append("<p>Please click on the link below to set your password.</p>\n");
			String appUrl = Config.getInstance().getValue("app.url");
			content.append("<p><a href='" + appUrl
					+ "/support/SetPassword!displayPage.action?passwordToken="
					+ person.passwordToken + "'>Set your password here</a></p>");
			
			mailMessage.setContent(content.toString());

			mailService.sendAsynchronous(mailMessage, null);

		} catch (Exception e) {
			UncheckedException failedSendEmailLink = new UncheckedException(
					"Failed to send email link for email:" + email, e);
			log.error("Failed to send email", failedSendEmailLink);
			mailService.sendErrorEmail(e);
		}

	}

	/**
	 * Set the password for this passwordToken and log the person in
	 * 
	 * @param passwordToken
	 * @param clearTextPassword
	 * @return the corresponding person
	 * @throws RecordNotFoundException
	 *             if no person with this password token exists
	 */

	public Person setPassword(String passwordToken, String clearTextPassword) {
		log.info("Setting new password");

		Person person = securityDao.getPersonForPasswordToken(passwordToken);

		String salt = randomGenerator.getNewRandom(SIGNIFICANT_BYTES);
		person.salt = salt;

		String hash = passwordHash.hash(clearTextPassword, salt);
		person.encryptedPassword = hash;

		person.passwordToken = null;

		securityDao.flush();

		// Log the person in to generate the new user token person =
		login(person.email, clearTextPassword);

		return person;
	}

	// Private supporting methods

	private String generateToken() {
		RandomGenerator randomGenerator = new RandomGenerator();
		return randomGenerator.getNewRandom(64);
	}

	private String encryptPassword(String clearTextPassword) {

		// XOR the clear text
		byte[] xor = xor(clearTextPassword.getBytes(), 126);

		// Convert to md5 sum
		byte[] digest = md5Sum(xor);

		// Format as a string
		String hexString = formatAsHexString(digest);

		return hexString;

	}

	// ----------------------------------------------------------------------------------------
	// Working with Hex strings and byte arrays
	private String formatAsHexString(byte[] digest) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < digest.length; i++) {
			String plainText = Integer.toHexString(0xFF & digest[i]);

			if (plainText.length() < 2) {
				plainText = "0" + plainText;
			}

			hexString.append(plainText);
		}
		return hexString.toString();
	}

	private byte[] formatAsByteArray(String hexString) {
		List<Byte> bytes = new ArrayList<Byte>();

		while (hexString.length() > 0) {
			String token = hexString.substring(0, 2);
			hexString = hexString.substring(2);

			byte thisByte = (byte) Integer.parseInt(token, 16);
			bytes.add(thisByte);
		}

		byte[] byteArray = new byte[bytes.size()];
		for (int i = 0; i < bytes.size(); i++) {
			byteArray[i] = bytes.get(i).byteValue();
		}
		return byteArray;
	}

	// --------------------------------------------------------------
	// Low level numerical

	private byte[] md5Sum(byte[] source) {
		try {
			MessageDigest mdAlgorithm = MessageDigest.getInstance("MD5");
			mdAlgorithm.update(source);

			byte[] digest = mdAlgorithm.digest();
			return digest;
		} catch (NoSuchAlgorithmException e) {
			throw new UncheckedException("Could not encrypt password", e);
		}
	}

	private byte[] xor(byte[] source, int pattern) {
		byte[] result = new byte[source.length];
		for (int i = 0; i < source.length; i++) {
			result[i] = (byte) (source[i] ^ pattern);
		}
		return result;
	}

	private String generateNewUserToken() {
		String userToken;
		do {
			userToken = generateToken();
		} while (userTokenExists(userToken));
		return userToken;
	}

	private boolean userTokenExists(String userToken) {
		try {
			securityDao.getPersonForUserToken(userToken);
			return true;
		} catch (RecordNotFoundException e) {
			return false;
		}
	}

	private String generateNewPasswordToken() {
		String passwordToken;
		do {
			passwordToken = generateToken();
		} while (passwordTokenExists(passwordToken));
		return passwordToken;
	}

	// Getters and setters

	public void setSecurityDao(SecurityDao securityDao) {
		this.securityDao = securityDao;
	}

	/*
	 * public void setMailMessageDao(MailMessageDao mailMessageDao) {
	 * this.mailMessageDao = mailMessageDao; }
	 */

	public static void main(String[] args) {
		SecurityService s = new SecurityService();
		System.out.println(s.encryptPassword("p4ss1"));
	}
}
