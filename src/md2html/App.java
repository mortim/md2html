package md2html;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import md2html.parser.Parser;

public class App extends Application {
	
	public void start(Stage stage) {
		HBox hbox = new HBox();
		TextArea textArea = new TextArea();
		WebView webView = new WebView();
		
		hbox.getChildren().addAll(textArea, webView);
		
		textArea.textProperty().addListener((observable, oldValue, newValue) -> {
			if(!newValue.equals("")) {
				Parser parser = new Parser(newValue);
				parser.parse();
				webView.getEngine().loadContent(parser.toHTML());
			}
		});
		
		Scene scene = new Scene(hbox, 1200,900);
		
		stage.setResizable(false);
		stage.setScene(scene);
		stage.setTitle("Markdown to HTML Parser");
		stage.show();
	}
	
	public static void main(String[] args) {
		Application.launch(args);
	}
}
