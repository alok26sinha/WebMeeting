package security;

import javax.annotation.Resource;

import model.Person;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import service.SubscriptionService;
import type.StringUtils;
import dao.SecurityDao;

@Service("securityContextFactory")
public class SecurityContextFactory {
	private static Log log = LogFactory.getLog(SecurityContextFactory.class);

	SecurityDao securityDao;
	SecurityService securityService;
	@Resource
	private SubscriptionService subscriptionService;

	private static final Person unknownUser = new Person();
	static {
		unknownUser.name = "Unknown";
	}

	public SecurityContext createContextFromUserToken(String userToken) {
		ApplicationSecurityContext securityContext = new ApplicationSecurityContext();

		if (userToken != null && !"".equals(userToken)) {
			try {

				Person authenticatedPerson = securityDao
						.authenticate(userToken);
				
				if( subscriptionService.hasASubscription(authenticatedPerson))
					securityContext.setValidUser(true);
				else{
					log.warn("No subscriptions found for current user");
					securityContext.setValidUser(false);
				}
				
				securityContext.setUser(authenticatedPerson);

			} catch (NotAuthenticatedException e) {
				log.info(e.getMessage());
				securityContext.setValidUser(false);
				securityContext.setUser(unknownUser);
			}
		} else {
			log.debug("No token found");
			securityContext.setValidUser(false);
			securityContext.setUser(unknownUser);
		}

		return securityContext;
	}

	public SecurityContext createContext(String email, String clearPassword) {
		ApplicationSecurityContext securityContext = new ApplicationSecurityContext();

		if (!StringUtils.isEmpty(email) && !StringUtils.isEmpty(clearPassword)) {
			try {
				Person authenticatedPerson = securityService.authenticate(
						email, clearPassword);

				securityContext.setValidUser(true);
				securityContext.setUser(authenticatedPerson);

				securityContext.setUser(unknownUser);
				securityContext.setValidUser(true);
				unknownUser.administrator = true;

			} catch (NotAuthenticatedException e) {
				log.info(e.getMessage());
				securityContext.setValidUser(false);
				securityContext.setUser(unknownUser);
			}
		} else {
			log.debug("No Email and Password set");
			securityContext.setValidUser(false);
			securityContext.setUser(unknownUser);
		}

		return securityContext;
	}

	public void setSecurityDao(SecurityDao securityDao) {
		this.securityDao = securityDao;
	}

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

}
