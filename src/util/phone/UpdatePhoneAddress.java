package util.phone;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UpdatePhoneAddress {
	public static List<String> sqls = new LinkedList<String>();
	
	public static void main(String[] args) {
		UpdatePhoneAddress u = new UpdatePhoneAddress();
		u.threadUpdatePhone();
	}
	
	public void threadUpdatePhone() {
		ExecutorService threads = Executors.newFixedThreadPool(10);
        CompletionService<Long> cs = new ExecutorCompletionService<Long>(threads);
        Integer page = 0;
        
        Random random = new Random(100);
        
        // 一次查500条
        for(int i = 1; i <= 10; i++) {
        	page = page + 1;
        	List<String> list = new ArrayList<String>();
        	for (int j = 0; j < 50; j++) {
        		list.add(String.valueOf(random.nextInt()));
			}
        	
            cs.submit(new GetPhoneAddress(list));
        }
        
        //关闭service
        threads.shutdown(); 
        
        // 5000 一个文件
        if (page % 100 == 0) {
        	sqls.add("commit;");
        	String file = "F:\\temp\\register\\register_" + (page/100) + ".sql";
        	convernToTxt(file);
        	sqls.clear();
        	System.out.println("save as : " + file);
        }
	}
	
	class GetPhoneAddress implements Callable<Long> {
		public GetPhoneAddress (List<String> list) {
			// 查询并更新归属地
			String province = "", sql = "";
			for (String phone : list) {
				if (phone == null) {
					continue;
				}
				
				if (phone != "") {
					sql = " WHERE phone = '" + phone + "';";
				}
				
				province = "广东";
				sql = "UPDATE tb_cfcusper_userregister SET province = '" + province + "'" + sql;
				sqls.add(sql);
				
				province = "";
				sql = "";
			}
		}

		@Override
		public Long call() throws Exception {
			return 1L;
		}
	}
	
	public static void convernToTxt(String file) {
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
			for (String s : sqls) {
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
