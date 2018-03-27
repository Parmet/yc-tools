package util.data;

import javax.servlet.http.HttpServletRequest;

public class ValidUtil {

	/** 身份证号从15转到18位 */
	public static String ID15To18(String strID15) {
		if (strID15 == null || strID15.equals("")) {
			return null;
		}
		if (strID15.length() == 18) {
			return strID15;
		}
		if (strID15.length() == 15) {
			int[] w = { 7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2, 1 };
			String[] a = { "1", "0", "X", "9", "8", "7", "6", "5", "4", "3",
					"2" };
			int sum = 0;
			strID15 = strID15.substring(0, 6) + "19" + strID15.substring(6, 15);
			for (int i = 0; i < 17; i++) {
				sum += Integer.parseInt(strID15.substring(i, i + 1)) * w[i];
			}
			int intMod = (int) (sum % 11);
			return strID15 + a[intMod];
		}
		return null;
	}

	/** 身份证号从18转到15位 */
	public static String IdFrom18To15(String strId) {
		if (strId == null || strId.length() != 18) {
			return strId;
		}
		return strId.substring(0, 6) + strId.substring(8, 17);
	}

	/**
	 * 检测字符串值，如果值为null，则返回空字符串
	 * 
	 * @Object checkValue 源字符串
	 * @return String 返回修改后的字符串
	 */
	public String ClearNull(Object checkValue) {
		if (checkValue == null) {
			// 如果值为空，则返回 "&nbsp;"
			return "";
		} else {
			// 如果值不为空，则返回原字符串的内容
			if (checkValue.toString().equals("null")) {
				return "";
			} else {
				return checkValue.toString();
			}
		}
	}
	/**
	 * 检测字符串值，如果值为null，则返回true
	 * 
	 * @Object checkValue 源字符串
	 * @return String 返回修改后的字符串
	 */
	public static boolean checkNull(String checkValue) {
		if (checkValue == null||checkValue.length()==0) {			
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 根据身份证号，得到出生年月字符串，如果身份证号长度不对返回""
	 * @param strId
	 * @return
	 */
	public static String birthFromId(String strId) {
		if (strId == null)
			return "";
		if (strId.length() == 15) {
			return "19"+strId.substring(6, 8) + "-" + strId.substring(8, 10)
			+ "-" + strId.substring(10, 12);
		}
		if (strId.length() == 18) {
			return  strId.substring(6, 10) + "-" + strId.substring(10, 12) + "-"
			+ strId.substring(12, 14);
			//440111196810313636
		}
		return "";
	}
	
	/**
	 * 根据身份证号，得到性别，男返回字符串"男",女返回字符串"女",如果身份证号长度不对返回""
	 * @param strId
	 * @return
	 */
	public static String sexFromId(String strId) {
		if (strId == null)
			return "";
		if (strId.length() == 15) {
			String sex=strId.substring(12, 15);
			if(Integer.parseInt(sex)%2==0){
				return "女";
			}else{
				return "男";
			}
		}
		if (strId.length() == 18) {
			String sex=strId.substring(14, 17);
			if(Integer.parseInt(sex)%2==0){
				return "女";
			}else{
				return "男";
			}
		}
		return "";
	}
	public static String null2string(String str){
		if(str==null){
			return "";
		}else{
			return str;
		}
	}
	public String getPath(HttpServletRequest req){
		//寻找项目根目录
		String prefix = req.getSession().getServletContext().getRealPath("/");		
        if (prefix == null || prefix.equals("")) {          
        	/*java.net.URL url = this.getClass().getResource("/");
        	String mSchemaPath = url.getFile();
        	if (mSchemaPath != null || !mSchemaPath.equals("")) {
        		String separator = "/";
        		int lastSlash = mSchemaPath.lastIndexOf(separator);
        		if (lastSlash == -1) {
        				separator = "\\";
        				lastSlash = mSchemaPath.lastIndexOf(separator);
        		}
        		prefix = mSchemaPath.substring(0, lastSlash);
        		prefix = prefix.substring(0, prefix.lastIndexOf(separator));
        		prefix = prefix.substring(0, prefix.lastIndexOf(separator) + 1);  
        	}*/
        	String mSchemaPath =this.getClass().getClassLoader().getResource("/").getPath();
        	if(mSchemaPath!=null){
        		int lastSlash=mSchemaPath.lastIndexOf("WEB-INF");
        		prefix=mSchemaPath.substring(0,lastSlash);
        	}
        } 
        return prefix;
	}
}
