package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import model.SpellCheckText;
import model.TextWord;
import view.ApplicationMain;

public class TextController {
	SpellCheckText text;
	DictionaryController dictionary;
	
	public TextController (File file, DictionaryController dictionary) throws IOException{
		parseTextFile(file);
		setDictionary(dictionary);
	}
	
	/*
	 * parses the text file and separates the text into words and delimiters.
	 * */
	public void parseTextFile(File file) throws IOException {// pass model argument 
		List<TextWord> content = new ArrayList<>();
		BufferedReader br = new BufferedReader(new FileReader(file));
		String[] lineWords;
		for(String line = br.readLine(); line != null; line = br.readLine()) {
			lineWords = line.split(ApplicationMain.splitRegex);
			for(String lineWord : lineWords) {
				content.add(new TextWord(lineWord));
			}
			content.add(new TextWord("\n"));
		}
		br.close();
		text = new SpellCheckText(content, file.getPath());
	}
	
	public void saveTo(File f) throws IOException {
		FileWriter fw = new FileWriter(f);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(text.getCorrectedText());
		bw.close();
		fw.close();
	}
	
	/*
	 * if the user decides to separate a word in order to correct it (ex: word1word2 becomes "word1" and "word2")
	 * we add the extra words to accomodate and correct each of them separately
	 * */
	public List<TextWord> wordCorrection(String correction) {
		String[] correctedWords = correction.split(ApplicationMain.splitRegex);
		List<TextWord> words = new ArrayList<>();
		TextWord currentWord;
		for(int i = 0; i < correctedWords.length; i++) {
			currentWord = new TextWord(correctedWords[i]);
			if(currentWord.getWord().matches(".*[a-zA-Z]{1,}.*"))
				currentWord.setValid(dictionary.spellCheck(currentWord.getWord()));
			words.add(currentWord);
		}
		return words;
	}
	
	/*
	 * using the parallelStream function, this function compares each word to the dictionary
	 * and sets the "valid" property.
	 * */
	public void correct() {
		text.getContent().parallelStream().forEach(w -> {
			TextWord word = (TextWord) w;
			if(word.getWord().matches(".*[a-zA-Z]{1,}.*"))
				word.setValid(dictionary.spellCheck(word.getReplacement()));
		});
	}
	
	public void setDictionary(DictionaryController dictionary) {
		this.dictionary = dictionary;
	}
	
	public SpellCheckText getText() {
		return text;
	}
	
	public String getPath() {
		return text.getPath();
	}
}
