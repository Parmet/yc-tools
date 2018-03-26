package util.folder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GetFoldFileName {
	static List<String> files = new ArrayList<String>(3600);
	static List<String> files2 = new ArrayList<String>(3600);

	/*
	 * 找到文件夹下的名称 遍历总项目的src找到对应名称的java文件 复制到新项目中的src 删除子目录不存在文件以及文件夹的文件夹
	 */

	public static void main(String[] args) {
		// 写一个获取文件名的java类，在cmd中运行，从args中获取各种参数（如getNamePath:gzlss_web, txtPath:gzlss_web.txt）
		long start = System.currentTimeMillis();
		String getNamePath = "F:\\temp\\0313\\entity.txt";
		//String getNamePath2 = "F:\\temp\\0116\\gzlss_web.txt";
		String oldPath = "F:\\project\\chinauip.cfc5\\src\\";
		String newPath = "F:\\temp\\0313\\entity\\";
		readTextToList(getNamePath);
//		retainFile(getNamePath, getNamePath2);
//		readTextToList(getNamePath);
//		System.out.println(files);
//		getFileToTxt(oldPath);
//		files.clear();
//		System.out.println("clear:" + files);
//		readTextToList(txtPath);
		copyFolder(oldPath, newPath);
		long end = System.currentTimeMillis();
		long l = end - start;
		long day = l / (24 * 60 * 60 * 1000);
		long hour = (l / (60 * 60 * 1000) - day * 24);
		long min = ((l / (60 * 1000)) - day * 24 * 60 - hour * 60);
		long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
		System.out.println("");
		System.out.println("Copy Done! Use " + hour + "小时" + min + "分" + s
				+ "秒");
	}
	
	public static void deleteAllFilesOfDir(File path) {  
	    if (!path.exists())  
	        return;  
	    if (path.isFile()) {  
	        path.delete();  
	        return;  
	    }  
	    File[] files = path.listFiles();  
	    for (int i = 0; i < files.length; i++) {  
	        deleteAllFilesOfDir(files[i]);  
	    }  
	    path.delete();  
	} 
	
	public static void retainFile(String file1, String file2) {
		readTextToList(file1);
		try {
			BufferedReader br = new BufferedReader(new FileReader(file2));
			while (br.readLine() != null) {
				String fileName = br.readLine().trim().replace(".class", ".java");
				files2.add(fileName);
			}
			
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		files.retainAll(files2);
		for (String file : files) {
			System.out.println(file);
		}
	}

	public static void getFile(String path) {
		File file = new File(path);
		File[] array = file.listFiles();

		for (int i = 0; i < array.length; i++) {
			if (array[i].isFile()) {
				if (!files.contains(array[i].getName())) {
					files.add(array[i].getName().replace(".class", ".java"));
				}
			} else if (array[i].isDirectory()) {
				getFile(array[i].getPath());
			}
		}
	}
	
	public static void readTextToList(String file) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
			while (br.readLine() != null) {
				String fileName = br.readLine().trim().replace(".class", ".java");
				files.add(fileName);
			}
			
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void getFileToTxt(String file) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			for (String s : files) {
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
	 * 复制整个文件夹内容
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf/ff
	 * @return boolean
	 */
	public static void copyFolder(String oldPath, String newPath) {

		try {
			(new File(newPath)).mkdirs(); // 如果文件夹不存在 则建立新文件夹
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}

				if (temp.isFile() && files.contains(temp.getName())) {

					FileInputStream input = new FileInputStream(temp);
					FileOutputStream output = new FileOutputStream(newPath
							+ "/" + (temp.getName()).toString());

					byte[] b = new byte[1024 * 5];
					int len;
					while ((len = input.read(b)) != -1) {
						output.write(b, 0, len);
					}

					output.flush();
					output.close();
					input.close();
					System.out.println((new Date(System.currentTimeMillis()))
							.toString()
							+ " [Copy File] : "
							+ temp.getAbsolutePath());
				}
				if (temp.isDirectory()) {// 如果是子文件夹
					copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
				}
			}
		} catch (Exception e) {
			System.out.println("复制整个文件夹内容操作出错");
			e.printStackTrace();

		}

	}
}
