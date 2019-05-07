package models;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class ReadData {
	//(String fileName, String penID, int strokeCount, boolean fileFlag, boolean fileProcessed,
	//		String testingTime) 
    private static String penSerialNumber;
    private static String penFileName;
    private static String testTime;
    private static boolean fileFlag;
    
   	static String pattern = "\\d+\\.\\d+\\s\\d+\\.\\d+\\s\\d+\\s\\d+";
    
   	public ReadData() {}
        
    public static StringBuffer getDataFromFile(String filename)
    {
    	//initalize penFile data to null
        BufferedReader penFile = null;
        penFileName = filename;
        
        try {
        	// load penFile data into buffer reader
            penFile = new BufferedReader(new FileReader(filename));
        }
        
        // file not found error 
        catch (FileNotFoundException e) {
        	//TODO: ERROR LOGGING
            e.printStackTrace();
            
        }

        // new string buffer to hold data
        StringBuffer data = new StringBuffer();
        try
        {
        	//read the first line to make sure file is correct
        	if (!penFile.readLine().startsWith("Pen id:")) {
        		setFileFlag(true);        		
        	} else {
        		setFileFlag(false);
        	}
        	//read each line in the pen file        	
            for (String line = penFile.readLine(); line != null; line = penFile.readLine()) {
            	
            	
            	
            	// tokenize line by whitespace chars
                String[] st = line.split("\\s");

                //Array to hold each token
               	ArrayList<String> lineTokens = new ArrayList<String>();

               	//add token to array
                for (int i = 0; i < st.length; i++) {
                    String token = new String();
                    token = st[i];
                    lineTokens.add(token);
                }
               
                //add each line to the data string buffer
                data.append(line + "\n");   
            }
            // close file reader
            penFile.close();
        }
        catch (IOException e1) {
        	//TODO: ERROR LOGGING
            e1.printStackTrace();
        }
        return data;
    }
    
    
	public static LinkedList<Stroke> getData(String filename)
            throws FileNotFoundException
    {
        BufferedReader penFile = null;
        penFile = new BufferedReader(new FileReader(filename));
        LinkedList<Point> points = new LinkedList<Point>();
        LinkedList<Stroke> strokes = new LinkedList<Stroke>();
        String thisStrokeID = "";
        String nextStrokeID = "";
        long strokeStartTime = 0;
        int strokeCounter = 1;
        boolean strokeIDEndsPoints = false;
        
        
        try {
        	// read first line to see if correct file
        	//read the first line to make sure file is correct
        	if (!penFile.readLine().startsWith("Pen id:")) {
        		setFileFlag(true);        		
        	} else {
        		setFileFlag(false);
        	}
        	//tokenize the lines
        	
            for (String line = penFile.readLine(); line != null; line = penFile.readLine()) {
                String[] st = line.split("\\s");
                if (st.length == 0) {
                    try {
                        line = penFile.readLine();
                    } catch (IOException e1) {
                    	//TODO: ERROR LOGGING
                        e1.printStackTrace();
                    }
                }
                
                else {     
                	
                	// Create a Pattern object
                    Pattern r = Pattern.compile(pattern);

                    // Now create matcher object.
                    Matcher m = r.matcher(line);
                	
                	//Array to hold each token               	
                    ArrayList<String> lineTokens = new ArrayList<String>();
                    for (int i = 0; i < st.length; i++) {
                        String token = new String();
                        token = st[i];
                        
                        
                        lineTokens.add(token);
                    }

                    
                    
                    // set the pen id
                    if (line.startsWith("Pen id: ")) {
                        penSerialNumber = line.substring(line.lastIndexOf(":") + 2);
                    }

                    // if the line starts with the Stroke ID:
                    else if (lineTokens.get(0).endsWith("StrokeID:"))
                    {
                        if (!strokeIDEndsPoints)
                        {
                            strokeIDEndsPoints = true;
                            thisStrokeID = (String)lineTokens.get(1);
                        }
                        
                        //end point of stroke
                        else if (points.size() != 0)
                        {
                        	//System.out.println("points " + points.size());
                        	//System.out.println("counter " + strokeCounter);
                        	
                            InkStroke stroke = new InkStroke(points, strokeCounter);
                            nextStrokeID = (String)lineTokens.get(1);
                            if (thisStrokeID.equals(""))
                            {

                            	//Error with file
                                //DebugUtilities.FatalError("Bad clock file", "File is badly formatted. Please report this clock.");
                            }


                            strokeCounter++;
                            stroke.setLabel(thisStrokeID);
                            strokes.add(stroke);

                            thisStrokeID = nextStrokeID;
                            strokeStartTime = -1L;
                            points = new LinkedList<Point>();
                        }

                    }
                    else if ((lineTokens.size() == 2) &&
                            (((String)lineTokens.get(0)).equals("StartTime:"))) {
                        double timeDouble = Double.parseDouble((String)lineTokens.get(1));
                        timeDouble *= 1000.0D; // to get seconds

                        strokeStartTime = (long)timeDouble;

                    }               
                 
                    // use the sample lines with regualr expression
                    else if (m.find())                   
                    {
                    	try {                    	
                        int timeDelta = Integer.parseInt((String)lineTokens.get(2));
                        InkPoint point = new InkPoint(Double.parseDouble((String)lineTokens.get(1)), 
                        							  Double.parseDouble((String)lineTokens.get(0)), 
                        							  timeDelta + strokeStartTime,
                        							  Integer.parseInt((String)lineTokens.get(3)));
                        strokeStartTime += timeDelta;
                        points.add(point);
                    	}
                    	catch (NumberFormatException e)
                    	{
                    		System.out.println(e);
                    	}
                    }
                    
                    //System.out.println(points.size());
                    //System.out.println(points);
                }
            }
            penFile.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return strokes;
    }

	public static boolean isFileFlag() {
		return fileFlag;
	}

	public static void setFileFlag(boolean fileFlag) {
		ReadData.fileFlag = fileFlag;
	}

}