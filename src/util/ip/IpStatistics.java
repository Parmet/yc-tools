package util.ip;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashSet;
import java.util.Set;

import net.sf.json.JSONException;

/**
 * ip统计
 * 
 * @author xiaoyc
 * @createtime 2018-01-22
 */
public class IpStatistics { 
    /**
     * 把日志文件放在同个目录下，命名(后缀为.log)为 access_yyyy_MM_dd(*).log
     * 修改 targetFolder 为日志文件所在目录
     * 非广东 ip 存放在日志文件所在目录下的 not-gd-ip.txt
     */
	// 目标文件
	static String targetFolder = "F:\\work\\201801\\ip归属\\wbqt2\\";
	
	// 非广东ip存放文件
	static String saveFile = targetFolder + "not-gd-ip.txt";
	// 所有ip
	static Set<String> ips = new HashSet<String>(6000);
	// 所有非广东ip
	static Set<String> noGDIps = new HashSet<String>(3600);

	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		
		// 统计总ip数量
		System.out.println("============================== 统计总ip数量 ==============================");
		String file = targetFolder;
		for (int i = 1; i < 7; i++) {
			String curFile = file + "access_yyyy_MM_dd(" + i + ").log";
			readTextToSet(curFile);
		}
		
		// 统计非广东ip数量
		System.out.println("");
		System.out.println("============================ 统计非广东ip数量 ============================");
		filteNotGDIp();
		
		// 打印信息
		System.out.println("");
		printMsg();
		
		System.out.println("");
		System.out.println("============================ 存档非广东ip数量 ============================");
		saveNotGDIpToTxt();
		System.out.println("非广东 ip 存档在" + saveFile);
		
		long end = System.currentTimeMillis();
		System.out.println("");
		printTime(end - start);
	}
	
	/**
	 * 读取文件中的ip到集合
	 * 
	 * @param file
	 */
	private static void readTextToSet(String file) {
		System.out.println("读取日志: " + file);
		String text = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			String ip = "";
			while (br.readLine() != null) {
				text = br.readLine();
				if (text != null && text != "null" 
					&& text != "" && text.split("\\s+").length > 1) {
					ip = text.split("\\s+")[0].trim();
					ips.add(ip);
				}
			}
	
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Exception: " + text);
		}
	}

	/**
	 * 筛选非广东ip
	 */
	private static void filteNotGDIp() {
		for (String ip : ips) {
			String gs = getAddressByIP(ip);
			if (gs == null || gs == "" || !gs.contains("广东")) {
				System.out.println(ip + ": " + gs);
				noGDIps.add(ip + ": " + gs);
			}
		}
	}

	/**
	 * 获取ip地址
	 * 
	 * @param strIP
	 * @return
	 */
	private static String getAddressByIP(String strIP) {
		try {
			URL url = new URL(
					"http://api.map.baidu.com/location/ip?ak=F454f8a5efe5e577997931cc01de3974&ip="
							+ strIP);
			URLConnection conn = url.openConnection();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "utf-8"));
			String line = null;
			StringBuffer result = new StringBuffer();
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}
			reader.close();
			
//			String ipAddr = result.toString();
			try {
//				JSONObject obj1 = new JSONObject(ipAddr);
//				if ("0".equals(obj1.get("status").toString())) {
//					JSONObject obj2 = new JSONObject(obj1.get("content").toString());
//					JSONObject obj3 = new JSONObject(obj2.get("address_detail").toString());
//					return obj3.get("province").toString();
//				} else {
					return "其他";
//				}
			} catch (JSONException e) {
				e.printStackTrace();
				return "其他";
			}
	
		} catch (IOException e) {
			return "其他";
		}
	}

	/**
	 * 保存非广东ip到文本
	 */
	private static void saveNotGDIpToTxt() {
		File f = new File(saveFile);
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(saveFile));
			for (String s : noGDIps) {
				bw.write(s);
				bw.newLine();
				bw.flush();
			}

			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 打印信息
	 */
	private static void printMsg() {
		System.out.println("================================== 信息 ==================================");
		// 数量信息
		System.out.println("总 ip 数量: " + ips.size() + " （个）");
		System.out.println("非广东 ip 数量: " + noGDIps.size() + " （个）");
		// 比例信息
		float proportion = (float) noGDIps.size() / (float) ips.size();
		proportion = proportion * 100;
		System.out.println("非广东 ip 所占比例： " + proportion + "%");
		System.out.println("");
	}

	/**
	 * 打印时间
	 * @param second
	 */
	private static void printTime(long second) {
		String str = secondToTime(second);
		System.out.println("================================== 结束 ==================================");
		System.out.println("Statistics Done! Use " + str);
	}

	/**
     * 将秒数转换为日时分秒，
     * @param second
     * @return
     */
    private static String secondToTime(long second){
    	long day = second / (24 * 60 * 60 * 1000);
		long hour = (second / (60 * 60 * 1000) - day * 24);
		long min = ((second / (60 * 1000)) - day * 24 * 60 - hour * 60);
		long s = (second / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        long ms = second % 1000;
        if (day > 0) {
            return day + "天" + hour + "小时" + min + "分" + s + "秒" + ms + "毫秒";
        } else if (hour > 0) {
            return hour + "小时" + min + "分" + s + "秒" + ms + "毫秒";
        } else if (min > 0) {
        	return min + "分" + s + "秒" + ms + "毫秒";
        } else if (s > 0) {
        	return s + "秒" + ms + "毫秒";
        } else {
        	return ms + "毫秒";
        }
    }
}
