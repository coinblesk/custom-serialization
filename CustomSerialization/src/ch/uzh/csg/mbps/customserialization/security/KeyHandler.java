package ch.uzh.csg.mbps.customserialization.security;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.spec.EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.jce.spec.ECParameterSpec;

import ch.uzh.csg.mbps.customserialization.SignatureAlgorithm;

//TODO: javadoc
public class KeyHandler {
	
	//TODO: add test! for base 64 crap
	
	static {
		Security.addProvider(new BouncyCastleProvider());
	}
	
	public static KeyPair generateECCKeyPair(SignatureAlgorithm signatureAlgorithm) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
		ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec(signatureAlgorithm.getKeyGenAlgorithm());
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "SC");
		keyGen.initialize(ecSpec, new SecureRandom());
		return keyGen.generateKeyPair();
	}
	
	public static String encodePrivateKey(PrivateKey privateKey) {
		byte[] privateEncoded = Base64.encodeBase64(privateKey.getEncoded());
		return new String(privateEncoded);
	}
	
	public static String encodePublicKey(PublicKey publicKey) {
		byte[] publicEncoded = Base64.encodeBase64(publicKey.getEncoded());
		return new String(publicEncoded);
	}

	public static PublicKey decodePublicKey(String publicKeyEncoded) throws Exception {
		byte[] decoded = Base64.decodeBase64(publicKeyEncoded.getBytes());
		EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(decoded);
		
		KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "SC");
		return keyFactory.generatePublic(publicKeySpec);
	}
	
	public static PrivateKey decodePrivateKey(String privateKeyEncoded) throws Exception {
		byte[] decoded = Base64.decodeBase64(privateKeyEncoded.getBytes());
		EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(decoded);
		
		KeyFactory keyFactory = KeyFactory.getInstance("ECDSA", "SC");
		return keyFactory.generatePrivate(privateKeySpec);
	}

}
