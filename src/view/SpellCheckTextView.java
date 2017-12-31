package view;

import java.util.List;

import controller.TextController;
import javafx.scene.Node;
import javafx.scene.control.TextInputDialog;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import model.TextWord;

public class SpellCheckTextView extends VBox {
	TextController text;
	
	private Font textFont;
	
	public SpellCheckTextView() {
		init();
	}
	
	public SpellCheckTextView(TextController text) {
		init();
		this.text = text;
		updateContent();
	}
	
	// adds text with color in the editor box
	public void updateContent() {
		ObservableList<Node> children = this.getChildren();
		children.clear();
		children.add(new FlowPane());
		text.getText().getContent().forEach(word -> {
			TextWord tw = (TextWord) word;
			if(tw.getWord().equals("\n"))
				children.add(new FlowPane());
			else {
				Text t = new Text(tw.getReplacement());
				t.setFont(textFont);
				t.setOnMouseClicked(new TextChunkEventHandler(tw));

				if(tw.isValid())
					t.setFill(Paint.valueOf("black"));
				else
					t.setFill(Paint.valueOf("red"));
				
				((FlowPane) children.get(children.size() - 1)).getChildren().add(t);
			}
		});
	}
	
	public TextController getText() {
		return text;
	}
	
	public void setText(TextController text) {
		this.text = text;
	}
	
	private void init() {
        textFont = new Font("Arial", 12);
	}
	
	// handles the click events on each text chunk
	private class TextChunkEventHandler implements EventHandler<MouseEvent> {
		private TextWord word;
		
		public TextChunkEventHandler(TextWord word) {
			this.word = word;
		}
		
		@Override
		public void handle(MouseEvent event) {
			Text textView = (Text) event.getSource();
			
			// Dialog to ask for a new spelling
			TextInputDialog input = new TextInputDialog(textView.getText());
			input.setTitle("Spell Check");
			input.setHeaderText("Input new spelling for '" + textView.getText() + "'");
			input.setGraphic(null);
			input.showAndWait();
			String result = input.getResult();
			
			if(result != null)
			{
				int index = text.getText().getContent().indexOf(word);
				List<TextWord> correctedWords = text.wordCorrection(result);
				if(!correctedWords.isEmpty()) {
					TextWord tw = (TextWord) text.getText().getContent().get(index);
					tw.setReplacement(correctedWords.get(0).getWord());
					tw.setValid(correctedWords.get(0).isValid());
					
					for(int i = 1; i < correctedWords.size(); i++) {
						text.getText().getContent().add(index + i, correctedWords.get(i));
					}
				}
				
				text.correct();
				updateContent();
			}
		}
	}
}
