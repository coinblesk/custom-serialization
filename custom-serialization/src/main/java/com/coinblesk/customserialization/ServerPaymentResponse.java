package com.coinblesk.customserialization;

import com.coinblesk.customserialization.exceptions.IllegalArgumentException;
import com.coinblesk.customserialization.exceptions.NotSignedException;
import com.coinblesk.customserialization.exceptions.SerializationException;

/**
 * This class represents a payment response, which is transferred from the
 * server to the client over TCP/IP.
 * 
 * It contains one signed {@link PaymentResponse} if both clients belong to the
 * same server. Therefore, they can verify the server's signature.
 * 
 * If the clients involved in this {@link PaymentRequest} (i.e., the payer and
 * the payee) belong to different servers, then this object must contain two
 * {@link PaymentResponse}s. The payer's {@link PaymentResponse} is then signed
 * with its server's public key. The payee's {@link PaymentResponse} is then
 * signed with its server's public key.
 * 
 * @author Jeton Memeti
 * 
 */
public class ServerPaymentResponse extends SerializableObject {
	private static final int NOF_BYTES_FOR_PAYMENT_RESPONSE_LENGTH = 2; // 2 bytes for the payload length, up to 65536 bytes
	
	private byte nofPaymentResponses;
	
	private PaymentResponse paymentResponsePayer;
	private PaymentResponse paymentResponsePayee;
	
	//this constructor is needed for the DecoderFactory
	protected ServerPaymentResponse() {
	}

	/**
	 * This constructor instantiates a new object for the case where the payer
	 * and the payee belong to the same server and only one signed
	 * {@link PaymentResponse} is needed.
	 * 
	 * @param paymentResponsePayer
	 *            the payer's {@link PaymentResponse} signed with the server's
	 *            private key
	 * @throws IllegalArgumentException
	 *             if any argument is null or does not fit into the foreseen
	 *             primitive type or if the {@link PaymentResponse} is not
	 *             signed
	 */
	public ServerPaymentResponse(PaymentResponse paymentResponsePayer) throws IllegalArgumentException {
		this(1, paymentResponsePayer);
	}
	
	/**
	 * This constructor instantiates a new object for the case where the payer
	 * and the payee do not belong to the same server. Therefore, two signed
	 * {@link PaymentResponse}s are needed.
	 * 
	 * @param paymentResponsePayer
	 *            the payer's {@link PaymentResponse} signed with his server's
	 *            private key
	 * @param paymentResponsePayee
	 *            the payee's {@link PaymentResponse} signed with his server's
	 *            private key
	 * @throws IllegalArgumentException
	 *             if any argument is null or does not fit into the foreseen
	 *             primitive type or if any {@link PaymentResponse} is not
	 *             signed
	 */
	public ServerPaymentResponse(PaymentResponse paymentResponsePayer, PaymentResponse paymentResponsePayee) throws IllegalArgumentException {
		this(1, paymentResponsePayer, paymentResponsePayee);
	}
	
	private ServerPaymentResponse(int version, PaymentResponse paymentResponsePayer) throws IllegalArgumentException {
		super(version);
		checkParameters(paymentResponsePayer, "payer");
		this.paymentResponsePayer = paymentResponsePayer;
		this.nofPaymentResponses = 1;
	}
	
	private ServerPaymentResponse(int version, PaymentResponse paymentResponsePayer, PaymentResponse paymentResponsePayee) throws IllegalArgumentException {
		super(version);
		
		checkParameters(paymentResponsePayer, "payer");
		checkParameters(paymentResponsePayee, "payee");
		
		this.paymentResponsePayer = paymentResponsePayer;
		this.paymentResponsePayee = paymentResponsePayee;
		this.nofPaymentResponses = 2;
	}
	
	private static void checkParameters(PaymentResponse paymentResponse, String role) throws IllegalArgumentException {
		if (paymentResponse == null)
			throw new IllegalArgumentException("The payment response cannot be null.");
		
		int maxPayloadLength = (int) Math.pow(2, NOF_BYTES_FOR_PAYMENT_RESPONSE_LENGTH*Byte.SIZE) - 1;
		byte[] payload = paymentResponse.getPayload();
		if (payload == null || payload.length == 0 || payload.length > maxPayloadLength)
			throw new IllegalArgumentException("The "+role+"'s payment response payload can't be null, empty or longer than "+maxPayloadLength+" bytes.");
		
		byte[] signature = paymentResponse.getSignature();
		if (signature == null || signature.length == 0)
			throw new IllegalArgumentException("The "+role+"'s payment response is not signed.");
		
		if (signature.length > 255)
			throw new IllegalArgumentException("The "+role+"'s payment response signature is too long. A signature algorithm with output longer than 255 bytes is not supported.");
	}
	
	/**
	 * Returns the number {@link PaymentResponse}s contained in this object
	 * (i.e., how many servers have signed the {@link PaymentResponse}).
	 */
	public byte getNofPaymentResponses() {
		return nofPaymentResponses;
	}
	
	/**
	 * Returns the payer's {@link PaymentResponse} signed with his server's
	 * public key. This object is always set. If nogPaymentResponses is 1, this
	 * {@link PaymentResponse} should also be forwarded to the payee.
	 */
	public PaymentResponse getPaymentResponsePayer() {
		return paymentResponsePayer;
	}

