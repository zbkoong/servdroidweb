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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.servDroid.db.LogAdapter;

import android.text.format.DateFormat;
import android.util.Log;

/**
 * 
 * @author Joan Puig Sanz and Jan Dunkerbeck
 * 
 */
public class HttpRequestHandler implements Runnable {
	
	private static final String HTTP_HEADER_IF_MODIFIED = "If-Modified-Since:";

	// Date Format pattern for HTTP headers
	private static final String HTTP_DATE_FORMAT = "EEE, dd MMMMM yyyyy hh:mm:ss zzzz";

	private static final String TAG = "ServDroid";

	final static String CRLF = "\r\n";

	private Socket mSocket;
	private OutputStream mOutput;
	private BufferedReader mBr;

	private LogAdapter mLogAdapter;

	private String mWwwPath, mErrorPath;

	private Boolean mFileIndexing;

	// After how many minutes a page will expire in the browser cache
	// TODO: Configuration?
	private int expires = 60;

	private static Map<String,String> mimeTypes;
	
	static {
		// Maybe there is a /etc/mime-types available?
		mimeTypes = new HashMap<String, String>();
		mimeTypes.put("htm","text/html");
		mimeTypes.put("css","text/css");
		mimeTypes.put("html","text/html");
		mimeTypes.put("xhtml","text/xhtml");
		mimeTypes.put("txt","text/html");
		mimeTypes.put("pdf","application/pdf");
		mimeTypes.put("jpg","image/jpeg");
		mimeTypes.put("gif","image/gif");
		mimeTypes.put("png","image/png");
	}

	public HttpRequestHandler(Socket socket, String wwwPath, String errorPath,
			LogAdapter logAdapter, boolean fileIndexing) throws Exception {
		this.mWwwPath = wwwPath;
		this.mErrorPath = errorPath;
		this.mSocket = socket;
		this.mOutput = socket.getOutputStream();
		this.mBr = new BufferedReader(new InputStreamReader(socket
				.getInputStream()));
		this.mLogAdapter = logAdapter;
		this.mErrorPath = errorPath;

		this.mFileIndexing = fileIndexing;

	}

	public void run() {
		try {
			processRequest();
		} catch (Exception e) {
			Log.e(TAG, "ERROR, Can not run the handler thread", e);
		}
	}

