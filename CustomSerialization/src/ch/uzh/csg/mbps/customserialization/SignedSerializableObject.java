package ch.uzh.csg.mbps.customserialization;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import ch.uzh.csg.mbps.customserialization.exceptions.IllegalArgumentException;
import ch.uzh.csg.mbps.customserialization.exceptions.NotSignedException;

//TODO: javadoc
public abstract class SignedSerializableObject extends SerializableObject {
	
	private PKIAlgorithm pkiAlgorithm;
	private int keyNumber;
	
	/*
	 * payload and signature are not serialized but only hold references in
	 * order to save cpu time
	 */
	protected byte[] payload;
	protected byte[] signature;
	
	//this constructor is needed for the DecoderFactory
	protected SignedSerializableObject() {
	}

	public SignedSerializableObject(int version, PKIAlgorithm pkiAlgorithm, int keyNumber) throws IllegalArgumentException {
		super(version);
		
		if (pkiAlgorithm == null)
			throw new IllegalArgumentException("The signature algorithm cannot be null.");
		
		if (keyNumber <= 0 || keyNumber > 255)
			throw new IllegalArgumentException("The key number must be between 1 and 255.");
		
		this.pkiAlgorithm = pkiAlgorithm;
		this.keyNumber = keyNumber;
	}
	
	public PKIAlgorithm getPKIAlgorithm() {
		return pkiAlgorithm;
	}
	
	public int getKeyNumber() {
		return keyNumber;
	}
	
	public byte[] getPayload() {
		return payload;
	}
	
	public byte[] getSignature() {
		return signature;
	}
	
	public void sign(PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Signature sig = Signature.getInstance(pkiAlgorithm.getSignatureAlgorithm());
		sig.initSign(privateKey);
		sig.update(payload);
		signature = sig.sign();
	}
	
	public boolean verify(PublicKey publicKey) throws NotSignedException, NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		if (signature == null)
			throw new NotSignedException();
		
		Signature sig = Signature.getInstance(pkiAlgorithm.getSignatureAlgorithm());
		sig.initVerify(publicKey);
		sig.update(payload);
		return sig.verify(signature);
	}
	
	/**
	 * Returns the raw payload of this object and attaches the raw
	 * signature to it.
	 * 
	 * @throws NotSignedException
	 *             if the object was not signed before
	 */
	@Override
	public byte[] encode() throws NotSignedException {
		if (signature == null)
			throw new NotSignedException();
		
		int index = 0;
		byte[] result = new byte[payload.length+signature.length];
		for (byte b : payload) {
			result[index++] = b;
		}
		for (byte b : signature) {
			result[index++] = b;
		}
		
		return result;
	}

}
