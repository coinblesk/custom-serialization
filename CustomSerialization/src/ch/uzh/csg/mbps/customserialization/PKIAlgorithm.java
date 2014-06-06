package ch.uzh.csg.mbps.customserialization;

import java.util.HashMap;
import java.util.Map;

/**
 * This class contains the supported algorithms to create asymmetric keys and
 * their corresponding signature algorithms.
 * 
 * @author Jeton Memeti
 * 
 */
public enum PKIAlgorithm {
	DEFAULT((byte) 0x01, "ECDSA", "brainpoolp256r1", "SHA256withECDSA");
	
	private byte code;
	private String keyPairAlgorithm;
	private String keyPairSpecification;
	private String signaturAlgorithm;
	
	private PKIAlgorithm(byte code, String keyPairAlgorithm, String keyPairSpecification, String signatureAlgorithm) {
		this.code = code;
		this.keyPairAlgorithm = keyPairAlgorithm;
		this.keyPairSpecification = keyPairSpecification;
		this.signaturAlgorithm = signatureAlgorithm;
	}

	/**
	 * Returns the code of the PKIAlgorithm.
	 */
	public byte getCode() {
		return code;
	}

	/**
	 * Returns the algorithm to be used for the asymmetric keys.
	 */
	public String getKeyPairAlgorithm() {
		return keyPairAlgorithm;
	}

	/**
	 * Returns the specification used to generate the asymmetric keys. In the
	 * case of ECC, a named curve has to be provided. In the case of RSA, the
	 * key size has to be provided.
	 */
	public String getKeyPairSpecification() {
		return keyPairSpecification;
	}

	/**
	 * Returns the signature algorithm used to create digital signatures. This
	 * signature algorithm corresponds to the key pair algorithm.
	 */
	public String getSignatureAlgorithm() {
		return signaturAlgorithm;
	}
	
	private static Map<Byte, PKIAlgorithm> codeAlgorithmMap = null;
	
	/**
	 * Returns the PKIAlgorithm object based on the code
	 * 
	 * @param b
	 *            the code of the algorithm
	 * @return
	 * @throws UnknownPKIAlgorithmException
	 *             if the given code is not known
	 */
	public static PKIAlgorithm getPKIAlgorithm(byte b) throws UnknownPKIAlgorithmException {
		if (codeAlgorithmMap == null)
			initMap();
		
		PKIAlgorithm pkiAlgorithm = codeAlgorithmMap.get(b);
		if (pkiAlgorithm == null)
			throw new UnknownPKIAlgorithmException();
		else
			return pkiAlgorithm;
	}

	private static void initMap() {
		codeAlgorithmMap = new HashMap<Byte, PKIAlgorithm>();
		for (PKIAlgorithm s : values()) {
			codeAlgorithmMap.put(s.getCode(), s);
		}
	}
	
}
