import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class InputText implements Iterable<TextWord>{
	private List<TextWord> words = new ArrayList<>();
	private WordDictionary dictionary;

	// a non word character is any combination of character that doesn't include a letter
	// this prevents the dictionary to look for things like spaces and numbers
	private final String nonWordCharacter = "([^a-zA-Z_0-9\\-]|\\s)";
	/* non capturing regex, this lets the non word characters in the text in order to be able to rebuild the text
	 * as it is (with punctuations, line breaks, etc....
	*/
	private final String splitRegex = "((?<=" + nonWordCharacter + ")|(?=" + nonWordCharacter + "))";
	
	public InputText(File text) throws IOException {
		parseTextFile(text);
	}
	
	/*
	 * parses the text file and separates the text into words and delimiters.
	 * */
	public void parseTextFile(File text) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(text));
		String[] lineWords;
		for(String line = br.readLine(); line != null; line = br.readLine()) {
			lineWords = line.split(splitRegex);
			for(String lineWord : lineWords) {
				words.add(new TextWord(lineWord));
			}
			words.add(new TextWord("\n"));
		}
		br.close();
	}

	/*
	 * using the parallelStream function, this method checks in parallel all the words in the text
	 * and compares them to the dictionary
	 * */
	public void spellCheck() {
		words.parallelStream().forEach(word -> {
			String currentWord = word.getWord();
			// only compare words
			if(currentWord.matches(".*[a-zA-Z]{1,}.*"))
				word.setValid(dictionary.spellCheck(currentWord));
			if(word.isValid())
				word.setReplacement(word.getWord());
		});
	}
	
	public void setDictionary(WordDictionary dictionary) {
		this.dictionary = dictionary;
	}
	
	public void saveTo(File f) throws IOException {
		FileWriter fw = new FileWriter(f);
		BufferedWriter bw = new BufferedWriter(fw);
		words.forEach(w-> {
			try {
				bw.write(w.getReplacement());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		});
		bw.close();
		fw.close();
	}
	
	/*
	 * if the user decides to separate a word in order to correct it (ex: word1word2 becomes "word1" and "word2")
	 * we add the extra words to accomodate and correct each of them separately
	 * */
	public boolean wordCorrection(TextWord w, String correction) {
		String[] correctedWords = correction.split(splitRegex);
		Integer index = null;
		if(correctedWords.length > 0) {
			if(correctedWords.length > 1) {
				index = words.indexOf(w);
				words.remove(w);
				TextWord currentWord;
				for(int i = 0; i < correctedWords.length; i++) {
					currentWord = new TextWord(correctedWords[i]);
					if(currentWord.getWord().matches(".*[a-zA-Z]{1,}.*"))
						currentWord.setValid(dictionary.spellCheck(currentWord.getWord()));
					words.add(index + i, currentWord);
					
				}
			} else {
				w.setReplacement(correction);
			}
		}
		return correctedWords.length > 1;
	}
	
	public TextWord getWordAt(int index) {
		return words.get(index);
	}
	
	@Override
	public Iterator<TextWord> iterator() {
		return words.iterator();
	}
}
