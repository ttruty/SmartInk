package controllers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;
import java.util.regex.Pattern;

import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleDoubleProperty;
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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.CheckProjId;
import models.Cluster;
import models.ClusterTableData;
import models.DBScan;
import models.Point;
import models.ReadData;
import models.Stroke;
import models.StrokeTableData;
import models.WriteCSV;
import models.WriteXLSX;

/** Controls the main application screen */
public class MainViewController {
  
  @FXML private Button logoutButton;
  @FXML private Button rotateButton;
  @FXML private Button clusterButton;
  @FXML private Button resetButton;
  @FXML private Button saveButton;
  @FXML private Button nextButton;  
  
  @FXML private Label clusterDistLabel;
  @FXML private Label clusterMinNeighborLabel;
  @FXML private Label  fileNameLabel;
  @FXML private Label  testTimeLabel;
  @FXML private Label  staffIdLabel;
  
  @FXML private TextField  projIdTextField;
  @FXML private TextField  fuTextField;
  @FXML private TextField  sentTextField;
  @FXML private TextField  pdTextField;
  
  @FXML private MenuBar fileMenuBar;
  @FXML private TabPane tableTabPane;
  @FXML private Pane drawPane;
  @FXML private ScrollPane scrollPane;
  
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
  
  @FXML private Tab strokeTab;
  @FXML private Tab clusterTab;
  
  @FXML private GridPane controllerGrid;

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
  
  private Group lineGroup = new Group();
  private Group redLineGroup = new Group();
  private Group greenLineGroup = new Group();
  
  //zoom vars
  private final DoubleProperty zoomProperty = new SimpleDoubleProperty(1.0d);
  private final DoubleProperty deltaY = new SimpleDoubleProperty(0.0d);
  
  //scoring vars
  private int processedCount = 0;
  private File focusDirectory;


  //map for holding clusters
  private Map<Integer, ArrayList<Stroke>> myMap = new HashMap<Integer, ArrayList<Stroke>>();
  
