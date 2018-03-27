package util.xml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.QName;
import org.dom4j.Visitor;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.dom4j.util.NodeComparator;

import util.data.JsonUtil;



/**
 * 线程安全的XML操作类，单例。 每个File一个读写锁 使用ThreadLocal实现每个线程一个data
 * 
 * @author huj
 * 
 */
@SuppressWarnings("rawtypes")
public class XmlUtil {

        /**  
         * Map转换成Xml  
         * @param map  
         * @return  
         */  
        public static String mapToXmlString(Map<String, Object> map){  
            StringBuffer sb = new StringBuffer("");  
            sb.append("<xml>");  
            
            Set<String> set = map.keySet();  
            for(Iterator<String> it = set.iterator(); it.hasNext();){  
                String key = it.next();  
                Object value = map.get(key);  
                sb.append("<").append(key).append(">");  
                sb.append(JsonUtil.toJson(value));  
                sb.append("</").append(key).append(">");  
            }  
            
            sb.append("</xml>");  
            return sb.toString();  
        }  
        
        /**  
         * Xml string转换成Map  
         * @param xmlStr  
         * @return  
         */  
        public static Map<String,Object> xmlStringToMap(String xmlStr){  
            Map<String,Object> map = new HashMap<String,Object>();  
            Document doc;  
            try {  
                doc = DocumentHelper.parseText(xmlStr);  
                Element el = doc.getRootElement();  
                map = recGetXmlElementValue(el,map);  
            } catch (DocumentException e) {  
                e.printStackTrace();  
            }  
            return map;  
        }
        
        /**  
         * 循环解析xml  
         * @param ele  
         * @param map  
         * @return  
         */  
        @SuppressWarnings({ "unchecked" })  
        private static Map<String, Object> recGetXmlElementValue(Element ele, Map<String, Object> map){  
            List<Element> eleList = ele.elements();  
            if (eleList.size() == 0) {  
                map.put(ele.getName(), ele.getTextTrim()); 
                return map;  
            } else {  
                for (Iterator<Element> iter = eleList.iterator(); iter.hasNext();) {  
                    Element innerEle = iter.next();  
                    recGetXmlElementValue(innerEle, map);  
                }  
                return map;  
            }  
        } 

        /**
         * 获得DocumentFactory
         * 
         * @return DocumentFactory
         */
        public DocumentFactory getDocumentFactory() {
                return DocumentFactory.getInstance();
        }

        /**
         * 获得Document对象，默认编码为utf-8
         * 
         * @param file
         * @throws DocumentException
         */
        public static Document getDocument(File file) throws DocumentException {
                return XmlUtil.getDocument(file, "utf-8");
        }

        /**
         * 获得Document对象
         * 
         * @param file
         * @param encoding
         * @throws DocumentException
         */
        public static Document getDocument(File file, String encoding) throws DocumentException {
                SAXReader reader = new SAXReader();
                reader.setEncoding(encoding);
                Document doc = reader.read(file);
                return doc;
        }

        /**
         * 将Document写入到file
         * 
         * @param file
         *            为null时写入到源文件
         * @param doc
         *            写入的文档
         * @throws IOException
         */
        public static void writeDocument(File file, Document doc) throws IOException {
                XMLWriter writer = new XMLWriter(new FileWriter(file));
                writer.write(doc);
                writer.close();
        }

        /**
         * 获得root元素，默认编码为utf-8
         * 
         * @param file
         * @return
         * @throws DocumentException
         */
        public static Element getRootElement(File file) throws DocumentException {
                SAXReader reader = new SAXReader();
                reader.setEncoding("utf-8");
                Document doc = reader.read(file);
                return doc.getRootElement();
        }

        /**
         * 获得root元素
         * 
         * @param file
         * @param encoding
         * @return
         * @throws DocumentException
         */
        public static Element getRootElement(File file, String encoding) throws DocumentException {
                SAXReader reader = new SAXReader();
                reader.setEncoding(encoding);
                Document doc = reader.read(file);
                return doc.getRootElement();
        }

        /**
         * 遍历元素的所有子元素（下一层）
         * 
         * @param element
         *            元素
         * @param handler
         *            对每个子类的操作
         */
		public static ArrayList<Object> elementWalk(Element element, XMLHandler handler,
                        Object arguments) {
                ArrayList<Object> list = new ArrayList<Object>();
                for (Iterator it = element.elementIterator(); it.hasNext();) {
                        Object r = handler.handle((Element) it.next(), arguments);
                        if (r != null) {
                                list.add(r);
                        }
                }
                return list;
        }

        /**
         * 遍历元素的指定子元素（下一层）
         * 
         * @param element
         *            元素
         * @param name
         *            子元素名
         * @param handler
         *            处理方法
         */
        
