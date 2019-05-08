package models;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import javax.swing.filechooser.FileSystemView;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class WriteXLSX {
	private static String[] columns = { "StrokeNumber", "Label", "startTime", "stopTime", "Duration", "Length", "Distance", "Samples", "thinkTime", "avgForce",
			"strokeSpeed", "BBCenterX", "BBCenterY", "MaxX", "MaxY", "MinX", "MinY" };
	private static String[] segHeader = { "StrokeNumber", "TimeStamp", "XCoordinate", "YCoordinate", "Force" };
	private File FocusDirectory;
	
	@SuppressWarnings("resource")
	public void write(String user, String file, String ProjId, String FuYear, String sentenceText, String deviationsText,  ArrayList<Stroke> data) throws IOException {
		
		DateFormat saveXLSXFormat = new SimpleDateFormat( "yyyyMMdd_HHmmss" );
    	final long XLSXDate = System.currentTimeMillis();
    	String BaseFilename = ProjId + "_"+ FuYear + "_"+ saveXLSXFormat.format( XLSXDate );
    	
    	File fullFilePath = new File(file);
		File parentDir = fullFilePath.getParentFile(); // to get the parent dir 
		FocusDirectory = parentDir;
    	
    	String dirname = "/Scored";
    	String fullPath = FilenameUtils.getFullPathNoEndSeparator(file);
		String saveDir = parentDir.toString().concat(dirname);
    	
		File directory = new File(saveDir);
        if (! directory.exists()){
            directory.mkdir();
        }
    	
		String filename = FilenameUtils.removeExtension(file) + ".xlsx"; // returns "txt"	
		

        Workbook workbook = new XSSFWorkbook();
		
		Sheet scoreSheet = workbook.createSheet("Scored");
		Sheet sentSheet = workbook.createSheet("Sentence");
		Sheet pentSheet = workbook.createSheet("Pentagon");
		Sheet raSheet = workbook.createSheet("RaData");
		Sheet otherSheet = workbook.createSheet("Other");
		
		//inital metadata
		Row idRow = scoreSheet.createRow(0);
		idRow.createCell(0).setCellValue("ProjId:");
		idRow.createCell(1).setCellValue(ProjId);
		
		Row fileRow = scoreSheet.createRow(1);
		fileRow.createCell(0).setCellValue("Filename:");
		fileRow.createCell(1).setCellValue(file);
		
		Row testTimeRow = scoreSheet.createRow(2);
		testTimeRow.createCell(0).setCellValue("Test Time:");
		Date time = Date.from(Instant.ofEpochMilli(data.get(0).getStartOfStroke()));
		testTimeRow.createCell(1).setCellValue(time);
		
		Row userRow = scoreSheet.createRow(3);
		userRow.createCell(0).setCellValue("Scorer:");
		userRow.createCell(1).setCellValue(user);
		
		Row pdRow = scoreSheet.createRow(4);
		pdRow.createCell(0).setCellValue("Protocol Deviations:");
		pdRow.createCell(1).setCellValue(deviationsText);
		
	    // Create a Row
	    Row headerRow = scoreSheet.createRow(7);

	    for (int i = 0; i < columns.length; i++) {
	      Cell cell = headerRow.createCell(i);
	      cell.setCellValue(columns[i]);
	    }

	    // Create Other rows and cells with contacts data
	    int rowNum = 8;
	    int sentRow = 0;
	    int pentRow = 0;
	    int raRow = 0;
	    int otherRow = 0;
	    
	  //Write a new stroke object list to the CSV file
        long end = 0;
        int strokeCounter = 1; // start at 1 not index 
        int sentStrokeCounter = 1; //
        int pentStrokeCount = 1;
        
        Row headerRowS = sentSheet.createRow(sentRow++);
        Row headerRowP = pentSheet.createRow(pentRow++);
        Row headerRowR = raSheet.createRow(raRow++);
        Row headerRowO = otherSheet.createRow(otherRow++);

	    for (int i = 0; i < segHeader.length; i++) {
	      Cell cellS = headerRowS.createCell(i);
	      Cell cellP = headerRowP.createCell(i);
	      Cell cellR = headerRowR.createCell(i);
	      Cell cellO = headerRowO.createCell(i);
	      cellS.setCellValue(segHeader[i]);
	      cellP.setCellValue(segHeader[i]);
	      cellR.setCellValue(segHeader[i]);
	      cellO.setCellValue(segHeader[i]);
	    }
		
        
        

	    for (Stroke stroke : data) {
	    	//computed variables
        	switch(stroke.getLabel())
        	{
        		case "Sentence" :           					
        			TreeSet<Point> pointList = stroke.getPointsSet();
        			
        			for (Point point : pointList) {
        				Row rows = sentSheet.createRow(sentRow++);        	
        				rows.createCell(0).setCellValue(strokeCounter);
        				rows.createCell(1).setCellValue(point.getTimeStamp());
        				rows.createCell(2).setCellValue(point.getX());
        				rows.createCell(3).setCellValue(point.getY());
        				rows.createCell(4).setCellValue(point.getForce());
        			}        			
        			break;
        		case "Pentagon" :
        			TreeSet<Point> pointListP = stroke.getPointsSet();
        			
        			for (Point point : pointListP) {
        				Row rowp = pentSheet.createRow(pentRow++);
        				rowp.createCell(0).setCellValue(strokeCounter);
        				rowp.createCell(1).setCellValue(point.getTimeStamp());
        				rowp.createCell(2).setCellValue(point.getX());
        				rowp.createCell(3).setCellValue(point.getY());
        				rowp.createCell(4).setCellValue(point.getForce());
        			}
        			break;
        		case "RA Data" :
        			TreeSet<Point> pointListR = stroke.getPointsSet();
        			
        			for (Point point : pointListR) {
        				Row rowr = raSheet.createRow(raRow++);
        				rowr.createCell(0).setCellValue(strokeCounter);
        				rowr.createCell(1).setCellValue(point.getTimeStamp());
        				rowr.createCell(2).setCellValue(point.getX());
        				rowr.createCell(3).setCellValue(point.getY());
        				rowr.createCell(4).setCellValue(point.getForce());
        			}
        			break; 
        		case "Other Data" :
        			TreeSet<Point> pointListO = stroke.getPointsSet();
        			
        			for (Point point : pointListO) {
        				Row rowo = otherSheet.createRow(otherRow++);
        				rowo.createCell(0).setCellValue(strokeCounter);
        				rowo.createCell(1).setCellValue(point.getTimeStamp());
        				rowo.createCell(2).setCellValue(point.getX());
        				rowo.createCell(3).setCellValue(point.getY());
        				rowo.createCell(4).setCellValue(point.getForce());
        			}
        			break;
        	}
        	long thinkTime = 0;
        	
	      Row row = scoreSheet.createRow(rowNum++);
	      row.createCell(0).setCellValue(strokeCounter);
	      row.createCell(1).setCellValue(stroke.getLabel());
	      row.createCell(2).setCellValue(stroke.getStartOfStroke());
	      row.createCell(3).setCellValue(stroke.getEndOfStroke());
	      row.createCell(4).setCellValue(stroke.getStrokeDuration());
	      row.createCell(5).setCellValue(stroke.getLength());
	      row.createCell(6).setCellValue(stroke.getDistance());
	      row.createCell(7).setCellValue(stroke.getPointsSet().size());
	      if (strokeCounter == 1)
          {
	    	  row.createCell(8).setCellValue(0);
              end = stroke.getEndOfStroke();
          }
          else{
              thinkTime = stroke.getStartOfStroke() - end;
              row.createCell(8).setCellValue(thinkTime);
              //thinkTimeFull += thinkTime; //get thinkTime full
              //TODO: think time sentence object field
              end = stroke.getEndOfStroke();
          }
	      
	      row.createCell(9).setCellValue(stroke.getAvgForce());
	      row.createCell(10).setCellValue((stroke.getDistance()/stroke.getStrokeDuration()));
	      row.createCell(11).setCellValue(stroke.getBBCenter().getX());
	      row.createCell(12).setCellValue(stroke.getBBCenter().getY());
	      row.createCell(13).setCellValue(stroke.getMaxXCoordinate().getX());
	      row.createCell(14).setCellValue(stroke.getMaxYCoordinate().getY());
	      row.createCell(15).setCellValue(stroke.getMinXCoordinate().getX());
	      row.createCell(16).setCellValue(stroke.getMinYCoordinate().getY());
	      
	      strokeCounter++;
	    }
	    
	    //sentence content
	    rowNum += 2;
	    Row row = scoreSheet.createRow(rowNum++);
	    row.createCell(0).setCellValue("Sentence:");
	    row.createCell(1).setCellValue(sentenceText);
	    
	    row = scoreSheet.createRow(rowNum++);
	    int cellCount = 0;
	    row.createCell(cellCount).setCellValue("Words:");
	    List<String> wordList = Arrays.asList(sentenceText.split("\\s"));
	    for (String word : wordList) {
	    	row.createCell(++cellCount).setCellValue(word);
	    }
	    
	    row = scoreSheet.createRow(rowNum++);
	    cellCount = 0;
	    row.createCell(cellCount).setCellValue("Letters:");
	    wordList = Arrays.asList(sentenceText.split("\\s*"));
	    for (String word : wordList) {
	    	row.createCell(++cellCount).setCellValue(word);
	    }
	    
	    // Write the output to a file
	    FileOutputStream fileOut = new FileOutputStream(saveDir + "/" + BaseFilename + ".xlsx" );
	    workbook.write(fileOut);
	    fileOut.close();
	    MovingFile(fullFilePath, parentDir, BaseFilename + "_SCORED.txt");
	}
	
	private double calcThinkTime(Stroke stroke, long end, int strokeCounter)
    {
    	double thinkTime = 0;
    	if (strokeCounter == 1)
        {
            end = stroke.getEndOfStroke();
        }
        else{
            thinkTime += stroke.getStartOfStroke() - end;
            end = stroke.getEndOfStroke();
        }
    	return thinkTime;
    }
	
	private void MovingFile(File fileFullPath, File parentDir, String saveName) throws IOException {
		String dirname = "/RAW_MMSE";
		String dir = parentDir.toString().concat(dirname);
    	File directory = new File(dir);
        if (! directory.exists()){
            directory.mkdir();
        }
	    FileUtils.moveFile(
	      FileUtils.getFile(fileFullPath), 
	      FileUtils.getFile(dir + "/" + saveName));
	}
	
	public File getParentDirectory() {
		return FocusDirectory;
		}
	
	
}
