package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Cluster;
import models.ClusterTableData;
import models.DBScan;
import models.FileDetails;
import models.Point;
import models.ReadData;
import models.Stroke;
import models.StrokeTableData;

/** Controls the main application screen */
public class MainViewController {
  
  @FXML private Button logoutButton;
  @FXML private Button rotateButton;
  @FXML private Button clusterButton;
  
  @FXML private Label  sessionLabel;
  @FXML private MenuBar fileMenuBar;
  @FXML private Pane drawPane;
  
  //table variables
  @FXML private TableView<StrokeTableData> strokeTable;
  @FXML private TableColumn<StrokeTableData, String> strokeColumn;
  @FXML private TableColumn<StrokeTableData, String> labelColumn;
  @FXML private TableColumn<StrokeTableData, String> timeColumn;
  @FXML private TableColumn<StrokeTableData, String> distanceColumn;
  
  @FXML private TableView<ClusterTableData> clusterTable;
  @FXML private TableColumn<ClusterTableData, String> clusterIndexColumn;
  @FXML private TableColumn<ClusterTableData, String> clusterLabelColumn;
  @FXML private TableColumn<ClusterTableData, String> clusterCountColumn;
  
  @FXML private Slider clusterDistSlider;
  @FXML private Slider clusterNeighborSlider;
  
  @FXML private Label clusterDistLabel;
  @FXML private Label clusterMinNeighborLabel;
  
  @FXML private Tab strokeTab;
  @FXML private Tab clusterTab;

  private ObservableList<String> labelPicker;
  
  private LinkedList<Stroke> finalStrokeData;

  private static File loadFile;
  private int strokeCount;
  private int clusterCount;
  private double rotationDegs = 90.0;
  
  private Stroke currentHighlight;
  
  private Property<StrokeTableData> stokeProperty;
  private Property<ClusterTableData> clusterProperty;
  private ChangeListener<StrokeTableData> strokeListener;
  private ChangeListener<ClusterTableData> clusterListener;
  private Stroke highlightedStroke;


  
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
     
    
     Group clusterGroup = new Group();
     clusterButton.setOnAction((e) -> {  
    	 AnchorPane p = new AnchorPane();
    	 clusterTable.getItems().clear();
 		 clusterTable.refresh();
    	 System.out.println("CLUSTER GROUP: " + clusterGroup.getChildren().size());
    	 p.getChildren().removeAll(clusterGroup);
    	 clusterGroup.getChildren().clear();
    	 
    	 Cluster.clearData(); //clear the bounding box list
    	 Cluster.clusterData(this.getFinalStrokeData());
    	 
    	 //remove the rectangles in the drawpane with each press to redraw
    	 //Node drawNode = (Node) p.getChildren().get(0);
    	 //if (drawNode instanceof Rectangle)
    	 //{
    	//	 p.getChildren().remove(drawNode);
    	// }
    	 //reset a pane to be able to press button again on frame
    	 ArrayList<Rectangle> clusterBoxes = Cluster.boundClusterBox();
    	 
    	 for (Rectangle rect : clusterBoxes) {   		 
    		 rect.setStroke(Color.RED);
    		 rect.setFill(Color.TRANSPARENT);
    		 clusterGroup.getChildren().add(rect);
    		 
         }
    	 
    	 p.getChildren().add(clusterGroup);  
    	 drawPane.getChildren().add(p);
    	 
    	 populateClusterTableView(this.getFinalStrokeData());
    	 //showSelection(this.getFinalStrokeData());
     });
     
     //Sliders for cluster variables
     clusterDistSlider.valueProperty().addListener(new ChangeListener<Number>() {
         @Override
         public void changed(ObservableValue<? extends Number> arg0,Number arg1, Number arg2) {
             clusterDistLabel.textProperty().setValue("Min Dist: " + String.valueOf((int) clusterDistSlider.getValue()));
             DBScan.minDist = (int) clusterDistSlider.getValue();
             }
     });
     
     clusterNeighborSlider.valueProperty().addListener(new ChangeListener<Number>() {
         @Override
         public void changed(ObservableValue<? extends Number> arg0,Number arg1, Number arg2) {
        	 clusterMinNeighborLabel.textProperty().setValue("Min Neighbs: " + String.valueOf((int) clusterNeighborSlider.getValue()));
        	 DBScan.minPt = (int) clusterNeighborSlider.getValue();
             }
     });
     
     // select stroke tab
     strokeTab.setOnSelectionChanged(new EventHandler<Event>() {
         @Override
         public void handle(Event t) {
             if (strokeTab.isSelected()) {
            	 
         		//add listeners		
             	stopListeningToSelect(); // stop listener to ensure not more than one on obj
             	strokeTable.getSelectionModel().clearSelection();
             	clusterTable.getSelectionModel().clearSelection();
             	startListeningToTableSelect(stokeProperty, strokeTable);
             }
         }
     });
     
