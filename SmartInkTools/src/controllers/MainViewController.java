package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.FileDetails;
import models.Point;
import models.ReadData;
import models.Stroke;
import models.StrokeTableData;

/** Controls the main application screen */
public class MainViewController {
  
  @FXML private Button logoutButton;
  @FXML private Button rotateButton;
  
  @FXML private Label  sessionLabel;
  @FXML private MenuBar fileMenuBar;
  @FXML private Pane drawPane;
  
  //table variables
  @FXML private TableView<StrokeTableData> strokeTable;
  @FXML private TableColumn<StrokeTableData, String> strokeColumn;
  @FXML private TableColumn<StrokeTableData, String> labelColumn;
  @FXML private TableColumn<StrokeTableData, String> timeColumn;
  @FXML private TableColumn<StrokeTableData, String> distanceColumn;

  private ObservableList<String> labelPicker;
  
  static LinkedList<Stroke> finalStrokeData;
  
  private static File loadFile;
  private int strokeCount;
  private double rotationDegs = 90.0;
  
  private Stroke currentHighlight;
  
  
  public void initSessionID(final LoginManager loginManager, String sessionID) {
    
	 sessionLabel.setText(sessionID);
	
     logoutButton.setOnAction(e->loginManager.logout()); 
     
   //rotate button
     rotateButton.setOnAction((e) -> {
    	 drawPane.setRotate(rotationDegs);
    	 if (rotationDegs < 360)
         {
             rotationDegs += 90;
         }
         else{
             rotationDegs = 90;
         }
 			}); // end rotate
 	
  }
  
  //file chooser
  @FXML
  public void pickAFile(ActionEvent event) {
	  Stage stage = (Stage) fileMenuBar.getScene().getWindow();
	  FileChooser fileChooser = new FileChooser();
	  fileChooser.setTitle("Open Resource File");
	  FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
      fileChooser.getExtensionFilters().add(extFilter);
      File file = fileChooser.showOpenDialog(stage);
      
      System.out.println(file);
      setLoadFile(file);
      getData(file);
  }
  
  
  @FXML
  public void closeApp(ActionEvent event) {
	  Platform.exit();
	  System.exit(0);
  }
  
	public static File getLoadFile() {
		return loadFile;
	}
	
	public static void setLoadFile(File loadFile) {
		MainViewController.loadFile = loadFile;
	}
	
