package ch.uzh.csg.mbps.customserialization.exceptions;

import ch.uzh.csg.mbps.customserialization.Currency;

/**
 * This Exception is thrown when a {@link Currency} with an unknown code is
 * tried to be created.
 * 
 * @author Jeton Memeti
 * 
 */
public class UnknownCurrencyException extends SerializationException {
	
	private static final long serialVersionUID = -7110301898988281908L;
	
}
