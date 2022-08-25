package test.distle;

import main.distle.*;
import java.io.FileNotFoundException;

/**
 * Empirical test suite / basic runner for having either a human or AI play
 * the game of Distle with the following configurable parameters.
 */
public class DistleGameShow {
    
    /**
     * MAX_GUESSES = number of tries the player gets to guess the word
     * N_GAMES     = number of new games to be played
     */
    public static final int MAX_GUESSES = 10,
                            N_GAMES = 1;
    
    /**
     * WORD = correct answer; leave null for a random word
     */
    public static final String WORD = null;
    
    /**
     * VERBOSE = true to play with terminal prints, false otherwise
     * AI_PLAYER = true to have your DistlePlayer on the show, false to play yourself
     */
    public static boolean VERBOSE = true,
                          AI_PLAYER = true;
    
    /**
     * May need to be changed on your system depending on your development environment;
     * Should be some path that points to a dictionary file
     */
    public static final String DICTIONARY_PATH = "./src/dat/distle/dictionary14.txt";
    
    /**
     * Runs some number of games of Distle based on the parameters above.
     */
    public static void main (String[] args) throws FileNotFoundException {
        DistleGame game = new DistleGame(DICTIONARY_PATH, VERBOSE, (AI_PLAYER) ? new DistlePlayer() : null);
        int victories = 0;
        for (int g = 0; g < N_GAMES; g++) {
            if (VERBOSE) {
                System.out.println("[!] Game Starting: " + g + " / " + N_GAMES);
            }
            game.newGame(WORD, MAX_GUESSES);
            victories += (game.wonGame()) ? 1 : 0;
        }
        
        System.out.println("=================================");
        System.out.println("= Won: " + victories + " / " + N_GAMES);
        System.out.println("=================================");
    }

}
