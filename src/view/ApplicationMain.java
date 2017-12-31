package view;

import java.io.File;
import java.io.IOException;

import controller.DictionaryController;
import controller.TextController;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.SpellCheckDictionary;
import javafx.stage.FileChooser.ExtensionFilter;

public class ApplicationMain extends Application {

	// a non word character is any combination of character that doesn't include a letter
	// this prevents the dictionary to look for things like spaces and numbers
	public static final String nonWordCharacter = "([^a-zA-Z_0-9\\-]|\\s)";
	/* non capturing regex, this leaves the non word characters in the text in order to be able to rebuild the text
	 * as it is (with punctuations, line breaks, etc....
	*/
	public static final String splitRegex = "((?<=" + nonWordCharacter + ")|(?=" + nonWordCharacter + "))";
	
	private static final File pwd = new File(System.getProperty("user.dir"));
	private DictionaryController dictionary;
	private TextController text;
	
	public static void main(String... args) {
		launch(args);
	}
	
	@Override
    public void start(Stage primaryStage) {//add classes dictionary and text to be able to have the path
        primaryStage.setTitle("Spell Check");
        
        // root of the scene
        VBox root = new VBox();
        primaryStage.setScene(new Scene(root, 1024, 768));
        
        // contains the text, the dictionaray and their respective titles
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10,10,10,10));
        grid.setVgap(8);
        grid.setHgap(10);
        
        //contains the text viewer and corrector
        ScrollPane editorScroll = new ScrollPane();
        editorScroll.prefHeightProperty().bind(primaryStage.heightProperty());
        editorScroll.prefWidthProperty().bind(primaryStage.widthProperty());
        
        //contains the words that constitute the text
        SpellCheckTextView editor = new SpellCheckTextView();
        editor.prefHeightProperty().bind(editorScroll.heightProperty());
        editor.prefWidthProperty().bind(editorScroll.widthProperty());
        editor.setPadding(new Insets(10,15,10,15));
        editor.setStyle("-fx-background-color: #FFFFFF;");
        
        //displays the path to the currently opened text file
        Label inputTextPath = new Label("SpellCheck");
        inputTextPath.setFont(new Font("Arial", 15));
        
        //displays the name of the currently opened dictionary
        Label dictionaryPath = new Label("Dictionary");
        dictionaryPath.setFont(new Font("Arial", 15));
        
        //contains the list of words in the dictionary
        ScrollPane dictWordListScroll = new ScrollPane();
        dictWordListScroll.setPadding(new Insets(10,10,10,10));
        
        //list containing the words of the dictionary
        Label dictWords = new Label();
        dictWords.setPrefWidth(200);
        
        // menu bar
        MenuBar menuBar = new MenuBar();
        
        // file menu item
        // Load Dictionary: loads a dictionary from a text file
        // Load Text: loads a text to correct from a text file
        // Load Save: saves the corrected text to a file
        Menu menuFile = new Menu("File");
        MenuItem loadDict = new MenuItem("Load Dictionary");
        MenuItem loadText = new MenuItem("Load Text File");
        MenuItem saveChanges = new MenuItem("Save");
        
        loadText.setDisable(true);
        saveChanges.setDisable(true);
        
        editorScroll.setContent(editor);
        dictWordListScroll.setContent(dictWords);
        
        menuFile.getItems().add(loadDict);
        menuFile.getItems().add(loadText);
        menuFile.getItems().add(saveChanges);
        menuBar.getMenus().add(menuFile);
        
        GridPane.setConstraints(inputTextPath, 0, 0, 2, 1);
        GridPane.setConstraints(dictionaryPath, 3, 0, 1, 1);
        GridPane.setConstraints(editorScroll, 0, 2, 2, 1);
        GridPane.setConstraints(dictWordListScroll, 3, 2, 1, 1);
        
        grid.getChildren().addAll(inputTextPath, editorScroll, dictionaryPath, dictWordListScroll);
        root.getChildren().addAll(menuBar, grid);
        primaryStage.show();
        
        loadDict.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				try {
					File f = loadFileDialog(primaryStage);
					if(f != null) {
						dictionary = new DictionaryController(f);
						SpellCheckDictionary dict = dictionary.getDictionary();
						dictionaryPath.setText(dict.getPath());
						dictWords.setText(dict.toString());
						loadText.setDisable(false);
						if(text != null) {
							text.setDictionary(dictionary);
							text.correct();
							editor.updateContent();
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
					loadText.setDisable(true);
				}
			}
		});
        
        loadText.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					File f = loadFileDialog(primaryStage);
					if(f != null) {
						text = new TextController(f, dictionary);
						inputTextPath.setText(text.getPath());
						editor.setText(text);
						text.correct();
						editor.updateContent();
						saveChanges.setDisable(false);
					}
				} catch (IOException e) {
					e.printStackTrace();
					saveChanges.setDisable(true);
				}
			}
		});
        
        saveChanges.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				File f = saveFileDialog(primaryStage);
				if(f != null)
					try {
						text.saveTo(f);
						
						Alert alert = new Alert(AlertType.INFORMATION);
		            	alert.setTitle("Save Successful!");
		            	alert.setHeaderText(null);
		            	alert.setContentText("Corrected text saved to: " + f.getPath());
		            	alert.showAndWait();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		});
    }
	
	private static File loadFileDialog(Stage stage) {
		FileChooser fc = new FileChooser();
    	fc.setInitialDirectory(pwd);
    	fc.getExtensionFilters().add(new ExtensionFilter("Text Files", "*.txt"));
    	return fc.showOpenDialog(stage);
	}
	
	private static File saveFileDialog(Stage stage) {
		FileChooser fc = new FileChooser();
    	fc.setInitialDirectory(pwd);
    	fc.getExtensionFilters().add(new ExtensionFilter("Text Files", "*.txt"));
    	return fc.showSaveDialog(stage);
	}
}
