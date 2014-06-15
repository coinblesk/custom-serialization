package ch.uzh.csg.mbps.customserialization;

import ch.uzh.csg.mbps.customserialization.exceptions.IllegalArgumentException;
import ch.uzh.csg.mbps.customserialization.exceptions.NotSignedException;
import ch.uzh.csg.mbps.customserialization.exceptions.SerializationException;

/**
 * This is an abstract class for objects which can be serialized into a byte
 * array. The version allows distinguishing from future implementations with
 * another structure or additional fields.
 * 
 * @author Jeton Memeti
 * 
 */
public abstract class SerializableObject {
	
	private int version;
	
	//this constructor is needed for the DecoderFactory
	protected SerializableObject() {
	}
	
	/**
	 * This constructor is only used by sub classes since this class is
	 * abstract. The constructor allows setting the version of this object.
	 * 
	 * @param version
	 *            the version of this object
	 * @throws IllegalArgumentException
	 *             if version is < 0 or > 255
	 */
	public SerializableObject(int version) throws IllegalArgumentException {
		if (version <= 0 || version > 255)
			throw new IllegalArgumentException("The version number must be between 1 and 255.");
		
		this.version = version;
	}
	
	/**
	 * Returns the version of this object. The version is used for future
	 * extensions which would have a greater version number and can therefore be
	 * distinguished from previous versions.
	 */
	public int getVersion() {
		return version;
	}
	
	/**
	 * Returns the raw payload of this object. If it is a
	 * {@link SignedSerializableObject} the raw signature is attached to the
	 * payload.
	 * 
	 * @throws NotSignedException
	 *             if this is a subclass of {@link SignedSerializableObject} and
	 *             was not signed before
	 */
	public abstract byte[] encode() throws NotSignedException;
	
	/**
	 * Deserializes a SerializableObject based on the given bytes.
	 * 
	 * @param bytes
	 *            the raw data
	 * @throws IllegalArgumentException
	 *             if bytes is null or does not contain enough information to
	 *             deserialize the object
	 * @throws SerializationException
	 *             any subclass of {@link SerializationException}
	 */
	public abstract SerializableObject decode(byte[] bytes) throws IllegalArgumentException, SerializationException;
	
}
