package util.encode;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Date;

import javax.crypto.Cipher;

public class CAUtil {
	/**
	 * Java密钥库(Java 密钥库，JKS)KEY_STORE
	 */
	public static final String KEY_STORE = "JKS";
    
    /**
     * 最大文件加密块
     */
    private static final int MAX_ENCRYPT_BLOCK = 117;
    
    /**
     * 最大文件解密块
     */
    private static final int MAX_DECRYPT_BLOCK = 128;

	/**
     * <p>
     * 获得密钥库
     * </p>
     * 
     * @param keyStorePath 密钥库存储路径
     * @param storeFilePass 密钥库（JKS文件的密码）
     * @return
     * @throws Exception
     */
    private static KeyStore getKeyStore(String keyStorePath, String storeFilePass)
            throws Exception {
    	 FileInputStream in=null;
    	 KeyStore keyStore=null;
    	try{
        in = new FileInputStream(keyStorePath);
        System.out.println("getKeyStore in ="+in);
        keyStore = KeyStore.getInstance(KEY_STORE);
        keyStore.load(in, storeFilePass.toCharArray());
        KeyStore.getDefaultType();
    	}finally{
    		 in.close();
    	}
    	System.out.println("getKeyStore keyStore="+keyStore);
        return keyStore;
    }
    