	/**
	 * Function to process the request
	 * 
	 * @throws Exception
	 */
	private void processRequest() throws Exception {

		try {
			
			String httpRequest = mBr.readLine();
	
			StringTokenizer s = new StringTokenizer(httpRequest);
			String httpCommand = s.nextToken();
			String fileGet = s.nextToken();
			String fileName = mWwwPath + fileGet;
	
			Map<String,String> requestHeader = new HashMap<String, String>();
			
			// Analyze following HTTP-Headers
			while (true) {
	
				String headerLine = mBr.readLine();
				
				if (headerLine.equals(CRLF) || headerLine.equals("")) {
					break;
				}
				
				int idx =  headerLine.indexOf(" ");
				if	( idx >= 1 ) 
					requestHeader.put(headerLine.substring(0,idx),headerLine.substring(idx+1));
				
				//Log.d(TAG, "Header line: " + headerLine);
			}


			if (httpCommand.equals("GET")) {

				File file = new File(fileName);

				FileInputStream fis = null;
				boolean fileExists = true;
				boolean isDirectory = false;

				if (file.exists()) {
					if (file.isDirectory()) {
						file = new File(fileName + "/index.html");
						isDirectory = true;

						if (!file.exists()) {
							fileExists = false;
						} else {
							try {

								fis = new FileInputStream(file);

							} catch (FileNotFoundException e) {
								fileExists = false;
							}
						}
					} else {
						fis = new FileInputStream(file);
					}

				} else {
					fileExists = false;
				}

				if (fileExists && fis == null) {
					fis = new FileInputStream(file);
				}

				String serverLine = "Server: ServDroid server" + CRLF;
				String statusLine = null;
				String contentTypeLine = null;
				String entityBody = null;
				String contentLengthLine = null;
				String lastModifiedLine = null;
				String expiresLine = null;
				boolean notModified = false;
				
				if (fileExists) {
					String lastModified = DateFormat.format(HTTP_DATE_FORMAT,file.lastModified()).toString();
					lastModifiedLine = "Last-Modified: " + lastModified + CRLF;
					MessageDigest.getInstance("MD5");
					
					if	( requestHeader.containsKey(HTTP_HEADER_IF_MODIFIED) ) {
						if	( requestHeader.get(HTTP_HEADER_IF_MODIFIED).equals(lastModified)) {
							
							notModified = true;
							statusLine = "HTTP/1.0 304 Not Modified" + CRLF;
							contentLengthLine = "Content-Length: 0" + CRLF;
						}
					}
					
					if	( !notModified)
					{
						statusLine = "HTTP/1.0 200 OK" + CRLF;
						contentTypeLine = "Content-type: " + contentType(file.getName())
						+ CRLF;
						contentLengthLine = "Content-Length: "
							+ (new Integer(fis.available())).toString() + CRLF;
						
						if	( this.expires > 0) {
							expiresLine = "Expires: " + DateFormat.format(HTTP_DATE_FORMAT,System.currentTimeMillis()+(expires*60*1000)) + CRLF;
						}
					}
					
					mLogAdapter.addLog(("" + mSocket.getInetAddress()).replace(
							"/", ""), "GET " + fileGet, "", "");
					
				} else if (isDirectory && !fileExists && mFileIndexing) { // Indexing

					statusLine = "HTTP/1.0 200 OK" + CRLF;
					contentTypeLine = "Content-type: text/html" + CRLF;

					FileIndexing fi = new FileIndexing();
					entityBody = fi.getIndexing(fileName, fileGet);

					contentLengthLine = "Content-Length: "
							+ (entityBody.getBytes().length) + CRLF;
					mLogAdapter.addLog(("" + mSocket.getInetAddress()).replace(
							"/", ""), "GET " + fileGet, "", "File Indexig");

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
						mLogAdapter.addLog(("" + mSocket.getInetAddress())
								.replace("/", ""), "GET " + fileGet, "",
								"ERROR 404, File not found");

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
								"ERROR 404, File 404.html not found", "");
					}

				}

				// Send the status line.
				mOutput.write(statusLine.getBytes());

				// Send the server line.
				mOutput.write(serverLine.getBytes());

				if	( lastModifiedLine != null )
					mOutput.write( lastModifiedLine.getBytes() );
				
				if	( expiresLine != null )
					mOutput.write( expiresLine.getBytes() );
				
				// Send the content type line.
				if	( contentTypeLine != null )
					mOutput.write(contentTypeLine.getBytes());

				// Send the entity body.
				if (notModified) {
					// do nothing, the body is empty. The browser will now use its own cache.
				}
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
			} else {
				

				String statusLine = "HTTP/1.0 400 Bad Request" + CRLF;
				String contentTypeLine = "Content-type: text/html" + CRLF;
				String entityBody = "<HTML>"
						+ "<HEAD><title>Bad Request</title>"
						+ "</head><body> <div style=\"text-align: center;\">"
						+ "<big><big><big><span style=\"font-weight: bold;\">"
						+ "<br>ERROR 400: Bad request<br></span></big></big></big></div>"
						+ "</BODY></HTML>";
				String contentLengthLine = "Content-Length: "
						+ (entityBody.getBytes().length) + CRLF;
				
				// Send the status line.
				mOutput.write(statusLine.getBytes());
				mOutput.write(contentLengthLine.getBytes());
	
				// Send the content type line.
				mOutput.write(contentTypeLine.getBytes());
				
				// Send a blank line to indicate the end of the header
				// lines.
				mOutput.write(CRLF.getBytes());
				
				mOutput.write(entityBody.getBytes());
				
				mLogAdapter.addLog("", "",
						"ERROR 400 bad request", "Unkown Command: "+httpCommand);
			}


		} catch (Exception e) {
			Log.w(TAG, "Internal Server error",e);
			String statusLine = "HTTP/1.0 500 Internal Server Error" + CRLF;
			String contentTypeLine = "Content-type: text/html" + CRLF;
			String entityBody = "<HTML>"
					+ "<HEAD><title>Internal Server Error</title>"
					+ "</head><body> <div style=\"text-align: center;\">"
					+ "<big><big><big><span style=\"font-weight: bold;\">"
					+ "<br>ERROR 500: Internal Server Error<br></span></big></big></big></div>"
					+ "</BODY></HTML>";
			String contentLengthLine = "Content-Length: "
					+ (entityBody.getBytes().length) + CRLF;
			
			// Send the status line.
			mOutput.write(statusLine.getBytes());
			mOutput.write(contentLengthLine.getBytes());

			// Send the content type line.
			mOutput.write(contentTypeLine.getBytes());
			// Log.d(TAG, "Content type: " + contentTypeLine);

			// Log.d(TAG, "Content line: " + contentLengthLine);
			
			// Send a blank line to indicate the end of the header
			// lines.
			mOutput.write(CRLF.getBytes());
			
			mOutput.write(entityBody.getBytes());
			
			mLogAdapter.addLog("", "",
					"ERROR 503 internal server error", e.getMessage());
			Log.w(TAG, "Internal Server Error", e);
		}
		
		try {
			
			mOutput.close();
			mBr.close();
			mSocket.close();
		}
		catch(Exception e ) {
			
			Log.e(TAG, "ERROR closing socket", e);
		}
	}

	/**
	 * Send bytes for the request
	 * 
	 * @param fis
	 *            fileInputStream to send
	 * @param os
	 *            The output Stream to use for sending
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
	 * @return Content type
	 */
	private String contentType(String fileName) {
		
		String ext = "";
		int idx = fileName.lastIndexOf(".");
		if	( idx >= 0 )
		{
			ext = fileName.substring(idx+1);
		}
		
		if	( mimeTypes.containsKey(ext) )
			return mimeTypes.get(ext);
		else
			return "application/octet-stream";
		
	}
}