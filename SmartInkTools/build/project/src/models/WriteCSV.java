package models;

import org.apache.commons.io.FilenameUtils;
import java.io.*;
import java.lang.String;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;


public class WriteCSV {
	
	//write file to csv for use with stats methods
	
    public void write(String user, String file, String ProjId, String sentenceText, String deviationsText,  ArrayList<Stroke> data) throws IOException {

        String filename = FilenameUtils.removeExtension(file) + ".csv"; // returns "txt"

        //Delimiter used in CSV file
        final String COMMA_DELIMITER = ",";
        final String NEW_LINE_SEPARATOR = "\n";

        //CSV file header
        final String FILE_HEADER = "StrokeNumber, Label, startTime, stopTime, Duration, Length, Distance, Samples, thinkTime, avgForce, strokeSpeed, BBCenterX, BBCenterY, MaxX, MaxY, MinX, MinY";
        System.out.println("Print to csv");

        //Sentence accumulators
        long inkTimeFull = 0;
        long thinkTimeFull = 0;

        int numSentStrokes = 0;
        long sentInkTime = 0;
        long sentThinkTime = 0;
        double sentTotalDist = 0;
        double sentFullForce = 0;
        
        int numPentStrokes = 0;
        long pentInkTime = 0;
        long pentThinkTime = 0;
        double pentTotalDist = 0;
        double pentFullForce = 0;

        FileWriter fileWriter = null;
        try {

            fileWriter = new FileWriter(filename);
            //Write the CSV file header
            fileWriter.append("PROJID");
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(ProjId);
            fileWriter.append(NEW_LINE_SEPARATOR);
            fileWriter.append("Filename");
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(file);
            fileWriter.append(NEW_LINE_SEPARATOR);
            fileWriter.append("Start Test TIme");
            fileWriter.append(COMMA_DELIMITER);
            Date time = Date.from(Instant.ofEpochMilli(data.get(0).getStartOfStroke()));
            fileWriter.append(time.toString());
            fileWriter.append(NEW_LINE_SEPARATOR);
            fileWriter.append("Scorer");
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(user);
            fileWriter.append(NEW_LINE_SEPARATOR);
            fileWriter.append("Protocol Deviations");
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(deviationsText);
            fileWriter.append(NEW_LINE_SEPARATOR);
            
            
            
            fileWriter.append(NEW_LINE_SEPARATOR);

            fileWriter.append(FILE_HEADER.toString());

            //Add a new line separator after the header

            fileWriter.append(NEW_LINE_SEPARATOR);

            //Write a new stroke object list to the CSV file
            long end = 0;
            int strokeCounter = 1; // start at 1 not index 
            int sentStrokeCounter = 1; //
            int pentStrokeCount = 1;
            
            for (Stroke stroke : data)
            {
            	//computed variables
            	switch(stroke.getLabel())
            	{
            		case "Sentence" :   
            			numSentStrokes++;
            			sentInkTime += (stroke.getStrokeDuration() * 1000);
            			sentTotalDist += stroke.getDistance();
            			sentThinkTime += calcThinkTime(stroke, end, strokeCounter);
            			sentFullForce += stroke.getAvgForce();
            			break;
            		case "Pentagon" :
            			numPentStrokes++;
            			pentInkTime += (stroke.getStrokeDuration() * 1000);
            			pentTotalDist += stroke.getDistance();
            			pentThinkTime += calcThinkTime(stroke, end, strokeCounter);
            			pentFullForce += stroke.getAvgForce();
            			break;
            	
            	}
            	
                long thinkTime = 0;
                fileWriter.append(String.valueOf(strokeCounter)); //stroke number
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(stroke.getLabel());
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(stroke.getStartOfStroke()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(stroke.getEndOfStroke()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(stroke.getStrokeDuration()));
                inkTimeFull += stroke.getStrokeDuration(); //get full ink time

                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(stroke.getLength()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(stroke.getDistance()));
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(stroke.getPointsSet().size()));
                fileWriter.append(COMMA_DELIMITER);
                if (strokeCounter == 1)
                {
                    fileWriter.append(String.valueOf(0));
                    end = stroke.getEndOfStroke();
                }
                else{
                    thinkTime = stroke.getStartOfStroke() - end;
                    fileWriter.append(String.valueOf(thinkTime));
                    //thinkTimeFull += thinkTime; //get thinkTime full
                    //TODO: think time sentence object field
                    end = stroke.getEndOfStroke();
                }
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(stroke.getAvgForce())); // FORCE
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf((stroke.getDistance()/stroke.getStrokeDuration()))); //SPEED
                
                //BBCenter, MaxX, MinX, MaxY, MinY
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(stroke.getBBCenter().getX())); // Bounding Box center X
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(stroke.getBBCenter().getY())); // Bounding Box center X
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(stroke.getMaxXCoordinate().getX())); // Max X
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(stroke.getMaxYCoordinate().getY())); // Max Y
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(stroke.getMinXCoordinate().getX())); // Min X
                fileWriter.append(COMMA_DELIMITER);
                fileWriter.append(String.valueOf(stroke.getMinYCoordinate().getY())); // Min Y
                fileWriter.append(COMMA_DELIMITER);
                
                fileWriter.append(NEW_LINE_SEPARATOR);
            strokeCounter++;   
            }            
            fileWriter.append(NEW_LINE_SEPARATOR);
            fileWriter.append(NEW_LINE_SEPARATOR);
            
            fileWriter.append("Sentence Text:");
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(sentenceText);
            fileWriter.append(NEW_LINE_SEPARATOR);
            
            fileWriter.append("Words:");
            String wordList = sentenceText.replaceAll(" ",","); // or "\\.", it doesn't matter...
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(wordList);
            fileWriter.append(NEW_LINE_SEPARATOR);
            
            fileWriter.append(NEW_LINE_SEPARATOR);
            fileWriter.append(NEW_LINE_SEPARATOR);
            
            fileWriter.append("sentenceStrokeNums");
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(String.valueOf(numSentStrokes));
            fileWriter.append(NEW_LINE_SEPARATOR);
            
            fileWriter.append("sentenceInkTime");
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(String.valueOf(sentInkTime));
            fileWriter.append(NEW_LINE_SEPARATOR);
            
            fileWriter.append("sentenceThinkTime");
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(String.valueOf(sentThinkTime));
            fileWriter.append(NEW_LINE_SEPARATOR);
            
            fileWriter.append("sentenceTotalDist");
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(String.valueOf(sentTotalDist));
            fileWriter.append(NEW_LINE_SEPARATOR);
            
            fileWriter.append("sentenceAvgForce");
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(String.valueOf((sentFullForce / numSentStrokes)));
            fileWriter.append(NEW_LINE_SEPARATOR);
            
            fileWriter.append(NEW_LINE_SEPARATOR);
            
            fileWriter.append("pentagonStrokeNums");
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(String.valueOf(numPentStrokes));
            fileWriter.append(NEW_LINE_SEPARATOR);
            
            fileWriter.append("pentagonInkTime");
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(String.valueOf(pentInkTime));
            fileWriter.append(NEW_LINE_SEPARATOR);
            
            fileWriter.append("pentagonThinkTime");
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(String.valueOf(pentThinkTime));
            fileWriter.append(NEW_LINE_SEPARATOR);
            
            fileWriter.append("pentagonTotalDist");
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(String.valueOf(pentTotalDist));
            fileWriter.append(NEW_LINE_SEPARATOR);
            
            fileWriter.append("pentagonAvgForce");
            fileWriter.append(COMMA_DELIMITER);
            fileWriter.append(String.valueOf((pentFullForce / numPentStrokes)));
            fileWriter.append(NEW_LINE_SEPARATOR);
            
            

            System.out.println("CSV file was created successfully !!!");
        } catch (Exception e) {

            System.out.println("Error in CsvFileWriter !!!");

            e.printStackTrace();

        } finally {

            try {

                fileWriter.flush();

                fileWriter.close();

            } catch (IOException e) {

                System.out.println("Error while flushing/closing fileWriter !!!");

                e.printStackTrace();

            }

        }

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
}