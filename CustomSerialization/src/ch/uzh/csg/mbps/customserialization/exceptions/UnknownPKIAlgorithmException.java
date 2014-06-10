package ch.uzh.csg.mbps.customserialization.exceptions;

import ch.uzh.csg.mbps.customserialization.PKIAlgorithm;

/**
 * This Exception is thrown when a {@link PKIAlgorithm} with an unknown code is
 * tried to be created.
 * 
 * @author Jeton Memeti
 * 
 */
public class UnknownPKIAlgorithmException extends SerializationException {

	private static final long serialVersionUID = 5656412485494040288L;

}