		public static ArrayList<Object> elementWalk(Element element, String name, XMLHandler handler,
                        Object arguments) {
                ArrayList<Object> list = new ArrayList<Object>();
                for (Iterator it = element.elementIterator(name); it.hasNext();) {
                        Object r = handler.handle((Element) it.next(), arguments);
                        if (r != null) {
                                list.add(r);
                        }
                }
                return list;
        }

        /**
         * 遍历元素的指定子元素（下一层）
         * 
         * @param element
         *            元素
         * @param qname
         *            子元素质量名
         * @param handler
         *            处理方法
         */
        public static ArrayList<Object> elementWalk(Element element, QName qname, XMLHandler handler,
                        Object arguments) {
                ArrayList<Object> list = new ArrayList<Object>();
                for (Iterator it = element.elementIterator(qname); it.hasNext();) {
                        Object r = handler.handle((Element) it.next(), arguments);
                        if (r != null) {
                                list.add(r);
                        }
                }
                return list;
        }

        /**
         * 访问元素所有的子节点（多层）
         * 
         * @param element
         *            元素
         * @param visitor
         *            访问者
         */
        public static void visit(Element element, Visitor visitor) {
                element.accept(visitor);
        }

        /**
         * 访问元素所有的子节点（多层）
         * 
         * @param doc
         *            文档
         * @param visitor
         *            访问者
         */
        public static void visit(Document doc, Visitor visitor) {
                doc.accept(visitor);
        }

        /**
         * 删除自己
         * 
         * @param e
         */
        public static void remove(Element e) {
                e.detach();
        }

        /**
         * 在不包含同名直接子元素的情况下，添加
         * 
         * @param parent
         * @param son
         * @return
         */
        public static boolean addElementIfAbsent(Element parent, Element son) {
                if (parent.element(son.getName()) == null) {
                        parent.add(son);
                        return true;
                }
                return false;
        }

        /**
         * 在包含同名直接子元素的情况下，更新子元素的数据（前提是子元素没有子节点）
         * 
         * @param parent
         * @param map
         * @return
         */
        @SuppressWarnings("unchecked")
        public static boolean updateElement(Element parent, String key, String value) {
                boolean updated = false;
                List list = parent.elements(key);
                if (list.size() > 0) {
                        for (Element son : (List<Element>) list) {
                                if (!son.getText().equals(value)) {
                                        son.setText(value);
                                        updated = true;
                                }
                        }
                }
                return updated;
        }

        /**
         * 在包含同名直接子元素的情况下，更新子元素的数据（前提是子元素没有子节点）
         * 
         * @param parent
         * @param map
         * @return
         */
        @SuppressWarnings("unchecked")
        public static boolean updateElement(Element parent, Map<String, String> map) {
                boolean updated = false;
                for (String key : map.keySet()) {
                        List list = parent.elements(key);
                        if (list.size() > 0) {
                                for (Element son : (List<Element>) list) {
                                        String value = map.get(key);
                                        if (!son.getText().equals(value)) {
                                                son.setText(value);
                                                updated = true;
                                        }
                                }
                        }
                }
                return updated;
        }

        /**
         * 如果元素包含此属性则修改，否则添加
         * 
         * @param e
         * @param name
         * @param value
         */
        public static void setAttribute(Element e, String name, String value) {
                Attribute a = e.attribute(name);
                if (a == null) {
                        DocumentHelper.createAttribute(e, name, value);
                } else {
                        a.setValue(value);
                }
        }

        /**
         * 基于节点的值来比较两个节点 如果要多次比较，建议直接调用NodeComparator类的compare方法，提高效率
         * 
         * @param n1
         * @param n2
         * @return 0则相同、正数则n1大、负数则n1小
         */
        public static int compare(Node n1, Node n2) {
                return new NodeComparator().compare(n1, n2);
        }

        public static String doc2String(Document doc) {
                return doc.asXML();
        }

        public static Document string2Doc(String text) throws DocumentException {
                return DocumentHelper.parseText(text);
        }



        /**
         * 判断元素的直接元素是否全部拥有键（节点名）、值（节点值）对
         * 
         * @param e
         *            元素
         * @param map
         *            键值对
         * @return
         */
        public static boolean hasKeyValue(Element parent, Map<String, String> map) {
                if (map == null || map.size() == 0)
                        return false;
                boolean thisHas = true; // 表示是否包含
                for (String name : map.keySet()) {
                        thisHas = thisHas && hasKeyValue(parent, name, map.get(name));
                }
                return thisHas;
        }

        /**
         * 判断元素的直接子元素是否全部拥有键（节点名）、值（节点值）对
         * 
         * @param parent
         *            父元素
         * @return
         */
        public static boolean hasKeyValue(Element parent, String name, String value) {
                for (Iterator it = parent.elementIterator(name); it.hasNext();) {
                        Element e = (Element) it.next();
                        if (e.getTextTrim().equals(value)) {
                                return true;
                        }
                }
                return false;
        }

