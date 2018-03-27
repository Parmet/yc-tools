package util.ip;
import javax.servlet.http.HttpServletRequest;

public class IPUtil {
	
	/**
	 * 获取客户端IP
	 * @param request
	 * @return
	 */
	public static String getIpAddr(HttpServletRequest  request) {
		try {
			String ip = request.getHeader("x-forwarded-for"); 
			System.out.println("x-forwarded-for ip: " + ip);
			if (ip != null && ip.length() != 0 && !"unknown".equalsIgnoreCase(ip)) {  
			    // 多次反向代理后会有多个ip值，第一个ip才是真实ip
			    if( ip.indexOf(",")!=-1 ){
			        ip = ip.split(",")[0];
			    }
			}  
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
			    ip = request.getHeader("Proxy-Client-IP");  
			    System.out.println("Proxy-Client-IP ip: " + ip);
			}  
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
			    ip = request.getHeader("WL-Proxy-Client-IP");  
			    System.out.println("WL-Proxy-Client-IP ip: " + ip);
			}  
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
			    ip = request.getHeader("HTTP_CLIENT_IP");  
			    System.out.println("HTTP_CLIENT_IP ip: " + ip);
			}  
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
			    ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
			    System.out.println("HTTP_X_FORWARDED_FOR ip: " + ip);
			}  
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
			    ip = request.getHeader("X-Real-IP");  
			    System.out.println("X-Real-IP ip: " + ip);
			}  
			if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
			    ip = request.getRemoteAddr();  
			    System.out.println("getRemoteAddr ip: " + ip);
			} 
			System.out.println("获取客户端ip: " + ip);
			return ip;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}  
	}
	
	
	/** 
     * ip地址转成long型数字 
     * 将IP地址转化成整数的方法如下： 
     * 1、通过String的split方法按.分隔得到4个长度的数组 
     * 2、通过左移位操作（<<）给每一段的数字加权，第一段的权为2的24次方，第二段的权为2的16次方，第三段的权为2的8次方，最后一段的权为1 
     * @param strIp 
     * @return 
     */  
    public static long ipToLong(String strIp) {  
        String[]ip = strIp.split("\\.");  
        return (Long.parseLong(ip[0]) << 24) + (Long.parseLong(ip[1]) << 16) + (Long.parseLong(ip[2]) << 8) + Long.parseLong(ip[3]);  
    }
    
    
    /** 
     * 将十进制整数形式转换成127.0.0.1形式的ip地址 
     * 将整数形式的IP地址转化成字符串的方法如下： 
     * 1、将整数值进行右移位操作（>>>），右移24位，右移时高位补0，得到的数字即为第一段IP。 
     * 2、通过与操作符（&）将整数值的高8位设为0，再右移16位，得到的数字即为第二段IP。 
     * 3、通过与操作符吧整数值的高16位设为0，再右移8位，得到的数字即为第三段IP。 
     * 4、通过与操作符吧整数值的高24位设为0，得到的数字即为第四段IP。 
     * @param longIp 
     * @return 
     */  
    public static String longToIP(long longIp) {  
        StringBuffer sb = new StringBuffer("");  
        // 直接右移24位  
        sb.append(String.valueOf((longIp >>> 24)));  
        sb.append(".");  
        // 将高8位置0，然后右移16位  
        sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16));  
        sb.append(".");  
        // 将高16位置0，然后右移8位  
        sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8));  
        sb.append(".");  
        // 将高24位置0  
        sb.append(String.valueOf((longIp & 0x000000FF)));  
        return sb.toString();  
    }  
    
}
