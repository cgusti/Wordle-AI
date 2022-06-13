package test.distle;

import main.distle.*;
import java.io.FileNotFoundException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;

public class DistleTests {
    
    // =================================================
    // Test Configuration
    // =================================================
    
    // Global timeout to prevent infinite loops from
    // crashing the test suite
    // [!] You might want to comment these lines out while
    // developing, just so you know whether or not you're
    // inefficient or bugged!
    @Rule
    public Timeout globalTimeout = Timeout.seconds(180);
    
    static final int GAMESHOW_ROUNDS = 100,
                     MAX_GUESSES = 10;
    
    
    // Distle Player Tests
    // -------------------------------------------------
    private void runGameShowRounds (String dictionaryPath, String testName) throws FileNotFoundException {
        DistleGame game = new DistleGame(dictionaryPath, false, new DistlePlayer());
        
        int won = 0;
        for (int g = 0; g < GAMESHOW_ROUNDS; g++) {
            game.newGame(MAX_GUESSES);
            won += (game.wonGame()) ? 1 : 0;
        }
        
        System.out.println("[!] " + testName + " Tests: " + won + " / " + GAMESHOW_ROUNDS);
    }
    
    @Test
    public void distlePlayer_t14() throws FileNotFoundException {
        runGameShowRounds("./src/dat/distle/dictionary14.txt", "Dictionary14");
        System.out.println("  [>] Benchmark: ~95% Won");
    }
    
    @Test
    public void distlePlayer_t10() throws FileNotFoundException {
        runGameShowRounds("./src/dat/distle/dictionary10.txt", "Dictionary10");
        System.out.println("  [>] Benchmark: ~90% Won");
    }
    
    @Test
    public void distlePlayer_t6() throws FileNotFoundException {
        runGameShowRounds("./src/dat/distle/dictionary6.txt", "Dictionary6");
        System.out.println("  [>] Benchmark: ~80% Won");
    }
    
}
