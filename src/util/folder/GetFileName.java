package util.folder;

import java.io.File;
import java.util.ArrayList;

public class GetFileName {
    private static final String path = "F:\\Work\\201707\\yibaoju\\zs\\gzlss_web";
    
    public static String [] getFileName(String path)
    {
        File file = new File(path);
        String [] fileName = file.list();
        return fileName;
    }
    
    public static void getAllFileName(String path, ArrayList<String> fileName)
    {
        File file = new File(path);
        File [] files = file.listFiles();
        
        for(File a:files)
        {
            if (a.getAbsolutePath().contains(".svn")) {
                continue;
            }
            
            if(a.isDirectory())
            {
                getAllFileName(a.getAbsolutePath(),fileName);
            } else if (a.getName() != null) {
                fileName.add(a.getName());
            }
        }
    }
    
    public static void main(String[] args)
    {
        String [] fileName = getFileName(path);
        for(String name : fileName)
        {
            System.out.println(name);
        }
        System.out.println("--------------------------------");
        ArrayList<String> listFileName = new ArrayList<String>(); 
        getAllFileName(path, listFileName);
        for(String name:listFileName)
        {
            System.out.println(name);
        }
         
    }
}
