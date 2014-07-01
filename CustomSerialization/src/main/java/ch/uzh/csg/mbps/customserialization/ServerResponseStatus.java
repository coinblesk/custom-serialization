package ch.uzh.csg.mbps.customserialization;

import java.util.HashMap;
import java.util.Map;

import ch.uzh.csg.mbps.customserialization.exceptions.UnknownServerResponseStatusException;

/**
 * This class contains the possible server response status.
 * 
 * @author Jeton Memeti
 * 
 */
public enum ServerResponseStatus {
	SUCCESS((byte) 0x01),
	FAILURE((byte) 0x02),
	DUPLICATE_REQUEST((byte) 0x03);

	private byte code;
	
	private ServerResponseStatus(byte code) {
		this.code = code;
	}
	
	/**
	 * Returns the code of this ServerResponseStatus
	 */
	public byte getCode() {
		return code;
	}
	
	private static Map<Byte, ServerResponseStatus> codeStatusMap = null;
	
	/**
	 * Returns the ServerResponseStatus based on the code.
	 * 
	 * @param b
	 *            the code
	 * @throws UnknownServerResponseStatusException
	 *             if the given code is not known
	 */
	public static ServerResponseStatus getStatus(byte code) throws UnknownServerResponseStatusException {
		if (codeStatusMap == null)
			initMap();
		
		ServerResponseStatus status = codeStatusMap.get(code);
		if (status == null)
			throw new UnknownServerResponseStatusException();
		else
			return status;
	}
	
	private static void initMap() {
		codeStatusMap = new HashMap<Byte, ServerResponseStatus>();
		for (ServerResponseStatus s : values()) {
			codeStatusMap.put(s.getCode(), s);
		}
	}
		
}
