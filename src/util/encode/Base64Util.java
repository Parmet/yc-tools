package util.encode;

/**
 * base64���빤����
 * 
 * @author wanggang
 * @version 2010-12-31
 */
public class Base64Util {

	/**
	 * �� s ���� BASE64 ����
	 * 
	 * @param s
	 * @return
	 */
	public static String encode(byte[] s) {
		if (s == null)
			return null;
		return (new sun.misc.BASE64Encoder()).encode(s);
	}

	/**
	 * �� s ���� BASE64 ����,���url�ı���
	 * 
	 * @param s
	 * @return
	 */
	public static String encodeForUrl(byte[] s) {
		if (s == null)
			return null;
		String standerBase64 = encode(s);
		String encodeForUrl = standerBase64;
		// ת�����url��base64����
		encodeForUrl = encodeForUrl.replace("=", "");
		encodeForUrl = encodeForUrl.replace("+", "*");
		encodeForUrl = encodeForUrl.replace("/", "-");
		// ȥ����
		encodeForUrl = encodeForUrl.replace("\n", "");
		encodeForUrl = encodeForUrl.replace("\r", "");

		// ת��*��Ϊ -x-
		// ��ֹ��Υ��Э����ַ�
		encodeForUrl = encodeSpecialLetter1(encodeForUrl);

		return encodeForUrl;

	}

	/**
	 * ת��*��Ϊ -x-�� Ϊ�˷�ֹ��Υ��Э����ַ�-x ת��Ϊ-xx
	 * 
	 * @param str
	 * @return
	 */
	private static String encodeSpecialLetter1(String str) {
		str = str.replace("-x", "-xx");
		str = str.replace("*", "-x-");
		return str;
	}

	/**
	 * ת�� -x-��Ϊ*��-xxת��Ϊ-x
	 * 
	 * @param str
	 * @return
	 */
	private static String decodeSpecialLetter1(String str) {
		str = str.replace("-x-", "*");
		str = str.replace("-xx", "-x");
		return str;
	}

	/**
	 * �� s ���� BASE64 ����
	 * 
	 * @param s
	 * @return
	 */
	public static String encode(String s) {

		if (s == null)
			return null;
		return encode(s.getBytes());
	}

	/**
	 * �� BASE64 ������ַ� s ���н���
	 * 
	 * @param s
	 * @return
	 */
	public static byte[] decode(String s) {
		if (s == null)
			return null;
		sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
		try {
			byte[] b = decoder.decodeBuffer(s);
			return b;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * �� BASE64 ������ַ� s ���н���
	 * 
	 * @param s
	 * @return
	 */
	public static byte[] decodeForUrl(String s) {
		if (s == null)
			return null;
		s = decodeSpecialLetter1(s);
		s = s.replace("*", "+");
		s = s.replace("-", "/");
		s += "=";
		sun.misc.BASE64Decoder decoder = new sun.misc.BASE64Decoder();
		try {
			byte[] b = decoder.decodeBuffer(s);
			return b;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String a = "�� s ���� BASE64 ����,���url�ı��뽫 suserId=1441&mailId=981&date=2011-02-15-62";
		String b = encodeForUrl(a.getBytes());
		// b = b.replace("\n", "");
		// b = b.replace("\r", "");
		System.out.println(b);

		System.out.println(new String(decodeForUrl(b)));

	}

}