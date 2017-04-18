package util.string;

import org.apache.commons.lang.StringUtils;

import util.security.SecretUtil;

public class StringUtil {
	
	/**
	 * 获取名字的背景颜色
	 * @param name
	 * @return
	 */
	public static String getAvatarBgColor(String name){
		if(StringUtils.isNotBlank(name)){
			return "#"+SecretUtil.MD5(name.substring(0, 1)).substring(0, 6);
		}
		return "";
	}
}
