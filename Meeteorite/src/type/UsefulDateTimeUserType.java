package type;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.usertype.UserType;

public class UsefulDateTimeUserType implements UserType {

	private static int[] sqlTypes = new int[] { Hibernate.TIMESTAMP.sqlType() };

	@Override
	public int[] sqlTypes() {
		return sqlTypes;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Class returnedClass() {
		return UsefulDateTime.class;
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
		Timestamp timestamp = resultSet.getTimestamp(names[0]);
		// Deferred check after first read
		if (resultSet.wasNull())
			return null;

		// Database always has Sydney time
		UsefulDateTime sydneyTime = UsefulDateTime.create(timestamp,
				TimeZone.TZ_SYD);

		UsefulDateTime localTime = sydneyTime.convertToUserTimeZone();

		return localTime;
	}

	@Override
	public void nullSafeSet(PreparedStatement statement, Object value, int index)
			throws HibernateException, SQLException {
		if (value == null) {
			statement.setNull(index, Hibernate.TIMESTAMP.sqlType());
		} else {
			UsefulDateTime localDateTime = (UsefulDateTime) value;
			// Always save in sydney time
			UsefulDateTime sydneyTime = localDateTime
					.convertToTimeZone(TimeZone.TZ_SYD);

			statement.setTimestamp(index, sydneyTime.getTimestamp());
		}

	}

}
