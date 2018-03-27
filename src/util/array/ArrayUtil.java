package util.array;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * 
 * @ClassName: ArrayUtil
 * @Description: 数组集合工具类
 * @author zhengzh@chinauip.com
 * @date 2017-10-31 上午11:25:21
 *
 */
public class ArrayUtil {

	/**
	 * @Description:  用于连接数组或集合 
	 * @param obj   需求连接的数组 或集合
	 * @param 连接的符号  例如： "," "|"
	 * @return  连接后的字符串
	 */
	public static String arrayJoin(Object obj,String regex){
		if(regex == null){
			regex = "";
		}
		if(obj == null){
			return "";
		}
		StringBuffer sb = new StringBuffer();
		String returnStr = "";		
		if (obj instanceof Collection) {
			@SuppressWarnings("unchecked")
			Collection<String> collection = (Collection<String>) obj;
			for(String  st: collection){
				if(StringUtils.isNotBlank(st)){
					sb.append(st+regex);
				}
			}
		}else if(obj instanceof Object[]){
			Object[] objs = (Object[]) obj;
			for(Object  o: objs){
				if(StringUtils.isNotBlank(o.toString())){
					sb.append(o.toString()+regex);
				}
			}
		}else if(obj instanceof char[]){
			char[] objs = (char[]) obj;
			for(Object  o: objs){
				if(StringUtils.isNotBlank(o.toString())){
					sb.append(o.toString()+regex);
				}
			}
		}else if(obj instanceof char[]){
			char[] objs = (char[]) obj;
			for(Object  o: objs){
				if(StringUtils.isNotBlank(o.toString())){
					sb.append(o.toString()+regex);
				}
			}
		}
		if(sb.length()>1){
			returnStr = sb.toString().substring(0,sb.length()-1);
		}
		return returnStr;
	}
	
	/**
	 * @Description:  用于连接数组或集合 
	 * @param obj   需求连接的数组 或集合
	 * @param 连接的符号  例如： "," "|"
	 * @param  open 
	 * @param  close
	 * @return  连接后的字符串  
	 */
	public static String arrayJoin(Object obj,String regex,String open,String close){
		
		String joinStr = arrayJoin(obj,regex);
		if(!"".equals(joinStr)){
			joinStr = open + joinStr+close;
		}
		return joinStr;
	}
	/**
	 * 
	 * @Description: 用于list集合的切割
	 * @param @param list
	 * @param @param splitSize 切割大小 
	 * @return List<List<T>>    返回类型
	 * @throws
	 */
	public static <T> List<List<T>> splitList(List<T> list, int splitSize) {
		List<List<T>> listArray = new ArrayList<List<T>>();
		if(list == null || list.size()==0){
			return listArray;
		}
		List<T> subList = null; 
		for (int i = 0; i < list.size(); i++) {
			if (i % splitSize == 0) {//每次到达页大小的边界就重新申请一个subList
				subList = new ArrayList<T>();
				listArray.add(subList);
			}
			subList.add(list.get(i));
		}
		return listArray;
	}
	
	/**
	 * @Description:  判断数组或集合是否为空 ,或长度是否为0(字符串数组除外)
	 * @param obj   数组 或集合
	 * @return  boolean 为空时 true 否则为 false
	 */
	public static boolean isEmpty(Object obj){
		if(obj == null){
			return true;
		}
		if (obj instanceof Collection) {
			@SuppressWarnings("rawtypes")
			Collection collection = (Collection) obj;
			return collection.isEmpty();
			
		}else if(obj instanceof Object[]){
			Object[] objs = (Object[]) obj;
			return objs.length==0?true:false;
		}else if(obj instanceof char[]){
			char[] objs = (char[]) obj;
			return objs.length==0?true:false;
		}
		else if(obj instanceof int[]){
			int[] objs = (int[]) obj;
			return objs.length==0?true:false;
		}
		else{
			System.out.println("参数类型不正确");
		}
		
		return false;
	}
	/**
	 * @Description:  判断数组或集合是否为空 ,或长度是否为0(字符串数组除外)
	 * @param obj   数组 或集合
	 * @return  boolean 为空时 true 否则为 false
	 */
	public static boolean isNotEmpty(Object obj){
		return !ArrayUtil.isEmpty(obj);
	}
}
