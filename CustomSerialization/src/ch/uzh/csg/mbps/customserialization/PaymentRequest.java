package ch.uzh.csg.mbps.customserialization;

import java.nio.charset.Charset;

import ch.uzh.csg.mbps.customserialization.exceptions.IllegalArgumentException;
import ch.uzh.csg.mbps.customserialization.exceptions.NotSignedException;
import ch.uzh.csg.mbps.customserialization.exceptions.UnknownCurrencyException;
import ch.uzh.csg.mbps.customserialization.exceptions.UnknownPKIAlgorithmException;

/**
 * This class represents a payment request, which is transferred via NFC between
 * two clients. The byte array serialization allows keeping the payload and
 * signature as as small as possible, which is important especially for the NFC.
 * 
 * @author Jeton Memeti
 * 
 */
public class PaymentRequest extends SignedSerializableObject {
	
	private String usernamePayer;
	private String usernamePayee;
	
	private Currency currency;
	private long amount;
	private Currency inputCurrency;
	private long inputAmount;
	private long timestamp;
	
	//this constructor is needed for the DecoderFactory
	protected PaymentRequest() {
	}

	/**
	 * This constructor instantiates a new object.
	 * 
	 * @param pkiAlgorithm
	 *            the {@link PKIAlgorithm} to be used for
	 *            {@link SignedSerializableObject} super class
	 * @param keyNumber
	 *            the key number to be used for the
	 *            {@link SignedSerializableObject} super class
	 * @param usernamePayer
	 *            the payer's username
	 * @param usernamePayee
	 *            the payee's username
	 * @param currency
	 *            the {@link Currency} of the payment
	 * @param amount
	 *            the requested amount
	 * @param timestamp
	 *            the payee's timestamp in order to detect repeated but
	 *            unintended payments
	 * @throws IllegalArgumentException
	 *             if any argument is null or does not fit into the foreseen
	 *             primitive type
	 */
	public PaymentRequest(PKIAlgorithm pkiAlgorithm, int keyNumber, String usernamePayer, String usernamePayee, Currency currency, long amount, long timestamp) throws IllegalArgumentException {
		this(1, pkiAlgorithm, keyNumber, usernamePayer, usernamePayee, currency, amount, timestamp);
	}
	
	private PaymentRequest(int version, PKIAlgorithm pkiAlgorithm, int keyNumber, String usernamePayer, String usernamePayee, Currency currency, long amount, long timestamp) throws IllegalArgumentException {
		super(version, pkiAlgorithm, keyNumber);
		
		checkParameters(usernamePayer, usernamePayee, currency, amount, timestamp);
		
		this.usernamePayer = usernamePayer;
		this.usernamePayee = usernamePayee;
		this.currency = currency;
		this.amount = amount;
		this.inputCurrency = null;
		this.inputAmount = 0;
		this.timestamp = timestamp;
		
		setPayload(false);
	}
	
	/**
	 * This constructor instantiates a new object. In addition to the other
	 * constructor, it allows adding an input currency as well as an input
	 * amount (e.g., when the amount is entered in another {@link Currency} than
	 * BTC).
	 * 
	 * @param pkiAlgorithm
	 *            the {@link PKIAlgorithm} to be used for
	 *            {@link SignedSerializableObject} super class
	 * @param keyNumber
	 *            the key number to be used for the
	 *            {@link SignedSerializableObject} super class
	 * @param usernamePayer
	 *            the payer's username
	 * @param usernamePayee
	 *            the payee's username
	 * @param currency
	 *            the {@link Currency} of the payment
	 * @param amount
	 *            the requested amount
	 * @param inputCurrency
	 *            if the user entered the amount in a {@link Currency} other
	 *            than BTC
	 * @param inputAmount
	 *            if the user entered the amount in a {@link Currency} other
	 *            than BTC
	 * @param timestamp
	 *            the payee's timestamp in order to detect repeated but
	 *            unintended payments
	 * @throws IllegalArgumentException
	 *             if any argument is null or does not fit into the foreseen
	 *             primitive type
	 */
	public PaymentRequest(PKIAlgorithm pkiAlgorithm, int keyNumber, String usernamePayer, String usernamePayee, Currency currency, long amount, Currency inputCurrency, long inputAmount, long timestamp) throws IllegalArgumentException {
		this(1, pkiAlgorithm, keyNumber, usernamePayer, usernamePayee, currency, amount, inputCurrency, inputAmount, timestamp);
	}
	
	private PaymentRequest(int version, PKIAlgorithm pkiAlgorithm, int keyNumber, String usernamePayer, String usernamePayee, Currency currency, long amount, Currency inputCurrency, long inputAmount, long timestamp) throws IllegalArgumentException {
		super(version, pkiAlgorithm, keyNumber);
		
		checkParameters(usernamePayer, usernamePayee, currency, amount, inputCurrency, inputAmount, timestamp);
		
		this.usernamePayer = usernamePayer;
		this.usernamePayee = usernamePayee;
		this.currency = currency;
		this.amount = amount;
		this.inputCurrency = inputCurrency;
		this.inputAmount = inputAmount;
		this.timestamp = timestamp;
		
		setPayload(true);
	}

