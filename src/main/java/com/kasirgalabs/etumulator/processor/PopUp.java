package com.kasirgalabs.etumulator.processor;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextArea;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class PopUp{
	private TextArea console;

	public PopUp(){
		console = new TextArea();
	}
	/**
		*Converting stacktrace elements to string to show in TextArea which is named console.
	*/
	public void exceptionStacktraceToString(Exception e){
		ByteArrayOutputStream baos= new ByteArrayOutputStream();
    	PrintStream ps = new PrintStream(baos);
    	e.printStackTrace(ps);
    	ps.close();
    	console.appendText(baos.toString());
	}
	/**
		*Creating Popup to show exceptions in new window.
		*
		*In implementation users cannot edit the popUp's TextArea.
		*
	*/
	public void createPopup(){
        console.setEditable(false); 
        Stage stage = new Stage();
        stage.setTitle("Exception(s)!");
        VBox box = new VBox();
        box.setPadding(new Insets(5));
        box.setAlignment(Pos.CENTER);
        Button btnClose = new Button();
        btnClose.setText("Close");
        btnClose.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stage.close();
                console.clear();
           	}
        });
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override 
            public void handle(WindowEvent t) {
                stage.close();
                console.clear();
            }
        });
        Button btnClear = new Button();
        btnClear.setText("Clear");
        btnClear.setOnAction(new EventHandler<ActionEvent>() {
        	@Override
        	public void handle(ActionEvent event) {
            	console.clear();
            }
        });
        box.getChildren().add(console);
        box.getChildren().add(btnClose);
        box.getChildren().add(btnClear);
        Scene scene1 = new Scene(box, 500, 250);
        stage.setScene(scene1);
        stage.show();          
    }
}
