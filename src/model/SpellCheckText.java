package model;

import java.util.ArrayList;
import java.util.List;

public class SpellCheckText extends SpellCheckComponent {

	public SpellCheckText(List<TextWord> content, String path) {
		super(new ArrayList<Object>(content), path);
	}
	
	public String getOriginalText() {
		StringBuilder s = new StringBuilder();
		content.forEach(word -> s.append(((TextWord) word).getWord()));
		return s.toString();
	}
	
	public String getCorrectedText() {
		StringBuilder s = new StringBuilder();
		content.forEach(word -> s.append(((TextWord) word).getReplacement()));
		return s.toString();
	}
	
	@Override
	public String toString() {
		return getCorrectedText();
	}
}
