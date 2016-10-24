package hibernate;

@SuppressWarnings("serial")
public class MultipleRecordsFoundException extends NotFoundException {

	public MultipleRecordsFoundException(String s) {
		super(s);
	}

}
