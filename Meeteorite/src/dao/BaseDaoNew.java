package dao;

import hibernate.HibernateSession;
import hibernate.NoRecordsFoundException;

import java.io.Serializable;
import java.util.List;

import model.BaseModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;

import type.BeanUtils;

import common.UncheckedException;

public abstract class BaseDaoNew<T extends BaseModel, PK extends Serializable> {
	private static Log log = LogFactory.getLog(BaseDaoNew.class);

	protected Class<T> persistentClass;

	public BaseDaoNew(final Class<T> persistentClass) {
		this.persistentClass = persistentClass;
	}

	//--------------------------------------------------------------------------
	/**
	 * Load an entity from the database using the id
	 */
	public T load(PK id) {
		if (id == null) {
			throw new UncheckedException("Could not load "
					+ persistentClass.getName() + " The id parameter is null.");
		}

		// First look to see if we have loaded this class in this session
		// already
		@SuppressWarnings("unchecked")
        T object = (T) getSession().get(persistentClass, id);
		if (object != null) {

			return object;

		} else {

			String errorMessage = "Could not find:" + this.persistentClass
					+ " id:" + id;
			RuntimeException exception = new NoRecordsFoundException(
					errorMessage);
			throw exception;

		}
	}

	/**
	 * Save and entity to the database
	 */
	@SuppressWarnings("unchecked")
	public T save(T object) {
		T saved = (T) getSession().merge(object);

		// Some actions are doing a save on an entity and then forwarding to
		// an edit method to reload the page.
		// On linux the new row is not yet found in the db.
		// This is a heavy but effective solution.
		flush();

		return saved;
	}

	/**
	 * Delete and entity based on the id
	 */
	public void delete(PK id) {
		if (id != null) {
			try {
				/*
				 * Note this will apply the standard criterion which will
				 * prevent deleting an object in another company
				 */
				T instance = load(id);
				getSession().delete(instance);
				// For the same reasons above
				flush();
			} catch (NoRecordsFoundException e) {
				log
						.warn("Could delete class:"
								+ persistentClass
								+ " id:"
								+ id
								+ " as it does not exist in the database.  Ignoring delete and continuing.");
			}
		}
	}

	/**
	 * Delete an object from the database
	 */
	@SuppressWarnings("unchecked")
	public void delete(T object) {
		// Check we have something to delete
		if (object != null)
			delete((PK) object.getId());
	}

	/**
	 * Delete all objects in a list
	 */
	public void deleteAll(List<T> objects) {
		// Check we have something to delete
		if (objects != null)
			for(T object: objects)
				delete(object);
	}

	/**
	 * Get all of the entities. Sorted by name if that filed exists
	 */
	@SuppressWarnings("unchecked")
	public List<T> getAll() {
		Criteria criteria = getCriteria();

		// Order by name if this class has name property
		if (BeanUtils.hasField(persistentClass, "name")) {
			criteria.addOrder(Order.asc("name"));
		}

		return criteria.list();
	}

	//--------------------------------------------------------------------------
	// Search functions
	/**
	 * Get the hibernate criteria for this class. This is used by most of the
	 * retrieval methods. See the hibernate manual about criteria queries.
	 * 
	 * This method is overridden by subclasses who want to add additional
	 * criteria
	 */
	protected Criteria getCriteria() {
		Criteria criteria = getSession().createCriteria(persistentClass);
		return criteria;
	}

	/**
	 * The core query method. It adds all the criteria passed in and executes.
	 */
	@SuppressWarnings("unchecked")
	protected List<T> find(Criterion... criterion) {
		Criteria criteria = getCriteria();

		for (int i = 0; i < criterion.length; i++) {
			criteria.add(criterion[i]);
		}

		return (List<T>) criteria.list();
	}

	/**
	 * Adds an order parameter to
	 */
	@SuppressWarnings("unchecked")
	protected List<T> find(Order order, Criterion... criterion) {
		Criteria criteria = getCriteria();

		for (int i = 0; i < criterion.length; i++) {
			criteria.add(criterion[i]);
		}

		if (order != null)
			criteria.addOrder(order);

		return (List<T>) criteria.list();
	}

	/**
	 * The old HQL way of writing queries. Harder to get correct than criteria
	 * queries
	 */
	@SuppressWarnings("unchecked")
	protected List<T> find(String queryString, Object[] values) {
		return (List<T>) untypedFind(queryString, values);
	}

	protected List<?> untypedFind(String queryString, Object[] values) {
		Query query = getSession().createQuery(queryString);
		for (int i = 0; i < values.length; i++)
			query.setParameter(i, values[i]);
		return query.list();
	}

	//--------------------------------------------------------------------------
	// Support methods
	/**
	 * Flush changes so they are available for the load.
	 * 
	 * This function is usually called automatically when the transaction
	 * commits. In some cases we would like to see changes before the
	 * transactions commits (usually when we save an object and then look it up
	 * straight away.)
	 */
	public void flush() {
		getSession().flush();
	}

	/**
	 * Remove an object from the session cache
	 */
	public void evict(Object object) {
		getSession().evict(object);
	}

	protected Session getSession() {
		return HibernateSession.getCurrentSession();
	}

}
