package com.coinblesk.customserialization;

import java.util.HashMap;
import java.util.Map;

import com.coinblesk.customserialization.exceptions.UnknownCurrencyException;

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
		if (codeCurrencyMap == null) {
			codeCurrencyMap = new HashMap<Byte, Currency>();
			for (Currency c : values()) {
				codeCurrencyMap.put(c.getCode(), c);
			}
		}
		
		Currency currency = codeCurrencyMap.get(b);
		if (currency == null)
			throw new UnknownCurrencyException();
		else
			return currency;
	}

	private static Map<String, Currency> abbrevCurrencyMap = null;
	
	/**
	 * Returns the Currency based on the abbreviation.
	 * 
	 * @param b
	 *            the currency code or abbreviation
	 * @throws UnknownCurrencyException
	 *             if the given abbreviation is not known
	 */
	public static Currency getCurrency(String abbreviation) throws UnknownCurrencyException {
		if (abbrevCurrencyMap == null) {
			abbrevCurrencyMap = new HashMap<String, Currency>();
			for (Currency c : values()) {
				abbrevCurrencyMap.put(c.getCurrencyCode(), c);
			}
		}
		
		Currency currency = abbrevCurrencyMap.get(abbreviation);
		if (currency == null)
			throw new UnknownCurrencyException();
		else
			return currency;
	}

}
