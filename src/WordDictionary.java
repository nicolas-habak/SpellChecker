import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class WordDictionary implements Iterable<String> {
	private List<String> dictionary = new ArrayList<>();
	
	public WordDictionary(File dictionary) throws IOException {
		parseDictionaryFile(dictionary);
	}
	
	/*
	 * parses a dictionary file to have a list of words that will be used to be compared to the text 
	 * */
	public void parseDictionaryFile(File file) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(file));
		for(String line = br.readLine(); line != null; line = br.readLine()) {
			dictionary.add(line);
		}
		br.close();
	}
	
	/*
	 * using the parallelStream function, this function searches the dictionary to find a word
	 * it will return true if the word is found and false if it isn't.
	 * */
	public boolean spellCheck(String word) {
		return dictionary.parallelStream().filter(w -> w.equalsIgnoreCase(word)).findAny().orElse(null) != null;
	}

	@Override
	public Iterator<String> iterator() {
		return dictionary.iterator();
	}
}
