package util.data;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesHelp {

	private InputStream fileIn;
	private Properties props ;

	
	
	public InputStream getFileIn() {
		return fileIn;
	}


	public void setFileIn(InputStream fileIn) throws Exception {
		this.fileIn = fileIn;
		try {
			initProperties();
		} catch (Exception e) {
			throw new Exception("属性文件没有初始化");
		}
	}


	public void initProperties()throws Exception{
		
		props = new Properties();  
		try{
			InputStream in = new BufferedInputStream (fileIn);  
			props.load(in);  
		}catch(Exception ex){
			throw new Exception();
		}

	}
	
	
	public String getKeyValue(String key) throws Exception{
		if(props==null){
			try {
				initProperties();
			} catch (Exception e) {
				throw new Exception("属性文件没有初始化");
			}
		}
			
		String value = props.getProperty (key);  
		return value;
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Map getKeyValues() throws Exception{
		if(props==null){
			try {
				initProperties();
			} catch (Exception e) {
				throw new Exception("属性文件没有初始化");
			}
		}
		Map map  =  new HashMap();
		Enumeration en = props.propertyNames();  
        while (en.hasMoreElements()) {  
         String key = (String) en.nextElement();  
               String Property = props.getProperty (key);  
               map.put(key, Property);
           }  
        return map;
	}
	
	
	
}
