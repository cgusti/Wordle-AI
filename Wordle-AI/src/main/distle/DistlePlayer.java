package main.distle;

import static main.distle.EditDistanceUtils.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;



import java.util.*;

/**
 * AI Distle Player! Contains all logic used to automagically play the game of
 * Distle with frightening accuracy (hopefully).
 */
public class DistlePlayer {
    
	String openingWord;
	int lengthOfWord;
    int guessNumber;
    String topGuess;
    Set<String> dictionary;

    
    /**
     * Constructs a new DistlePlayer.
     * [!] You MAY NOT change this signature, meaning it may not accept any arguments.
     * Still, you can use this constructor to initialize any fields that need to be,
     * though you may prefer to do this in the {@link #startNewGame(Set<String> dictionary, int maxGuesses)}
     * method.
     */
    
    public DistlePlayer () {
    	this.openingWord = "heaven";
    }
    
    
    /**
     * Called at the start of every new game of Distle, and parameterized by the
     * dictionary composing all possible words that can be used as guesses / one of
     * which is the correct.
     * 
     * @param dictionary The dictionary from which the correct answer and guesses
     * can be drawn.
     * @param maxGuesses The max number of guesses available to the player.
     */
    
    public void startNewGame (Set<String> dictionary, int maxGuesses) {
       this.dictionary = dictionary;
       this.lengthOfWord = 0;
       this.guessNumber = 1;
       topGuess = null;

    }
    
    /**
     * Requests a new guess to be made in the current game of Distle. Uses the
     * DistlePlayer's fields to arrive at this decision.
     * 
     * @return The next guess from this DistlePlayer.
     */
    
    public String makeGuess () {
        if (guessNumber == 1) {
        	guessNumber += 1;
        	return ("heaven");
        }
        guessNumber += 1;
        topGuess = dictionary.stream().skip(new Random().nextInt(this.dictionary.size())).findFirst().orElse(null);
        return topGuess;
        
    }
    
    /**
     * Called by the DistleGame after the DistlePlayer has made an incorrect guess. The
     * feedback furnished is as follows:
     * <ul>
     *   <li>guess, the player's incorrect guess (repeated here for convenience)</li>
     *   <li>editDistance, the numerical edit distance between the guess and secret word</li>
     *   <li>transforms, a list of top-down transforms needed to turn the guess into the secret word</li>
     * </ul>
     * 
     * [!] This method should be used by the DistlePlayer to update its fields and plan for
     * the next guess to be made.
     * 
     * @param guess The last, incorrect, guess made by the DistlePlayer
     * @param editDistance Numerical distance between the guess and the secret word
     * @param transforms List of top-down transforms needed to turn the guess into the secret word
     */
    
    public void getFeedback (String guess, int editDistance, List<String> transforms) {
    
    lengthOfWord = getLengthWord(transforms, guess);

    dictionary = filterDictionaryByWordLength(dictionary, lengthOfWord);

    dictionary = findCandidate(dictionary, guess, transforms);
    
   
    }

    
    //private methods

    /*finds the length of words based on the transformation list given by getFeedback()
     * @param guess for the game round 
     * @param transformation list for the round 
     */

    private int getLengthWord (List<String> transforms, String guess ) {
    	 int insertOccurances = Collections.frequency(transforms, "I");
    	 int deleteOccurances = Collections.frequency(transforms, "D") * -1 ;
    	 return ( guess.length() + insertOccurances + deleteOccurances);
    }

    
    /* filters the dictionary and exclude any words that does not match the secret word's length
     * @param dictionary set of dictionary words
     * @param length o the secret word
     */
    
    private Set<String> filterDictionaryByWordLength (Set<String> dictionary, int lengthOfWord ) {
    	Set<String> filteredDict = dictionary.stream()
                .filter(s -> s.length() == lengthOfWord)
                .collect(Collectors.toSet());
    			return filteredDict;  
    }
    
    /* filters the dictionary and exclude any words that does not match the transformation list of 
     * to the secret word  
     * @param dictionary set of dictionary words
     * @param guess guess for the previous round 
     * @transforms transformation list given by getFeedback()
     */
    
    private Set<String> findCandidate(Set<String> dictionary, String guess, List<String> transforms) {
    	Set<String> possibleWords = new HashSet<>();
    	for (String word: dictionary) {
    		List<String> tList = getTransformationList (guess, word, getEditDistTable(guess, word));
    		if (transforms.equals( tList ) ){
    			possibleWords.add(word);
    			
    		}
    	}
    	return possibleWords;
    }
    
    
}
