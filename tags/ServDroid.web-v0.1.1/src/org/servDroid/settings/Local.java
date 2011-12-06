package org.servDroid.settings;

public class Local {  
	
    private int mLocalName;
    private String mLocalSummary;
    private int mLocalImage;    
   
    public int getLocalName() {
        return mLocalName;
    }
    public void setLocalName(int localName) {
        this.mLocalName = localName;
    }
    public String getLocalSummary() {
        return mLocalSummary;
    }
    public void setLocalSummary(String localSummary) {
        this.mLocalSummary = localSummary;
    }
	public int getLocalImage() {
		return mLocalImage;
	}
	public void setLocalImage(int i) {
		this.mLocalImage = i;
	}

}



