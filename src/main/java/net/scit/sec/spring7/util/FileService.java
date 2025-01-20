package net.scit.sec.spring7.util;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

public class FileService {

	// 保存ファイル名を返す

	public static String saveFile(MultipartFile uploadFile, String uploadPath) {
		
		// ストレージディレクトリの作成

		if (!uploadFile.isEmpty()) {
			File path = new File(uploadPath);	// ディレクトリもファイルです

			if(!path.isDirectory()) {	// パスが存在しない場合

				path.mkdirs();	
			}
		}
			
			// 例）jquery-3.7.1.min.js
			// 

			String originalFileName = uploadFile.getOriginalFilename();		// オリジナルファイル名

			String savedFileName = null;		// 保存時に使用するファイル名

			String filename = null;				// 防弾少年団

			String ext = null;					// JPG

			String uuid = UUID.randomUUID().toString();		// 乱数

			
			// の位置を探す

			int position = originalFileName.lastIndexOf("."); // 12 0 ~ 11

			if (position == -1) {	// 拡張子のないファイル

				ext = "";
				filename = originalFileName;
				
			} else {
				ext = "." + originalFileName.substring(position+1);
				filename = originalFileName.substring(0, position);
			}
			savedFileName = filename + "_" + uuid  + ext;
			
//			ディレクトリに保存する
//			c://uploadPath /savedFileName

			String fullPath = uploadPath + "/" + savedFileName;
			
			File serverFile = null;
			serverFile = new File(fullPath);
			
			try {
				uploadFile.transferTo(serverFile);				
			} catch (IOException e) {		// ストレージに保存できないため、DBにも保存しないでください。

				savedFileName = null;
				e.printStackTrace();
			}
		return savedFileName;
	}
	
//	ファイルの削除

	public static boolean deleteFile(String fullPath) {
		boolean result = false;		// 削除するかどうかを返す

		
		File file = new File(fullPath);
		if (file.isFile()) {
			result = file.delete();
			
		}
		
		return result;
	}
}