     //select cluster tab
     clusterTab.setOnSelectionChanged(new EventHandler<Event>() {
         @Override
         public void handle(Event t) {
             if (clusterTab.isSelected()) {
         		//add listeners		
             	stopListeningToSelect(); // stop listener to ensure not more than one on obj
             	strokeTable.getSelectionModel().clearSelection();
             	clusterTable.getSelectionModel().clearSelection();
             	startListeningToClusterSelect(clusterProperty, clusterTable);
             }
         }
     });
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
  
	public File getLoadFile() {
		return loadFile;
	}
	
	public void setLoadFile(File loadFile) {
		// clear variables on new load
		if (!(this.finalStrokeData == null))
		{
			this.finalStrokeData.clear();
		}
		
		drawPane.getChildren().clear();
		
		MainViewController.loadFile = loadFile;

	}
	
	public void getData(File file) {
		String path = file.getAbsolutePath();
		FileDetails fileDetails = new FileDetails();
		fileDetails.setFileName(path);
		try {			
			LinkedList<Stroke> data = new LinkedList<Stroke>();
			data = ReadData.getData(path);
			this.setFinalStrokeData(data);
			
			drawData(data); 
			populateTableView(data);
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
		//clear the table each run
		strokeTable.getItems().clear();
		strokeTable.refresh();
		
		labelPicker = FXCollections.observableArrayList();
		
		//set up combo box to set label
		labelPicker.add("Sentence");
		labelPicker.add("Pentagon");
		labelPicker.add("RA Data");
		labelPicker.add("Other Data");
			
		strokeCount = 0;
		// reset to null
		ObservableList<StrokeTableData> strokeList = null;
		
		strokeList = FXCollections.observableArrayList();
		
		for (Stroke s : data)
		{	
			strokeCount++;
			//round the distance to 2 places
			double distance = new BigDecimal(s.getDistance()).setScale(2, RoundingMode.HALF_UP).doubleValue();
			strokeList.add(new StrokeTableData(s.getLabel().toString(), "<Click to set>", Double.toString(s.getStrokeDuration()), Double.toString(distance)));
		}
		strokeTable.setItems(strokeList);
		
		strokeTable.getSelectionModel().setSelectionMode( //set the table to allow multiple selections
			    SelectionMode.MULTIPLE
			);
		labelColumn.setCellFactory(ComboBoxTableCell.forTableColumn(labelPicker)); 
	}
	
	public void populateClusterTableView(LinkedList<Stroke> data) {
		//clear the table each run
		//stopListeningToSelect();
		clusterTable.getItems().clear();
		clusterTable.refresh();
		
		labelPicker = FXCollections.observableArrayList();
		
		//set up combo box to set label
		labelPicker.add("Sentence");
		labelPicker.add("Pentagon");
		labelPicker.add("RA Data");
		labelPicker.add("Other Data");
			
		clusterCount = 0;
		// reset to null
		ObservableList<ClusterTableData> clusterList = null;
		
		clusterList = FXCollections.observableArrayList();
		
		for (Stroke s : data)
		{	
			clusterCount++;
			//round the distance to 2 places
			//double distance = new BigDecimal(s.getDistance()).setScale(2, RoundingMode.HALF_UP).doubleValue();
			clusterList.add(new ClusterTableData(s.getLabel().toString(), "<Click to set>", Integer.toString(s.getStrokCluster() ) ) );
		}
		clusterTable.setItems(clusterList);
		
		clusterTable.getSelectionModel().setSelectionMode( //set the table to allow multiple selections
			    SelectionMode.MULTIPLE
			);
		labelColumn.setCellFactory(ComboBoxTableCell.forTableColumn(labelPicker)); 
		
		//add listeners		
//		stopListeningToSelect(); // stop listener to ensure not more than one on obj
//    	strokeTable.getSelectionModel().clearSelection();
//    	clusterTable.getSelectionModel().clearSelection();    	
//    	startListeningToClusterSelect(clusterProperty, clusterTable);
	}
	
	// this is used to listen to table clicks
    public void showSelection(LinkedList<Stroke> data){  
    	//need to stop listener first so if a new tableview is loaded it will not hav
    	// multiple listenres
    	stopListeningToSelect();
    	//strokeTable.getSelectionModel().clearSelection();
    	startListeningToTableSelect(stokeProperty, clusterTable);

    }
    
    //listern method to make selection
    private void startListeningToTableSelect(Property<?> name, TableView<?> table) {
    	// THIS TOOK SOME SERIOUS DEBUGGING
    	// STILL NOT SURE IT IS THE CORRECT WAY TO GO ABOUT HIGHLIGHTING
    	// BUT IT WORKS AND DOES NOT HAVE THE SERIOUS LAG FROM BEFORE
    	stopListeningToSelect();
    	//stokeProperty = name;
    	strokeListener = (obs, oldValue, newValue) -> {        	
    		if (newValue != null) {        			
    			Group redLineGroup = new Group();
    	    	Group blueLineGroup = new Group();
    			
    			int rowSelected =  table.getSelectionModel().getSelectedIndex();
    			//System.out.println("ROW SELECTED:" + rowSelected);
    			LinkedList<Stroke> data = this.getFinalStrokeData();
    			System.out.println(data.get(rowSelected).toString());
    			data.get(rowSelected).setHighlighted(true);
    			
    			
    			//clear stroke data from pane
    			for (Stroke stroke : data) {         	
    				
    				if (stroke.isHighlighted()) {
    					
    					ArrayList<Line> strokeLines = drawStrokeLine(stroke);
	                    for (Line line : strokeLines)
	                	{
	                    	//drawPane.getChildren().remove(line);
	                		line.setStroke(Color.RED);
	                		line.setStrokeWidth(4);
	                		redLineGroup.getChildren().add(line);               		
	                	}
	                    if (!strokeLines.isEmpty()) {
	                    	drawPane.getChildren().remove(redLineGroup);
	                    	drawPane.getChildren().add(redLineGroup);
	                    }
    				}
    				else {
    					ArrayList<Line> returnStrokes = drawStrokeLine(stroke);
    					for (Line l : returnStrokes)
    	        		{
	                    	//drawPane.getChildren().remove(l);

    						//drawPane.getChildren().remove(l);
    	        			l.setStroke(Color.BLUE);
    	        			blueLineGroup.getChildren().add(l);  	        			           		
    	        		}
    					if (!returnStrokes.isEmpty()) {
    						drawPane.getChildren().remove(blueLineGroup);
    						drawPane.getChildren().add(blueLineGroup);	
    					}
    				}
                    stroke.setHighlighted(false);
                    highlightedStroke =  data.get(rowSelected);
                    currentHighlight = highlightedStroke;
    			}
    		}
    	};    	
    	strokeTable.getSelectionModel().selectedItemProperty().addListener(strokeListener);
    }
    
    //listern method to make selection
    private void startListeningToClusterSelect(Property<?> name, TableView<?> table) {
    	// THIS TOOK SOME SERIOUS DEBUGGING
    	// STILL NOT SURE IT IS THE CORRECT WAY TO GO ABOUT HIGHLIGHTING
    	// BUT IT WORKS AND DOES NOT HAVE THE SERIOUS LAG FROM BEFORE
    	stopListeningToSelect();
    	//stokeProperty = name;
    	clusterListener = (obs, oldValue, newValue) -> {        	
    		if (newValue != null) {        			
    			Group redLineGroup = new Group();
    	    	Group blueLineGroup = new Group();
    			drawPane.getChildren().clear(); // this clears out the pane- even the cluster
    		    			
    			int rowSelected =  table.getSelectionModel().getSelectedIndex();
    			//System.out.println("ROW SELECTED:" + rowSelected);
    			LinkedList<Stroke> data = this.getFinalStrokeData();
    			System.out.println(data.get(rowSelected).toString());
    			data.get(rowSelected).setHighlighted(true);
    			
    			
    			//clear stroke data from pane
    			for (Stroke stroke : data) {         	
    				
    				if (stroke.isHighlighted()) {
    					
    					ArrayList<Line> strokeLines = drawStrokeLine(stroke);
	                    for (Line line : strokeLines)
	                	{
	                    	//drawPane.getChildren().remove(line);
	                		line.setStroke(Color.RED);
	                		line.setStrokeWidth(4);
	                		redLineGroup.getChildren().add(line);               		
	                	}
	                    if (!strokeLines.isEmpty()) {
	                    	drawPane.getChildren().remove(redLineGroup);
	                    	drawPane.getChildren().add(redLineGroup);
	                    }
    				}
    				else {
    					ArrayList<Line> returnStrokes = drawStrokeLine(stroke);
    					for (Line l : returnStrokes)
    	        		{
	                    	//drawPane.getChildren().remove(l);

    						//drawPane.getChildren().remove(l);
    	        			l.setStroke(Color.BLUE);
    	        			blueLineGroup.getChildren().add(l);  	        			           		
    	        		}
    					if (!returnStrokes.isEmpty()) {
    						drawPane.getChildren().remove(blueLineGroup);
    						drawPane.getChildren().add(blueLineGroup);	
    					}
    				}
                    stroke.setHighlighted(false);
                    highlightedStroke =  data.get(rowSelected);
                    currentHighlight = highlightedStroke;
    			}
    		}
    	};    	
    	clusterTable.getSelectionModel().selectedItemProperty().addListener(clusterListener);
    }

    private void stopListeningToSelect() {
    	if (clusterListener != null) {
    		clusterTable.getSelectionModel().selectedItemProperty().removeListener(clusterListener);
    	}
    	if (strokeListener != null) {
    		strokeTable.getSelectionModel().selectedItemProperty().removeListener(strokeListener);
    	}
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
    
    public LinkedList<Stroke> getFinalStrokeData() {
    	return finalStrokeData;
    }

    public void setFinalStrokeData(LinkedList<Stroke> finalStrokeData) {
    	this.finalStrokeData = finalStrokeData;
    }
}