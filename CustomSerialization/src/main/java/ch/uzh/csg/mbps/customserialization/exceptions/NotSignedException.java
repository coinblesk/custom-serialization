package ch.uzh.csg.mbps.customserialization.exceptions;

import ch.uzh.csg.mbps.customserialization.SignedSerializableObject;

/**
 * This Exception is thrown when a {@link SignedSerializableObject} is being
 * encoded before it has been signed.
 * 
 * @author Jeton Memeti
 * 
 */
public class NotSignedException extends SerializationException {

	private static final long serialVersionUID = 8214149865215084985L;

}
