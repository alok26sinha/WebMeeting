package security;

import common.UncheckedException;

@SuppressWarnings("serial")
public class NotAuthenticatedException extends UncheckedException {
	
	public NotAuthenticatedException(String message) {
		super(message);
	}
	
}
