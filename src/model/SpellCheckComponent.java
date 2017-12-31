package model;

import java.util.List;

public class SpellCheckComponent {
	
	protected List<Object> content;
	protected String path;
	
	protected SpellCheckComponent(List<Object> content, String path) {
		this.content = content;
		this.path = path;
	}
	public List<Object> getContent() {
		return content;
	}

	public String getPath() {
		return path;
	}
}
