import java.util.ArrayList;
import java.util.List;

public class Variable {
	private String name;
	private String words;
	private int wordsNumber;
	private List<String>lemmatizedWords;
	private boolean isValid;
	private boolean isInContext;
	
	public Variable() {
		this.name = "";
		this.words = "";
		this.wordsNumber = 0;
		this.lemmatizedWords = new ArrayList<String>();
		this.isValid = true;
		this.isInContext = false;
	}
	
	public Variable(String name) {
		this.name = name;
		this.words = "";
		this.wordsNumber = 0;
		this.lemmatizedWords = new ArrayList<String>();
		this.isValid = true;
		this.isInContext = false;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getwords() {
		return words;
	}
	
	public void setWords(String words) {
		this.words = words;
	}
	
	public int getWordsNumber() {
		return wordsNumber;
	}
	
	public void setWordsNumber(int wordsNumber) {
		this.wordsNumber = wordsNumber;
	}
	
	public List<String> getLemmatizedWords(){
		return lemmatizedWords;
	}
	
	public void setLemmatizedWords(List<String>lemmatizedWords) {
		this.lemmatizedWords = lemmatizedWords;
	}
	
	public boolean getIsValid() {
		return isValid;
	}
	
	public void setIsValid(boolean isValid) {
		this.isValid = isValid;
	}
	
	public boolean getIsInContext() {
		return isInContext;
	}
	
	public void setIsInContext(boolean isInContext) {
		this.isInContext = isValid;
	}
}
