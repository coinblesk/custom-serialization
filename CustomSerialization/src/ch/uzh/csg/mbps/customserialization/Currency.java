package ch.uzh.csg.mbps.customserialization;

import java.util.HashMap;
import java.util.Map;

import ch.uzh.csg.mbps.customserialization.exceptions.UnknownCurrencyException;

/**
 * This class contains the supported currencies.
 * 
 * @author Jeton Memeti
 * 
 */
public enum Currency {
	BTC((byte) 0x01, "BTC"),
	CHF((byte) 0x02, "CHF");
	
	private byte code;
	private String currencyCode;
	
	private Currency(byte code, String currencyCode) {
		this.code = code;
		this.currencyCode = currencyCode;
	}

	/**
	 * Returns the code/identifier of this Currency.
	 */
	public byte getCode() {
		return code;
	}
	
	/**
	 * Returns the currency code, which is a three letter abbreviation of the
	 * currency (e.g. "CHF", "BTC").
	 */
	public String getCurrencyCode() {
		return currencyCode;
	}
	
	private static Map<Byte, Currency> codeCurrencyMap = null;
	
	/**
	 * Returns the Currency based on the code.
	 * 
	 * @param b
	 *            the code
	 * @throws UnknownCurrencyException
	 *             if the given code is not known
	 */
	public static Currency getCurrency(byte b) throws UnknownCurrencyException {
		if (codeCurrencyMap == null)
			initMap();
		
		Currency currency = codeCurrencyMap.get(b);
		if (currency == null)
			throw new UnknownCurrencyException();
		else
			return currency;
	}

	private static void initMap() {
		codeCurrencyMap = new HashMap<Byte, Currency>();
		for (Currency c : values()) {
			codeCurrencyMap.put(c.getCode(), c);
		}
	}
	
}
