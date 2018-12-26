package com;

import java.io.IOException;
import java.security.PrivilegedExceptionAction;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.security.UserGroupInformation;

public class ConnectionHadoop {
	
	public static void main(String[] args) {
		PrivilegedExceptionAction<Void> pea = new PrivilegedExceptionAction<Void>() {

			@Override
			public Void run() throws Exception {
				Configuration config = new Configuration();
				config.set("fs.defaultFS", "hdfs://192.168.0.150:9000/user/bdi");
				config.setBoolean("dfs.support.append",true);
				
				FileSystem fs = FileSystem.get(config); // fs -> /user/bdi 경로를 의미
				
				Path upFileName = new Path("word.txt");	// 기본 루트 -> /user/bdi
				Path logFileName = new Path("word.log");
				if(fs.exists(upFileName)) {
					fs.delete(upFileName,true);
					fs.delete(logFileName,true);
				}
				FSDataOutputStream fsdo = fs.create(upFileName);	// 파일을 만듬
				fsdo.writeUTF("hi hi hi hey hey lol start hi");	// 파일에 내용을 씀
				fsdo.close();
				
				Path dirName = new Path("/user/bdi");	// /user/bdi에 뭐가 있는지 찾아봄
				FileStatus[] files = fs.listStatus(dirName);
				for(FileStatus file:files) {
					System.out.println(file);
				}
				return null;
			}
		};
		
		UserGroupInformation ugi = UserGroupInformation.createRemoteUser("bdi");
		try {
			ugi.doAs(pea);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