  //set up the scene
  public void start(final LoginManager loginManager, String userName) {
	  
	  //disable buttons and controlls in gripane
	 controllerGrid.setDisable(true);
	 
	  
	 staffIdLabel.setText("Staff id: " + userName);
	 
     logoutButton.setOnAction(e->loginManager.logout()); 
     
     //rotate button
     rotateButton.setOnAction((e) -> {
    	 //rotate by 90 degress
    	 drawPane.setRotate(rotationDegs);
    	 if (rotationDegs < 360)
         {
             rotationDegs += 90;
         }
         else{
             rotationDegs = 90;
         }
 			}); // end rotate
     
    
     Group clusterGroup = new Group(); // new group to hold the cluster bounding boxes
     
     //cluster button listener
     clusterButton.setOnAction((e) -> { 
    	 AnchorPane p = new AnchorPane();
    	 clusterTable.getItems().clear();
 		 clusterTable.refresh();
    	 System.out.println("CLUSTER GROUP: " + clusterGroup.getChildren().size());
    	 p.getChildren().removeAll(clusterGroup);
    	 clusterGroup.getChildren().clear();
    	 
    	 Cluster.clearData(); //clear the bounding box list
    	 Cluster.clusterData(this.getFinalStrokeData());
    
    	 ArrayList<Rectangle> clusterBoxes = Cluster.boundClusterBox();
    	 
    	 for (Rectangle rect : clusterBoxes) {   		 
    		 rect.setStroke(Color.RED);
    		 rect.setFill(Color.TRANSPARENT);
    		 clusterGroup.getChildren().add(rect);
    		 
         }
    	 
    	 p.getChildren().add(clusterGroup);  
    	 drawPane.getChildren().add(p);
    	 populateClusterTableView(this.getFinalStrokeData());
    	 
    	 // change the tab in the tab view pane
    	 tableTabPane.getSelectionModel().select(clusterTab);
    	 
    	 saveButton.setDisable(false);
     }); // end cluster button listener
     
     //reset button
     resetButton.setOnAction((e) -> { 
    	 reset();
     }); //end reset button
     
     //save button listener
     saveButton.setOnAction((e) -> { 
    	 boolean saved = true;
    	 ArrayList<Stroke> saveData = new ArrayList<Stroke>();
    	 ClusterTableData stroketd = (ClusterTableData) clusterTable.getSelectionModel().getSelectedItem();
		 //System.out.println("ROW SELECTED:" + rowSelected);
		 LinkedList<Stroke> data = this.getFinalStrokeData();
		
		 //user input errors
		 //TODO: Set in valid project Id
		 if (!validityCheck(projIdTextField.getText())) {
			 Alert alert = new Alert(AlertType.WARNING,
	    	         "Project ID Field is not valid \n"
	    	         + "Please set correctly \n "
	    	         + "(8 Digits)");
	
	    	 alert.setTitle("Project ID Error");
	    	 alert.showAndWait();
	    	 saved = false;
		 }
		 
		 if (!Pattern.matches("^\\d{2}$", fuTextField.getText())) {
			 Alert alert = new Alert(AlertType.WARNING,
	    	         "Visit Year Field is not valid \n"
	    	         + "Please set correctly \n "
	    	         + " (2 Digits [ex- 01]");
	
	    	 alert.setTitle("Visit Year Error");
	    	 alert.showAndWait();
	    	 saved = false;
		 }
		 		 
		 //get the set labels for each cluster
		 for (ClusterTableData item : clusterTable.getItems()) {
			//make sure the whole list of cluster labels are set
			 if (item.getStrokeLabel() == "<Click to set>")
			 {	
				 saved = false;
				 Alert alert = new Alert(AlertType.WARNING,
		    	         "Cluster label not set \n"
		    	         + "Please set label at Cluster " + item.getStrokeIndex());
		
		    	 alert.setTitle("CLUSTER LABEL NOT SET");
		    	 alert.showAndWait();
		    	 break;
			 }
			 
			 System.out.println("Setting cluster: " + item.getStrokeIndex()  + " to " +  item.getStrokeLabel());
			 
			 System.out.println(myMap.get(Integer.parseInt(item.getStrokeIndex())));
			 //ArrayList<Stroke> st = new ArrayList<Stroke>();
			 
			 // new array list of the object and hmap key
			 ArrayList<Stroke> st = myMap.get(Integer.parseInt(item.getStrokeIndex()));//need to parse to integer
			 
			 for (Stroke s : st) {
				 //set label for each stroke in cluster list
				 s.setLabel(item.getStrokeLabel());
				 //add stroke to the save stroke list
				 saveData.add(s);
			 }	
		 }
		 for (Stroke k : saveData) {
			 System.out.println(k.toString());
		 }
    	 
		 //alert for saving file
		 // option to load next file or quit
		 if (saved != false) {
			 //write the data the CSV file for upload later
			 //WriteCSV writeCSV = new WriteCSV();
			 WriteXLSX writeXLSX = new WriteXLSX();
			 try {
				 //writeCSV.write(userName, fileNameLabel.getText(), projIdTextField.getText(), sentTextField.getText(), pdTextField.getText(), saveData);
				 writeXLSX.write(userName, fileNameLabel.getText(), projIdTextField.getText(), fuTextField.getText(), sentTextField.getText(), pdTextField.getText(), saveData);
				 focusDirectory = writeXLSX.getParentDirectory();
		    	 ButtonType more = new ButtonType("Score More", ButtonBar.ButtonData.OK_DONE);
		    	 ButtonType exit = new ButtonType("Exit", ButtonBar.ButtonData.CANCEL_CLOSE);
		    	 Alert alert = new Alert(AlertType.WARNING,
		    	         "Smart Ink Segment File Saved \n"
		    	         + fileNameLabel.getText() + "\n"
		    	         + "Score more or Exit?",
		    	         more,
		    	         exit);
		
		    	 alert.setTitle("File Saved");
		    	 Optional<ButtonType> result = alert.showAndWait();
		
		    	 
		    	 // exit is pressed
		    	 if (result.orElse(more) == exit) {
		    		 closeApp(e);
		    	 }
		    	 else { // more is pressed
		    		 System.out.println("More Selected");
		    		 pickAFile(e);
		    		 
		    	 }
		    	 processedCount++; //inrement processed counter
				
			 } catch (IOException e1) {
				// TODO Auto-generated catch block
				Alert alert = new Alert(AlertType.ERROR,
		    	         "File not saved due to File Error \n"
		    	         + "Please make sure CSV file is not open \n "
		    	         + "If error persists contact IT");
		
		    	 alert.setTitle("File Error");
		    	 alert.showAndWait();
				e1.printStackTrace();
			}	  
		 }
     }); // end save button listener
     
     
     //Sliders for cluster variables
     clusterDistSlider.valueProperty().addListener(new ChangeListener<Number>() {
         @Override
         public void changed(ObservableValue<? extends Number> arg0,Number arg1, Number arg2) {
             clusterDistLabel.textProperty().setValue("Min Dist: " + String.valueOf((int) clusterDistSlider.getValue()));
             DBScan.minDist = (int) clusterDistSlider.getValue();
             }
     }); // end cluster distance listener
     
     clusterNeighborSlider.valueProperty().addListener(new ChangeListener<Number>() {
         @Override
         public void changed(ObservableValue<? extends Number> arg0,Number arg1, Number arg2) {
        	 clusterMinNeighborLabel.textProperty().setValue("Min Neighbs: " + String.valueOf((int) clusterNeighborSlider.getValue()));
        	 DBScan.minPt = (int) clusterNeighborSlider.getValue();
             }
     }); // end cluster neighbor slider listener
     
     // select stroke tab
     strokeTab.setOnSelectionChanged(new EventHandler<Event>() {
         @Override
         public void handle(Event t) {
             if (strokeTab.isSelected()) {
                drawPane.getChildren().removeAll(greenLineGroup);
         		//add listeners		
            	stopListeningToSelect(); // stop listener to ensure not more than one on obj
             	strokeTable.getSelectionModel().clearSelection();
             	clusterTable.getSelectionModel().clearSelection();
             	startListeningToTableSelect(stokeProperty, strokeTable);             	
             }
         }
     }); // end stroke tab listener
     
     //select cluster tab
     clusterTab.setOnSelectionChanged(new EventHandler<Event>() {
         @Override
         public void handle(Event t) {
             if (clusterTab.isSelected()) {
            	drawPane.getChildren().removeAll(redLineGroup);
         		//add listeners		
             	stopListeningToSelect(); // stop listener to ensure not more than one on obj
             	strokeTable.getSelectionModel().clearSelection();
             	clusterTable.getSelectionModel().clearSelection();
             	startListeningToClusterSelect(clusterProperty, clusterTable);
             }
         }
     }); // end cluster tab listener
  } // end start
         	

