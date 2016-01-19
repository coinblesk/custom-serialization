package com.coinblesk.customserialization;

import com.coinblesk.customserialization.exceptions.IllegalArgumentException;
import com.coinblesk.customserialization.exceptions.NotSignedException;
import com.coinblesk.customserialization.exceptions.SerializationException;

/**
 * This class represents a payment request, which is transferred from a client
 * to the server over TCP/IP.
 * 
 * It contains one signed {@link PaymentRequest} if the payer initializes the
 * payment. If the payee initializes the payment, then this contains two
 * {@link PaymentRequest}, signed by each party respectively. The latter assures
 * that the payer does not pay less than the payee requested.
 * 
 * @author Jeton Memeti
 * 
 */
public class ServerPaymentRequest extends SerializableObject {
	private static final int NOF_BYTES_FOR_PAYLOAD_LENGTH = 2; // 2 bytes for the payload length, up to 65536 bytes
	
	private byte nofSignatures;
	
	private PaymentRequest paymentRequestPayer;
	private PaymentRequest paymentRequestPayee;
	
	//this constructor is needed for the DecoderFactory
	protected ServerPaymentRequest() {
	}
	
	private ServerPaymentRequest(int version, PaymentRequest paymentRequestPayer) throws IllegalArgumentException {
		super(version);
		this.nofSignatures = 1;
		checkParameters(nofSignatures, paymentRequestPayer);
		this.paymentRequestPayer = paymentRequestPayer;
	}
	
	/**
	 * This constructor instantiates a new object with one
	 * {@link PaymentRequest}, signed by the payer.
	 * 
	 * @param paymentRequestPayer
	 *            the payer's {@link PaymentRequest}
	 * @throws IllegalArgumentException
	 *             if any argument is null or does not fit into the foreseen
	 *             primitive type or if the {@link PaymentRequest} is not signed
	 */
	public ServerPaymentRequest(PaymentRequest paymentRequestPayer) throws IllegalArgumentException {
		this(1, paymentRequestPayer);
	}
	
	private ServerPaymentRequest(int version, PaymentRequest paymentRequestPayer, PaymentRequest paymentRequestPayee) throws IllegalArgumentException {
		super(version);
		this.nofSignatures = 2;
		
		checkParameters(nofSignatures, paymentRequestPayer, paymentRequestPayee);
		
		this.paymentRequestPayer = paymentRequestPayer;
		this.paymentRequestPayee = paymentRequestPayee;
	}
	
	/**
	 * This constructor instantiates a new object with two
	 * {@link PaymentRequest}s, signed by the payer and by the payee
	 * respectively.
	 * 
	 * @param paymentRequestPayer
	 *            the payer's {@link PaymentRequest}
	 * @param paymentRequestPayee
	 *            the payee's {@link PaymentRequest}
	 * @throws IllegalArgumentException
	 *             if any argument is null or does not fit into the foreseen
	 *             primitive type or if any {@link PaymentRequest} is not signed
	 */
	public ServerPaymentRequest(PaymentRequest paymentRequestPayer, PaymentRequest paymentRequestPayee) throws IllegalArgumentException {
		this(1, paymentRequestPayer, paymentRequestPayee);
	}
	
	private void checkParameters(byte nofSignatures, PaymentRequest paymentRequestPayer) throws IllegalArgumentException {
		if (nofSignatures <= 0 || nofSignatures > 2)
			throw new IllegalArgumentException("The Server Payment Request can only handle 1 or 2 signatures.");
		
		checkPaymentRequest(paymentRequestPayer, "payer");
	}
	
	private void checkParameters(byte nofSignatures, PaymentRequest paymentRequestPayer, PaymentRequest paymentRequestPayee) throws IllegalArgumentException {
		checkParameters(nofSignatures, paymentRequestPayer);
		checkPaymentRequest(paymentRequestPayee, "payee");
		
		if (!paymentRequestPayee.requestsIdentic(paymentRequestPayer))
			throw new IllegalArgumentException("The two payment requests must be identic.");
	}
	
	private void checkPaymentRequest(PaymentRequest paymentRequest, String role) throws IllegalArgumentException {
		if (paymentRequest == null)
			throw new IllegalArgumentException("The "+role+"'s Payment Request can't be null.");
		
		byte[] payload = paymentRequest.getPayload();
		if (payload == null || payload.length == 0)
			throw new IllegalArgumentException("The "+role+"'s payload can't be null or empty.");
		
		byte[] signature = paymentRequest.getSignature();
		if (signature == null || signature.length == 0)
			throw new IllegalArgumentException("The "+role+"'s Payment Request is not signed.");
		
		int maxPayloadLength = (int) Math.pow(2, NOF_BYTES_FOR_PAYLOAD_LENGTH*Byte.SIZE) - 1;
		if (payload.length + signature.length > maxPayloadLength)
			throw new IllegalArgumentException("The "+role+"'s raw payment request is too long (longer than "+maxPayloadLength+" bytes).");
	}
	
	/**
	 * Returns the number of signatures or {@link PaymentRequest} this object
	 * contains.
	 */
	public byte getNofSignatures() {
		return nofSignatures;
	}
	
	/**
	 * Returns the payer's {@link PaymentRequest}. This object is always set.
	 */
	public PaymentRequest getPaymentRequestPayer() {
		return paymentRequestPayer;
	}

