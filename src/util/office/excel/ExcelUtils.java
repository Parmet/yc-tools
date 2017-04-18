package util.office.excel;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import sun.misc.BASE64Encoder;
/** 
 * poi 读取excel 支持2003 --2007 及以上文件 
 *  
 */  
public class ExcelUtils {  
  
  
   
      
    /**
     * 读取 excel
     * @param fileName
     * @param filepathtemp
     * @param file
     * @return
     * @throws Exception
     */
    public static List<Map<Integer, Object>> readExcel(String fileName,String filepathtemp,MultipartFile file) throws Exception{  
        //准备返回值列表  
        List<Map<Integer, Object>> valueList=new ArrayList<Map<Integer, Object>>();  
        String tmpFileName= System.currentTimeMillis()+"."+getExtensionName(fileName);  
        String ExtensionName=getExtensionName(fileName);  
        File filelist = new File(filepathtemp);  
        if  (!filelist .exists()  && !filelist .isDirectory())        
        {         
            filelist .mkdir();      
        }   
        String filePath = filepathtemp+System.getProperty("file.separator")+tmpFileName;  
        File tmpfile = new File(filePath);  
        //拷贝文件到服务器缓存目录（在项目下）  
        copy(file, filepathtemp,tmpFileName);
        if(ExtensionName.equalsIgnoreCase("xls")){  
            valueList=readExcel2003(filePath);  
        }else if(ExtensionName.equalsIgnoreCase("xlsx")) {  
            valueList=readExcel2007(filePath);  
        }  
        //删除缓存文件  
        tmpfile.delete();  
        return valueList;  
    }  
    
