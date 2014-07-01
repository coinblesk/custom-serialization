package ch.uzh.csg.mbps.customserialization;

import ch.uzh.csg.mbps.customserialization.exceptions.IllegalArgumentException;
import ch.uzh.csg.mbps.customserialization.exceptions.SerializationException;

/**
 * This factory allows decoding sub classes of {@link SerializableObject}. The
 * implementation of decode lives in the corresponding class itself.
 * 
 * @author Jeton Memeti
 * 
 */
public class DecoderFactory {
	
	/**
	 * Decodes a byte array into a sub class of {@link SerializableObject}.
	 * 
	 * @param clazz
	 *            the type the byte array should be decoded into
	 * @param bytes
	 *            the serialized {@link SerializableObject}
	 * @return a new instance of a sub class of {@link SerializableObject}
	 *         indicated in the clazz parameter, or null if reflection does not
	 *         work (i.e., no public default constructor)
	 * @throws IllegalArgumentException
	 *             if the byte array does not match to the type provided (i.e.,
	 *             the byte array does not contain enough data)
	 * @throws SerializationException
	 *             any sub class of {@link SerializationException}
	 */
	@SuppressWarnings("unchecked")
	public static <T extends SerializableObject> T decode(Class<? extends SerializableObject> clazz, byte[] bytes) throws IllegalArgumentException, SerializationException {
		try {
			T t = (T) clazz.newInstance();
			return (T) t.decode(bytes);
		} catch (InstantiationException e) {
			return null;
		} catch (IllegalAccessException e) {
			return null;
		}
	}
	
}
