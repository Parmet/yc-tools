package util.reflect;

import java.lang.reflect.Field;

import org.apache.commons.lang.StringUtils;

/**
 * 
* @ClassName: ReflectHelper
* @Description: 反射工具
* @author huj hujianopp@163.com
* @date Apr 19, 2012 3:10:13 PM
*
 */
public class ReflectHelper {
	/** 
     * 获取obj对象fieldName的Field 
     * @param obj 
     * @param fieldName 
     * @return 
     */  
    public static Field getFieldByFieldName(Object obj, String fieldName) {  
        for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass  
                .getSuperclass()) {  
            try {  
                return superClass.getDeclaredField(fieldName);  
            } catch (NoSuchFieldException e) {  
            }  
        }  
        return null;  
    }  
  
    /** 
     * 获取obj对象fieldName的属性值 
     * @param obj 
     * @param fieldName 
     * @return 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws IllegalArgumentException 
     * @throws IllegalAccessException 
     */  
    public static Object getValueByFieldName(Object obj, String fieldName)  
            throws SecurityException, NoSuchFieldException,  
            IllegalArgumentException, IllegalAccessException {  
        Field field = getFieldByFieldName(obj, fieldName);  
        Object value = null;  
        if(field!=null){  
            if (field.isAccessible()) {  
                value = field.get(obj);  
            } else {  
                field.setAccessible(true);  
                value = field.get(obj);  
                field.setAccessible(false);  
            }  
        }  
        return value;  
    }  
  
    /** 
     * 设置obj对象fieldName的属性值 
     * @param obj 
     * @param fieldName 
     * @param value 
     * @throws SecurityException 
     * @throws NoSuchFieldException 
     * @throws IllegalArgumentException 
     * @throws IllegalAccessException 
     */  
    public static void setValueByFieldName(Object obj, String fieldName,  
            Object value) throws SecurityException, NoSuchFieldException,  
            IllegalArgumentException, IllegalAccessException {  
        Field field = obj.getClass().getDeclaredField(fieldName);  
        if (field.isAccessible()) {  
            field.set(obj, value);  
        } else {  
            field.setAccessible(true);  
            field.set(obj, value);  
            field.setAccessible(false);  
        }  
    }  
    
    
    public static Object convertLangData(String type,String val){
    	if(type==null )
    		return val;
    	type = type.trim();
    	if(type.equals("java.lang.Integer")){
    		if(StringUtils.isEmpty(val))return null;
    		return new Integer(val);
    	}else if(type.equals("java.lang.Long")){
    		if(StringUtils.isEmpty(val))return null;
    		return new Long(val);
    	}else if(type.equals("java.lang.Double")){
    		if(StringUtils.isEmpty(val))return null;
    		return new Double(val);
    	}else if(type.equals("java.lang.Float")){
    		if(StringUtils.isEmpty(val))return null;
    		return new Float(val);
    	}else if(type.equals("java.lang.Short")){
    		if(StringUtils.isEmpty(val))return null;
    		return new Short(val);
    	}
    	return val;
    	
    }

}
