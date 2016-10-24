package hibernate;

import java.io.File;
import java.lang.annotation.Annotation;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;

import model.Company;
import model.Person;
import model.Subscription;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.stat.CollectionStatistics;
import org.hibernate.stat.EntityStatistics;
import org.hibernate.stat.Statistics;

import security.LocalSecurityContext;
import security.TestSecurityContext;
import spring.LocalApplicationContext;
import subsystems.operatingsystem.OperatingSystem;

import common.Config;
import common.UncheckedException;

import dao.CompanyDao;
import dao.HibernateInterceptor;
import dao.PersonDao;
import dao.SubscriptionDao;

public class HibernateSession {
	private static Log log = LogFactory.getLog(HibernateSession.class);

	// private static ThreadLocal<List<TransactionCompleteListener>>
	// transactionCompleteListeners = new
	// ThreadLocal<List<TransactionCompleteListener>>();

	private static SessionFactory sessionFactory;
	static {
		log.info("Creating hibernate config");
		AnnotationConfiguration config = new AnnotationConfiguration();

		config.setInterceptor(new HibernateInterceptor());

		OperatingSystem os = new OperatingSystem();
		try {
			ArrayList<String> fileNames = os
					.getClassFilesInSubdirectory("model");
			for (String fileName : fileNames) {
				String className = fileName.substring(0, fileName.length() - 6);
				className = className.replace('/', '.');

				Class<?> clazz = Class.forName(className);
				Annotation[] annotations = clazz.getAnnotations();
				for (int i = 0; i < annotations.length; i++) {
					Annotation annotation = annotations[i];
					if (annotation instanceof Entity) {
						if (log.isDebugEnabled())
							log.debug("Configuring annotated class:"
									+ className + " with entity annotation:"
									+ annotation);
						config.addAnnotatedClass(clazz);
					}

				}
			}
		} catch (Exception e) {
			throw new UncheckedException(
					"Failed to add classes from directory", e);
		}

		setProperty("hibernate.dialect", config, os);
		setProperty("hibernate.connection.username", config, os);
		setProperty("hibernate.connection.password", config, os);
		setProperty("hibernate.connection.driver_class", config, os);
		setProperty("hibernate.connection.url", config, os);

		log.info("Building hibernate session factory");
		sessionFactory = config.buildSessionFactory();
		log.info("Hibernate session factory ready");

		createTestCompanyData();

	}

	public static void beginTransaction() {
		log.debug("Beginning transaction");
		// transactionCompleteListeners.set(null);
		getCurrentSession().beginTransaction();
	}



	protected static void runSql(String sql) {
		try {

			@SuppressWarnings("deprecation")
			Connection connection = HibernateSession.getCurrentSession()
					.connection();
			Statement stmt = connection.createStatement();
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			throw new UncheckedException("Failed run run sql:" + sql, e);
		}
	}

	private static void setProperty(String key, AnnotationConfiguration config,
			OperatingSystem os) {
		String value = Config.getInstance().getValue(key);
		if ("hibernate.connection.url".equals(key) && "jdbc:h2".equals(value)) {
			//File file = os.getWebInfFile("C:/Project/apache-tomcat-7.0.37/apache-tomcat-7.0.37/webapps/Shift/WEB-INF/database/shiftdb");
			config.setProperty(key, "jdbc:h2:" + "C:/Project/apache-tomcat-7.0.37/apache-tomcat-7.0.37/webapps/Shift/WEB-INF/database/shiftdb");
		} else
			config.setProperty(key, value);
	}

	/**
	 * Check we have data in this database. If not create Test company
	 */
	private static void createTestCompanyData() {

		HibernateSession.beginTransaction();
		//
		CompanyDao companyDao = (CompanyDao) LocalApplicationContext
				.getBean("companyDao");
		List<Company> companies = companyDao.getAll();

		if (companies.size() == 0) {
			log.info("No company data exists. This database is probably empty. Creating test company.");
			Company company = new Company();
			company.name = "Test";
			company = companyDao.save(company);

			LocalSecurityContext.set(new TestSecurityContext());

			PersonDao personDao = (PersonDao) LocalApplicationContext
					.getBean("personDao");
			Person person = new Person();
			person.name = "John Smith";
			person.email = "symdevtstmail@gmail.com";
			person.administrator = true;
			person.userToken = "08ee86e981";
			person.encryptedPassword = "08ee86e9816f652da8eafb825bc545ac"; // p4ss1
			person.userTimeZone = "Australia/Sydney";
			person = personDao.save(person);
			
			SubscriptionDao subDao = (SubscriptionDao)LocalApplicationContext.getBean("subscriptionDao");
			Subscription subscription = new Subscription();
			subscription.company = company;
			subscription.person = person;
			subscription.type = Subscription.FULL_USER;
			subDao.save(subscription);
			
			person = new Person();
			person.name = "Andrew Jones";
			person.email = "andrew.jones@shiftmeetings.com";
			person.administrator = true;
			person.userToken = "652da8eafb8";
			person.encryptedPassword = "08ee86e9816f652da8eafb825bc545ac"; // p4ss1
			person.userTimeZone = "Australia/Sydney";
			person = personDao.save(person);
			
			subscription = new Subscription();
			subscription.company = company;
			subscription.person = person;
			subscription.type = Subscription.FULL_USER;
			subDao.save(subscription);

			LocalSecurityContext.clear();

			HibernateSession.commit();
		}
		HibernateSession.close();
	}

