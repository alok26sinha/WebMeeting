package type;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

public class MinuteDurationUserType implements UserType {

	private static int[] sqlTypes = new int[] { Hibernate.LONG.sqlType() };

	@Override
	public int[] sqlTypes() {
		return sqlTypes;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class returnedClass() {
		return MinuteDuration.class;
	}

	@Override
	public boolean isMutable() {
		return false;
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		return value;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return (Serializable) value;
	}

	@Override
	public Object assemble(Serializable cached, Object owner)
			throws HibernateException {
		return cached;
	}

	@Override
	public Object replace(Object original, Object target, Object owner)
			throws HibernateException {
		return original;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		if (x == y)
			return true;
		if (x == null || y == null)
			return false;
		return x.equals(y);
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	@Override
	public Object nullSafeGet(ResultSet resultSet, String[] names, Object owner)
			throws HibernateException, SQLException {
		long centsValue = resultSet.getLong(names[0]);
		// Deferred check after first read
		if (resultSet.wasNull())
			return null;

		MinuteDuration amount = MinuteDuration.create(centsValue);

		return amount;
	}

	@Override
	public void nullSafeSet(PreparedStatement statement, Object value, int index)
			throws HibernateException, SQLException {
		if (value == null) {
			statement.setNull(index, Hibernate.LONG.sqlType());
		} else {
			MinuteDuration amount = (MinuteDuration) value;
			statement.setLong(index, amount.getMinutes());
		}

	}

}
