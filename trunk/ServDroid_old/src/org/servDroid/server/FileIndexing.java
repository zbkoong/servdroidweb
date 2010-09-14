/*
 * Copyright (C) 2010 Joan Puig Sanz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.servDroid.server;

import java.io.File;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;

public class FileIndexing implements FileIndexingInterface {

	private String mPath, mFileGet;

	/**
	 * Constructor to create a HTML document of file indexing
	 * 
	 */
	public FileIndexing() {

	}

	/**
	 * Get the file indexing document for an specific folder
	 * 
	 * @param path
	 *            The www path
	 * @param fileGet
	 *            The path to indexing
	 * @return File indexing document
	 */
	public String getIndexing(String path, String fileGet) {
		mPath = path;
		mFileGet = fileGet;
		return "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN/"
				+ "http://www.w3.org/TR/REC-html40/loose.dtd\">"
				+ "<HTML>"
				+ "<HEAD>"
				+ "<TITLE>Index of "
				+ mFileGet
				+ "</TITLE>"
				+ "<link href=\"/default_style.css\" rel=\"stylesheet\" type=\"text/css\" />"
				+ "</HEAD>"
				+ "<H1>Index of "
				+ mFileGet
				+ "</H1>"
				+ "</PRE><HR>"
				+ "<table><tr><th scope=\"col\">Name</th><th scope=\"col\">Last modified</th><th scope=\"col\">Size</th></tr>"

				+ listPath()

				+ "</table>"

				+ "</PRE><HR>" + "<ADDRESS>ServDroid.web</ADDRESS>"
				+ "</BODY></HTML>";
	}

	/**
	 * For each file/folder generate a HTML line
	 * 
	 * @return HTML ready to append in to the file indexing HTML
	 */
	private String listPath() {
		File files[];
		DateFormat dateFormat = new SimpleDateFormat("d-MMM-yyyy HH:mm");

		File _path = new File(mPath);
		files = _path.listFiles();

		// String text =
		// "<IMG border=\"0\" src=\"/icons/back.gif\" ALT=\"[DIR]\"> <A HREF=\"/\">Parent Directory</A>        "
		// + dateString + "      -<br>";

		String text = "";
		String tmp = "/";

		if (!mFileGet.equals("/")) {
			text = "<tr><td<IMG border=\"0\" src=\"/icons/go-back.png\" ALT=\"[DIR]\"> <A HREF=\"/\">Parent Directory</A></td><td>"
					+ dateFormat.format(_path.lastModified())
					+ "</td><td>-</td></tr>";

		} else {
			tmp = "";
		}
		Arrays.sort(files);
		for (int i = 0, n = files.length; i < n; i++) {

			// "<IMG border=\"0\" src=\"/icons/back.gif\" ALT=\"[DIR]\"> <A HREF=\"/\">Parent Directory</A>        09-Aug-2009 19:22      -</br>"
			// +

			if (files[i].isDirectory()) {
				text = text
						+ "<tr><td><IMG border=\"0\" src=\"/icons/directory.png\" ALT=\"[DIR]\"> <A HREF=\""
						+ mFileGet + tmp + files[i].getName() + "\">"
						+ files[i].getName() + "</A> " + "</td><td>"
						+ dateFormat.format(files[i].lastModified())
						+ "</td><td>-</td></tr>";
			} else if (files[i].getName().toLowerCase().endsWith(".jpg")
					|| files[i].getName().toLowerCase().endsWith(".png")
					|| files[i].getName().toLowerCase().endsWith(".bmp")
					|| files[i].getName().toLowerCase().endsWith(".jpeg")
					|| files[i].getName().toLowerCase().endsWith(".gif")) {
				text = text
						+ "<tr><td><IMG border=\"0\" src=\"/icons/picture.png\" ALT=\"[IMG]\"> <A HREF=\""
						+ mFileGet + tmp + files[i].getName() + "\">"
						+ files[i].getName() + "</A> " + "</td><td>"
						+ dateFormat.format(files[i].lastModified())
						+ "</td><td>" + pharseFileSize(files[i].length())
						+ "</td></tr>";
			} else if (files[i].getName().toLowerCase().endsWith(".pdf")) {
				text = text
						+ "<tr><td><IMG border=\"0\" src=\"/icons/pdf.png\" ALT=\"[PDF]\"> <A HREF=\""
						+ mFileGet + tmp + files[i].getName() + "\">"
						+ files[i].getName() + "</A> " + "</td><td>"
						+ dateFormat.format(files[i].lastModified())
						+ "</td><td>" + pharseFileSize(files[i].length())
						+ "</td></tr>";

			} else if (files[i].getName().toLowerCase().endsWith(".doc")
					|| files[i].getName().toLowerCase().endsWith(".docx")
					|| files[i].getName().toLowerCase().endsWith(".odt")
					|| files[i].getName().toLowerCase().endsWith(".rtf")
					|| files[i].getName().toLowerCase().endsWith(".sxw")) {
				text = text
						+ "<tr><td><IMG border=\"0\" src=\"/icons/document.png\" ALT=\"[DOC]\"> <A HREF=\""
						+ mFileGet + tmp + files[i].getName() + "\">"
						+ files[i].getName() + "</A> " + "</td><td>"
						+ dateFormat.format(files[i].lastModified())
						+ "</td><td>" + pharseFileSize(files[i].length())
						+ "</td></tr>";

			} else if (files[i].getName().toLowerCase().endsWith(".css")) {
				text = text
						+ "<tr><td><IMG border=\"0\" src=\"/icons/css.png\" ALT=\"[CSS]\"> <A HREF=\""
						+ mFileGet + tmp + files[i].getName() + "\">"
						+ files[i].getName() + "</A> " + "</td><td>"
						+ dateFormat.format(files[i].lastModified())
						+ "</td><td>" + pharseFileSize(files[i].length())
						+ "</td></tr>";

			} else if (files[i].getName().endsWith(".xls")
					|| files[i].getName().toLowerCase().endsWith(".xlsx")
					|| files[i].getName().toLowerCase().endsWith(".ods")
					|| files[i].getName().toLowerCase().endsWith(".sxc")) {
				text = text
						+ "<tr><td><IMG border=\"0\" src=\"/icons/spreadsheet.png\" ALT=\"[CAL]\"> <A HREF=\""
						+ mFileGet + tmp + files[i].getName() + "\">"
						+ files[i].getName() + "</A> " + "</td><td>"
						+ dateFormat.format(files[i].lastModified())
						+ "</td><td>" + pharseFileSize(files[i].length())
						+ "</td></tr>";

			} else if (files[i].getName().toLowerCase().endsWith(".exe")) {
				text = text
						+ "<tr><td><IMG border=\"0\" src=\"/icons/executable.png\" ALT=\"[EXE]\"> <A HREF=\""
						+ mFileGet + tmp + files[i].getName() + "\">"
						+ files[i].getName() + "</A> " + "</td><td>"
						+ dateFormat.format(files[i].lastModified())
						+ "</td><td>" + pharseFileSize(files[i].length())
						+ "</td></tr>";

			} else if (files[i].getName().toLowerCase().endsWith(".zip")
					|| files[i].getName().toLowerCase().endsWith(".rar")
					|| files[i].getName().toLowerCase().endsWith(".gz")
					|| files[i].getName().toLowerCase().endsWith(".tar")
					|| files[i].getName().toLowerCase().endsWith(".jar")
					|| files[i].getName().toLowerCase().endsWith(".bz2")
					|| files[i].getName().toLowerCase().endsWith(".lzma")
					|| files[i].getName().toLowerCase().endsWith(".7z")
					|| files[i].getName().toLowerCase().endsWith(".cbz")
					|| files[i].getName().toLowerCase().endsWith(".ar")) {
				text = text
						+ "<tr><td><IMG border=\"0\" src=\"/icons/file-archiver.png\" ALT=\"[PAK]\"> <A HREF=\""
						+ mFileGet + tmp + files[i].getName() + "\">"
						+ files[i].getName() + "</A> " + "</td><td>"
						+ dateFormat.format(files[i].lastModified())
						+ "</td><td>" + pharseFileSize(files[i].length())
						+ "</td></tr>";

			} else if (files[i].getName().toLowerCase().endsWith(".mp3")
					|| files[i].getName().toLowerCase().endsWith(".mp4")
					|| files[i].getName().toLowerCase().endsWith(".wmv")
					|| files[i].getName().toLowerCase().endsWith(".mpg")
					|| files[i].getName().toLowerCase().endsWith(".divx")
					|| files[i].getName().toLowerCase().endsWith(".ogg")
					|| files[i].getName().toLowerCase().endsWith(".avi")
					|| files[i].getName().toLowerCase().endsWith(".aac")
					|| files[i].getName().toLowerCase().endsWith(".ogm")
					|| files[i].getName().toLowerCase().endsWith(".cda")
					|| files[i].getName().toLowerCase().endsWith(".wma")
					|| files[i].getName().toLowerCase().endsWith(".wav")
					|| files[i].getName().toLowerCase().endsWith(".mid")
					|| files[i].getName().toLowerCase().endsWith(".midi")
					|| files[i].getName().toLowerCase().endsWith(".mkv")
					|| files[i].getName().toLowerCase().endsWith(".mov")
					|| files[i].getName().toLowerCase().endsWith(".3gp")
					|| files[i].getName().toLowerCase().endsWith(".asf")) {
				text = text
						+ "<tr><td><IMG border=\"0\" src=\"/icons/multimedia.png\" ALT=\"[MUL]\"> <A HREF=\""
						+ mFileGet + tmp + files[i].getName() + "\">"
						+ files[i].getName() + "</A> " + "</td><td>"
						+ dateFormat.format(files[i].lastModified())
						+ "</td><td>" + pharseFileSize(files[i].length())
						+ "</td></tr>";

			} else if (files[i].getName().toLowerCase().endsWith(".html")
					|| files[i].getName().toLowerCase().endsWith(".htm")) {
				text = text
						+ "<tr><td><IMG border=\"0\" src=\"/icons/html.png\" ALT=\"[HTM]\"> <A HREF=\""
						+ mFileGet + tmp + files[i].getName() + "\">"
						+ files[i].getName() + "</A> " + "</td><td>"
						+ dateFormat.format(files[i].lastModified())
						+ "</td><td>" + pharseFileSize(files[i].length())
						+ "</td></tr>";

			} else if (files[i].getName().toLowerCase().endsWith(".sh")
					|| files[i].getName().toLowerCase().endsWith(".vbs")
					|| files[i].getName().toLowerCase().endsWith(".py")
					|| files[i].getName().toLowerCase().endsWith(".pyc")
					|| files[i].getName().toLowerCase().endsWith(".pyd")
					|| files[i].getName().toLowerCase().endsWith(".pyo")
					|| files[i].getName().toLowerCase().endsWith(".pyw")) {
				text = text
						+ "<tr><td><IMG border=\"0\" src=\"/icons/script.png\" ALT=\"[SCR]\"> <A HREF=\""
						+ mFileGet + tmp + files[i].getName() + "\">"
						+ files[i].getName() + "</A> " + "</td><td>"
						+ dateFormat.format(files[i].lastModified())
						+ "</td><td>" + pharseFileSize(files[i].length())
						+ "</td></tr>";

			} else {
				text = text
						+ "<tr><td><IMG border=\"0\" src=\"/icons/file.png\" ALT=\"[FILE]\"> <A HREF=\""
						+ mFileGet + tmp + files[i].getName() + "\">"
						+ files[i].getName() + "</A> " + "</td><td>"
						+ dateFormat.format(files[i].lastModified())
						+ "</td><td>" + pharseFileSize(files[i].length())
						+ "</td></tr>";
			}
		}
		return text;
	}

	/**
	 * Convert the length (in Bytes) to String
	 * 
	 * @param lengthBytes
	 * @return The length ready to be showed
	 */
	private String pharseFileSize(long lengthBytes) {
		String size;

		if (lengthBytes <= 1024) {
			return "1 k";
		}
		long m = lengthBytes / 1024;
		int cont = 1;
		DecimalFormat numberFormat = new DecimalFormat("0");

		while (m >= 1024) {
			cont++;
			m = m / 1024;

		}

		switch ((int) cont) {
		case 1:// KB
			size = " KB";
			break;
		case 2: // MB
			size = " MB";
			break;

		case 3: // GB
			size = " GB";
			break;

		case 4: // TB
			size = " TB";
			break;

		default:
			size = " B";
			return numberFormat.format(lengthBytes) + size;
		}
		return numberFormat.format((lengthBytes / (Math.pow(1024, cont))))
				+ size;
	}

}
