package com.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/*
 * @author M.Skripnikov
 * 1/4/20
 */

public class FileCodeLinesCounter {

	private static final String LINE_COMMENT = "//";

	public static int getLOCCount(File file){
		int count = 0;
		try (BufferedReader reader = new BufferedReader(new FileReader(file))){

			boolean insideComment=false;
			String aLine;
			while((aLine = reader.readLine()) != null){
				String line = aLine.trim();
				if(line.length()==0){
					continue;
				}
				if(insideComment){
					int closingCommentPos = line.indexOf("*/");
					if (closingCommentPos==-1) {
						//still inside comment
						continue;
					}
					line = line.substring(closingCommentPos + 2).trim();
					insideComment = false;

				}


				int pos = 1;
				int prev = line.charAt(0);


				int slashStart = line.indexOf("/");
				if(slashStart ==-1){
					count++;
					continue;
				}
				if(slashStart>0){
					count++;
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return count;
	}
}
