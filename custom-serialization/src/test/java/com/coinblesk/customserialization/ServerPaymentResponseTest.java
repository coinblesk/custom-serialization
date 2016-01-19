package com.coinblesk.customserialization;

import com.coinblesk.customserialization.Currency;
import com.coinblesk.customserialization.ServerResponseStatus;
import com.coinblesk.customserialization.DecoderFactory;
import com.coinblesk.customserialization.ServerPaymentRequest;
import com.coinblesk.customserialization.PaymentResponse;
import com.coinblesk.customserialization.PKIAlgorithm;
import com.coinblesk.customserialization.ServerPaymentResponse;
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

import com.coinblesk.customserialization.exceptions.IllegalArgumentException;
import com.coinblesk.customserialization.exceptions.SerializationException;
import com.coinblesk.customserialization.exceptions.UnknownPKIAlgorithmException;
import com.coinblesk.customserialization.testutils.TestUtils;

public class ServerPaymentResponseTest {

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
	public void testConstructor_IllegalArgumentException() throws IllegalArgumentException, InvalidKeyException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException, InvalidAlgorithmParameterException, UnknownPKIAlgorithmException {
		boolean exceptionThrown = false;
		long timestamp = System.currentTimeMillis();
		
		KeyPair keyPairPayee = TestUtils.generateKeyPair();
		PaymentResponse prPayer = new PaymentResponse(PKIAlgorithm.DEFAULT, 1, ServerResponseStatus.SUCCESS, null, "buyer", "seller", Currency.BTC, 12, timestamp);
		PaymentResponse prPayee = new PaymentResponse(PKIAlgorithm.DEFAULT, 1, ServerResponseStatus.SUCCESS, null, "buyer", "seller", Currency.BTC, 12, timestamp);
		
		try {
			new ServerPaymentRequest(null);
		} catch (IllegalArgumentException e) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);
		exceptionThrown = false;
		
		try {
			new ServerPaymentResponse(prPayer);
		} catch (IllegalArgumentException e) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);
		exceptionThrown = false;
		
		prPayee.sign(keyPairPayee.getPrivate());
		
		try {
			new ServerPaymentResponse(prPayee, null);
		} catch (IllegalArgumentException e) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);
		exceptionThrown = false;
		
		try {
			new ServerPaymentResponse(prPayee, prPayer);
		} catch (IllegalArgumentException e) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);
		exceptionThrown = false;
	}
	
	@Test
	public void testEncodeDecode() throws IllegalArgumentException, InvalidKeyException, NoSuchAlgorithmException, SignatureException, NoSuchProviderException, InvalidAlgorithmParameterException, SerializationException {
		long timestamp = System.currentTimeMillis();
		KeyPair keyPairPayee = TestUtils.generateKeyPair();
		KeyPair keyPairPayer = TestUtils.generateKeyPair();
		PaymentResponse prPayer = new PaymentResponse(PKIAlgorithm.DEFAULT, 1, ServerResponseStatus.SUCCESS, null, "buyer", "seller", Currency.BTC, 12, timestamp);
		PaymentResponse prPayee = new PaymentResponse(PKIAlgorithm.DEFAULT, 1, ServerResponseStatus.SUCCESS, null, "buyer", "seller", Currency.BTC, 12, timestamp);
		
		prPayer.sign(keyPairPayer.getPrivate());
		prPayee.sign(keyPairPayee.getPrivate());
		
		ServerPaymentResponse sr = new ServerPaymentResponse(prPayer, prPayee);
		
		byte[] encode = sr.encode();
		
		ServerPaymentResponse decode = DecoderFactory.decode(ServerPaymentResponse.class, encode);
		
		assertTrue(sr.equals(decode));
	}

}