    /**
     * 读取2003
     * @param filePath
     * @return
     * @throws IOException
     */
    public static List<Map<Integer,Object>> readExcel2003(String filePath) throws IOException{  
        //返回结果集  
        List<Map<Integer,Object>> valueList=new ArrayList<Map<Integer,Object>>();  
        FileInputStream fis=null;  
        try {  
            fis=new FileInputStream(filePath);  
            HSSFWorkbook wookbook = new HSSFWorkbook(fis);  // 创建对Excel工作簿文件的引用  
            HSSFSheet sheet = wookbook.getSheetAt(0);   // 在Excel文档中，第一张工作表的缺省索引是0  
            int rows = sheet.getPhysicalNumberOfRows(); // 获取到Excel文件中的所有行数­  
            Map<Integer,String> keys=new HashMap<Integer, String>();  
            int cells=0;  
            // 遍历行­（第1行  表头） 准备Map里的key  
            HSSFRow firstRow = sheet.getRow(0);  
            if (firstRow != null) {  
                // 获取到Excel文件中的所有的列  
                cells = firstRow.getPhysicalNumberOfCells();  
                // 遍历列  
                for (int j = 0; j < cells; j++) {  
                    // 获取到列的值­  
                    try {  
                        HSSFCell cell = firstRow.getCell(j);  
                        String cellValue = getCellValue(cell);  
                        keys.put(j,cellValue);                        
                    } catch (Exception e) {  
                        e.printStackTrace();      
                    }  
                }  
            }  
            // 遍历行­（从第二行开始）  
            for (int i = 1; i < rows; i++) {  
                // 读取左上端单元格(从第二行开始)  
                HSSFRow row = sheet.getRow(i);  
                // 行不为空  
                if (row != null) {  
                    //准备当前行 所储存值的map  
                    Map<Integer, Object> val=new HashMap<Integer, Object>();  
                      
                    boolean isValidRow = false;  
                      
                    // 遍历列  
                    for (int j = 0; j < cells; j++) {  
                        // 获取到列的值­  
                        try {  
                            HSSFCell cell = row.getCell(j);  
                            String cellValue = getCellValue(cell);  
                            val.put(j,cellValue);   
                            if(!isValidRow && cellValue!=null && cellValue.trim().length()>0){  
                                isValidRow = true;  
                            }  
                        } catch (Exception e) {  
                            e.printStackTrace();          
                        }  
                    }  
                    //第I行所有的列数据读取完毕，放入valuelist  
                    if(isValidRow){  
                        valueList.add(val);  
                    }  
                }  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }finally {  
            fis.close();  
        }  
        return valueList;  
    }  
    
    
    
    /** 
     * 读取2007-2013格式 
     * @param filePath 文件路径 
     * @return 
     * @throws java.io.IOException 
     */  
    public static List<Map<Integer, Object>> readExcel2007(String filePath) throws IOException{  
        List<Map<Integer, Object>> valueList=new ArrayList<Map<Integer, Object>>();  
        FileInputStream fis =null;  
        try {  
            fis =new FileInputStream(filePath);  
            XSSFWorkbook xwb = new XSSFWorkbook(fis);   // 构造 XSSFWorkbook 对象，strPath 传入文件路径  
            XSSFSheet sheet = xwb.getSheetAt(0);            // 读取第一章表格内容  
            // 定义 row、cell  
            XSSFRow row;  
            // 循环输出表格中的第一行内容   表头  
            Map<Integer, String> keys=new HashMap<Integer, String>();  
            row = sheet.getRow(0);  
            if(row !=null){  
                for (int j = row.getFirstCellNum(); j <=row.getPhysicalNumberOfCells(); j++) {  
                    // 通过 row.getCell(j).toString() 获取单元格内容，  
                    if(row.getCell(j)!=null){  
                        if(!row.getCell(j).toString().isEmpty()){  
                            keys.put(j, row.getCell(j).toString());  
                        }  
                    }else{  
                        keys.put(j, "K-R1C"+j+"E");  
                    }  
                }  
            }  
            // 循环输出表格中的从第二行开始内容  
            for (int i = sheet.getFirstRowNum() + 1; i <= sheet.getPhysicalNumberOfRows(); i++) {  
                row = sheet.getRow(i);  
                if (row != null) {  
                    boolean isValidRow = false;  
                    Map<Integer, Object> val = new HashMap<Integer, Object>();  
                    for (int j = row.getFirstCellNum(); j <= row.getPhysicalNumberOfCells(); j++) {  
                        XSSFCell cell = row.getCell(j);  
                        if (cell != null) {  
                            String cellValue = null;  
                            if(cell.getCellType()==XSSFCell.CELL_TYPE_NUMERIC){  
                                if(DateUtil.isCellDateFormatted(cell)){  
                                    cellValue = new DataFormatter().formatRawCellContents(cell.getNumericCellValue(), 0, "yyyy-MM-dd HH:mm:ss");  
                                }  
                                else{  
                                    cellValue = String.valueOf(cell.getNumericCellValue());  
                                }  
                            }  
                            else{  
                                cellValue = cell.toString();  
                            }  
                            if(cellValue!=null&&cellValue.trim().length()<=0){  
                                cellValue=null;  
                            }  
                            val.put(j, cellValue);  
                            if(!isValidRow && cellValue!= null && cellValue.trim().length()>0){  
                                isValidRow = true;  
                            }  
                        }  
                    }  
  
                    // 第I行所有的列数据读取完毕，放入valuelist  
                    if (isValidRow) {  
                        valueList.add(val);  
                    }  
                }  
            }  
        } catch (IOException e) {  
            e.printStackTrace();  
        }finally {  
            fis.close();  
        }  
  
        return valueList;  
    }  
    
    /** 
     * 文件操作 获取文件扩展名 
     *  
     * @param filename 
     *            文件名称包含扩展名 
     * @return 
     */  
    public static String getExtensionName(String filename) {  
        if ((filename != null) && (filename.length() > 0)) {  
            int dot = filename.lastIndexOf('.');  
            if ((dot > -1) && (dot < (filename.length() - 1))) {  
                return filename.substring(dot + 1);  
            }  
        }  
        return filename;  
    }  
  
    /** -----------上传文件,工具方法--------- */  
    private static final int BUFFER_SIZE = 2 * 1024;  
  
    /** 
     *  
     * @param src 
     *            源文件 
     * @param dst 
     *            目标位置 
     */  
    private static void copy(File src, File dst) {  
        InputStream in = null;  
        OutputStream out = null;  
        try {  
            in = new BufferedInputStream(new FileInputStream(src), BUFFER_SIZE);  
            out = new BufferedOutputStream(new FileOutputStream(dst),  
                    BUFFER_SIZE);  
            byte[] buffer = new byte[BUFFER_SIZE];  
            int len = 0;  
            while ((len = in.read(buffer)) > 0) {  
                out.write(buffer, 0, len);  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            if (null != in) {  
                try {  
                    in.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
            if (null != out) {  
                try {  
                    out.close();  
                } catch (IOException e) {  
                    e.printStackTrace();  
                }  
            }  
        }  
    }  
  
    /** 
     * 上传copy文件方法(for MultipartFile) 
     * @param savePath 在linux上要保存完整路径 
     * @param newname 新的文件名称， 采用系统时间做文件名防止中文报错的问题 
     * @throws Exception 
     */  
    public static void copy(MultipartFile file,String savePath,String newname) throws Exception {  
        try {  
            File targetFile = new File(savePath,newname);  
            if (!targetFile.exists()) {  
                //判断文件夹是否存在，不存在就创建  
                targetFile.mkdirs();  
            }  
  
            file.transferTo(targetFile);  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
  
    }  
      
    private static String getCellValue(HSSFCell cell) {  
        DecimalFormat df = new DecimalFormat("#");  
        String cellValue=null;  
        if (cell == null)  
            return null;  
        switch (cell.getCellType()) {  
            case HSSFCell.CELL_TYPE_NUMERIC:  
                if(HSSFDateUtil.isCellDateFormatted(cell)){  
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
                    cellValue=sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));  
                    break;  
                }  
                cellValue=df.format(cell.getNumericCellValue());  
                break;  
            case HSSFCell.CELL_TYPE_STRING:           
                cellValue=String.valueOf(cell.getStringCellValue());  
                break;  
            case HSSFCell.CELL_TYPE_FORMULA:  
                cellValue=String.valueOf(cell.getCellFormula());  
                break;  
            case HSSFCell.CELL_TYPE_BLANK:  
                cellValue=null;  
                break;  
            case HSSFCell.CELL_TYPE_BOOLEAN:  
                cellValue=String.valueOf(cell.getBooleanCellValue());  
                break;  
            case HSSFCell.CELL_TYPE_ERROR:  
                cellValue=String.valueOf(cell.getErrorCellValue());  
                break;  
        }  
        if(cellValue!=null&&cellValue.trim().length()<=0){  
            cellValue=null;  
        }  
        return cellValue;  
    }  
    
    /**
     * 导出Excel
     * @param response
     * @param fileName 文件名
     * @param header 表头
     * @param fileNames 标题key
     * @param list 列表数据
     */
    public static <E> void exportExcel(HttpServletRequest request,HttpServletResponse response,String fileName,String[] header,String[] fileNames,List<E> list) {
        //创建工作簿
        HSSFWorkbook wb=new HSSFWorkbook();
        //创建一个sheet
        HSSFSheet sheet=wb.createSheet("Sheet1");
         
        HSSFRow headerRow=sheet.createRow(0);
        HSSFRow contentRow=null;
         
        //设置标题
        for(int i=0;i<header.length;i++){
            headerRow.createCell(i).setCellValue(header[i]);
        }
        try {
        	if(list!=null && list.size()>0){
	            for(int i=0;i<list.size();i++){
	                contentRow=sheet.createRow(i+1);
	                //获取每一个对象
	                E o=list.get(i);
	                Class cls=o.getClass();
	                for(int j=0;j<fileNames.length;j++){
	                    String fieldName = fileNames[j].substring(0, 1).toUpperCase()+ fileNames[j].substring(1);
	                    Method getMethod = cls.getMethod("get" + fieldName);
	                    Object value = getMethod.invoke(o);
	                    if(value!=null){
	                        contentRow.createCell(j).setCellValue(value.toString());
	                    }
	                }
	            }
        	}
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        OutputStream os=null;
        try {
        	String userAgent = request.getHeader("User-Agent").toUpperCase();
    		if (userAgent.indexOf("MSIE") > 0) {  
    			fileName = URLEncoder.encode(fileName, "UTF-8");  
    		}else if(userAgent.indexOf("FIREFOX") > 0){
    			 fileName = "=?UTF-8?B?" + (new String(new BASE64Encoder().encode(fileName.getBytes("UTF-8")))) + "?=";  
    		} else {  
    			fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");  
    		}  
            response.reset();
            response.addHeader("Content-Disposition", "attachment;filename="+ fileName +".xls");
            response.setContentType("application/vnd.ms-excel;charset=utf-8"); 
            os=response.getOutputStream();
            wb.write(os);
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            if(os!=null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
	
}  
