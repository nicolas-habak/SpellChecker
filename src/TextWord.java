
public class TextWord {
	private String word;
	private String replacement;
	private Boolean valid;
	
	public TextWord(String word) {
		this.word = word;
		replacement = word;
		valid = true;
	}
	
	public String getWord() {
		return word;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}
	
	public Boolean isValid() {
		return valid;
	}

	public String getReplacement() {
		return replacement;
	}

	public void setReplacement(String replacement) {
		this.replacement = replacement;
	}
}
