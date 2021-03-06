package kr.com.inspect.util;

import java.io.File;

/**
 * 파일 및 폴더와 관련된 처리를 하는 유틸리티
 * @author Yeonhee Kim
 * @version 1.0
 *
 */
public class FileManager {
	
	/**
	 * 파일을 삭제함
	 * @param path 파일 경로
	 */
	public void deleteFile(String path) {
		File file = new File(path);
		try {
			if (file.exists()) {
				if (file.delete()) {
					// System.out.println("파일 삭제 성공");
				}else {
					// System.out.println("파일 삭제 실패");
				}
			}else {
				// System.out.println("파일이 존재하지 않습니다.");
			}
		}catch(Exception e) {
			//e.printStackTrace();
		}
	}
	
	/**
	 * 폴더를 삭제함
	 * @param path 폴더 경로
	 */
	public void deleteFolder(String path) {
		File folder = new File(path);
		try {
			if(folder.exists()){
				File[] folderList = folder.listFiles(); 
				for (int i = 0; i < folderList.length; i++) {
					if(!folderList[i].isFile()) {
						deleteFolder(folderList[i].getPath()); 
					}
					folderList[i].delete();
				}
				folder.delete(); 
			}
		}catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	/**
	 * 파일이 존재하지 않는 빈 폴더를 삭제함
	 * @param path 폴더 경로
	 */
	public void deleteEmptyFolder(String path) {
		File folder = new File(path);
		File[] folderList = null;
		try {
			if(folder.exists()){
				folderList = folder.listFiles(); 
				for (int i = 0; i < folderList.length; i++) {
					if(!folderList[i].isFile()) {
						if(checkFileCount(folderList[i].getPath()) == 0) {
							deleteFolder(folderList[i].getPath());
						}else {
							deleteEmptyFolder(folderList[i].getPath());
						}
					}
				}
				if(checkFileCount(path) == 0){
					deleteFolder(path);
				}
			}
		}catch(Exception e) {
			//e.printStackTrace();
		}
	}
	
	/**
	 * 폴더에 존재하는 파일의 개수를 반환함
	 * @param path 폴더 경로
	 * @return 폴더에 존재하는 파일의 개수
	 */
	public int checkFileCount(String path) {
		int fileCount = 0;
		File folder = new File(path);
		try {
			if(folder.exists()){
				File[] folderList = folder.listFiles(); 
				for (int i = 0; i < folderList.length; i++) {
					if(!folderList[i].isFile()) {
						fileCount += checkFileCount(folderList[i].getPath()); 
					}else {
						fileCount++;
					}
				}
			}
		}catch (Exception e) {
			//e.printStackTrace();
		}
		return fileCount;
	}
}
