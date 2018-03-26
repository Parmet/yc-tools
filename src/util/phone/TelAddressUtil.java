package util.phone;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class TelAddressUtil {
	public static void main(String[] args) {
		System.out.println(getAddress("18931853495"));
	}

	public static String getAddress(String phone) {
		String jsonString = null;
		JSONArray array = null;
		JSONObject jsonObject = null;
		String urlString = "https://tcc.taobao.com/cc/json/mobile_tel_segment.htm?tel=" + phone;
		StringBuffer sb = new StringBuffer();
		BufferedReader buffer;
		try {
			URL url = new URL(urlString);
			InputStream in = url.openStream();

			// 解决乱码问题
			buffer = new BufferedReader(new InputStreamReader(in, "gb2312"));
			String line = null;
			while ((line = buffer.readLine()) != null) {
				sb.append(line);
			}
			
			in.close();
			buffer.close();
			
			jsonString = sb.toString();
			// 替换掉“__GetZoneResult_ = ”，让它能转换为JSONArray对象
			jsonString = jsonString.replaceAll("^[__]\\w{14}+[_ = ]+", "[");
			// System.out.println(jsonString+"]");
			String jsonString2 = jsonString + "]";
			// 把STRING转化为json对象
			array = JSONArray.fromObject(jsonString2);

			// 获取JSONArray的JSONObject对象，便于读取array里的键值对
			jsonObject = array.getJSONObject(0);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return jsonObject.getString("province");
	}
}
