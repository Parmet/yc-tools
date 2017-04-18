package util.word;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRow;

public class TestClass {
    private int rowNum = 0;
    
    /**
     * map转换适配器
     * 
     * @param obj
     * @return
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> ConvertObjToMapAdpater(Object obj) {
        
        // 如果是集合或者数组，获取当前行的数据，最后尝试转换成map
        if (obj instanceof Collection<?>) {
            Collection<?> col = (Collection<?>) obj;
            return this.ConvertObjToMapAdpater(col.toArray()[this.rowNum]);
        } else if (obj instanceof Map) {
            return (Map<String, Object>) obj;
        } else if (obj.getClass().isArray()) {
            return this.ConvertObjToMapAdpater(((Object[]) obj)[this.rowNum]);
        } else {
            return this.ConvertObjToMap(obj);
        }
    }
    
    /**
     * 转换成map
     * 
     * @param obj
     * @return
     */
    public Map<String, Object> ConvertObjToMap(Object obj) {
        Map<String, Object> reMap = new HashMap<String, Object>();
        if (obj == null)
            return null;
        Field[] fields = obj.getClass().getDeclaredFields();
        try {
            for (int i = 0; i < fields.length; i++) {
                try {
                    Field f = obj.getClass().getDeclaredField(fields[i].getName());
                    f.setAccessible(true);
                    Object o = f.get(obj);
                    reMap.put(fields[i].getName(), o);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        } catch (SecurityException e) {
            System.out.println("不支持转换该类型的数据:" + obj.toString());
            e.printStackTrace();
        }
        return reMap;
    }
    
    public void ConvertWord(String fileName, Map<String, Object> map) throws Exception {
        
        String pathFileName = "D:\\201704\\watermark\\testFile\\tempfile_" + System.currentTimeMillis() + ".doc";
        File file = new File(pathFileName); // 创建临时文件
        OutputStream os = new FileOutputStream(file);
        InputStream fileInputStream = new FileInputStream(fileName);

        try {
            // 读取word源文件
            XWPFDocument document = new XWPFDocument(fileInputStream);
            // 替换段落里面的变量
            this.replaceInPara(document, map);
            // 替换表格里面的变量
            this.replaceInTable(document, map);
            
            document.write(os);
            this.close(os);
            this.close(fileInputStream);

            System.out.println("save as : " + pathFileName);
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            this.close(os);
            this.close(fileInputStream);
        }

    }

    /**
     * 替换段落里面的变量
     * 
     * @param doc
     *            要替换的文档
     * @param params
     *            参数
     */
    private void replaceInPara(XWPFDocument doc, Map<String, Object> params) {
        Iterator<XWPFParagraph> iterator = doc.getParagraphsIterator();
        XWPFParagraph para;
        while (iterator.hasNext()) {
            para = iterator.next();
            Map<String, Object> map = getParagraphData(para, params);
            this.replaceInPara(para, map);
        }
    }

    /**
     * 替换段落里面的变量
     * 
     * @param para
     *            要替换的段落
     * @param params
     *            参数
     */
    private void replaceInPara(XWPFParagraph para, Map<String, Object> params) {
        List<XWPFRun> runs;

        String runText = "";
        if (this.matcher(para.getParagraphText()).find()) {
            runs = para.getRuns();
            if (runs.size() > 0) {
                int j = runs.size();
                for (int i = 0; i < j; i++) {
                    XWPFRun run = runs.get(0);
                    String i1 = run.toString();
                    runText += i1;
                    
                    //保留最后一个run对象，以便利用原先的样式
                    if (i < (j - 1)) {
                        para.removeRun(0);
                    }
                }

                // 直接调用XWPFRun的setText()方法设置文本时，在底层会重新创建一个XWPFRun，把文本附加在当前文本后面，
                // 所以我们不能直接设值，需要先删除当前run,然后再自己手动插入一个新的run。
                runText = this.replaceText(runText, params);
                runs.get(0).setText(runText, 0);
            }
        }
    }

    /**
     * 替换字符串中所有的匹配项
     * 
     * @param text
     * @param params
     * @return
     */
    private String replaceText(String text, Map<String, Object> params) {
        Matcher matcher = this.matcher(text);
        String key = "";
        
        if (matcher.find()) {
            key = matcher.group(1);
            if (params.containsKey(key)) {
                text = matcher.replaceFirst(String.valueOf(params.get(key)));
            } else {
                text = matcher.replaceFirst("");
            }
            
            text = this.replaceText(text, params);
        }
        
        return text;
    }
    
    /**
     * 替换表格里面的变量
     * 
     * @param doc
     *            要替换的文档
     * @param params
     *            参数
     */
    private void replaceInTable(XWPFDocument doc, Map<String, Object> params) {
        Iterator<XWPFTable> iterator = doc.getTablesIterator();
        XWPFTable table;
        
        int modelRowCount = 0;
        List<XWPFTableRow> rows;

        while (iterator.hasNext()) {
            table = iterator.next();
            rows = table.getRows();
            modelRowCount = rows.size();
            for (int i = (modelRowCount - 1); i >= 0; i--) {
                XWPFTableRow dataRow = rows.get(i);
                int rowCount = this.getRowCount(dataRow, params);
                if (rowCount == 0) {
                    Map<String, Object> rowData = this.getRowData(dataRow, params);
                    this.replaceInRow(table, dataRow, rowData);
                    continue;
                }
                
                for (int j = 0; j < rowCount; j++) {
                    this.copyTableRow(table, dataRow, (i + j + 1), params);
                    
                    if (j == (rowCount - 1)) {
                        table.removeRow(i);
                    }
                }
            }
            
            this.rowNum = 0;
        }
        
    }
    
    private void replaceInRow(XWPFTable table, XWPFTableRow newRow, Map<String, Object> params) {
        for (XWPFTableCell cell : newRow.getTableCells()) {
            this.replaceInCell(cell, params);
        }
    }
    
    private void replaceInCell(XWPFTableCell cell, Map<String, Object> params) {
        List<XWPFParagraph> paras = cell.getParagraphs();
        int size = paras.size();
        for (int i = 0; i < size; i++) {
            XWPFParagraph para = paras.get(i);
            this.replaceInPara(para, params);
        }
    }
    
    /**
     * 获取当前行数据map
     * 
     * @param row
     * @param params
     * @return
     */
    private Map<String, Object> getRowData(XWPFTableRow row, Map<String, Object> params) {
        List<XWPFParagraph> paras;
        List<XWPFTableCell> cells = row.getTableCells();
        Map<String, Object> map = new HashMap<String, Object>();
        
        for (XWPFTableCell cell : cells) {
            paras = cell.getParagraphs();
            for (XWPFParagraph para : paras) {
                map.putAll(this.getParagraphData(para, params));
            }
        }
        
        return map;
    }
    
    /**
     * 获取当前字符串中的数据Map
     * 
     * @param para
     * @param params
     * @return
     */
    private Map<String, Object> getParagraphData(XWPFParagraph para, Map<String, Object> params) {
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> data = params;
        Matcher matcher = this.matcher(para.getParagraphText());
        while (matcher.find()) {
            
            String key = matcher.group(1);
            String[] keys = key.split("\\.");
            for (int i = 0; i < keys.length; i++) {
                Object val = data.get(keys[i]);
                if (val == null) {
                    map.put(key, "");
                    break;
                }
                
                if (i != (keys.length - 1)) {
                    data = this.ConvertObjToMapAdpater(val);
                } else {
                    map.put(key, String.valueOf(val));
                    data = params;
                }
            }
        }
        
        return map;
    }

    /**
     * 复制表格的行
     * 
     * @param table
     * @param row
     * @param rowData 
     * @param pos
     * @param params 
     */
    private void copyTableRow(XWPFTable table, XWPFTableRow row, int pos, Map<String, Object> params) {
        XWPFTableRow newRow = new XWPFTableRow((CTRow) row.getCtRow().copy(), table) ;
        
        Map<String, Object> rowData = this.getRowData(newRow, params);
        this.replaceInRow(table, newRow, rowData);
        
        table.addRow(newRow, pos);
        this.rowNum++;
    }
    
   
    /**
     * 获取表格数据项的大小
     * 
     * @param row
     * @param params
     * @return
     */
    private int getRowCount(XWPFTableRow row, Map<String, Object> params) {
        int rowCount = 0;
        Matcher matcher = null;
        String[] key;
        List<XWPFTableCell> cells;
        List<XWPFParagraph> paras;
        
        cells = row.getTableCells();
        for (XWPFTableCell cell : cells) {
            
            paras = cell.getParagraphs();
            for (XWPFParagraph para : paras) {
                
                matcher = this.matcher(para.getParagraphText());
                if (matcher.find()) {
                    key = (matcher.group(1)).split("\\.");
                    if (params.get(key[0]) instanceof Collection<?>) {
                        Collection<?> col = (Collection<?>) params.get(key[0]);
                        rowCount = col.size();
                    } else if (params.get(key[0]).getClass().isArray()) {
                        Object[] objs = (Object[]) params.get(key[0]);
                        rowCount = objs.length;
                    } else {
                        return rowCount;
                    }
                    
                    if (rowCount > 0) {
                        return rowCount;
                    }
                }
            }
        }
        
        return rowCount;
    }

    /**
     * 正则匹配字符串
     * 
     * @param str
     * @return
     */
    public Matcher matcher(String str) {
        Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        return matcher;
    }

    /**
     * 关闭输入流
     * 
     * @param is
     */
    private void close(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭输出流
     * 
     * @param os
     */
    private void close(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private Map<String, Object> getData() {
        Map<String, Object> map = new HashMap<String, Object>();
        
        Map<String, Object> map1 = new HashMap<String, Object>();
        map1.put("aac147", "440104195404242811");
        map1.put("aac003", "测试人员");
        map1.put("aac004", "男");
        
        map.put("info", map1);
        map.put("currDate", "2017-04-10");
        map.put("authCode", "152452");
        
        Map<String, Object> mouth = new HashMap<String, Object>();
        mouth.put("synx", "1");
        mouth.put("syeys", "11");
        mouth.put("gsnx", "11");
        mouth.put("gsys", "1");
        mouth.put("syunx", "2");
        mouth.put("syuys", "3");
        
        map.put("mouthTotal", mouth);

        List<Map<String, Object>> lists = new ArrayList<Map<String, Object>>();
        Map<String, Object> maps1 = new HashMap<String, Object>();
        maps1.put("aae003", "2015.5-2015.6");
        maps1.put("aae202", "2");
        maps1.put("aae180_unemply", "100");
        maps1.put("aae140_unemply_dw", "55");
        maps1.put("aae140_unemply_gr", "45");
        maps1.put("aae180_gs", "33");
        maps1.put("aae140_gs", "655");
        maps1.put("aae180_sy", "458");
        maps1.put("aae140_sy", "885");
        maps1.put("aab069", "广州威慑么么科技公司");
        
        Map<String, Object> maps2 = new HashMap<String, Object>();
        maps2.put("aae003", "2015.1-2015.4");
        maps2.put("aae202", "4");
        maps2.put("aae180_unemply", "100");
        maps2.put("aae140_unemply_dw", "55");
        maps2.put("aae140_unemply_gr", "45");
        maps2.put("aae180_gs", "33");
        maps2.put("aae140_gs", "655");
        maps2.put("aae180_sy", "458");
        maps2.put("aae140_sy", "885");
        maps2.put("aab069", "广州威慑么么科技公司");
        
        Map<String, Object> maps3 = new HashMap<String, Object>();
        maps3.put("aae003", "2014.11-2014.12");
        maps3.put("aae202", "2");
        maps3.put("aae180_unemply", "100");
        maps3.put("aae140_unemply_dw", "55");
        maps3.put("aae140_unemply_gr", "45");
        maps3.put("aae180_gs", "33");
        maps3.put("aae140_gs", "655");
        maps3.put("aae180_sy", "458");
        maps3.put("aae140_sy", "885");
        maps3.put("aab069", "广州威慑么么科技公司");

        lists.add(maps1);
        lists.add(maps2);
        lists.add(maps3);

        map.put("content", lists);
        
        return map;
    }

    public static void main(String[] args) {

        try {
            long startTime = System.currentTimeMillis();
            System.out.println("start:" + startTime);
            
            TestClass testClass = new TestClass();
            Map<String, Object> map = testClass.getData();
            testClass.ConvertWord("d:/project/test/src/test/person_pay_history_three_insurance.docx", map);
            long endTime = System.currentTimeMillis();
            System.out.println("end:" + endTime);
            System.out.println("useTime:" + (endTime - startTime));
            System.out.println("success!1:1100--2:");
            
            int count = 0;
            String str = "123${123}ee${111}";
            Matcher matcher = testClass.matcher(str);
            while(matcher.find()) {
                count++;
                System.out.println(matcher.groupCount());  
                System.out.println(matcher.group(1));  
            }
            
            System.out.println("共有 " + count + "个 ");   
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
