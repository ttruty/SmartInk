package models;

import java.io.FileNotFoundException;
import java.util.LinkedList;

public class  FileDetails {

	String fileName;
	String penID;
	int strokeCount;
	boolean fileFlag;
	boolean fileProcessed;
	String testingTime;	
	
	public void readData() {
        //LinkedList<Stroke> data = ReadData.getData(path);
		
	}
	
	public String getFileName() {
		return fileName;
	}



	public void setFileName(String fileName) {
		this.fileName = fileName;
	}



	public String getPenID() {
		return penID;
	}



	public void setPenID(String penID) {
		this.penID = penID;
	}



	public int getStrokeCount() {
		return strokeCount;
	}



	public void setStrokeCount(int strokeCount) {
		this.strokeCount = strokeCount;
	}



	public boolean isFileFlag() {
		return fileFlag;
	}



	public void setFileFlag(boolean fileFlag) {
		this.fileFlag = fileFlag;
	}



	public boolean isFileProcessed() {
		return fileProcessed;
	}



	public void setFileProcessed(boolean fileProcessed) {
		this.fileProcessed = fileProcessed;
	}



	public String getTestingTime() {
		return testingTime;
	}



	public void setTestingTime(String testingTime) {
		this.testingTime = testingTime;
	}
}