        /**
         * 根据元素是否包含指定的子元素，来删除元素，删除多个
         * 
         * @param parent
         *            父元素
         * @param sonName
         *            子元素名
         * @param value
         *            子元素值
         * @return
         */
        public static boolean removeElementWithKeyValue(Element parent, String sonName, String value) {
                for (Iterator it = parent.elementIterator(sonName); it.hasNext();) {
                        Element e = (Element) it.next();
                        if (e.getTextTrim().equals(value)) {
                                parent.detach();
                                return true;
                        }
                }
                return false;
        }

        public static boolean removeElementWithKeyValue(Element parent, Map<String, String> map) {
                boolean has = true;
                for (String name : map.keySet()) {
                        boolean thisHas = false;
                        for (Iterator it = parent.elementIterator(name); it.hasNext();) {
                                Element e = (Element) it.next();
                                if (e.getTextTrim().equals(map.get(name))) {
                                        parent.detach();
                                        thisHas = true;
                                }
                        }
                        has = has && thisHas;
                }
                return has;
        }

        /**
         * 根据子元素是否包含idName元素且idName元素的值是否与新元素的idName元素值相等，来切换元素
         * 
         * @param out
         *            将要被替换的元素
         * @param son
         *            替换的元素
         * @param idName
         *            用于判断元素名
         * @return 是否切换成功
         */
        public static boolean switchElement(Element out, Element in, String idName) {
                for (Iterator it = out.elementIterator(idName); it.hasNext();) {
                        Element e = (Element) it.next();
                        if (e.getTextTrim().equals(in.element(idName).getTextTrim())) {
                                out.getParent().add(in);
                                out.detach();
                                return true;
                        }
                }
                return false;
        }

        /**
         * 根据子元素是否包含idName元素且idName元素的值是否与新元素的idName元素值相等，来切换元素
         * 
         * @param out
         *            将要被替换的元素
         * @param son
         *            替换的元素
         * @param idlist
         *            用于判断元素名
         * @return 是否切换成功
         */
        public static boolean switchElement(Element out, Element in, String[] idlist) {
                boolean has = true;
                for (String id : idlist) {
                        boolean thisHas = false;
                        String value = in.element(id).getTextTrim();
                        for (Iterator it = out.elementIterator(); it.hasNext();) {
                                if (((Element) it.next()).getTextTrim().equals(value))
                                        thisHas = true;
                        }
                        has = has && thisHas;
                }
                if (has) {
                        out.getParent().add(in);
                        out.detach();
                        return true;
                }
                return false;
        }

        public static boolean hasChild(Element parent, Element son) {
                for (Iterator it = parent.elementIterator(); it.hasNext();) {
                        if (compare((Element) it.next(), son))
                                return true;
                }
                return false;
        }

        /**
         * 判断两个元素是否有相同的名字和值（值可以比较）
         * 
         * @param a
         * @param b
         * @return
         */
        public static boolean compare(Element a, Element b) {
                return a.getName().equals(b.getName())
                                && (new NodeComparator().compare(a, b) == 0 ? true : false);
        }
        
        /**
         * 根据xPath,获取某元素的值
         * 
         * @param doc
         * @param xPath
         * @return
         */
        public static String getElementValue(Document doc, String xPath) throws NullPointerException {
        	Element e = (Element)doc.selectSingleNode(xPath);
        	return e.getStringValue();
        }
        
        /**
         * 获取xPath位置下的所有元素
         * 
         * @param doc
         * @param xPath
         * @return
         */
        public static List getElementByXpath(Document doc, String xPath) {
        	List list = doc.selectNodes(xPath);
        	return list;
        }
        
        
        
        /**
         * 获取某个元素的所有属性
         * 
         * @param e
         * @return
         */
        public static List getAttributeByElement(Element e){
        	List list = e.attributes();
        	return list;
        }
        
        /**
         * 获取元素中某属性的值
         * 
         * @param e
         * @param aName
         * @return
         */
        public static String getAttributeValue(Element e, String aName){
        	List list = getAttributeByElement(e);
        	String aValue="";
        	for (Iterator it = list.iterator(); it.hasNext();) {
        		Attribute attr=(Attribute)it.next();
        		if(attr.getName().equals(aName)){
        			aValue=attr.getText();
        		}
        	}
        	return aValue;
        }
        
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
    	 * 获取当前节点的指定子节点的内容
    	 * 
    	 * @param doc
    	 * @param nodeName
    	 * @return
    	 */
    	public static String getNodeText(Node doc, String nodeName) {
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