package model;

import java.util.ArrayList;
import java.util.List;

public class SpellCheckDictionary extends SpellCheckComponent {

	public SpellCheckDictionary(List<String> content, String path) {
		super(new ArrayList<Object>(content), path);
	}
	
	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		content.forEach(word -> s.append((String) word + "\n"));
		return s.toString();
	}
}
