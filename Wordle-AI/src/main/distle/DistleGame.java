package main.distle;

import static main.distle.EditDistanceUtils.*;
import java.io.*;
import java.util.*;

/**
 * Class responsible for managing the mechanics of a game of Distle, plus
 * for managing the input sources, either a human or AI player.
 */

public class DistleGame {
    
    private Set<String> dictionary;
    private Scanner input;
    private String word;
    private DistlePlayer ai;
    private int guesses, maxGuesses;
    private boolean verbose, wonGame;
    private Random rng;
    
    /**
     * Constructs a new DistleGame object that is responsible for managing the game
     * mechanics.
     * 
     * @param dictionaryPath Path to a dictionary file with new-line separated words
     * composing all possible words that may be selected as the secret answers.
     * @param verbose Boolean flag that can be set to true to see all of the game's mechanics
     * printed out, false otherwise.
     * @param ai Pass in a new DistlePlayer object to have it play the game; otherwise, leave
     * null to play as human.
     * @param seed A numerical seed for the random number generator that can be used to reproduce /
     * get consistently chosen words by the game.
     * @throws FileNotFoundException
     */
    
    public DistleGame (String dictionaryPath, boolean verbose, DistlePlayer ai, int seed) throws FileNotFoundException {
        this.init(dictionaryPath, verbose, ai);
        this.rng.setSeed(seed);
    }
    
    /**
     * See {@link #DistleGame(String dictionaryPath, boolean verbose, DistlePlayer ai, int seed)}. This version does
     * not specify a random seed, which means that each time it is run, a new set of secret words will be selected
     * during the course of the game.
     */
    
    public DistleGame (String dictionaryPath, boolean verbose, DistlePlayer ai) throws FileNotFoundException {
        this.init(dictionaryPath, verbose, ai);
    }
    
    /**
     * Initializes the fields of the DistleGame object.
     * 
     * See {@link #DistleGame(String dictionaryPath, boolean verbose, DistlePlayer ai, int seed)}
     * @throws FileNotFoundException
     */
    private void init (String dictionaryPath, boolean verbose, DistlePlayer ai) throws FileNotFoundException {
        this.loadDictionary(dictionaryPath);
        this.ai = ai;
        this.verbose = verbose;
        this.rng = new Random();
        if (this.input != null) {
            this.input.close();
        }
        if (this.ai == null) {
            this.input = new Scanner(System.in);
        }
    }
    
    /**
     * Begins a new game of Distle with the specified word as the secret, and the given number
     * of maximum attempts that the player can make to guess it.
     * 
     * @param word The secret word of the game
     * @param maxGuesses The max number of guesses the player can make
     */
    public void newGame (String word, int maxGuesses) {
        if (word == null) {
            this.newGame(maxGuesses);
            return;
        }
        if (!dictionary.contains(word)) {
            throw new IllegalArgumentException("Word must be contained in the given dictionary");
        }
        
        this.initializeGame(word, maxGuesses);
    }
    
    /**
     * See {@link #newGame(String word, int maxGuesses)}. This method serves the same purpose
     * except that a random word will be selected as opposed to being hand-picked as an argument.
     */
    public void newGame (int maxGuesses) {
        this.initializeGame(this.getRandomWord(), maxGuesses);
    }
    
    /**
     * Getter for whether or not the player won a game after it has reached a conclusion.
     * @return Whether or not the player won the last begun game.
     */
    public boolean wonGame () {
        return this.wonGame;
    }
    
    /**
     * Loads the dictionary in the given path into this DistleGame's set.
     * 
     * @param dictionaryPath Path pointing to a dictionary file.
     * @throws FileNotFoundException
     */
    private void loadDictionary (String dictionaryPath) throws FileNotFoundException {
        this.dictionary = new HashSet<>();
        File file = new File(dictionaryPath);
        Scanner sc = new Scanner(file);
        while (sc.hasNextLine()) {
            String word = sc.nextLine().toLowerCase();
            if (!dictionary.contains(word)) {
                dictionary.add(word);
            }
        }
        sc.close();
    }
    
    /**
     * Selects a random secret word from this DistleGame's dictionary.
     * @return A random word from this.dictionary
     */
    private String getRandomWord () {
        // Turns out this is the best way to get a random value from a Set in Java;
        // yikes. https://stackoverflow.com/questions/124671/picking-a-random-element-from-a-set
        return this.dictionary.stream().skip(this.rng.nextInt(this.dictionary.size())).findFirst().orElse(null);
    }
    
    /**
     * The main workhorse method of a newly started DistleGame that sequentially selects / sets
     * a secret word, resets fields, asks the player for guesses, and manages the game logic / hints.
     * 
     * @param word The secret word for this game.
     * @param maxGuesses The max number of guesses the player can make.
     */
    private void initializeGame (String word, int maxGuesses) {
        this.word = word;
        this.maxGuesses = maxGuesses;
        this.guesses = 0;
        this.wonGame = false;
        
        if (this.ai != null) {
            this.ai.startNewGame(new HashSet<>(this.dictionary), maxGuesses);
        }
        
        if (this.verbose) {
            System.out.println("=================================");
            System.out.println("=       Welcome to Distle       =");
            System.out.println("=================================");
        }
        
        while (this.guesses < this.maxGuesses) {
            if (this.verbose) {
                System.out.println("[G] Guess " + (this.guesses+1) + " / " + this.maxGuesses);
            }
            String guess = this.getGuess();
            this.guesses++;
            
            if (! this.dictionary.contains(guess)) {
                if (this.verbose) {
                    System.out.println("  [X] Word not in dictionary, try again (lost your turn lul)");
                }
                continue;
            }
            
            int[][] table = getEditDistTable(guess, this.word);
            int distance = table[guess.length()][this.word.length()];
            if (distance == 0) {
                this.wonGame = true;
                if (this.verbose) {
                    if (this.ai != null) {
                        System.out.println("  > " + guess);
                    }
                    System.out.println("[W] You guessed correctly, congratulations!");
                }
                return;
            }
            
            List<String> transforms = getTransformationList(guess, this.word, table);
            if (this.ai != null) {
                if (this.verbose) {
                    System.out.println("  > " + guess);
                }
                this.ai.getFeedback(guess, distance, transforms);
            }
            
            if (this.verbose) {
                System.out.println("  [~] Not quite, here are some hints:");
                System.out.println("    [!] Edit Distance: " + distance);
                System.out.println("    [!] Transforms (top-down): " + transforms);
            }
        }
        
        if (this.verbose) {
            System.out.println("[L] Your word game is weak, too bad. The correct answer: " + this.word);
        }
    }
    
    /**
     * Requests a guess from the player of this DistleGame. If a human, accepts their answer via
     * the terminal. If an AI, calls its makeGuess() method.
     * @return The guess provided by the player.
     */
    
    private String getGuess () {
        if (this.ai == null) {
            if (this.verbose) {
                System.out.print("  > ");
            }
            String guess = this.input.nextLine();
            return guess;
        } else {
            return this.ai.makeGuess();
        }
    }

}
