package util.xml;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;

public class XMLHelper {
	/**
	 * 将xml字符串转换为doc对象
	 * 
	 * @param xmlString
	 * @return
	 */
	public static Document readXMLContentString(String xmlString) {
		Document doc = null;
		try {
			if (xmlString != null && !xmlString.equals("")) {
				xmlString = xmlString.trim().replaceAll("\n", "");
				doc = DocumentHelper.parseText(xmlString);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return doc;
	}
	
	/**
	 * 获取dom中指定节点的内容
	 * 
	 * @param doc
	 * @param nodeName
	 * @return
	 */
	public static String getNodeText(Document doc, String nodeName) {
		String xpath = "//" + nodeName;
		String nodeText = "";
		if (doc != null) {
			try {
				Node node = doc.selectSingleNode(xpath);
				if (node != null)
					nodeText = node.getText().trim();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return nodeText;
	}

}
