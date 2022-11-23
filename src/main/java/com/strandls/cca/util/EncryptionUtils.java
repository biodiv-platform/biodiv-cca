package com.strandls.cca.util;


import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Arun
 *
 */

public class EncryptionUtils {

	private final Logger logger = LoggerFactory.getLogger(EncryptionUtils.class);
	private static final String ALGORITHM = "Blowfish";

	private String key;

	public EncryptionUtils() {
		try {
			InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties");
			Properties properties = new Properties();
			try {
				properties.load(in);
			} catch (IOException e) {
				logger.error(e.getMessage());
			}

			key = properties.getProperty("encryptKey");

			in.close();

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	public String encrypt(String plainText) {
		String strData = "";

		try {
			SecretKeySpec skeyspec = new SecretKeySpec(key.getBytes(), ALGORITHM);
			Cipher cipher = Cipher.getInstance(ALGORITHM); // NOSONAR
			cipher.init(Cipher.ENCRYPT_MODE, skeyspec);
			byte[] encrypted = cipher.doFinal(plainText.getBytes());
			strData = DatatypeConverter.printBase64Binary(encrypted);

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return strData;
	}

	public String decrypt(String encryptedText) {
		String strData = "";

		try {
			SecretKeySpec skeyspec = new SecretKeySpec(key.getBytes(), ALGORITHM);
			Cipher cipher = Cipher.getInstance(ALGORITHM); // NOSONAR
			cipher.init(Cipher.DECRYPT_MODE, skeyspec);
			byte[] decrypted = cipher.doFinal(DatatypeConverter.parseBase64Binary(encryptedText));
			strData = new String(decrypted);

		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return strData;
	}

}

