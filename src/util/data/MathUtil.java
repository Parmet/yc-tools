package util.data;

import java.math.BigDecimal;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @作者 liuyj
 * @创建日期 May 16, 2016
 */
public class MathUtil {
	
	/**
	 * 
	 * @作者 liuyj
	 * @日期 May 16, 2016 
	 * @param v 要转换的数值
	 * @param scale 保留的精度
	 * @param divisor 被除数，默认为1
	 * @return
	 */
	public static double round(double v, int scale,String divisor) {  
		   if (scale < 0) {  
		    throw new IllegalArgumentException(  
		      "The scale must be a positive integer or zero");  
		   }  
		   if(StringUtils.isEmpty(divisor))divisor="1";
		   BigDecimal b = new BigDecimal(Double.toString(v));  
		   BigDecimal ne = new BigDecimal(divisor);  
		   return b.divide(ne, scale, BigDecimal.ROUND_HALF_UP).doubleValue();  
		}  
}
