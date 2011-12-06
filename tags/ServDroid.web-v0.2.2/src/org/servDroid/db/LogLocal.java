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

package org.servDroid.db;

/**
 * 
 * @author Joan Puig Sanz
 * 
 */
public class LogLocal {

	private String mIp, mPath, mInfoBegining, mInfoEnd;
	private long mTimeStamp;

	public String getLocalIp() {
		return mIp;
	}

	public void setLocalIp(String ip) {
		this.mIp = ip;
	}

	public String getLocalPath() {
		return mPath;
	}

	public void setLocalPath(String path) {
		this.mPath = path;
	}

	public String getLocalInfoBegining() {
		return mInfoBegining;
	}

	public void setLocalInfoBegining(String infoBegining) {
		this.mInfoBegining = infoBegining;
	}

	public String getLocalInfoEnd() {
		return mInfoEnd;
	}

	public void setLocalInfoEnd(String infoEnd) {
		this.mInfoEnd = infoEnd;
	}

	public long getLocalTimeStamp() {
		return mTimeStamp;
	}

	public void setLocalTimeStamp(long timeStamp) {
		this.mTimeStamp = timeStamp;
	}

}
