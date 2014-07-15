package ch.uzh.csg.mbps.customserialization;

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
		return new byte[] {
				(byte) (l >> 56),
				(byte) (l >> 48),
				(byte) (l >> 40),
				(byte) (l >> 32),
				(byte) (l >> 24),
				(byte) (l >> 16),
				(byte) (l >> 8),
				(byte) l
			};
	}

	/**
	 * Returns a long from a given byte array.
	 */
	public static long getBytesAsLong(byte[] b) {
		return ((long) (b[0] & 0xff) << 56)
				| ((long) (b[1] & 0xFF) << 48)
				| ((long) (b[2] & 0xFF) << 40)
				| ((long) (b[3] & 0xFF) << 32)
				| ((long) (b[4] & 0xFF) << 24)
				| ((long) (b[5] & 0xFF) << 16)
				| ((long) (b[6] & 0xFF) << 8)
				| ((long) (b[7] & 0xFF));
	}
	
	/**
	 * Returns a short as a byte array.
	 */
	public static byte[] getShortAsBytes(short s) {
		return new byte[] {
				(byte) (s >>> 8),
				(byte) s
		};
	}
	
	/**
	 * Returns a short from a given byte array.
	 */
	public static short getBytesAsShort(byte[] b) {
		int i = (b[0] & 0xFF << 8)
				| (b[1] & 0xFF);
		
		return (short) i;
	}
	
}