  //file chooser
  @FXML
  public void pickAFile(ActionEvent event) {
	  // select file from the menu bar
	  Stage stage = (Stage) fileMenuBar.getScene().getWindow();
	  FileChooser fileChooser = new FileChooser();
	  if (processedCount != 0) {		  
		  fileChooser.setInitialDirectory(focusDirectory);
	  }
	  fileChooser.setTitle("Open Resource File");
	  //only be able to select text files
	  FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
      fileChooser.getExtensionFilters().add(extFilter);
      File file = fileChooser.showOpenDialog(stage);
      
      //System.out.println(file);
      fileNameLabel.setText(file.toString());
      setLoadFile(file);
      getData(file);
  } //end pickAFile
  
  //About menu item on menu bar
  @FXML
  public void aboutHelp(ActionEvent event) {
	  // select file from the menu bar
	  Stage stage = (Stage) fileMenuBar.getScene().getWindow();
	  Alert alert = new Alert(AlertType.INFORMATION,
 	           "SMART INK TOOL TOOL\n\n\n"
 	         + "Used to segment MMSE data acquired through*\n "
 	         + "the use of a Smart Pen and Paper\n\n\n"
 	         + " CREATED BY TIM TRUTY\n"
 	         + "ITMO 510 IIT FALL 2018\n"
 	         + "*RUSH ALZHEIMER'S DISEASE CENTER **\n"
 	         + "CHICAGO IL\n");


 	 alert.setTitle("About info");
 	 alert.showAndWait();
  } //end aboutmenu
  
  @FXML
  public void closeApp(ActionEvent event) {
	  //select close on the menu bar
	  Platform.exit();
	  System.exit(0);
	  } //end closeApp


