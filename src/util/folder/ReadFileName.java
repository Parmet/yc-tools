package util.folder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ReadFileName {
	static List<String> files = new ArrayList<String>(3600);

	public static void main(String[] args) {
		String path = "F:\\project\\chinauip.cfc5\\src\\";
		String file = "F:\\temp\\0313\\entity.txt";
		ReadFileName rfn = new ReadFileName();
//		// 获取当前路径
//		String curPath = rfn.getCurFilePath();
//		System.out.println("curPath is " + curPath);
//		// 输入目标目录
//		System.out.print("input path: ");
//		Scanner sc = new Scanner(System.in);
//		path = sc.nextLine();
//		System.out.println("");
//
//		if (path == null || path == "") {
//			System.out.println("plase input param path!");
//			return;
//		}
//
//		// 生成的txt文件
//		file = curPath + "/" + path + ".txt";
//		System.out.println("save file as " + file);

		// 获取文件名
		rfn.getFile(path);
		// 保存到txt
		rfn.convernToTxt(file);
	}

	public String getCurFilePath() {
		File f = new File("");
		return f.getAbsolutePath();
	}

	public void getFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			return;
		}
		
		File[] array = file.listFiles();
		
		for (int i = 0; i < array.length; i++) {
			if (array[i].isFile()) {
				System.out.println(array[i].getParent());
				if (array[i].getParent().contains("entity")) {
					files.add(array[i].getName());
				}
				
			} else if (array[i].isDirectory()) {
				getFile(array[i].getPath());
			}
		}
	}

	public void convernToTxt(String file) {
		File f = new File(file);
		if (!f.exists()) {
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

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
}