	public void getData(File file) {
		String path = file.getAbsolutePath();
		FileDetails fileDetails = new FileDetails();
		fileDetails.setFileName(path);
		try {
			
			
			LinkedList<Stroke> data = ReadData.getData(path);
			drawData(data); 
			populateTableView(data);
			showSelection(data);
//						
//			for (Stroke s : data)
//			{
//				System.out.println(s.toString());
//				for (Point p : s.getPointsSet())
//				{
//					System.out.println(p.toString());
//					
//				}
//			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	 
	
	public void drawData(LinkedList<Stroke> data) {

		ArrayList<Double> xMaxMinList = new ArrayList<>();
        ArrayList<Double> yMaxMinList = new ArrayList<>();

        for (int i = 0; i < data.size(); i++) {

            Stroke single_stroke = data.get(i);
            TreeSet<Point> pointSet = single_stroke.getPointsSet();
            Iterator<Point> point = pointSet.iterator();

            ArrayList<Double> xPointList = new ArrayList<>();
            ArrayList<Double> yPointList = new ArrayList<>();
            int pointCount = 0;
            while (point.hasNext()) {
                Point value = point.next();
                xPointList.add(pointCount, value.getX());
                xMaxMinList.add(value.getX());
                yPointList.add(pointCount, value.getY());
                yMaxMinList.add(value.getY());
                pointCount++;

            }
            
            for (int p = 0; p < xPointList.size() - 1; p++) {
            	//this produces a mirrored image
            	//Line strokeLine = new Line(xPointList.get(p), yPointList.get(p), xPointList.get(p+1), yPointList.get(p+1));
            	
            	/* Coordinate of the ink on paper shows a mirrored image of 
            	due to the coordinates of page so the x and y must be transposed
            	*/
            	Line strokeLine = new Line(yPointList.get(p), xPointList.get(p), yPointList.get(p+1), xPointList.get(p+1));
                
            	strokeLine.setStroke(Color.BLUE);
            	drawPane.getChildren().add(strokeLine);
            }
        }
		
	}
	
	public void populateTableView(LinkedList<Stroke> data) {
		labelPicker = FXCollections.observableArrayList();
		
		//set up combo box to set label
		labelPicker.add("Sentence");
		labelPicker.add("Pentagon");
		labelPicker.add("RA Data");
		labelPicker.add("Other Data");
			
		strokeCount = 0;
		final ObservableList<StrokeTableData> strokeList = FXCollections.observableArrayList();
		
		for (Stroke s : data)
		{	
			strokeCount++;
			strokeList.add(new StrokeTableData(s.getLabel().toString(), "<Click to set>", Double.toString(s.getStrokeDuration()), Double.toString(s.getDistance())));
		}
		strokeTable.setItems(strokeList);
		
		strokeTable.getSelectionModel().setSelectionMode( //set the table to allow multiple selections
			    SelectionMode.MULTIPLE
			);
		final ComboBox<String> comboBox = new ComboBox<String>(labelPicker);
		labelColumn.setCellFactory(ComboBoxTableCell.forTableColumn(labelPicker)); 
		
		
	}
	
	// this is used to listen to table clicks
    public void showSelection(LinkedList<Stroke> data){
    	List<StrokeTableData> selected = strokeTable.getSelectionModel().getSelectedItems();
    	

    	strokeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
    	    if (newSelection != null) {
    	    	int rowSelected =  strokeTable.getSelectionModel().getSelectedIndex();
    	        System.out.println("ROW SELECTED:" + rowSelected);
    	        System.out.println(data.get(rowSelected).toString());
    	        
    	        
    	        //removed the highlight if it is there
    	        for (Stroke stroke : data) {
    	        	
    	        	if (stroke.equals(currentHighlight))
    	        	{
    	        		System.out.println("Remove highliht from" + stroke.toString());
    	        		ArrayList<Line> returnStrokes = drawStrokeLine(stroke);
    	        		for (Line l : returnStrokes) {
    	        			l.setStroke(Color.BLUE);
    	        			drawPane.getChildren().add(l);
    	        		}
    	        		
    	        		
    	        	}
                    stroke.setHighlighted(false);
                    
                }

                Stroke highlightedStroke =  data.get(rowSelected);
                currentHighlight = highlightedStroke;
                
                ArrayList<Line> strokeLines = drawStrokeLine(highlightedStroke);
                	
                for (Line line : strokeLines)
            	{
            		line.setStroke(Color.RED);
            		//line.setStrokeWidth(5);
            		drawPane.getChildren().add(line);
            	}
                
                
    	    }
    	});
    	System.out.println(selected);
        
    }
    
    public ArrayList<Line> drawStrokeLine(Stroke stroke) {
    	// draws an individual stroke
    	ArrayList<Line> strokeLineList = new ArrayList<Line>();
    	
    	ArrayList<Double> xMaxMinList = new ArrayList<>();
        ArrayList<Double> yMaxMinList = new ArrayList<>();
        
    	TreeSet<Point> pointSet = stroke.getPointsSet();
        Iterator<Point> point = pointSet.iterator();

        ArrayList<Double> xPointList = new ArrayList<>();
        ArrayList<Double> yPointList = new ArrayList<>();
        int pointCount = 0;
        while (point.hasNext()) {
            Point value = point.next();
            xPointList.add(pointCount, value.getX());
            xMaxMinList.add(value.getX());
            yPointList.add(pointCount, value.getY());
            yMaxMinList.add(value.getY());
            pointCount++;

        }
        
        for (int p = 0; p < xPointList.size() - 1; p++) {
        	//this produces a mirrored image
        	//Line strokeLine = new Line(xPointList.get(p), yPointList.get(p), xPointList.get(p+1), yPointList.get(p+1));
        	
        	/* Coordinate of the ink on paper shows a mirrored image of 
        	due to the coordinates of page so the x and y must be transposed
        	*/
        	Line strokeLine = new Line(yPointList.get(p), xPointList.get(p), yPointList.get(p+1), xPointList.get(p+1));
            //strokeLine.setStrokeWidth(10);
        	strokeLineList.add(strokeLine);
        }
		//return null;
        return strokeLineList;
    }
}