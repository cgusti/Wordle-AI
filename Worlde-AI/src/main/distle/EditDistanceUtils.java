package main.distle;

import java.util.*;

public class EditDistanceUtils {

	/**
	 * Returns the completed Edit Distance memoization structure, a 2D array of ints
	 * representing the number of string manipulations required to minimally turn
	 * each subproblem's string into the other.
	 * 
	 * @param s0 String to transform into other
	 * @param s1 Target of transformation
	 * @return Completed Memoization structure for editDistance(s0, s1)
	 */
	public static int[][] getEditDistTable(String s0, String s1) {
		// column = s0, row = s1
		int board[][] = new int[s0.length() + 1][s1.length() + 1];
		
		// fill out column gutters
		for (int i = 0; i < board[0].length; i++) {
			board[0][i] = i;
		}

		// fill out row gutters
		for (int i = 0; i < board.length; i++) {
			board[i][0] = i;
		}

		// fill out the rest of the memoization table
		for (int row = 1; row < s0.length() + 1; row++) {
			for (int col = 1; col < s1.length() + 1; col++) {

				// case 1: case match
				if (s1.charAt(col - 1) == s0.charAt(row - 1)) {
					board[row][col] = board[row - 1][col - 1];
				}

				// case 2: case mismatch
				else {
					// transposition
					int transposition = 1000;
					if (row >= 2 && col >= 2 && s0.charAt(row - 1) == s1.charAt(col - 2)
							&& s0.charAt(row - 2) == s1.charAt(col - 1)) {
						transposition = board[row - 2][col - 2] + 1;

					}
					// evaluate deletion, insertion, replacement case and take the minimum distance
					int delete = board[row - 1][col] + 1;
					int insert = board[row][col - 1] + 1;
					int replace = board[row - 1][col - 1] + 1;

					board[row][col] = min(delete, insert, replace, transposition);
				}
			}
		}
		
		return board;
	}

	/*
	 * Private method that returns the minimum of three integers
	 * @param a integer 
	 * @param b integer
	 * @param c integer
	 * @param d integer
	 */

	private static int min(int a, int b, int c, int d) {
		int smallest1 = Math.min(a, b);
		int smallest2 = Math.min(c, d);
		return Math.min(smallest1, smallest2);
	}

	/**
	 * Returns one possible sequence of transformations that turns String s0 into
	 * s1. The list is in top-down order (i.e., starting from the largest subproblem
	 * in the memoization structure) and consists of Strings representing the String
	 * manipulations of:
	 * <ol>
	 * <li>"R" = Replacement</li>
	 * <li>"T" = Transposition</li>
	 * <li>"I" = Insertion</li>
	 * <li>"D" = Deletion</li>
	 * </ol>
	 * In case of multiple minimal edit distance sequences, returns a list with ties
	 * in manipulations broken by the order listed above (i.e., replacements
	 * preferred over transpositions, which in turn are preferred over insertions,
	 * etc.)
	 * 
	 * @param s0    String transforming into other
	 * @param s1    Target of transformation
	 * @param table Precomputed memoization structure for edit distance between s0,
	 *              s1
	 * @return List that represents a top-down sequence of manipulations required to
	 *         turn s0 into s1, e.g., ["R", "R", "T", "I"] would be two replacements
	 *         followed by a transposition, then insertion.
	 */

	public static List<String> getTransformationList(String s0, String s1, int[][] table) {
		// initialize ArrayList to store LCS
		ArrayList<String> result = new ArrayList<String>();
		

		// start from the very right of the memoization table
		int row = s0.length(); // s1
		int col = s1.length(); // s0
		int currentPosition = table[row ][col ];


		// retrace steps in the memoization table
		while ( !(row == 0 && col == 0) ) {
			// case match

			
			
			if (row > 0 && col > 0 && s0.charAt(row - 1) == s1.charAt((col - 1))) {
				row = row - 1;
				col = col - 1;
				currentPosition = table[row][col];
				continue;
			}

			// case mismatch
			else {

				// check for replacement case
				if (row > 0 && col > 0 && currentPosition == table[row - 1][col - 1] + 1) {
					row = row - 1;
					col = col - 1;
					currentPosition = table[row][col];
					result.add("R");
					continue;
				} 
				
				//check for transposition case
				if (row >= 2 && col >= 2 && currentPosition == table[row - 2][col - 2] + 1 
						&& s0.charAt(row - 1) == s1.charAt(col - 2) && s0.charAt(row - 2) == s1.charAt(col - 1)) {
					row = row - 2;
					col = col - 2;
					currentPosition = table[row ][col ];
					result.add("T");
					continue;
				}
				
				// check for insertion case
				if (col > 0 && currentPosition == table[row][col - 1] + 1) {
					col = col - 1;
					currentPosition = table[row][col];
					result.add("I");
					continue;
				}
				
				// check for deletion case
				if (row > 0 && currentPosition == table[row - 1][col] + 1) {
					row = row - 1;
					currentPosition = table[row ][col];
					result.add("D");
					continue;
				}

				
			}

		}

		return result;

	}

	/**
	 * Returns the edit distance between the two given strings: an int representing
	 * the number of String manipulations (Insertions, Deletions, Replacements, and
	 * Transpositions) minimally required to turn one into the other.
	 * 
	 * @param s0 String to transform into other
	 * @param s1 Target of transformation
	 * @return The minimal number of manipulations required to turn s0 into s1
	 */
	public static int editDistance(String s0, String s1) {

		
		if (s0.equals(s1)) {
			return 0;
		}
		
		else if (s1 == "") {
			return s0.length();
		}
		
		else if (s0 == "") {
			return s1.length();
		}
		
		
		return getEditDistTable(s0, s1)[s0.length()][s1.length()];
		
		//actually s0 is supposed to be row and s1 is supposed to be column 
	}

	/**
	 * See {@link #getTransformationList(String s0, String s1, int[][] table)}.
	 */
	public static List<String> getTransformationList(String s0, String s1) {
		// initializing arrayList to store lcs
		return getTransformationList(s0, s1, getEditDistTable(s0, s1));
	}

	
	public static void main(String[] args) {
		
		List<String> t1 = getTransformationList("pitchfork", "lovely", getEditDistTable("pitchfork", "lovely"));
		List<String> t2 = getTransformationList("pitchfork", "reproduce", getEditDistTable("pitchfork", "reproduce"));
		System.out.println( getTransformationList("pitchfork", "lovely", getEditDistTable("pitchfork", "lovely"))     );
		System.out.println( getTransformationList("pitchfork", "reproduce", getEditDistTable("pitchfork", "reproduce")) );
		System.out.println(t1.equals(t2));                
	}


}