    /**
     * <p>
     * 根据密钥库获得私钥
     * </p>
     * 
     * @param keyStorePath 密钥库存储路径
     * @param storeFilePass 密钥库密码
     * @param alias 密钥库别名
     * @param privateKeyPassword 私钥密码
     * @return
     * @throws Exception
     */
    private static PrivateKey getPrivateKey(String keyStorePath,String storeFilePass,String alias, String privateKeyPassword) 
            throws Exception {
        KeyStore keyStore = getKeyStore(keyStorePath, storeFilePass);
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, privateKeyPassword.toCharArray());
        return privateKey;
    }
    
    /**
     * <p>
     * 根据密钥库获得证书
     * </p>
     * 
     * @param keyStorePath 密钥库存储路径
     * @param storeFilePass 密钥库密码
     * @param alias 密钥库别名
     * @return
     * @throws Exception
     */
    private static Certificate getCertificate(String keyStorePath,String storeFilePass,String alias) 
            throws Exception {
        KeyStore keyStore = getKeyStore(keyStorePath, storeFilePass);
        Certificate certificate = keyStore.getCertificate(alias);
        return certificate;
    }

    /**
     * <p>
     * 根据密钥库获得私钥
     * </p>
     * 
     * @param keyStorePath 密钥库存储路径
     * @param storeFilePass 密钥库密码
     * @param alias 密钥库别名
     * @return
     * @throws Exception
     */
    private static PublicKey getPublicKey(String keyStorePath,String storeFilePass,String alias) 
            throws Exception {
    	Certificate cer=getCertificate(keyStorePath,storeFilePass,alias);
    	return cer.getPublicKey();
    }

    /**
     * <p>
     * 公钥加密
     * </p>
     * 
     * @param data 源数据
     * @param keyStorePath 证书存储路径
     * @param storeFilePass 证书存储路径
     * @param alias 证书存储路径
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String keyStorePath,String storeFilePass,String alias)
            throws Exception {
        // 取得公钥
        PublicKey publicKey = getPublicKey(keyStorePath,storeFilePass,alias);
        Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }
    
    
    /** 
     * <p>
     * 公钥解密
     * </p>
     * 
     * @param encryptedData 已加密数据
     * @param keyStorePath 证书存储路径
     * @param storeFilePass 证书存储路径
     * @param alias 证书存储路径
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] encryptedData, String keyStorePath,String storeFilePass,String alias)
            throws Exception {
        PublicKey publicKey = getPublicKey(keyStorePath,storeFilePass,alias);
        Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }
    
    /** 
     * <p>
     * 私钥加密
     * </p>
     * 
     * @param data 源数据
     * @param keyStorePath 密钥库存储路径
     * @param storeFilePass 密钥库密码
     * @param alias 密钥库别名
     * @param privateKeyPassword 私钥密码
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String keyStorePath,String storeFilePass,String alias, String privateKeyPassword) 
            throws Exception {
        // 取得私钥
        PrivateKey privateKey = getPrivateKey(keyStorePath,storeFilePass,alias,privateKeyPassword);
        Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段加密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_ENCRYPT_BLOCK) {
                cache = cipher.doFinal(data, offSet, MAX_ENCRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_ENCRYPT_BLOCK;
        }
        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }

    
    
    /**
     * <p>
     * 私钥解密
     * </p>
     * 
     * @param encryptedData 已加密数据
     * @param keyStorePath 密钥库存储路径
     * @param storeFilePass 密钥库密码
     * @param alias 密钥库别名
     * @param privateKeyPassword 私钥密码
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] encryptedData,String keyStorePath,String storeFilePass,String alias, String privateKeyPassword) 
            throws Exception {
        // 取得私钥
        PrivateKey privateKey = getPrivateKey(keyStorePath,storeFilePass,alias,privateKeyPassword);
        Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        // 解密byte数组最大长度限制: 128
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] cache;
        int i = 0;
        // 对数据分段解密
        while (inputLen - offSet > 0) {
            if (inputLen - offSet > MAX_DECRYPT_BLOCK) {
                cache = cipher.doFinal(encryptedData, offSet, MAX_DECRYPT_BLOCK);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }
            out.write(cache, 0, cache.length);
            i++;
            offSet = i * MAX_DECRYPT_BLOCK;
        }
        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }
    
    /**
     * <p>
     * 生成数据签名
     * </p>
     * 
     * @param data 源数据
     * @param keyStorePath 密钥库存储路径
     * @param storeFilePass 密钥库密码
     * @param alias 密钥库别名
     * @param privateKeyPassword 私钥密码
     * @return
     * @throws Exception
     */
    public static byte[] sign(byte[] data, String keyStorePath,String storeFilePass,String alias, String privateKeyPassword) 
            throws Exception {
        // 获得证书
    	//Certificate cert = getCertificate(keyStorePath,storeFilePass,alias);
    	X509Certificate x509Certificate = (X509Certificate) getCertificate(keyStorePath,storeFilePass,alias);
    	PrivateKey privateKey = getPrivateKey(keyStorePath,storeFilePass,alias,privateKeyPassword);
        // 构建签名
        Signature signature = Signature.getInstance(x509Certificate.getSigAlgName());
        signature.initSign(privateKey);
        signature.update(data);
        return signature.sign();
    }
    
    /**
     * <p>
     * 生成数据签名并以BASE64编码
     * </p>
     * 
     * @param data 源数据
     * @param keyStorePath 密钥库存储路径
     * @param alias 密钥库别名
     * @param password 密钥库密码
     * @return
     * @throws Exception
     */
    public static String signToBase64(byte[] data, String keyStorePath,String storeFilePass,String alias, String privateKeyPassword) 
            throws Exception {
        //return new sun.misc.BASE64Encoder().encode(sign(data, keyStorePath,storeFilePass, alias, privateKeyPassword));
        return Base64Util.encodeForUrl(sign(data, keyStorePath,storeFilePass, alias, privateKeyPassword));
    }
    
    /**
     * <p>
     * 验证签名
     * </p>
     * 
     * @param data 已加密数据
     * @param sign 数据签名[BASE64]
     * @param keyStorePath 密钥库存储路径
     * @param alias 密钥库别名
     * @param password 密钥库密码
     * @return
     * @throws Exception
     */
    public static boolean verifySign(byte[] data, String sign,String keyStorePath,String storeFilePass,String alias) 
            throws Exception {
        // 获得证书
        X509Certificate x509Certificate = (X509Certificate) getCertificate(keyStorePath,storeFilePass,alias);
        // 获得公钥
        PublicKey publicKey = x509Certificate.getPublicKey();
        // 构建签名
        Signature signature = Signature.getInstance(x509Certificate.getSigAlgName());
        signature.initVerify(publicKey);
        signature.update(data);
        //return signature.verify(new sun.misc.BASE64Decoder().decodeBuffer(sign));
        return signature.verify(Base64Util.decodeForUrl(sign));
    }
    
    /**
     * <p>
     * 验证数字证书是在给定的日期是否有效
     * </p>
     * 
     * @param keyStorePath 密钥库存储路径
     * @param alias 密钥库别名
     * @param password 密钥库密码
     * @return
     */
    public static boolean verifyCertificate(Date date,String keyStorePath,String storeFilePass,String alias) {
        Certificate certificate;
        try {
        	System.out.println("verifyCertificate keyStorePath==="+keyStorePath);
            certificate = getCertificate(keyStorePath,storeFilePass,alias);
            System.out.println("verifyCertificate certificate==="+certificate);
            return verifyCertificate(date,certificate);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * <p>
     * 验证证书是否过期或无效
     * </p>
     * 
     * @param date 日期
     * @param certificate 证书
     * @return
     */
    public static boolean verifyCertificate(Date date, Certificate certificate) {
        boolean isValid = true;
        try {
            X509Certificate x509Certificate = (X509Certificate) certificate;
            x509Certificate.checkValidity(date);
        } catch (Exception e) {
        	e.printStackTrace();
            isValid = false;
        }
        return isValid;
    }

   
	public static void main(String[] str) throws Exception {
		 FileInputStream in=null;
    	
		
//		String alias="wbxtws";//证书的通用名
//		String keyStorePath = "F:\\work\\201802\\接口证书\\"+alias+".jks";
//		String storeFilePass="wbxtws20160128";//JKS文件的密码
//		String privateKeyPassword="wbxtws20160128";//私钥密码
//		System.out.println("证书是否有效:"+verifyCertificate(new Date(),keyStorePath, storeFilePass, alias));
//		
		String alias="wbxtwstest";//证书的通用名
		String keyStorePath = "F:\\work\\201802\\接口证书\\"+alias+".jks";
		String storeFilePass="wbxtwstest";//JKS文件的密码
		String privateKeyPassword="wbxtwstest";//私钥密码
		System.out.println("证书是否有效:"+verifyCertificate(new Date(),keyStorePath, storeFilePass, alias));
		
		/*
        String source =UUID.randomUUID().toString();
        System.out.println(source.length());
        byte[] data = source.getBytes();
        System.err.println("====私钥加密=====");
        //私钥加密
        byte[] encrypt =CAUtil.encryptByPrivateKey(data, keyStorePath, storeFilePass, alias, privateKeyPassword);
        //"ISO-8859-1"
        String baseS = Base64Util.encode(encrypt);
        System.out.println("baseS=== "+baseS);
        baseS= baseS.replaceAll("\r\n","");
        System.out.println("baseS=== "+baseS);
        encrypt=Base64Util.decode(baseS);
        
        System.err.println("====公钥解密=====");
        
        //公钥解密
        byte[] decrypt =CAUtil.decryptByPublicKey(encrypt, keyStorePath, storeFilePass, alias);
        
        System.err.println("====签名=====");
        String sign=CAUtil.signToBase64(decrypt, keyStorePath, storeFilePass, alias, privateKeyPassword);
//        System.out.println("数字签名："+sign);
        
        //对解密后的数据进行验证签名
        System.out.println("签名是否有效:"+verifySign(decrypt,sign,keyStorePath, storeFilePass, alias));
        System.out.println("");
        String outputStr = new String(decrypt);

        System.out.println("加密前: \r\n" + source + "\r\n" + "解密后: \r\n" + outputStr);
       
        String desRes = DesUtil.encrypt("发送的内容", source);
        System.out.println(desRes);
        
        System.out.println(DesUtil.decrypt(desRes, source));
      */
//		
		String conform = "WBXT-001";
		String KeyValue_s = "8ac6a2cc-f564-4fca-9c43-d96caa2a42b5";
		String InterfaceNO = "NB_WBXT_EXTER_013";
		String InXML_s="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><input><aac001>1012094391</aac001></input>";
		//String InXML_s="<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><input><usercode>1010711036</usercode></input>";
//		
		byte[] data = KeyValue_s.getBytes();
		byte[] encrypt =CAUtil.encryptByPrivateKey(data, keyStorePath, storeFilePass, alias, privateKeyPassword);
        String KeyValue_t = Base64Util.encode(encrypt);
        KeyValue_t= KeyValue_t.replaceAll("\r\n","");
//		
        String InXML_t = DesUtil.encrypt(InXML_s, KeyValue_s);
        InXML_t=InXML_t.replaceAll("\r\n","");
        System.out.println("========================================");
        System.out.println("conform= "+conform);
        System.out.println("KeyValue_s= "+KeyValue_s);
        System.out.println("KeyValue_t= "+KeyValue_t);
        System.out.println("InterfaceNO= "+InterfaceNO);
        System.out.println("InXML_t= "+InXML_t);
        System.out.println("========================================");
        
        byte[] aa=Base64Util.decode(KeyValue_t);
//        
        System.out.println(new String( CAUtil.decryptByPublicKey(aa, keyStorePath, storeFilePass, alias)));
        
        System.out.println(KeyValue_s);
        String a = "xvF+v3ANT9QfQ4/hyoNZhk2IjoLukDX9ewp2yc+Q5h8loV+O2w60eVDGFwxZHxCJHVcSC1GMU82IUozpmWkmT7bJ4Zu+TMr9negROxiiORQ2p3C18yxyiFyONzhVnx5XejtCSDLBFJZ/7AkSQWtrWUNx3qh0D1Lzo5lj6DY+MRjkOEPpfIJ7yxlQx9Q5OAaQLeKe1d/a8nS8UcTICXciASeL6YpdKZ0iOZ28Ya0I4ptYwYf9R3BJnUz1cLNTHcSHV7Dfw0nbsai2yeGbvkzK/Z3oETsYojkUpaAqIufopN8=";
        String desStr = DesUtil.decrypt(a, KeyValue_s);
        System.out.println(desStr);
        desStr = new String(desStr.getBytes("UTF-8"),"UTF-8");
        System.out.println(desStr);
        
        
        
        
//        String b = "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";
//        String desStr = DesUtil.decrypt(b, "8ac6a2cc-f564-4fca-9c43-d96caa2a42b5");
        
//        System.out.println(desStr);
        
        
     /*   String res = "1ruMUveYPaB0t1nfmzVWMGveYx3GT+tZHBYKmA2ddrP91hd567NVtRCq9TiKmRAKLgzDZBQNyILE"+
		"wMVowJQ5vzzgRVFzcLQ7+IbVjRc6cGm77SgZlrUNOgUlZ1D9cn6pUjZIyKCTgI13WtgKykfs4wES"+
		"WSMNjzQ+r4vY7NEJe25jtTMZddxnz+DHRm+5roIyD0EfRCFtamo//awcdCKVmgS8aiy9mXkw0JIo"+
		"9vK085d1j9cP9SRPKg2o4wlKkpuiCHh7/HLdVUpD/OgY+WcmVEfiwwpsTVJWl6lQ728IVBY=";
        String decKey = "138e6ddc-d3e6-47ca-a2a3-61241888b344";
        System.out.println(DesUtil.decrypt(res, decKey));*/
	}
	
}
