
/*
 * Contains a word part or a separator from the text to correct.
 * It keeps in memory the original word and the corrected replacement
 * */

public class TextWord {
	private String word;
	private String replacement;
	private Boolean valid;			// is true when the word is contained in the dictionary
	
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
