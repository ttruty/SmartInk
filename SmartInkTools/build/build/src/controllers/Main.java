package controllers;
	
import java.io.IOException;
import java.util.LinkedList;

import javafx.application.Application;
import javafx.stage.Stage;
import models.Stroke;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;


public class Main extends Application {	
	public static void main(String[] args) { launch(args); }
	 @Override public void start(Stage stage) throws IOException {
		    Scene scene = new Scene(new StackPane());
		    
		    LoginManager loginManager = new LoginManager(scene);
		    loginManager.showLoginScreen();

		    stage.setScene(scene);
		    stage.setTitle("Digital Ink");
		    stage.setHeight(700);
		    stage.setWidth(1200);
		    stage.show();	
		    
		    
		    //launch(args);
		//System.out.println("Reading Data");
		//StringBuffer data = ReadData.getDataFromFile("Resources/test_mmse.txt");
		
	}	 
}
