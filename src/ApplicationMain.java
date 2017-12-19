import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class ApplicationMain extends Application {
	private static final File pwd = new File(System.getProperty("user.dir"));
	private static WordDictionary dictionary = null;
	private static InputText inputText = null;
	
	public static void main(String... args) {
		launch(args);
	}
	
	@Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Spell Check");
        
        VBox root = new VBox();
        primaryStage.setScene(new Scene(root, 1024, 768));
        
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10,10,10,10));
        grid.setVgap(8);
        grid.setHgap(10);
        
        ScrollPane editorScroll = new ScrollPane();
        editorScroll.setPadding(new Insets(10,10,10,10));
        editorScroll.prefHeightProperty().bind(primaryStage.heightProperty());
        editorScroll.prefWidthProperty().bind(primaryStage.widthProperty());
        
        VBox editor = new VBox();
        editor.prefHeightProperty().bind(editorScroll.heightProperty());
        editor.prefWidthProperty().bind(editorScroll.widthProperty());
        editor.setStyle("-fx-background-color: #FFFFFF;");
        
        Label inputTextPath = new Label("SpellCheck");
        inputTextPath.setFont(new Font("Arial", 15));
        
        Label dictionaryPath = new Label("Dictionary");
        dictionaryPath.setFont(new Font("Arial", 15));
        
        ScrollPane dictWordListScroll = new ScrollPane();
        dictWordListScroll.setPadding(new Insets(10,10,10,10));
        
        VBox dictWordList = new VBox();
        dictWordList.setPrefWidth(200);
        
        List<FlowPane> textChunks = new ArrayList<>();
        
        MenuBar menuBar = new MenuBar();
        
        Menu menuFile = new Menu("File");
        
        MenuItem loadDict = new MenuItem("Load Dictionary");
        loadDict.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	File fDict = loadFileDialog(primaryStage);
            	if(fDict != null) {
            		try {
						dictionary = new WordDictionary(fDict);
	            		dictionaryPath.setText(fDict.getName());
	            		dictWordList.getChildren().clear();
	            		dictionary.forEach(w -> {
	            			dictWordList.getChildren().add(new Text(w));
            			});
						if(inputText != null) {
							runSpellCheck(editor, textChunks, inputText, dictionary);
						}
						
					} catch (IOException e) {
						e.printStackTrace();
					}
            	}
            }
        });
        
        MenuItem loadText = new MenuItem("Load Text File");
        loadText.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	File fText = loadFileDialog(primaryStage);
            	if(fText != null) {
            		try {
						inputText = new InputText(fText);
	            		inputTextPath.setText(fText.getPath());
						if(dictionary != null) {
							runSpellCheck(editor, textChunks, inputText, dictionary);
						}
						
					} catch (IOException e) {
						e.printStackTrace();
					}
            	}
            }
        });
        
        MenuItem saveChanges = new MenuItem("Save");
        saveChanges.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent t) {
            	if(inputText != null){
            		try {
		            	FileChooser fc = new FileChooser();
		            	fc.setInitialDirectory(pwd);
		            	fc.getExtensionFilters().add(new ExtensionFilter("Text Files", "*.txt"));
		            	File saveTo = fc.showSaveDialog(primaryStage);
		            	if(saveTo != null) {
							inputText.saveTo(saveTo);
		            	}
		            	
		            	Alert alert = new Alert(AlertType.INFORMATION);
		            	alert.setTitle("Save Successful!");
		            	alert.setHeaderText(null);
		            	alert.setContentText("Corrected text saved to: " + saveTo.getPath());
		            	alert.showAndWait();
		            	
	            	} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
            	}
            }
        });
        
        editorScroll.setContent(editor);

        dictWordListScroll.setContent(dictWordList);
        
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
    }
	
	private static void runSpellCheck(VBox root, List<FlowPane> textChunks, InputText inputText, WordDictionary dictionary) {
		inputText.setDictionary(dictionary);
		root.getChildren().removeAll(textChunks);
		textChunks.clear();
		
		List<FlowPane> lines = new ArrayList<>();
		FlowPane currentPane = new FlowPane();
		
		inputText.spellCheck();
		for(final TextWord word : inputText) {
			Text t;
			if (!word.isValid()) {
				t = correctibleWord(word, root, textChunks, inputText, dictionary);
			} else {
				t = new Text(word.getWord());
			}
			if (word.getWord().equals("\n")){
				lines.add(currentPane);
				currentPane = new FlowPane();
			} else {
				currentPane.getChildren().add(t);
			}
		}
		
		textChunks.addAll(lines);
		root.getChildren().addAll(textChunks);
	}
	
	/*private static List<FlowPane> runSpellCheck(InputText inputText, WordDictionary dictionary) {
		
		return lines;
	}
	*/
	private static Text correctibleWord(TextWord word, VBox root, List<FlowPane> textChunks, InputText inputText, WordDictionary dictionary) {
		final Text t = new Text(word.getWord());
		t.setFill(Paint.valueOf("red"));
		t.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				TextInputDialog input = new TextInputDialog(t.getText());
				input.setTitle("Spell Check");
				input.setHeaderText("Input new spelling for '" + t.getText() + "'");
				input.setGraphic(null);
				input.showAndWait();
				String result = input.getResult();
				if(result != null) {
					boolean multipleWordCorrection = inputText.wordCorrection(word, result);
					if(!multipleWordCorrection){
						t.setText(result);
						word.setReplacement(input.getResult());
						word.setValid(dictionary.spellCheck(word.getReplacement()));
						if(word.isValid()) {
							t.setFill(Paint.valueOf("black"));
						} else {
							t.setFill(Paint.valueOf("red"));
						}
					} else {
						runSpellCheck(root, textChunks, inputText, dictionary);
					}
				}
			}
		});
		return t;
	}
	
	private static File loadFileDialog(Stage stage) {
		FileChooser fc = new FileChooser();
    	fc.setInitialDirectory(pwd);
    	fc.getExtensionFilters().add(new ExtensionFilter("Text Files", "*.txt"));
    	return fc.showOpenDialog(stage);
	}
}
