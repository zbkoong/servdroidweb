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

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.StringTokenizer;

import org.servDroid.db.LogAdapter;

import android.util.Log;

/**
 * 
 * @author Joan Puig Sanz
 * 
 */
public class httpRequestHandler implements Runnable {
	private static final String TAG = "ServDroid";

	final static String CRLF = "\r\n";

	private Socket mSocket;
	private OutputStream mOutput;
	private BufferedReader mBr;

	private LogAdapter mLogAdapter;

	private String mWwwPath, mErrorPath;

	public httpRequestHandler(Socket socket, String wwwPath, String errorPath,
			LogAdapter logAdapter) throws Exception {
		this.mWwwPath = wwwPath;
		this.mErrorPath = errorPath;
		this.mSocket = socket;
		this.mOutput = socket.getOutputStream();
		this.mBr = new BufferedReader(new InputStreamReader(socket
				.getInputStream()));
		this.mLogAdapter = logAdapter;
		this.mErrorPath = errorPath;

	}

	public void run() {
		try {
			processRequest();
		} catch (Exception e) {
			Log.e(TAG, "ERROR, Can not runn the handler thread", e);
		}
	}

	/**
	 * Function to process the request
	 * 
	 * @throws Exception
	 */
	private void processRequest() throws Exception {
		while (true) {

			String headerLine = mBr.readLine();
			// Log.d(TAG, "Header line: " + headerLine);
			if (headerLine.equals(CRLF) || headerLine.equals(""))
				break;

			StringTokenizer s = new StringTokenizer(headerLine);
			String temp = s.nextToken();

			if (temp.equals("GET")) {

				String fileGet = s.nextToken();
				String fileName;
				if (fileGet.equals("/"))
					fileGet = "/index.html";

				// fileName = fileName.replace("/../", "/");

				fileName = mWwwPath + fileGet;

				// fileName = fileName.replace("//", "/");
				// Log.d(TAG, "File Name request: " + fileName);

				FileInputStream fis = null;
				boolean fileExists = true;
				try {
					fis = new FileInputStream(fileName);
				} catch (FileNotFoundException e) {
					fileExists = false;
					// Log.d(TAG, "File " + fileName + " not found.");
					// mLogAdapter.addLog(("" +
					// mSocket.getInetAddress()).replace(
					// "/", ""), "GET " + fileGet, "ERROR 404", "");
				}
				String serverLine = "ServDroid server";
				String statusLine = null;
				String contentTypeLine = null;
				String entityBody = null;
				String contentLengthLine = "error";
				if (fileExists) {
					statusLine = "HTTP/1.0 200 OK" + CRLF;
					contentTypeLine = "Content-type: " + contentType(fileName)
							+ CRLF;
					contentLengthLine = "Content-Length: "
							+ (new Integer(fis.available())).toString() + CRLF;
					mLogAdapter.addLog(("" + mSocket.getInetAddress()).replace(
							"/", ""), "GET " + fileGet, "", "");
				} else {
					try {
						fileName = mErrorPath + "/404.html";
						fis = new FileInputStream(fileName);
						fileExists = true;
						statusLine = "HTTP/1.0 404 Not Found" + CRLF;
						contentTypeLine = "Content-type: "
								+ contentType(fileName) + CRLF;
						contentLengthLine = "Content-Length: "
								+ (new Integer(fis.available())).toString()
								+ CRLF;

					} catch (FileNotFoundException e) {
						statusLine = "HTTP/1.0 404 Not Found" + CRLF;
						contentTypeLine = "Content-type: text/html" + CRLF;
						entityBody = "<HTML>"
								+ "<HEAD><title>404 Not Found</title>"
								+ "</head><body> <div style=\"text-align: center;\">"
								+ "<big><big><big><span style=\"font-weight: bold;\">"
								+ "<br>ERROR 404: Document not Found<br></span></big></big></big></div>"
								+ "</BODY></HTML>";
						contentLengthLine = "Content-Length: "
								+ (entityBody.getBytes().length) + CRLF;
						// Log.d(TAG, "File " + fileName + " not found.");
						mLogAdapter.addLog("", fileName,
								"ERROR, File 404.html not found", "");
					}

				}

				// Send the status line.
				mOutput.write(statusLine.getBytes());
				// Log.d(TAG, "Status line: " + statusLine);

				// Send the server line.
				mOutput.write(serverLine.getBytes());
				// Log.d(TAG, "Server line: " + serverLine);

				// Send the content type line.
				mOutput.write(contentTypeLine.getBytes());
				// Log.d(TAG, "Content type: " + contentTypeLine);

				// Send the entity body.
				if (fileExists) {
					// Send the Content-Length
					mOutput.write(contentLengthLine.getBytes());
					// Log.d(TAG, "Content line: " + contentLengthLine);

					// Send a blank line to indicate the end of the header
					// lines.
					mOutput.write(CRLF.getBytes());
					// Log.d(TAG, CRLF);

					sendBytes(fis, mOutput);
					fis.close();
				} else {
					mOutput.write(contentLengthLine.getBytes());
					// Log.d(TAG, "Content line: " + contentLengthLine);

					// Send a blank line to indicate the end of the header
					// lines.
					mOutput.write(CRLF.getBytes());
					// Log.d(TAG, CRLF);

					mOutput.write(entityBody.getBytes());
				}
			}
		}

		try {
			mOutput.close();
			mBr.close();
			mSocket.close();
		} catch (Exception e) {
			Log.e(TAG, "ERROR cloding socket", e);
		}
	}

	/**
	 * Send bytes
	 * 
	 * @param fis
	 *            fileInputStream to send
	 * @param os
	 *            The output Stream to use for send
	 * @throws Exception
	 */
	private void sendBytes(FileInputStream fis, OutputStream os)
			throws Exception {

		byte[] buffer = new byte[1024];
		int bytes = 0;

		while ((bytes = fis.read(buffer)) != -1) {
			os.write(buffer, 0, bytes);
		}
	}

	/**
	 * Get content type
	 * 
	 * @param fileName
	 *            The file
	 * @return Conten type
	 */
	private String contentType(String fileName) {
		if (fileName.endsWith(".htm") || fileName.endsWith(".html")
				|| fileName.endsWith(".txt")) {
			return "text/html";
		} else if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
			return "image/jpeg";
		} else if (fileName.endsWith(".gif")) {
			return "image/gif";
		} else if (fileName.endsWith(".png")) {
			return "image/png";
		} else {
			return "application/octet-stream";
		}
	}
}