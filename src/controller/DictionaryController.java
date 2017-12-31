package controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.SpellCheckDictionary;

public class DictionaryController {
	SpellCheckDictionary dictionary;
	
	public DictionaryController (File file) throws IOException{
		parseDictionary(file);
	}
	
	public void parseDictionary (File file) throws IOException {
		List<String> content = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(file));
		for(String line = br.readLine(); line != null; line = br.readLine()) {
			content.add(line);
		}
		br.close();
		dictionary = new SpellCheckDictionary(content, file.getName());
	}
	
	/*
	 * using the parallelStream function, this function searches the dictionary to find a word
	 * it will return true if the word is found and false if it isn't.
	 * */
	public boolean spellCheck(String word) {
		return dictionary.getContent().parallelStream().filter(w -> ((String) w).equalsIgnoreCase(word)).findAny().orElse(null) != null;
	}
	
	public SpellCheckDictionary getDictionary() {
		return dictionary;
	}
}
