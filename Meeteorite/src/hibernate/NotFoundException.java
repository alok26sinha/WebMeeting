package hibernate;

import common.UncheckedException;

@SuppressWarnings("serial")
public class NotFoundException extends UncheckedException{

	public NotFoundException(String s) {
		super(s);
	}

}
