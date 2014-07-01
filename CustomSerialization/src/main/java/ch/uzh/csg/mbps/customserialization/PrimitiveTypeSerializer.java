package ch.uzh.csg.mbps.customserialization;

import java.nio.ByteBuffer;

/**
 * This class serializes and deserializes primitive types such as long and
 * short.
 * 
 * @author Jeton Memeti
 * 
 */
public class PrimitiveTypeSerializer {
	
	/**
	 * Returns a long as a byte array.
	 */
	public static byte[] getLongAsBytes(long l) {
		return ByteBuffer.allocate(Long.SIZE / Byte.SIZE).putLong(l).array();
	}

	/**
	 * Returns a long from a given byte array.
	 */
	public static long getBytesAsLong(byte[] b) {
		ByteBuffer buffer = ByteBuffer.allocate(Long.SIZE / Byte.SIZE);
		buffer.put(b);
		buffer.flip();
		return buffer.getLong();
	}
	
	/**
	 * Returns a short as a byte array.
	 */
	public static byte[] getShortAsBytes(short s) {
		return ByteBuffer.allocate(Short.SIZE / Byte.SIZE).putShort(s).array();
	}
	
	/**
	 * Returns a short from a given byte array.
	 */
	public static short getBytesAsShort(byte[] b) {
		ByteBuffer buffer = ByteBuffer.allocate(Short.SIZE / Byte.SIZE);
		buffer.put(b);
		buffer.flip();
		return buffer.getShort();
	}
	
}