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
    private Map<String, Object> param = new HashMap<String, Object>();
    
    /**
     * 填充word数据
     * 
     * @param fileName
     * @param data
     * @throws Exception
     */
    public File ConvertWord(String fileName, Map<String, Object> data) throws Exception {
        // 初始化操作
        File tempFile = this.getTempFile();
        OutputStream os = new FileOutputStream(tempFile);
        InputStream fileInputStream = new FileInputStream(fileName);
        
        try {
            init(data);
            // 读取word源文件
            XWPFDocument document = new XWPFDocument(fileInputStream);
            // 替换段落里面的变量
            replaceInPara(document);
            // 替换表格里面的变量
            replaceInTable(document);
    
            document.write(os);
            close(os);
            close(fileInputStream);
            
        } catch (Exception e) {
            throw new Exception(e);
        } finally {
            this.close(os);
            this.close(fileInputStream);
        }
        
        return tempFile;
    }
    
    /**
     * 初始化
     * 
     * @param data 模板数据
     */
    private void init(Map<String, Object> data) {
        this.param = data;
    }

    /**
     * 创建并获取临时文件
     * 
     * @return
     */
    private File getTempFile() {
        StringBuffer tempFileName = new StringBuffer();
        
        tempFileName.append("F:/Temp/word/test2.docx");
        
        File tempFile = new File(tempFileName.toString());
        
        System.out.println("save as : " + tempFileName.toString());
        return tempFile;
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
        if (obj == null) {
            return null;
        }
        
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
    private void replaceInPara(XWPFDocument doc) {
        // word 中的所有段落 Iterator
        Iterator<XWPFParagraph> iterator = doc.getParagraphsIterator();
        XWPFParagraph para;
        while (iterator.hasNext()) {
            para = iterator.next();
            // 匹配段落中域的数据
            Map<String, Object> curData = getParagraphData(para);
            replaceRun(para, curData);
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
    private void replaceRun(XWPFParagraph para, Map<String, Object> curData) {
        // 段落中的所有词
        List<XWPFRun> runs = para.getRuns();
        String runText = "";
        
        if (matcherRegion(para.getParagraphText()).find()) {
            // 匹配到域之后，组合段落中所有的词再把里面的域全部替换数据
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

            runText = replaceText(runText, curData);
            //使用被保留的Run作为载体显示替换之后的内容
            runs.get(0).setText(runText, 0);
        }
    }
    
    /**
     * 替换字符串的首个匹配项
     * 
     * @param text
     * @param params
     * @return
     */
    private String replaceText(String text, Map<String, Object> curData) {
        Matcher matcher = this.matcherRegion(text);
        String key = "";
        
        if (matcher.find()) {
            // 获取第一个匹配项
            key = matcher.group(1);
            text = replaceVal(key, text, curData);
            
            // 该字符串存在多个匹配项的情况下，调用replaceText替换字符串的首个匹配项
            text = replaceText(text, curData);
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
    private String replaceVal(String key, String text, Map<String, Object> curData) {
        // 替换域的值
        String defaultVal = "";
        // 准备替换的域的字符窜
        String targetStr = "${" + key + "}";
        
        // 匹配域的默认值
        Matcher matcher = matcherDefaultVale(text);
        if (matcher.find()) {
            defaultVal = matcher.group(1);
            // 如果域设置了默认值，准备替换的域的字符窜要加上默认值的申明，
            // 确保不出现域被替换，默认值的申明仍跟在其后面的现象
            targetStr += "?\"" + defaultVal + "\""; 
        }
        
        if (curData.containsKey(key)) {
            defaultVal = String.valueOf(curData.get(key));
        }
        
        text = text.replace(targetStr, defaultVal);
        
        return text;
    }
    
    /**
     * 替换表格里面的域
     * 
     * 遍历表格中的行，接着遍历行中的单元格
     * 
     * @param doc
     *            要替换的文档
     * @param params
     *            参数
     */
    private void replaceInTable(XWPFDocument doc) {
        // 获取word所有表格（不包括表格嵌套的表格）
        Iterator<XWPFTable> iterator = doc.getTablesIterator();
        while (iterator.hasNext()) {
            replaceInTable(iterator.next());
        }
    }
    
    /**
     * 替换表格里面的域
     * 
     * @param table
     *            要替换的表格
     * @param params
     *            参数
     */
    private void replaceInTable(XWPFTable table) {
        // 表格中的所有行
        List<XWPFTableRow> rows = table.getRows();
        // 当前模板表格的初始行数
        int modelRowCount = rows.size();
        // 从最后一行开始填充数据，保证在后续动态添加行的时候能正常填充完整个表格模板
        for (int i = (modelRowCount - 1); i >= 0; i--) {
            // 模板行
            XWPFTableRow dataRow = rows.get(i);
            // 通过表格中的域匹配数据的大小，表示会在当前表格增加的行数
            int rowCount = getRowCount(dataRow);
            // 非集合数据
            if (rowCount == 0) {
                replaceInRow(dataRow);
                continue;
            }
            
            // 集合类型的数据
            // 重置当前填充行对应的数据项序号
            this.rowNum = 0;
            for (int j = 0; j < rowCount; j++) {
                // 复制模板行并创建新行填充新行数据
                // ps：根据集合的大小创建相同数量的行
                copyTableRow(table, dataRow, (i + j + 1));
                // 更新当前填充行对应的数据项序号
                this.rowNum++;
                
                // 在集合数据填充完之后把模板行删掉
                if (j == (rowCount - 1)) {
                    table.removeRow(i);
                    // 重置当前填充行对应的数据项序号
                    this.rowNum = 0;
                }
            }
        }
        
    }
    
    private void replaceInRow(XWPFTableRow newRow) {
        // 匹配当前行所有域的对应值
        Map<String, Object> curData = this.getRowData(newRow);
        
        for (XWPFTableCell cell : newRow.getTableCells()) {
            this.replaceInCell(cell, curData);
        }
    }
    
    private void replaceInCell(XWPFTableCell cell, Map<String, Object> curData) {
        replaceTableInCell(cell);
        
        List<XWPFParagraph> paras = cell.getParagraphs();
        int size = paras.size();
        for (int i = 0; i < size; i++) {
            XWPFParagraph para = paras.get(i);
            this.replaceRun(para, curData);
        }
    }
    
    /**
     * 判断是否嵌套表格并填充对应内容
     * 
     * @param cell
     * @param params
     */
    private void replaceTableInCell(XWPFTableCell cell) {
        // 存在嵌套的表格
        if (!cell.getTables().isEmpty()) {
            for (XWPFTable table : cell.getTables()) {
                this.replaceInTable(table);
            }
        }
    }
    
    /**
     * 获取当前行数据map
     * 
     * @param row
     * @param params
     * @return
     */
    private Map<String, Object> getRowData(XWPFTableRow row) {
        List<XWPFParagraph> paras;
        List<XWPFTableCell> cells = row.getTableCells();
        Map<String, Object> map = new HashMap<String, Object>();
        
        for (XWPFTableCell cell : cells) {
            paras = cell.getParagraphs();
            for (XWPFParagraph para : paras) {
                map.putAll(getParagraphData(para));
            }
        }
        
        return map;
    }
    
    /**
     * 获取当前字符串中域的对应数据Map
     * 
     * @param para
     * @param params
     * @return
     */
    private Map<String, Object> getParagraphData(XWPFParagraph para) {
        Map<String, Object> result = new HashMap<String, Object>();
        // 匹配字符串中的域
        Matcher matcher = matcherRegion(para.getParagraphText());
        while (matcher.find()) {
            String region = matcher.group(1);
            result.putAll(getRegionData(region));
        }
        
        return result;
    }
    
    private Map<String, Object> getRegionData(String region) {
        Object val = "";
        // 当前匹配中的对象对应的map
        Map<String, Object> curData = this.param;
        String[] keys = region.split("\\.");
        // 如果是一个复杂的对象，如'person.name'，则去格式化person对象
        // TODO 如果当前行中域是个复杂对象并且数据类型是集合，如'person.list'，该如何处理
        for (int j = 0; j < keys.length; j++) {
            val = curData.get(keys[j]);
            if (val == null || val.equals("")) {
                break; // 跳出当前域的循环拿数据
            }
            
            if (j != (keys.length - 1)) {
                curData = ConvertObjToMapAdpater(val);
            } else  {
                curData.put(region, String.valueOf(val));
            }
        }
        
        return curData;
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
    private void copyTableRow(XWPFTable table, XWPFTableRow row, int pos) {
        XWPFTableRow newRow = new XWPFTableRow((CTRow) row.getCtRow().copy(), table) ;
        this.replaceInRow(newRow);
        table.addRow(newRow, pos);
    }
    
   
    /**
     * 根据域获取表格数据项的大小
     * 
     * @param row
     * @param params
     * @return
     */
    private int getRowCount(XWPFTableRow row) {
        int rowCount = 0;
        Matcher matcher = null;
        String[] key;
        List<XWPFTableCell> cells;
        List<XWPFParagraph> paras;
        
        cells = row.getTableCells();
       //TODO 如果一行中同时存在数据大小为1和2以上的该如何处理
        for (XWPFTableCell cell : cells) {
            paras = cell.getParagraphs();
            for (XWPFParagraph para : paras) {
                matcher = matcherRegion(para.getParagraphText());
                if (matcher.find()) {
                    // 取第一个匹配到的域为准获取该域对应数据的大小
                    key = (matcher.group(1)).split("\\.");
                    if (key[0].equals("")) {
                        return rowCount;
                    }
                    
                    // 没有对应数据则跳过，后面的单元格可能会存在多数据的域
                    Object val = this.param.get(key[0]);
                    if (val == null) {
                        break;
                    }
                    
                    // 根据域匹配对应数据的大小
                    if (val instanceof Collection<?>) {
                        Collection<?> col = (Collection<?>) val;
                        rowCount = col.size();
                    } else if (val.getClass().isArray()) {
                        Object[] objs = (Object[]) val;
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
     * 正则匹配字符串，匹配域
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
     * 正则匹配字符串，匹配默认值
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
