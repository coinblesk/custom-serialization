package ch.uzh.csg.mbps.customserialization;

import static org.junit.Assert.assertTrue;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.uzh.csg.mbps.customserialization.exceptions.IllegalArgumentException;
import ch.uzh.csg.mbps.customserialization.exceptions.SerializationException;
import ch.uzh.csg.mbps.customserialization.testutils.TestUtils;

public class ServerPaymentRequestTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testConstructor_IllegalArgumentException() throws InvalidKeyException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException, InvalidAlgorithmParameterException, UnknownPKIAlgorithmException {
		boolean exceptionThrown = false;
		try {
			//this will fail because of the null argument
			new ServerPaymentRequest(null);
		} catch (IllegalArgumentException e) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);
		exceptionThrown = false;
		
		exceptionThrown = false;
		try {
			//this will fail because the PaymentRequest is not signed
			PaymentRequest pr = new PaymentRequest(PKIAlgorithm.DEFAULT, 1, "buyer", "seller", Currency.BTC, 12, System.currentTimeMillis());
			new ServerPaymentRequest(pr);
		} catch (IllegalArgumentException e) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);
		exceptionThrown = false;
		
		exceptionThrown = false;
		try {
			//this will fail because of the null argument
			PaymentRequest pr = new PaymentRequest(PKIAlgorithm.DEFAULT, 1, "buyer", "seller", Currency.BTC, 12, System.currentTimeMillis());
			new ServerPaymentRequest(pr, null);
		} catch (IllegalArgumentException e) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);
		exceptionThrown = false;
		
		exceptionThrown = false;
		try {
			//this will fail because the payee's PaymentRequest is not signed
			long timestamp = System.currentTimeMillis();
			PaymentRequest prPayer = new PaymentRequest(PKIAlgorithm.DEFAULT, 1, "buyer", "seller", Currency.BTC, 12, timestamp);
			prPayer.sign(TestUtils.generateKeyPair().getPrivate());
			PaymentRequest prPayee = new PaymentRequest(PKIAlgorithm.DEFAULT, 1, "buyer", "seller", Currency.BTC, 12, timestamp);
			new ServerPaymentRequest(prPayer, prPayee);
		} catch (IllegalArgumentException e) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);
		exceptionThrown = false;
		
		exceptionThrown = false;
		try {
			//this will fail because the PaymentRequests are not equals
			long timestamp = System.currentTimeMillis();
			PaymentRequest prPayer = new PaymentRequest(PKIAlgorithm.DEFAULT, 1, "buyer", "seller", Currency.BTC, 12, timestamp);
			prPayer.sign(TestUtils.generateKeyPair().getPrivate());
			PaymentRequest prPayee = new PaymentRequest(PKIAlgorithm.DEFAULT, 1, "seller", "buyer", Currency.BTC, 12, timestamp);
			prPayee.sign(TestUtils.generateKeyPair().getPrivate());
			new ServerPaymentRequest(prPayer, prPayee);
		} catch (IllegalArgumentException e) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);
		exceptionThrown = false;
	}
	
	@Test
	public void testEncodeDecode() throws IllegalArgumentException, InvalidKeyException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException, InvalidAlgorithmParameterException, SerializationException {
		long timestamp = System.currentTimeMillis();
		
		KeyPair keyPairPayer = TestUtils.generateKeyPair();
		KeyPair keyPairPayee = TestUtils.generateKeyPair();
		
		PaymentRequest prPayer = new PaymentRequest(PKIAlgorithm.DEFAULT, 1, "buyer", "seller", Currency.BTC, 12, timestamp);
		prPayer.sign(keyPairPayer.getPrivate());
		PaymentRequest prPayee = new PaymentRequest(PKIAlgorithm.DEFAULT, 1, "buyer", "seller", Currency.BTC, 12, timestamp);
		prPayee.sign(keyPairPayee.getPrivate());
		ServerPaymentRequest spr = new ServerPaymentRequest(prPayer, prPayee);
		
		byte[] encoded = spr.encode();
		ServerPaymentRequest decodedSpr = DecoderFactory.decode(ServerPaymentRequest.class, encoded);
		
		assertTrue(spr.equals(decodedSpr));
		assertTrue(decodedSpr.getPaymentRequestPayer().verify(keyPairPayer.getPublic()));
		assertTrue(decodedSpr.getPaymentRequestPayee().verify(keyPairPayee.getPublic()));
	}

}
