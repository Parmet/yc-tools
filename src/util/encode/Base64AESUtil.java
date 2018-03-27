package util.encode;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Base64AESUtil {

	public static String decode(String s, String secKeyp) throws Exception {
		String encStr = s;
		byte[] sendDataByte = (new sun.misc.BASE64Decoder()).decodeBuffer(encStr);
		String decStr = decryptAES(sendDataByte, secKeyp);
		return decStr;
	}

	public static String encode(String message, String secKeyp) throws Exception {
		message = (new sun.misc.BASE64Encoder()).encode(encryptAES(message, secKeyp));
		return message;
	}

	public static String encoderBASE64(String s) throws UnsupportedEncodingException {
		if (s == null)

			return null;
		return (new sun.misc.BASE64Encoder()).encode(s.getBytes("utf-8"));

	}

	public static String DecoderBASE64(String s) throws IOException {
		byte[] sendDataByte = (new sun.misc.BASE64Decoder()).decodeBuffer(s);

		String decodeXml = new String(sendDataByte);
		return decodeXml;
	}

	public static byte[] hex2byte(String strhex) {
		if (strhex == null) {
			return null;
		}
		int l = strhex.length();
		if (l % 2 == 1) {
			return null;
		}
		byte[] b = new byte[l / 2];
		for (int i = 0; i != l / 2; i++) {
			b[i] = (byte) Integer.parseInt(strhex.substring(i * 2, i * 2 + 2), 16);
		}
		return b;
	}

	public static String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
		}
		return hs.toUpperCase();
	}

	public static String utf8Togb2312(String str) {

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < str.length(); i++) {

			char c = str.charAt(i);

			switch (c) {

			case '+':

				sb.append(' ');

				break;

			case '%':

				try {

					sb.append((char) Integer.parseInt(

					str.substring(i + 1, i + 3), 16));

				}

				catch (NumberFormatException e) {

					throw new IllegalArgumentException();

				}

				i += 2;

				break;

			default:

				sb.append(c);

				break;

			}

		}

		String result = sb.toString();

		String res = null;

		try {

			byte[] inputBytes = result.getBytes("8859_1");

			res = new String(inputBytes, "UTF-8");

		}

		catch (Exception e) {
		}

		return res;

	}

	// 将 GB2312 编码格式的字符串转换为 UTF-8 格式的字符串：
	/**
	 * 注意：解密的时候要传入byte数组 解密
	 * 
	 * @param content
	 *            待解密内容
	 * @param password
	 *            解密密钥
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws UnsupportedEncodingException
	 * @throws InvalidAlgorithmParameterException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */

	public String AESEncrypt(String content, String password) throws NoSuchAlgorithmException, NoSuchPaddingException,
			UnsupportedEncodingException, InvalidKeyException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException {

		// //System.out.println("传入的明文：" + content);
		// //System.out.println("传入的密钥：" + password);
		// KeyGenerator提供对策密钥生成器的功能,支持各种算法
		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
		secureRandom.setSeed(password.getBytes());
		kgen.init(128, secureRandom);

		// SecretKey 负责保存对称密钥
		SecretKey secretKey = kgen.generateKey();
		byte[] enCodeFormat = secretKey.getEncoded();
		SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
		// //System.out.println("转换后的密钥：" + key.getEncoded());

		// 创建密码器 Cipher
		Cipher cipher = Cipher.getInstance("AES" + "/CBC/PKCS5Padding");
		byte[] byteContent = content.getBytes("UTF-8");

		// 初始化
		IvParameterSpec iv = new IvParameterSpec(password.getBytes("UTF-8"));
		cipher.init(Cipher.ENCRYPT_MODE, key, iv);
		// 加密
		byte[] result = cipher.doFinal(byteContent);

		return new String(result, "UTF-8");

		// return (new sun.misc.BASE64Encoder()).encode(result);

	}

	/**
	 * 注意：解密的时候要传入byte数组 解密
	 * 
	 * @param content
	 *            待解密内容
	 * @param password
	 *            解密密钥
	 * @return
	 * @throws UnsupportedEncodingException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchPaddingException
	 * @throws InvalidAlgorithmParameterException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public String AESDecrypt(String str, String password) throws UnsupportedEncodingException,
			NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException,
			IllegalBlockSizeException, BadPaddingException {
		byte[] content = str.getBytes("UTF-8");

		KeyGenerator kgen = KeyGenerator.getInstance("AES");
		SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
		secureRandom.setSeed(password.getBytes("UTF-8"));
		kgen.init(128, secureRandom);
		SecretKey secretKey = kgen.generateKey();

		// 创建密码器
		Cipher cipher = Cipher.getInstance("AES" + "/CBC/PKCS5Padding");
		// 初始化
		IvParameterSpec iv = new IvParameterSpec(password.getBytes("UTF-8"));
		cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
		// 加密
		byte[] result = cipher.doFinal(content);

		return new String(result, "UTF-8");

	}

	public static byte[] encryptAES(String content, String password) {
		try {
			// //System.out.println("传入的明文：" + content);
			// //System.out.println("传入的密钥：" + password);
			// KeyGenerator提供对策密钥生成器的功能,支持各种算法
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			secureRandom.setSeed(password.getBytes());
			kgen.init(128, secureRandom);

			// SecretKey 负责保存对称密钥
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
			// //System.out.println("转换后的密钥：" + key.getEncoded());

			// 创建密码器 Cipher
			Cipher cipher = Cipher.getInstance("AES" + "/CBC/PKCS5Padding");
			byte[] byteContent = content.getBytes("UTF-8");

			// 初始化
			IvParameterSpec iv = new IvParameterSpec(password.getBytes("UTF-8"));
			cipher.init(Cipher.ENCRYPT_MODE, key, iv);
			// 加密
			byte[] result = cipher.doFinal(byteContent);

			return result;

			// return (new sun.misc.BASE64Encoder()).encode(result);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}

	/**
	 * 注意：解密的时候要传入byte数组 解密
	 * 
	 * @param content
	 *            待解密内容
	 * @param password
	 *            解密密钥
	 * @return
	 */
	public static String decryptAES(byte[] content, String password) {
		try {

			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			secureRandom.setSeed(password.getBytes("UTF-8"));
			kgen.init(128, secureRandom);
			SecretKey secretKey = kgen.generateKey();

			// 创建密码器
			Cipher cipher = Cipher.getInstance("AES" + "/CBC/PKCS5Padding");
			// 初始化
			IvParameterSpec iv = new IvParameterSpec(password.getBytes("UTF-8"));
			cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
			// 加密
			byte[] result = cipher.doFinal(content);

			return new String(result, "UTF-8");

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new String();

	}

}
