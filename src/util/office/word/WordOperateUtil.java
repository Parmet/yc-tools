package util.office.word;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
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

/**
 * word操作类
 * 
 * @author xiaoyc 2017-04-14
 */
public class WordOperateUtil {
    private int rowNum = 0;
    
    /**
     * 填充word数据
     * 
     * @param fileName
     * @param map
     * @throws Exception
     */
    public File ConvertWord(String fileName, Map<String, Object> map) throws Exception {
        
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
            
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            this.close(os);
            this.close(fileInputStream);
        }
        
        return file;
    }

    
    /**
     * map转换适配器
     * 
     * @param obj
     * @return
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> ConvertObjToMapAdpater(Object obj) {
        
        // 如果是集合或者数组，获取当前行的数据，最后尝试转换成map
        if (obj instanceof Collection<?>) {
            Collection<?> col = (Collection<?>) obj;
            if (col.size() <= 0) {
                return new HashMap<String, Object>();
            }
            
            return this.ConvertObjToMapAdpater(col.toArray()[this.rowNum]);
        } else if (obj instanceof Map) {
            return (Map<String, Object>) obj;
        } else if (obj.getClass().isArray()) {
            if (((Object[]) obj).length <= 0) {
                return new HashMap<String, Object>();
            }
            
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
    private Map<String, Object> ConvertObjToMap(Object obj) {
        Map<String, Object> reMap = new HashMap<String, Object>();
        if (obj == null)
            return null;
        Field[] fields = obj.getClass().getDeclaredFields();
        try {
            for (int i = 0; i < fields.length; i++) {
                try {
                    Field f = obj.getClass().getDeclaredField(
                            fields[i].getName());
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
        if (this.matcherRegion(para.getParagraphText()).find()) {
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
     * 替换字符串的首个匹配项
     * 
     * @param text
     * @param params
     * @return
     */
    private String replaceText(String text, Map<String, Object> params) {
        Matcher matcher = this.matcherRegion(text);
        String key = "";
        
        if (matcher.find()) {
            key = matcher.group(1);
            text = this.replaceVal(key, text, params);
            
            // 该字符串存在多个匹配项的情况下，调用replaceText替换字符串的首个匹配项
            text = this.replaceText(text, params);
        }
        
        return text;
    }
    
    /**
     * 替代匹配项的值
     * 
     * @param text
     * @param params 
     * @return
     */
    private String replaceVal(String key, String text, Map<String, Object> params) {
        String defaultVal = "";
        String targetStr = "${" + key + "}";
        
        Matcher matcher = this.matcherDefaultVale(text);
        if (matcher.find()) {
            defaultVal = matcher.group(1);
            targetStr += "?\"" + defaultVal + "\"";
        }
        
        if (params.containsKey(key)) {
            defaultVal = String.valueOf(params.get(key));
        }
        
        text = text.replace(targetStr, defaultVal);
        
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
                        this.rowNum = 0;
                    }
                }
            }
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
        Matcher matcher = this.matcherRegion(para.getParagraphText());
        while (matcher.find()) {
            
            String key = matcher.group(1);
            String[] keys = key.split("\\.");
            for (int i = 0; i < keys.length; i++) {
                Object val = data.get(keys[i]);
                if (val == null) {
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
                
                matcher = this.matcherRegion(para.getParagraphText());
                if (matcher.find()) {
                    key = (matcher.group(1)).split("\\.");
                    if (key[0].equals("")) {
                        return rowCount;
                    }
                    
                    Object val = params.get(key[0]);
                    if (val == null) {
                        return rowCount;
                    }
                    
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
    public Matcher matcherRegion(String str) {
        Pattern pattern = Pattern.compile("\\$\\{(.+?)\\}", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(str);
        return matcher;
    }
    
    /**
     * 正则匹配字符串
     * 
     * @param str
     * @return
     */
    public Matcher matcherDefaultVale(String str) {
        Pattern pattern = Pattern.compile("\\?\"(.*?)\"", Pattern.CASE_INSENSITIVE);
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
}
