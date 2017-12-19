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

	private final String notWordCharacter = "([^a-zA-Z_0-9\\-]|\\s)";
	private final String splitRegex = "((?<=" + notWordCharacter + ")|(?=" + notWordCharacter + "))";
	
	public InputText(File text) throws IOException {
		parseTextFile(text);
	}
	
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

	public void spellCheck() {
		words.parallelStream().forEach(word -> {
			String currentWord = word.getWord();
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
