package nl.denhaag.tw.comparators.gui;

/*
 * #%L
 * wsdl-comparator
 * %%
 * Copyright (C) 2012 - 2013 Team Webservices (Gemeente Den Haag)
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */


import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Writer;

import nl.denhaag.tw.comparators.result.CompareResult;

import org.apache.commons.io.FileUtils;

public class ReportWriter {
	public static void writeReport(File reportDir, String icon, String message, CompareResult compareResult, File oldWsdl,  File newWsdl) throws IOException{
		FileUtils.deleteDirectory(reportDir);
		reportDir.mkdirs();		
		File layoutDir = new File(reportDir, "layout");
		layoutDir.mkdirs();
		String index = readFromClasspath("index.html");
		index = index.replaceAll("OLD_WSDL", oldWsdl.getCanonicalPath().replaceAll("\\\\", "/"));
		index = index.replaceAll("NEW_WSDL", newWsdl.getCanonicalPath().replaceAll("\\\\", "/"));
		index = index.replaceAll("RESULT_MESSAGE", message);
		index = index.replaceAll("RESULT_CLASS", icon);
		PrintWriter writer = new PrintWriter(new File(reportDir, "index.html"));
		writer.write(index);
		writer.flush();
		writer.close();		
		copyFileFromClasspath("jquery.js", layoutDir);
		copyFileFromClasspath("jquery.cookie.js", layoutDir);
		copyFileFromClasspath("jquery-ui.custom.js", layoutDir);
		copyFileFromClasspath("jquery.dynatree.js", layoutDir);
		copyFileFromClasspath("ui.dynatree.css", layoutDir);
		copyFileFromClasspath("icons.gif", layoutDir);
		copyFileFromClasspath("loading.gif", layoutDir);
		copyFileFromClasspath("vline.gif", layoutDir);
		copyFileFromClasspath("invalid.png", layoutDir);
		copyFileFromClasspath("breaks.png", layoutDir);
		copyFileFromClasspath("warning.png", layoutDir);
		copyFileFromClasspath("ok.png", layoutDir);	
		copyFileFromClasspath("default.css", layoutDir);				
		PrintWriter printWriter = new PrintWriter(new File(reportDir, "report-data.json"));
		printWriter.append("[");
		write(printWriter, compareResult);
		printWriter.append("]");
		printWriter.flush();
		printWriter.close();
	}
	private static String readFromClasspath(String name) throws IOException {
		ClassLoader classLoader = (ClassLoader) Thread.currentThread().getContextClassLoader();
		InputStream is = classLoader.getResourceAsStream(name);
		StringBuilder emailContent = new StringBuilder();
		InputStreamReader fileReader = new InputStreamReader(is);
		BufferedReader bFileReader = new BufferedReader(fileReader);
		String line = null;
		while ((line = bFileReader.readLine()) != null) {
			emailContent.append(line + "\n");
		}
		String result =  emailContent.toString();
		bFileReader.close();
		return result;
	}
	private static void copyFileFromClasspath(String name, File outputDir) throws IOException {
		ClassLoader classLoader = (ClassLoader) Thread.currentThread().getContextClassLoader();
		InputStream image = classLoader.getResourceAsStream(name);
		copyFile(image, new File(outputDir, name));
		image.close();
	}
	public static void write(Writer writer, CompareResult compareResult) throws IOException{
		writer.append("{");
		writer.append("\"title\": \""+compareResult.toString()+"\", \"addClass\": \"" + compareResult.getCssClass() + "\"");
		if (compareResult.getChildren().size() > 0){
			writer.append(", \"isFolder\": true, \"expand\": true, \"children\": [");
			boolean first = true;
			for (CompareResult child: compareResult.getChildren()){
				if (first){
					first = false;
				}else {
					writer.append(",");
				}
				write(writer,child);
			}
			writer.append("]");
		}
		writer.append("}");
	}
	public static void copyFile(InputStream in, File out)  throws IOException{
		FileOutputStream fos = new FileOutputStream(out);
		try {
			byte[] buf = new byte[1024];
			int i = 0;
			while ((i = in.read(buf)) != -1) {
				fos.write(buf, 0, i);
			}
		} finally {
			if (in != null)
				in.close();
			if (fos != null)
				fos.close();
		}
	}
}