	private void checkParameters(String usernamePayer, String usernamePayee, Currency currency, long amount, Currency inputCurrency, long inputAmount, long timestamp) throws IllegalArgumentException {
		checkParameters(usernamePayer, usernamePayee, currency, amount, timestamp);
		
		if (inputCurrency == null)
			throw new IllegalArgumentException("The input currency cannot be null.");
		
		if (inputAmount <= 0)
			throw new IllegalArgumentException("The input amount must be greatern than 0.");
	}

	private void checkParameters(String usernamePayer, String usernamePayee, Currency currency, long amount, long timestamp) throws IllegalArgumentException {
		if (usernamePayer == null || usernamePayer.length() == 0 || usernamePayer.length() > 255)
			throw new IllegalArgumentException("The payers's username cannot be null, empty, or longer than 255 characters.");
		
		if (usernamePayee == null || usernamePayee.length() == 0 || usernamePayee.length() > 255)
			throw new IllegalArgumentException("The payee's username cannot be null, empty, or longer than 255 characters.");
		
		if (usernamePayee.equalsIgnoreCase(usernamePayer))
			throw new IllegalArgumentException("The payee's username can't be equals to the payer's username.");
		
		if (currency == null)
			throw new IllegalArgumentException("The currency cannot be null.");
		
		if (amount <= 0)
			throw new IllegalArgumentException("The amount must be greatern than 0.");
		
		if (timestamp <= 0)
			throw new IllegalArgumentException("The timestamp must be greatern than 0.");
	}
	
	private void setPayload(boolean hasInputCurrency) {
		byte[] usernamePayerBytes = usernamePayer.getBytes(Charset.forName("UTF-8"));
		byte[] usernamePayeeBytes = usernamePayee.getBytes(Charset.forName("UTF-8"));
		byte[] amountBytes = PrimitiveTypeSerializer.getLongAsBytes(amount);
		byte[] inputAmountBytes = new byte[0];
		byte[] timestampBytes = PrimitiveTypeSerializer.getLongAsBytes(timestamp);
		
		byte nofCurrencies;

		int length;
		if (hasInputCurrency) {
			inputAmountBytes = PrimitiveTypeSerializer.getLongAsBytes(inputAmount);
			nofCurrencies = 2;
			/*
			 * version
			 * + signatureAlgorithm.getCode()
			 * + keyNumber
			 * + usernamePayer.length
			 * + usernamePayer
			 * + usernamePayee.length
			 * + usernamePayee
			 * + nofCurrencies
			 * + currency.getCode()
			 * + amount
			 * + inputCurrency.getCode()
			 * + inputAmount
			 * + timestamp
			 */
			length = 1+1+1+1+usernamePayerBytes.length+1+usernamePayeeBytes.length+1+1+8+1+8+8;
		} else {
			nofCurrencies = 1;
			/*
			 * version
			 * + signatureAlgorithm.getCode()
			 * + keyNumber
			 * + usernamePayer.length
			 * + usernamePayer
			 * + usernamePayee.length
			 * + usernamePayee
			 * + nofCurrencies
			 * + currency.getCode()
			 * + amount
			 * + timestamp
			 */
			length = 1+1+1+1+usernamePayerBytes.length+1+usernamePayeeBytes.length+1+1+8+8;
		}
		
		byte[] payload = new byte[length];
		
		int index = 0;
		payload[index++] = (byte) getVersion();
		payload[index++] = getPKIAlgorithm().getCode();
		payload[index++] = (byte) getKeyNumber();
		payload[index++] = (byte) usernamePayerBytes.length;
		for (byte b : usernamePayerBytes) {
			payload[index++] = b;
		}
		payload[index++] = (byte) usernamePayeeBytes.length;
		for (byte b : usernamePayeeBytes) {
			payload[index++] = b;
		}
		
		payload[index++] = nofCurrencies;
			
		payload[index++] = currency.getCode();
		for (byte b : amountBytes) {
			payload[index++] = b;
		}
		
		if (hasInputCurrency) {
			payload[index++] = inputCurrency.getCode();
			for (byte b : inputAmountBytes) {
				payload[index++] = b;
			}
		}
		
		for (byte b : timestampBytes) {
			payload[index++] = b;
		}
		
		this.payload = payload;
	}
	
	public String getUsernamePayer() {
		return usernamePayer;
	}

	public String getUsernamePayee() {
		return usernamePayee;
	}

	public Currency getCurrency() {
		return currency;
	}

	public long getAmount() {
		return amount;
	}

	public Currency getInputCurrency() {
		return inputCurrency;
	}
	
