package dao;

import common.UncheckedException;

@SuppressWarnings("serial")
public class RecordNotFoundException extends UncheckedException {
	
	public RecordNotFoundException(String message) {
		super(message);
	}
	
}