  //select the file to load from the open dialogue
  public void setLoadFile(File loadFile) {
	  // clear variables on new load
	  if (!(this.finalStrokeData == null))
	  {
		this.finalStrokeData.clear();
	  }
	
	  //enable buttons
	 //disable buttons and controlls in gripane
	  controllerGrid.setDisable(false);
	  saveButton.setDisable(true);
	  // clear out the draw pane for new files loaded
	  drawPane.getChildren().clear();
	 // load the file
	 MainViewController.loadFile = loadFile;
	 } //end setLoadFile
	
	public void getData(File file) {
		String path = file.getAbsolutePath();
		try {			
			LinkedList<Stroke> data = new LinkedList<Stroke>();
			data = ReadData.getData(path);
			//choosing a not correct file
			if(ReadData.isFileFlag()) {
				Alert alert = new Alert(AlertType.ERROR,
			 	           "Incorrect file selected \n"
			 	           + "Please choose a correct Pen File");
			 	 alert.setTitle("File Load Error");
			 	 alert.showAndWait();
			 	 return;
				
			}
			this.setFinalStrokeData(data);
			
			//set date label
		    Format format = new SimpleDateFormat("HH:mm:ss MM-dd-yyyy");
		    testTimeLabel.setText("Test time: " +  format.format(data.getFirst().getStartOfStroke()));
		    
		    //clear data before load if present
		    reset();
		    
			drawData(data); 
			populateTableView(data);

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	 
	
	public void drawData(LinkedList<Stroke> data) {

		drawPane.getChildren().clear();
		lineGroup.getChildren().clear();
		greenLineGroup.getChildren().clear();
		redLineGroup.getChildren().clear();	
		
		reset();
		
		PanAndZoomPane panAndZoomPane = new PanAndZoomPane();
        zoomProperty.bind(panAndZoomPane.myScale);
        deltaY.bind(panAndZoomPane.deltaY);
        

        SceneGestures sceneGestures = new SceneGestures(panAndZoomPane);

        scrollPane.setContent(panAndZoomPane);
        panAndZoomPane.toBack();
        scrollPane.addEventFilter( MouseEvent.MOUSE_CLICKED, sceneGestures.getOnMouseClickedEventHandler());
        scrollPane.addEventFilter( MouseEvent.MOUSE_PRESSED, sceneGestures.getOnMousePressedEventHandler());
        scrollPane.addEventFilter( MouseEvent.MOUSE_DRAGGED, sceneGestures.getOnMouseDraggedEventHandler());
        scrollPane.addEventFilter( ScrollEvent.ANY, sceneGestures.getOnScrollEventHandler());
		
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
            	lineGroup.getChildren().add(strokeLine);
            }
            
        }
        
        
     	drawPane.getChildren().add(lineGroup);
     	panAndZoomPane.getChildren().add(drawPane);
	}
	
	private void reset() {
		//reset data on mainview
		drawPane.getChildren().clear();
		projIdTextField.clear();
		fuTextField.clear();
		sentTextField.clear();
		pdTextField.clear();
		clusterTable.getItems().clear();
		clusterTable.refresh();
		strokeTable.getItems().clear();;
		strokeTable.refresh();
		clusterDistSlider.setValue(15);
		clusterNeighborSlider.setValue(2);
		
		  
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
		
		labelColumn.setCellFactory(ComboBoxTableCell.forTableColumn(labelPicker)); 
		//add listeners		
     	stopListeningToSelect(); // stop listener to ensure not more than one on obj
     	strokeTable.getSelectionModel().clearSelection();
     	clusterTable.getSelectionModel().clearSelection();
     	startListeningToTableSelect(stokeProperty, strokeTable);
	}
	