	/**
	 * Returns the payee's {@link PaymentResponse} signed with his server's
	 * public key, only if it is not the same server as the payer's server. If
	 * payer and payee belong to the same server, this returns null. In this
	 * case, nofPaymentResponses is equals to 1.
	 */
	public PaymentResponse getPaymentResponsePayee() {
		return paymentResponsePayee;
	}

	@Override
	public byte[] encode() throws NotSignedException {
		byte[] paymentResponsePayerRaw = null;
		byte[] paymentResponsePayeeRaw = null;
		
		int length;
		if (nofPaymentResponses == 1) {
			paymentResponsePayerRaw = paymentResponsePayer.encode();
			/*
			 * version
			 * + nofPaymentResponses
			 * + paymentResponsePayer.length
			 * + paymentResponsePayer
			 */
			length = 1+1+NOF_BYTES_FOR_PAYMENT_RESPONSE_LENGTH+paymentResponsePayerRaw.length;
		} else {
			paymentResponsePayerRaw = paymentResponsePayer.encode();
			paymentResponsePayeeRaw = paymentResponsePayee.encode();
			/*
			 * version
			 * + nofPaymentResponses
			 * + paymentResponsePayer.length
			 * + paymentResponsePayer
			 * + paymentResponsePayee.length
			 * + paymentResponsePayee
			 */
			length = 1+1+NOF_BYTES_FOR_PAYMENT_RESPONSE_LENGTH+paymentResponsePayerRaw.length+NOF_BYTES_FOR_PAYMENT_RESPONSE_LENGTH+paymentResponsePayeeRaw.length;
		}
		
		int index = 0;
		byte[] result = new byte[length];
		
		result[index++] = (byte) getVersion();
		result[index++] = nofPaymentResponses;
		
		byte[] paymentResponsePayerLengthBytes = PrimitiveTypeSerializer.getShortAsBytes((short) paymentResponsePayerRaw.length);
		for (byte b : paymentResponsePayerLengthBytes) {
			result[index++] = b;
		}
		for (byte b : paymentResponsePayerRaw) {
			result[index++] = b;
		}
		
		if (nofPaymentResponses > 1) {
			byte[] paymentResponsePayeeLengthBytes = PrimitiveTypeSerializer.getShortAsBytes((short) paymentResponsePayeeRaw.length);
			for (byte b : paymentResponsePayeeLengthBytes) {
				result[index++] = b;
			}
			for (byte b : paymentResponsePayeeRaw) {
				result[index++] = b;
			}
		}
		
		return result;
	}
	
	@Override
	public ServerPaymentResponse decode(byte[] bytes) throws IllegalArgumentException, SerializationException {
		if (bytes == null)
			throw new IllegalArgumentException("The argument can't be null.");
		
		try {
			int index = 0;
			
			int version = (bytes[index++] & 0xFF);
			byte nofPaymentResponses = bytes[index++];
			
			byte[] indicatedPaymentResponsePayerLengthBytes = new byte[NOF_BYTES_FOR_PAYMENT_RESPONSE_LENGTH];
			for (int i=0; i<NOF_BYTES_FOR_PAYMENT_RESPONSE_LENGTH; i++) {
				indicatedPaymentResponsePayerLengthBytes[i] = bytes[index++];
			}
			
			int paymentResponsePayerLength = (PrimitiveTypeSerializer.getBytesAsShort(indicatedPaymentResponsePayerLengthBytes) & 0xFF);
			byte[] paymentResponsePayerBytes = new byte[paymentResponsePayerLength];
			for (int i=0; i<paymentResponsePayerLength; i++) {
				paymentResponsePayerBytes[i] = bytes[index++];
			}
			
			PaymentResponse paymentResponsePayer = DecoderFactory.decode(PaymentResponse.class, paymentResponsePayerBytes);
			if (nofPaymentResponses == 1) {
				return new ServerPaymentResponse(version, paymentResponsePayer);
			} else if (nofPaymentResponses == 2) {
				byte[] indicatedPaymentResponsePayeeLengthBytes = new byte[NOF_BYTES_FOR_PAYMENT_RESPONSE_LENGTH];
				for (int i=0; i<NOF_BYTES_FOR_PAYMENT_RESPONSE_LENGTH; i++) {
					indicatedPaymentResponsePayeeLengthBytes[i] = bytes[index++];
				}
				
				int paymentResponsePayeeLength = (PrimitiveTypeSerializer.getBytesAsShort(indicatedPaymentResponsePayeeLengthBytes) & 0xFF);
				byte[] paymentResponsePayeeBytes = new byte[paymentResponsePayeeLength];
				for (int i=0; i<paymentResponsePayeeLength; i++) {
					paymentResponsePayeeBytes[i] = bytes[index++];
				}
				
				PaymentResponse paymentResponsePayee = DecoderFactory.decode(PaymentResponse.class, paymentResponsePayeeBytes);
				return new ServerPaymentResponse(version, paymentResponsePayer, paymentResponsePayee);
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
		if (!(o instanceof ServerPaymentResponse))
			return false;
		
		ServerPaymentResponse spr = (ServerPaymentResponse) o;
		if (getVersion() != spr.getVersion())
			return false;
		if (this.nofPaymentResponses != spr.nofPaymentResponses)
			return false;
		if (!getPaymentResponsePayer().equals(spr.getPaymentResponsePayer()))
			return false;
		if (nofPaymentResponses == 2) {
			if (!getPaymentResponsePayer().equals(spr.getPaymentResponsePayer()))
				return false;
		}
		
		return true;
	}

}
