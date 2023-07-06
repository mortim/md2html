package md2html;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import javafx.application.Application;
import javafx.application.HostServices;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;
import md2html.parser.Parser;
import md2html.parser.RenderType;

public class App extends Application {
	private static double WIDTH_WINDOW = Screen.getPrimary().getBounds().getWidth();
	private static double HEIGHT_WINDOW = Screen.getPrimary().getBounds().getHeight();
	
	private class HyperLinkEvent implements EventListener {
		private String value;
		private HostServices services;
		
		public HyperLinkEvent(String value, HostServices services) {
			this.value = value;
			this.services = services;
		}
		
		@Override
		public void handleEvent(Event ev) {
			this.services.showDocument(this.value);
			// Stop the default JavaFX event for hyperlinks
			ev.preventDefault();
		}
	}
	
	public void start(Stage stage) {
		HBox hbox = new HBox();
		
		TextArea textArea = new TextArea();
		textArea.setWrapText(true);
		textArea.setFocusTraversable(false);
		textArea.setPromptText("Écrivez du code Markdown juste ici...");
		
		WebView webView = new WebView();

		webView.getEngine().setUserStyleSheetLocation("data:,body { font-family: \"Helvetica Neue\",Helvetica,\"Segoe UI\",Arial,freesans,sans-serif; color: #333; word-break: break-all; }");
		
		hbox.getChildren().addAll(textArea, webView);

		textArea.textProperty().addListener((observable, oldValue, newValue) -> {
			if(!newValue.equals("")) {
				Parser parser = new Parser(newValue);
				parser.parse();
				String scriptJS = "<script>function customScroll(elementId) { document.getElementById(elementId).scrollIntoView(); }</script>";
				webView.getEngine().loadContent(Parser.renderTo(RenderType.HTML, parser.getAST()) + scriptJS);
			} else
				webView.getEngine().loadContent("");
		});
		
		webView.getEngine().documentProperty().addListener((observable, oldState, newState) -> {
			if(newState != null) {
                Document doc = newState;
                NodeList nodes = doc.getElementsByTagName("a");
				for(int i = 0; i < nodes.getLength(); i++) {
					Element el = (Element)nodes.item(i);
					((EventTarget)el).addEventListener("click", new HyperLinkEvent(el.getAttribute("href"), this.getHostServices()), false);
				}
			}
		});
		
		Scene scene = new Scene(hbox, App.WIDTH_WINDOW, App.HEIGHT_WINDOW);
		
		stage.widthProperty().addListener((observable, oldVal, newVal) -> {
			textArea.setPrefWidth(newVal.doubleValue()/2);
			webView.setPrefWidth(newVal.doubleValue()/2);
		});
		
		stage.setScene(scene);
		stage.setTitle("Markdown to HTML Parser");
		stage.show();
	}
	
	public static void main(String[] args) {
		Application.launch(args);
	}
}
