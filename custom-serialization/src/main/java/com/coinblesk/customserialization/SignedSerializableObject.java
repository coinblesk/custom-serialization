package com.coinblesk.customserialization;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

import com.coinblesk.customserialization.exceptions.IllegalArgumentException;
import com.coinblesk.customserialization.exceptions.NotSignedException;

/**
 * This is an abstract class for objects which can be serialized into a byte
 * array and which have a signature attached. Since it extensd
 * {@link SerializableObject} it can be encoded into a byte array or decoded
 * from such one.
 * 
 * @author Jeton Memeti
 * 
 */
public abstract class SignedSerializableObject extends SerializableObject {
	
	private PKIAlgorithm pkiAlgorithm;
	private int keyNumber;
	
	protected byte[] payload;
	protected byte[] signature;
	
	//this constructor is needed for the DecoderFactory
	protected SignedSerializableObject() {
	}

	/**
	 * This constructor is only used by sub classes since this class is
	 * abstract.
	 * 
	 * @param version
	 *            the version of this object (version of
	 *            {@link SerializableObject})
	 * @param pkiAlgorithm
	 *            the {@link PKIAlgorithm} used for signing the payload
	 *            /verifying the signature
	 * @param keyNumber
	 *            the key number of the user who signed this object in order to
	 *            retrieve the correct public key to verify the signature
	 * @throws IllegalArgumentException
	 *             if any parameter is null or does not fit into the foreseen
	 *             primitive type
	 */
	public SignedSerializableObject(int version, PKIAlgorithm pkiAlgorithm, int keyNumber) throws IllegalArgumentException {
		super(version);
		
		if (pkiAlgorithm == null)
			throw new IllegalArgumentException("The signature algorithm cannot be null.");
		
		if (keyNumber <= 0 || keyNumber > 255)
			throw new IllegalArgumentException("The key number must be between 1 and 255.");
		
		this.pkiAlgorithm = pkiAlgorithm;
		this.keyNumber = keyNumber;
	}
	
	/**
	 * Returns the {@link PKIAlgorithm} which has been used to sign the payload.
	 */
	public PKIAlgorithm getPKIAlgorithm() {
		return pkiAlgorithm;
	}
	
	/**
	 * Returns the key number of the user who signed this object in order to
	 * retrieve the correct public key to verify the signature.
	 */
	public int getKeyNumber() {
		return keyNumber;
	}
	
	/**
	 * Returns the payload of this object (excluding the signature).
	 */
	public byte[] getPayload() {
		return payload;
	}
	
	/**
	 * Returns only the signature of this object.
	 */
	public byte[] getSignature() {
		return signature;
	}
	
	/**
	 * Signs this object with the given private key.
	 * 
	 * @param privateKey
	 *            the private key used to sign the object
	 * @throws NoSuchAlgorithmException
	 *             if the {@link PKIAlgorithm} provided in the constructor is
	 *             not known
	 * @throws InvalidKeyException
	 *             if the private key does not belong to the given
	 *             {@link PKIAlgorithm}
	 * @throws SignatureException
	 *             if an error occured during the signing phase
	 */
	public void sign(PrivateKey privateKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Signature sig = Signature.getInstance(pkiAlgorithm.getSignatureAlgorithm());
		sig.initSign(privateKey);
		sig.update(payload);
		signature = sig.sign();
	}
	
	/**
	 * Verifies the signature contained in this object.
	 * 
	 * @param publicKey
	 *            the public key to be used for the verification
	 * @return true if the signature is valid for the given payload, false
	 *         otherwise
	 * @throws NotSignedException
	 *             if this object (the payload) has not been signed
	 * @throws NoSuchAlgorithmException
	 *             if the {@link PKIAlgorithm} provided in the constructor is
	 *             not known
	 * @throws InvalidKeyException
	 *             if the public key does not belong to the given
	 *             {@link PKIAlgorithm}
	 * @throws SignatureException
	 *             if an error occured during the verification phase
	 */
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
