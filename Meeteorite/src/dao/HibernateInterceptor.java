package dao;

import java.io.Serializable;

import model.Meeting;
import model.Person;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import security.LocalSecurityContext;
import security.SecurityContext;
import spring.LocalApplicationContext;

import common.UncheckedException;

@SuppressWarnings("serial")
public class HibernateInterceptor extends EmptyInterceptor {
	private static Log log = LogFactory.getLog(HibernateInterceptor.class);

	@Override
	public boolean onFlushDirty(Object entity, Serializable id,
			Object[] currentState, Object[] previousState,
			String[] propertyNames, Type[] types) {
		/*
		 * if (entity instanceof Job || entity instanceof Service) { for (int i
		 * = 0; i < propertyNames.length; i++) { if
		 * ("revision".equals(propertyNames[i])) { Integer revision =
		 * (Integer)previousState[i];
		 * 
		 * if( revision == null ){ revision = 1; } else{ revision = revision +
		 * 1; }
		 * 
		 * currentState[i] = revision; return true; } } }
		 */
		return false;
	}

	@Override
	public boolean onLoad(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {

		if (entity instanceof Meeting)
			checkAllowedAccessToMeeting(id);
		return false;
	}

	private void checkAllowedAccessToMeeting(Serializable id) {
		log.info("Checking user is allowed access to meeting id:" + id);

		SecurityContext securityContext = LocalSecurityContext.get();
		Person user = securityContext.getUser();

		if (!user.administrator) {

			GuestDao guestDao = (GuestDao) LocalApplicationContext
					.getBean("guestDao");

			boolean isAllowed = guestDao.isPersonAGuestOnMeeting((Long) id,
					user.getId());

			if (!isAllowed) {
				log.warn("User:" + user
						+ " is not allowed to access meetingid:" + id);
				throw new UncheckedException("User:" + user
						+ " is not allowed to access meetingid:" + id);
			}
		}

	}
}
