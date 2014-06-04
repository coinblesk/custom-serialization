package ch.uzh.csg.mbps.customserialization;

import ch.uzh.csg.mbps.customserialization.exceptions.IllegalArgumentException;
import ch.uzh.csg.mbps.customserialization.exceptions.SerializationException;

//TODO: javadoc
public class DecoderFactory {
	
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
