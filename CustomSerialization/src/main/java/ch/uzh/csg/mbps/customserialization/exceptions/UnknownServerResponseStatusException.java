package ch.uzh.csg.mbps.customserialization.exceptions;

import ch.uzh.csg.mbps.customserialization.ServerResponseStatus;

/**
 * This Exception is thrown when a {@link ServerResponseStatus} with an unknown code is
 * tried to be created.
 * 
 * @author Jeton Memeti
 * 
 */
public class UnknownServerResponseStatusException extends SerializationException {

	private static final long serialVersionUID = 7389107196542130619L;

}
