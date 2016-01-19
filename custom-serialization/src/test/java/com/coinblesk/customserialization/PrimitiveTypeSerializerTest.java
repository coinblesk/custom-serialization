package com.coinblesk.customserialization;

import com.coinblesk.customserialization.PrimitiveTypeSerializer;
import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class PrimitiveTypeSerializerTest {

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
	public void testLongs() {
		long l = 10L;
		byte[] longAsBytes = PrimitiveTypeSerializer.getLongAsBytes(l);
		assertEquals(8, longAsBytes.length);
		long convertedLong = PrimitiveTypeSerializer.getBytesAsLong(longAsBytes);
		assertEquals(l, convertedLong);
		
		long l2 = 20L;
		byte[] longAsBytes2 = PrimitiveTypeSerializer.getLongAsBytes(l2);
		assertEquals(8, longAsBytes2.length);
		long convertedLong2 = PrimitiveTypeSerializer.getBytesAsLong(longAsBytes2);
		assertEquals(l2, convertedLong2);
	}
	
	@Test
	public void testShorts() {
		short s = 40;
		byte[] shortAsBytes = PrimitiveTypeSerializer.getShortAsBytes(s);
		assertEquals(2, shortAsBytes.length);
		short convertedShort = PrimitiveTypeSerializer.getBytesAsShort(shortAsBytes);
		assertEquals(s, convertedShort);
		
		short s2 = 40;
		byte[] shortAsBytes2 = PrimitiveTypeSerializer.getShortAsBytes(s2);
		assertEquals(2, shortAsBytes2.length);
		short convertedShort2 = PrimitiveTypeSerializer.getBytesAsShort(shortAsBytes2);
		assertEquals(s2, convertedShort2);
	}

}
