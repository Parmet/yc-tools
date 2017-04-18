package util.security;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import sun.misc.BASE64Decoder;

/**
 * 加密解密类
 * 
 * @author Administrator
 * 
 */
public class SecretUtil {
	
	
	/**
	 * MD5加密
	 * @param s
	 * @return
	 */
	public final static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str).toLowerCase();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	private static String Algorithm = "DESede";//加密算法的名称
    private static Cipher c;//密码器
    private static byte[] cipherByte;
    private static SecretKey deskey;//密钥
    public static String keyString = "A3F2569DESOEIWBCJOTY49JEQWF68H1Y";//获得密钥的参数
    
     //对base64编码的string解码成byte数组
     public static  byte[] deBase64(String parm) throws IOException {
        BASE64Decoder dec=new BASE64Decoder();
        byte[] dnParm = dec.decodeBuffer(parm);
        return dnParm;
     }
     
    //把密钥参数转为byte数组
     public static byte[] dBase64(String parm) throws IOException {
        BASE64Decoder dec=new BASE64Decoder();
        byte[] dnParm = dec.decodeBuffer(parm);
        return dnParm;
     }
    /**
     * 对 Byte 数组进行解密
     * @param buff 要解密的数据
     * @return 返回加密后的 String
     */
     public static String createDecryptor(byte[] buff) throws
      NoSuchPaddingException, NoSuchAlgorithmException,
      UnsupportedEncodingException {
        try {
           c.init(Cipher.DECRYPT_MODE, deskey);//初始化密码器，用密钥deskey,进入解密模式
           cipherByte = c.doFinal(buff);
        }
        catch(java.security.InvalidKeyException ex){
            ex.printStackTrace();
        }
        catch(javax.crypto.BadPaddingException ex){
            ex.printStackTrace();
        }
        catch(javax.crypto.IllegalBlockSizeException ex){
            ex.printStackTrace();
        }
        return (new String(cipherByte,"UTF-8"));
     }
     
     public static  void getKey(String key) throws IOException, InvalidKeyException,
      InvalidKeySpecException {
      byte[] dKey = dBase64(key);
        try {
          deskey=new javax.crypto.spec.SecretKeySpec(dKey,Algorithm);
          c = Cipher.getInstance(Algorithm);
        }
        catch (NoSuchPaddingException ex) {
        }
        catch (NoSuchAlgorithmException ex) {
        }
     }
     
     
     public static void main(String args[]) throws IOException,
      NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeySpecException,
      InvalidKeyException, IOException {
       SecretUtil des = new SecretUtil();
       SecretUtil.getKey(keyString);
       byte[] dBy = des.deBase64("SMOmGToaSZZ9IRN41HCCzA==");
       String dStr = des.createDecryptor(dBy);
       System.out.println("解："+dStr);
     }
	
}