	public long getInputAmount() {
		return inputAmount;
	}
	
	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public PaymentRequest decode(byte[] bytes) throws IllegalArgumentException, UnknownPKIAlgorithmException, UnknownCurrencyException, NotSignedException {
		if (bytes == null)
			throw new IllegalArgumentException("The argument can't be null.");
		
		try {
			int index = 0;
			
			int version = bytes[index++] & 0xFF;
			PKIAlgorithm pkiAlgorithm = PKIAlgorithm.getPKIAlgorithm(bytes[index++]);
			int keyNumber = bytes[index++] & 0xFF;
			
			int usernamePayerLength = bytes[index++] & 0xFF;
			byte[] usernamePayerBytes = new byte[usernamePayerLength];
			for (int i=0; i<usernamePayerLength; i++) {
				usernamePayerBytes[i] = bytes[index++];
			}
			String usernamePayer = new String(usernamePayerBytes);
			
			int usernamePayeeLength = bytes[index++] & 0xFF;
			byte[] usernamePayeeBytes = new byte[usernamePayeeLength];
			for (int i=0; i<usernamePayeeLength; i++) {
				usernamePayeeBytes[i] = bytes[index++];
			}
			String usernamePayee = new String(usernamePayeeBytes);
			
			byte nofCurrencies = bytes[index++];
			
			Currency currency = Currency.getCurrency(bytes[index++]);
			
			byte[] amountBytes = new byte[Long.SIZE / Byte.SIZE]; //8 bytes (long)
			for (int i=0; i<amountBytes.length; i++) {
				amountBytes[i] = bytes[index++];
			}
			long amount = PrimitiveTypeSerializer.getBytesAsLong(amountBytes);
			
			Currency inputCurrency = null;
			long inputAmount = 0;
			if (nofCurrencies == 2) {
				inputCurrency = Currency.getCurrency(bytes[index++]);
				byte[] inputAmountBytes = new byte[Long.SIZE / Byte.SIZE]; //8 bytes (long)
				for (int i=0; i<inputAmountBytes.length; i++) {
					inputAmountBytes[i] = bytes[index++];
				}
				inputAmount = PrimitiveTypeSerializer.getBytesAsLong(inputAmountBytes);
			}
			
			byte[] timestampBytes = new byte[Long.SIZE / Byte.SIZE]; //8 bytes (long)
			for (int i=0; i<timestampBytes.length; i++) {
				timestampBytes[i] = bytes[index++];
			}
			long timestamp = PrimitiveTypeSerializer.getBytesAsLong(timestampBytes);
			
			PaymentRequest pr;
			if (nofCurrencies == 1) {
				pr = new PaymentRequest(version, pkiAlgorithm, keyNumber, usernamePayer, usernamePayee, currency, amount, timestamp);
			} else {
				pr = new PaymentRequest(version, pkiAlgorithm, keyNumber, usernamePayer, usernamePayee, currency, amount, inputCurrency, inputAmount, timestamp);
			}
			
			int signatureLength = bytes.length - index;
			if (signatureLength == 0) {
				throw new NotSignedException();
			} else {
				byte[] signature = new byte[signatureLength];
				for (int i=0; i<signature.length; i++) {
					signature[i] = bytes[index++];
				}
				pr.signature = signature;
			}
			
			return pr;
		} catch (IndexOutOfBoundsException e) {
			throw new IllegalArgumentException("The given byte array is corrupt (not long enough).");
		}
	}
	
	/**
	 * This method checks that two payment requests are identic regarding a
	 * payment. The username of payer and payee as well as the currency and the
	 * amount must be equals in order to return true.
	 */
	public boolean requestsIdentic(PaymentRequest pr) {
		if (pr == null)
			return false;
		
		if (!this.usernamePayer.equals(pr.usernamePayer))
			return false;
		if (!this.usernamePayee.equals(pr.usernamePayee))
			return false;
		if (this.currency.getCode() != pr.currency.getCode())
			return false;
		if (this.amount != pr.amount)
			return false;
		if (this.timestamp != pr.timestamp)
			return false;
		
		return true;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof PaymentRequest))
			return false;
		
		PaymentRequest other = (PaymentRequest) o;
		if (getVersion() != other.getVersion())
			return false;
		if (getPKIAlgorithm().getCode() != other.getPKIAlgorithm().getCode())
			return false;
		if (getKeyNumber() != other.getKeyNumber())
			return false;
		if (!this.usernamePayer.equals(other.usernamePayer))
			return false;
		if (!this.usernamePayee.equals(other.usernamePayee))
			return false;
		if (this.currency.getCode() != other.currency.getCode())
			return false;
		if (this.amount != other.amount)
			return false;
		if (this.timestamp != other.timestamp)
			return false;
		if (this.getInputCurrency() != null) {
			if (other.getInputCurrency() == null)
				return false;
			
			if (this.getInputCurrency().getCode() != other.getInputCurrency().getCode())
				return false;
		} else {
			if (other.getInputCurrency() != null)
				return false;
		}
		if (this.getInputAmount() != other.getInputAmount())
			return false;
		
		return true;
	}
	
}
