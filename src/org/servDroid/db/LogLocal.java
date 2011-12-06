package org.servDroid.db;

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
