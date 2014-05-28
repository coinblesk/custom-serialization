package ch.uzh.csg.paymentlib.security;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.Security;

import org.spongycastle.jce.ECNamedCurveTable;
import org.spongycastle.jce.provider.BouncyCastleProvider;
import org.spongycastle.jce.spec.ECParameterSpec;

//TODO: javadoc
public class KeyGenerator {
	
	static {
		Security.addProvider(new BouncyCastleProvider());
	}
	
	public static KeyPair generateECCKeyPair(SignatureAlgorithm signatureAlgorithm) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidAlgorithmParameterException {
		ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec(signatureAlgorithm.getKeyGenAlgorithm());
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA", "SC");
		keyGen.initialize(ecSpec, new SecureRandom());
		return keyGen.generateKeyPair();
	}

}