	/**
	 * Returns the payee's {@link PaymentRequest} if this object contains 2
	 * signatures. Returns null if the object was instantiated with only the
	 * payer's {@link PaymentRequest} (if this object contains only 1
	 * signature).
	 */
	public PaymentRequest getPaymentRequestPayee() {
		return paymentRequestPayee;
	}

	@Override
	public byte[] encode() throws NotSignedException {
		int outputLength;
		if (nofSignatures == 1) {
			/*
			 * version
			 * + nofSignatures
			 * + paymentRequestPayer.length
			 * + paymentRequestPayer
			 */
			outputLength = 1+1+NOF_BYTES_FOR_PAYLOAD_LENGTH+paymentRequestPayer.getPayload().length+paymentRequestPayer.getSignature().length;
		} else {
			/*
			 * version
			 * + nofSignatures
			 * + paymentRequestPayer.length
			 * + paymentRequestPayer
			 * + paymentRequestPayee.length
			 * + paymentRequestPayee
			 */
			outputLength = 1+1+NOF_BYTES_FOR_PAYLOAD_LENGTH+paymentRequestPayer.getPayload().length+paymentRequestPayer.getSignature().length+NOF_BYTES_FOR_PAYLOAD_LENGTH+paymentRequestPayee.getPayload().length+paymentRequestPayee.getSignature().length;
		}
		
		int index = 0;
		byte[] result = new byte[outputLength];
		
		result[index++] = (byte) getVersion();
		result[index++] = nofSignatures;
		
		byte[] paymentRequestPayerBytes = paymentRequestPayer.encode();
		byte[] paymentRequestPayerBytesLength = PrimitiveTypeSerializer.getShortAsBytes((short) paymentRequestPayerBytes.length);
		
		for (byte b : paymentRequestPayerBytesLength) {
			result[index++] = b;
		}
		for (byte b : paymentRequestPayerBytes) {
			result[index++] = b;
		}
		
		if (nofSignatures > 1) {
			byte[] paymentRequestPayeeBytes = paymentRequestPayee.encode();
			byte[] paymentRequestPayeeBytesLength = PrimitiveTypeSerializer.getShortAsBytes((short) paymentRequestPayeeBytes.length);
			
			for (byte b : paymentRequestPayeeBytesLength) {
				result[index++] = b;
			}
			for (byte b : paymentRequestPayeeBytes) {
				result[index++] = b;
			}
		}
		return result;
	}

	@Override
	public ServerPaymentRequest decode(byte[] bytes) throws IllegalArgumentException, SerializationException {
		if (bytes == null)
			throw new IllegalArgumentException("The argument can't be null.");
		
		try {
			int index = 0;
			
			int version = (bytes[index++] & 0xFF);
			byte nofSignatures = bytes[index++];
			
			byte[] indicatedLengthPayer = new byte[NOF_BYTES_FOR_PAYLOAD_LENGTH];
			for (int i=0; i<NOF_BYTES_FOR_PAYLOAD_LENGTH; i++) {
				indicatedLengthPayer[i] = bytes[index++];
			}
			
			int paymentRequestPayerLength = (PrimitiveTypeSerializer.getBytesAsShort(indicatedLengthPayer) & 0xFF);
			byte[] paymentRequestPayerBytes = new byte[paymentRequestPayerLength];
			for (int i=0; i<paymentRequestPayerLength; i++) {
				paymentRequestPayerBytes[i] = bytes[index++];
			}
			
			PaymentRequest paymentRequestPayer = DecoderFactory.decode(PaymentRequest.class, paymentRequestPayerBytes);
			
			if (nofSignatures == 1) {
				return new ServerPaymentRequest(version, paymentRequestPayer);
			} else if (nofSignatures == 2) {
				byte[] indicatedLengthPayee = new byte[NOF_BYTES_FOR_PAYLOAD_LENGTH];
				for (int i=0; i<NOF_BYTES_FOR_PAYLOAD_LENGTH; i++) {
					indicatedLengthPayee[i] = bytes[index++];
				}
				
				int paymentRequestPayeeLength = (PrimitiveTypeSerializer.getBytesAsShort(indicatedLengthPayee) & 0xFF);
				byte[] paymentRequestPayeeBytes = new byte[paymentRequestPayeeLength];
				for (int i=0; i<paymentRequestPayeeLength; i++) {
					paymentRequestPayeeBytes[i] = bytes[index++];
				}
				PaymentRequest paymentRequestPayee = DecoderFactory.decode(PaymentRequest.class, paymentRequestPayeeBytes);
				
				return new ServerPaymentRequest(version, paymentRequestPayer, paymentRequestPayee);
			} else {
				throw new IllegalArgumentException("The given byte array is corrupt.");
			}
		} catch (IndexOutOfBoundsException e) {
			throw new IllegalArgumentException("The given byte array is corrupt (not long enough).");
		}
	}
	
	@Override
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (!(o instanceof ServerPaymentRequest))
			return false;
		
		ServerPaymentRequest spr = (ServerPaymentRequest) o;
		if (getVersion() != spr.getVersion())
			return false;
		if (this.nofSignatures != spr.nofSignatures)
			return false;
		if (!getPaymentRequestPayer().equals(spr.getPaymentRequestPayer()))
			return false;
		if (nofSignatures == 2) {
			if (!getPaymentRequestPayee().equals(spr.getPaymentRequestPayee()))
				return false;
		}
		
		return true;
	}
	
}