	public static Session getCurrentSession() {
		return sessionFactory.getCurrentSession();
	}

	private static Transaction getCurrentTransaction() {
		return getCurrentSession().getTransaction();
	}

	public static void commit() {
		log.debug("Committing transaction");
		getCurrentTransaction().commit();
		// callTransactionCompleteListeners();
	}

	public static void rollback() {
		log.debug("Rolling back transaction");
		getCurrentTransaction().rollback();
		// callTransactionCompleteListeners();
	}

	public static void close() {
		log.debug("Closing session");
		getCurrentSession().close();
		// transactionCompleteListeners.set(null);
	}

	// --------------------------------------------------------------------------
	// Support for listeners

	/*
	 * public static void addListenerToCurrentTransaction(
	 * TransactionCompleteListener listener) {
	 * 
	 * /* We only expect this to be used very occasionally so lazy
	 * initialisation
	 *//*
		 * List<TransactionCompleteListener> listeners =
		 * transactionCompleteListeners .get(); if (listeners == null) {
		 * listeners = new ArrayList<TransactionCompleteListener>();
		 * transactionCompleteListeners.set(listeners); }
		 * 
		 * listeners.add(listener); }
		 * 
		 * private static void callTransactionCompleteListeners() {
		 * List<TransactionCompleteListener> listeners =
		 * transactionCompleteListeners .get(); if (listeners != null) { for
		 * (TransactionCompleteListener listener : listeners) {
		 * log.info("Calling transaction complete on:" + listener);
		 * listener.transactionComplete(); } }
		 * transactionCompleteListeners.set(null); }
		 */
	// --------------------------------------------------------------------------
	// Session factory management
	/**
	 * This method is only here so the HibernateSessionRequestFilter can get
	 * static block to run
	 */
	public static void openSessionFactory() {
		// Dont need to do anything. Init is done in static block
	}

	public static void closeSessionFactory() {
		logStatistics();
		log.info("Closing session factory");
		sessionFactory.close();
	}

	// --------------------------------------------------------------------------
	// Statistics
	public static void clearStatistics() {
		sessionFactory.getStatistics().clear();
	}

	/**
	 * Log collected statistics
	 */
	public static void logStatistics() {
		Statistics stat = sessionFactory.getStatistics();
		stat.logSummary();
		String[] roles = stat.getCollectionRoleNames();
		for (int i = 0; i < roles.length; i++) {
			String role = roles[i];
			CollectionStatistics collStat = stat.getCollectionStatistics(role);
			if (collStat.getFetchCount() > 0)
				log.info("Collection:" + role + " fetchCount:"
						+ collStat.getFetchCount());
		}
		String[] entities = stat.getEntityNames();
		for (int i = 0; i < entities.length; i++) {
			String entity = entities[i];
			EntityStatistics entityStat = stat.getEntityStatistics(entity);
			if (entityStat.getFetchCount() > 0)
				log.info("Entity:" + entity + " fetch count:"
						+ entityStat.getFetchCount());
		}
		String[] queries = stat.getQueries();
		for (int i = 0; i < queries.length; i++) {
			String query = queries[i];
			log.info("Query AvgTime:"
					+ stat.getQueryStatistics(query).getExecutionAvgTime()
					+ " executionCount:"
					+ stat.getQueryStatistics(query).getExecutionCount()
					+ " rowsReturned:"
					+ stat.getQueryStatistics(query).getExecutionRowCount()
					+ " Query:" + query);
		}

	}

	public static void main(String[] args) {
		// Simple test

	}
}