	public void populateClusterTableView(LinkedList<Stroke> data) {
		//clear the table each run
		//stopListeningToSelect();
		clusterTable.getItems().clear();
		myMap.clear();
		clusterTable.refresh();
		
		labelPicker = FXCollections.observableArrayList();
		
		//set up combo box to set label
		labelPicker.add("Sentence");
		labelPicker.add("Pentagon");
		labelPicker.add("RA Data");
		labelPicker.add("Other Data");
		
		//Hash map of strokes list in clusters
        for (int i = 0; i < data.size(); i++) {
            Stroke strokeI = data.get(i);
            int clusterI = data.get(i).getStrokCluster();
            
            if (myMap.get(clusterI) == null) { //gets the value for an id)
                myMap.put(clusterI, new ArrayList<Stroke>()); //no ArrayList assigned, create new ArrayLis
                myMap.get(clusterI).add(strokeI); //adds value to list.
            }
            else {
            	myMap.get(clusterI).add(strokeI); //adds value to list.
            }
        }

		clusterCount = 0;
		// reset to null
		ObservableList<ClusterTableData> clusterList = null;
		
		clusterList = FXCollections.observableArrayList();
		
		for (Integer cluster : myMap.keySet()) {
            System.out.println(cluster + ": " + myMap.get(cluster));
			clusterList.add(new ClusterTableData(Integer.toString(cluster), "<Click to set>", Integer.toString(myMap.get(cluster).size()) ) );

        }		
		clusterTable.setItems(clusterList);
		
		clusterLabelColumn.setCellFactory(ComboBoxTableCell.forTableColumn(labelPicker));
		
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
    			
    	    	//Group blueLineGroup = new Group();    	    	
    			//System.out.println("ROW SELECTED:" + rowSelected); 
    			LinkedList<Stroke> data = this.getFinalStrokeData();
    			StrokeTableData stroketd = (StrokeTableData) table.getSelectionModel().getSelectedItem();
    			//System.out.println(stroketd.getStrokeIndex());  
    			//System.out.println(table.getSelectionModel().getSelectedItem());
    			int selection = (Integer.parseInt(stroketd.getStrokeIndex().toString())) - 1;
    			data.get(selection).setHighlighted(true);
    			
    			drawPane.getChildren().removeAll(redLineGroup);
    			lineGroup.getChildren().removeAll(redLineGroup);
    		    redLineGroup.getChildren().clear();
    			//drawPane.getChildren().clear(); // this clears out the pane from lines
    				
    			for (Stroke stroke : data) {         	
    				
    				if (stroke.isHighlighted()) {
    					
    					ArrayList<Line> strokeLines = drawStrokeLine(stroke);
	                    for (Line line : strokeLines)
	                	{
	                    	drawPane.getChildren().remove(line);
	                		line.setStroke(Color.RED);
	                		line.setStrokeWidth(4);
	                		redLineGroup.getChildren().add(line);               		
	                	}
    				}

                    stroke.setHighlighted(false);
                    highlightedStroke =  data.get(selection);
                    currentHighlight = highlightedStroke;
    			}
    		//lineGroup.getChildren().add(blueLineGroup);	
    		//lineGroup.getChildren().add(redLineGroup);	
    		drawPane.getChildren().add(redLineGroup);	
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
    	//Group blueLineGroup = new Group();
    	
    	clusterListener = (obs, oldValue, newValue) -> {        	
    		if (newValue != null) {        			
    			
    	    	
    			//drawPane.getChildren().clear();; // this clears out the pane from lines
    			drawPane.getChildren().removeAll(greenLineGroup);
    			lineGroup.getChildren().removeAll(greenLineGroup);
    		    greenLineGroup.getChildren().clear();
    			ClusterTableData stroketd = (ClusterTableData) table.getSelectionModel().getSelectedItem();
    			//System.out.println("ROW SELECTED:" + rowSelected);
    			LinkedList<Stroke> data = this.getFinalStrokeData();
    			int selection = Integer.parseInt(stroketd.getStrokeIndex().toString());

    			System.out.println(data.get(selection).toString());
    			
    			//clear stroke data from pane
	            System.out.println(selection + ": " + myMap.get(selection));
	            //drawPane.getChildren().remove(lineGroup);
	            //drawPane.getChildren().removeAll(lineGroup);
    			for (Stroke stroke : myMap.get(selection)) {         	
					ArrayList<Line> strokeLines = drawStrokeLine(stroke);
                    for (Line line : strokeLines)
                	{
                    	//lineGroup.getChildren().remove(line);
                		line.setStroke(Color.GREEN);
                		line.setStrokeWidth(4);
                		greenLineGroup.getChildren().add(line);               		
                	}
                    if (!strokeLines.isEmpty()) {
                    	//drawPane.getChildren().remove(greenLineGroup);
                    	//drawPane.getChildren().add(greenLineGroup);
                    }                    
                    //lineGroup.getChildren().add(greenLineGroup);
    			}
	    		drawPane.getChildren().add(greenLineGroup);	
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
    
    private boolean validityCheck(String Id) {
		CheckProjId checker = new CheckProjId();
		return checker.isLegal(Id);
	}
}