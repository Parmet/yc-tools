package util.encode;

import java.util.Date;
import java.util.UUID;

public class DesTest {
	public static void main(String[] args) {
		 bulidKey();
		 decode();
		 check();
		bulidRequest();
	}

	private static void bulidRequest() {
		String alias = "wbxtwstest";// 证书的通用名
		String keyStorePath = "F:\\temp\\0117\\" + alias + ".jks";
		String storeFilePass = "wbxtwstest";// JKS文件的密码
		String privateKeyPassword = "wbxtwstest";// 私钥密码

		String source = UUID.randomUUID().toString();
		try {
			System.out.println(source);
			byte[] data = source.getBytes("UTF-8");
			byte[] encrypt = CAUtil.encryptByPrivateKey(data, keyStorePath,
					storeFilePass, alias, privateKeyPassword);
			String baseS = Base64Util.encode(encrypt);
			baseS = baseS.replaceAll("\r\n", "");
			System.out.println(baseS);

			String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><input><aac147>440181198109113319</aac147></input>";
			String InXML_t = DesUtil.encrypt(xml, source);
			InXML_t = InXML_t.replaceAll("\r\n", "");
			System.out.println(InXML_t);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static void bulidKey() {
		String alias = "wbxtwstest";// 证书的通用名
		String keyStorePath = "F:\\temp\\0117\\" + alias + ".jks";
		String storeFilePass = "wbxtwstest";// JKS文件的密码
		String privateKeyPassword = "wbxtwstest";// 私钥密码
		String source = UUID.randomUUID().toString();
		byte[] data = source.getBytes();
		System.err.println("====私钥加密=====");
		// 私钥加密
		byte[] encrypt = null;
		try {
			encrypt = CAUtil.encryptByPrivateKey(data, keyStorePath,
					storeFilePass, alias, privateKeyPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// "ISO-8859-1"
		String baseS = Base64Util.encode(encrypt);
		baseS = baseS.replaceAll("\r\n", "");
		System.out.println("baseS=== " + baseS);
		// encrypt = Base64Util.decode(baseS);
	}

	private static void decode() {
		try {
			String data = "k1xVXZcOpJRjySCrURQVW43/Cgkhq5NCXLg+QECRYoKD3Ip7KZz7S+DLmj6iJg9R2rYrdn18gRivm+9PRyGOFWuDl+W9qbir08zIYgY9jkAfeRb/4VAy1/7ETIlGqvxsOeyt0I+N5uWYDAnX9Q+gOF7OGgIUQmYD2MfQsjkKSL7hrrhblBRiNQccaPhNwhwiH+IpQfaz4gwBilU9EKNkc/7pkx9URzj8gmpj5+K9wf1BxeAfQbTzaHEL+a0fwHhUcnB2XO4twxDqKz9uyaPK1Moip3kfxB99Qzaqmi8ShJBAGo22CUe/TPU+ImmAn0jZjl4FlRt7xHs=";
			String desStr = DesUtil.decrypt(data, "1da0b422-f4c3-4d1d-b4ba-6b642d11f5da");
			System.out.println("result: " + desStr);
		} catch (Exception e) {
			System.out.println("decode error");
			e.printStackTrace();
		}

	}

	private static void check() {
		String alias = "wbxtws";// 证书的通用名
		String keyStorePath = "F:\\temp\\0117\\" + alias + ".jks";
		String storeFilePass = "wbxtws20160128";// JKS文件的密码
//		String privateKeyPassword = "wbxtws20160128";// 私钥密码
		System.out.println("证书是否有效:"
				+ CAUtil.verifyCertificate(new Date(), keyStorePath,
						storeFilePass, alias));
	}
}
