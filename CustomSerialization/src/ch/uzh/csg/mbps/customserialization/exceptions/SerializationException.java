package ch.uzh.csg.mbps.customserialization.exceptions;


/**
 * This is the base class for serialization exceptions.
 * 
 * @author Jeton Memeti
 * 
 */
public class SerializationException extends Exception {
	private static final long serialVersionUID = 5873024285412349432L;
	
	public SerializationException() {
	}
	
	public SerializationException(String msg) {
		super(msg);
	}

}
