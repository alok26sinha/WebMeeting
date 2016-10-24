package hibernate;

@SuppressWarnings("serial")
public class NoRecordsFoundException extends NotFoundException {

	public NoRecordsFoundException(String s) {
		super(s);
	}
}
