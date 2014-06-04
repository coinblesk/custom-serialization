package ch.uzh.csg.mbps.customserialization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.uzh.csg.mbps.customserialization.exceptions.IllegalArgumentException;
import ch.uzh.csg.mbps.customserialization.exceptions.SerializationException;


public class InitMessagePayeeTest {

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
	public void testConstructor_IllegalArgumentException() {
		boolean exceptionThrown = false;
		try {
			new InitMessagePayee("", Currency.BTC, 1);
		} catch (IllegalArgumentException e) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);
		exceptionThrown = false;
		
		try {
			new InitMessagePayee("payee", null, 1);
		} catch (IllegalArgumentException e) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);
		exceptionThrown = false;
		
		try {
			new InitMessagePayee("payee", Currency.BTC, -1);
		} catch (IllegalArgumentException e) {
			exceptionThrown = true;
		}
		assertTrue(exceptionThrown);
		exceptionThrown = false;
	}
	
	@Test
	public void testEncodeDecode() throws IllegalArgumentException, SerializationException {
		InitMessagePayee initMessagePayee = new InitMessagePayee("payee", Currency.BTC, 1);
		byte[] encode = initMessagePayee.encode();
		InitMessagePayee decode = DecoderFactory.decode(InitMessagePayee.class, encode);
		
		assertEquals(initMessagePayee.getVersion(), decode.getVersion());
		assertEquals(initMessagePayee.getUsername(), decode.getUsername());
		assertEquals(initMessagePayee.getCurrency().getCode(), decode.getCurrency().getCode());
		assertEquals(initMessagePayee.getAmount(), decode.getAmount());
	}

}
