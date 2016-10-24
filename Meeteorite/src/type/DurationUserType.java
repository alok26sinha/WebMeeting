package type;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

public class DurationUserType implements UserType {

	private static int[] sqlTypes = new int[] { Hibernate.DOUBLE.sqlType(),
			Hibernate.BOOLEAN.sqlType() };

	@Override
	public int[] sqlTypes() {
		return sqlTypes;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class returnedClass() {
		return Double.class;
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
		double hoursValue = resultSet.getDouble(names[0]);
		boolean enteredAsHours = resultSet.getBoolean(names[1]);
		// Deferred check after first read
		if (resultSet.wasNull())
			return null;

		Duration duration = Duration.create(hoursValue, enteredAsHours);
		return duration;
	}

	@Override
	public void nullSafeSet(PreparedStatement statement, Object value, int index)
			throws HibernateException, SQLException {
		if (value == null) {
			statement.setNull(index, Hibernate.DOUBLE.sqlType());
			statement.setNull(index + 1, Hibernate.BOOLEAN.sqlType());
		} else {
			Duration duration = (Duration) value;
			statement.setDouble(index, duration.getTime());
			statement.setBoolean(index + 1, duration.isEnteredAsHours());
		}

	}

}
