/*
 * File: Yahtzee.java
 * ------------------
 * This program will eventually play the Yahtzee game.
 */

import java.util.Arrays;

import acm.io.*;
import acm.program.*;
import acm.util.*;

public class Yahtzee extends GraphicsProgram implements YahtzeeConstants {

	public static void main(String[] args) {
		new Yahtzee().start(args);
	}

	public void run() {
		IODialog dialog = getDialog();
		nPlayers = dialog.readInt("Enter number of players");
		playerNames = new String[nPlayers];
		for (int i = 1; i <= nPlayers; i++) {
			playerNames[i - 1] = dialog.readLine("Enter name for player " + i);
		}
		display = new YahtzeeDisplay(getGCanvas(), playerNames);
		playGame();
	}

	/*
	 * main method to play the game. There are two for cycles, one to create
	 * multiple rounds and another to turn game into multiplayer.
	 * 
	 */
	private void playGame() {
		/* You fill this in */
		categories = new int[nPlayers][N_CATEGORIES];

		scoreCard = new int[nPlayers][N_CATEGORIES];

		for (int i = 0; i < N_SCORING_CATEGORIES; i++) {
			for (int j = 1; j <= nPlayers; j++) {
				oneRound(j);
			}
		}

		gameOver();
	}

	// method for one round
	private void oneRound(int player) {
		firstRoll(player);
		otherRolls();
		updateScoreboard(player);
	}

	// method for first roll of the round
	private void firstRoll(int player) {
		display.printMessage(playerNames[player - 1] + "'s turn! Click 'Roll Dice' button to roll the dice.");
		display.waitForPlayerToClickRoll(player);
		getDices();
	}

	// method for other two rolls of the round
	private void otherRolls() {
		for (int i = 0; i < 2; i++) {
			display.printMessage("Select the dice you wish to re-roll and click 'Roll Again'.");
			display.waitForPlayerToSelectDice();
			getDices2();
		}
	}

	// method to get random combination of dices on first roll
	private void getDices() {
		for (int i = 0; i < N_DICE; i++) {
			dice[i] = rgen.nextInt(1, 6);
		}
		display.displayDice(dice);
	}

	// method to roll again selected dices
	private void getDices2() {
		for (int i = 0; i < N_DICE; i++) {
			if (display.isDieSelected(i)) {
				dice[i] = rgen.nextInt(1, 6);
			}
		}
		display.displayDice(dice);
	}

	// method which updates scoreboard after every round
	private void updateScoreboard(int player) {
		display.printMessage("Select a category for this roll.");
		while (true) {
			category = display.waitForPlayerToSelectCategory();

			if (categories[player - 1][category - 1] == 0) { // checking if category is empty
				fillingCategory(player);
				break;
			}
			display.printMessage("Category is already full. Select empty category.");
		}

	}

	// fills in selected category
	private void fillingCategory(int player) {
		boolean p = isCorrectCategory();
		int score = getScore(p);
		scoreCard[player - 1][category - 1] = score; // updates score card
		display.updateScorecard(category, player, score);

		categories[player - 1][category - 1] = 1; // saves that category is already used
	}

	// checks if selected category is correct for combination of dices
	private boolean isCorrectCategory() {
		boolean result = true;
		if (category == THREE_OF_A_KIND || category == FOUR_OF_A_KIND || category == YAHTZEE) {
			result = isNOfAKind();
		} else if (category == SMALL_STRAIGHT || category == LARGE_STRAIGHT) {
			result = isStraight();
		} else if (category == FULL_HOUSE) {
			result = isFullHouse();
		}
		return result;

	}

	// checks if dices are full house
	private boolean isFullHouse() {
		boolean foundTwoSame = false;

		boolean foundThreeSame = false;

		for (int i = 0; i < N_DICE; i++) {
			int count = 0;
			for (int j = 0; j < N_DICE; j++) {
				if (dice[i] == dice[j]) {
					count++;
				}

				if (count == 2) {
					foundTwoSame = true;
				} else if (count == 3) {
					foundThreeSame = true;
				}
			}
		}
		if (foundTwoSame && foundThreeSame) {
			return true;
		} else
			return false;
	}

	// checks if dices are small or large straight
	private boolean isStraight() {
		Arrays.sort(dice);
		int count = 0;
		for (int i = 0; i < N_DICE - 1; i++) {

			if (dice[i + 1] - dice[i] == 1) {
				count++;
			}

			if (category == SMALL_STRAIGHT && count >= 3) {
				return true;
			} else if (category == LARGE_STRAIGHT && count == 4) {
				return true;
			}
		}

		return false;
	}

	// checks if dices are three or four of a kind or yahtzee
	private boolean isNOfAKind() {
		for (int i = 0; i < N_DICE; i++) {
			int count = 0;
			for (int j = 0; j < N_DICE; j++) {
				if (dice[i] == dice[j]) {
					count++;
				}
			}

			if (category == THREE_OF_A_KIND && count >= 3)
				return true;
			else if (category == FOUR_OF_A_KIND && count >= 4)
				return true;
			else if (category == YAHTZEE && count == 5)
				return true;
		}

		return false;
	}

	// gets score for selected category
	private int getScore(boolean correctCategory) {
		int score = 0;

		if (!correctCategory) // if category is not correct for dices returns 0
			return score;

		if (category >= ONES && category <= SIXES) {
			score = getScoreForNums(score);
		} else if (category == THREE_OF_A_KIND || category == FOUR_OF_A_KIND || category == CHANCE) {
			score = getDiceSum(score);
		} else if (category == FULL_HOUSE) {
			score = 25;
		} else if (category == SMALL_STRAIGHT) {
			score = 30;
		} else if (category == LARGE_STRAIGHT) {
			score = 40;
		} else if (category == YAHTZEE) {
			score = 50;
		}

		return score;
	}

	// get score for cases when we need sum of all dices
	private int getDiceSum(int score) {
		for (int i = 0; i < N_DICE; i++) {
			score += dice[i];
		}
		return score;
	}

	// get score for cases when we need sum of same kind of specific numbers
	private int getScoreForNums(int score) {
		for (int i = 0; i < N_DICE; i++) {
			if (dice[i] == category) {
				score += category;
			}
		}
		return score;
	}

	// game over method after all the rounds are over
	private void gameOver() {
		for (int i = 1; i <= nPlayers; i++) {
			fullScoreCard(i);
		}
		getWinner();
	}

	// checks who is winner and prints it with their score
	private void getWinner() {
		int winner = 0;
		int score = 0;

		for (int i = 1; i <= nPlayers; i++) {
			if (scoreCard[i - 1][TOTAL - 1] > score) {
				winner = i;
				score = scoreCard[i - 1][TOTAL - 1];
			}
		}

		display.printMessage("Congratulations, " + playerNames[winner - 1]
				+ ", you're the winner with a total score of " + score + "!");
	}

	// updates scoreboard to its final form
	private void fullScoreCard(int player) {
		int upperScore = 0;
		int lowerScore = 0;
		int totalScore = 0;

		upperScore = getUpperScore(upperScore, player);

		display.updateScorecard(UPPER_BONUS, player, getBonus(upperScore));

		lowerScore = getLowerScore(lowerScore, player);

		totalScore = getTotalScore(upperScore, lowerScore, totalScore, player);
	}

	// calculates total score
	private int getTotalScore(int upperScore, int lowerScore, int totalScore, int player) {
		totalScore = upperScore + lowerScore + scoreCard[player - 1][UPPER_BONUS - 1];
		scoreCard[player - 1][TOTAL - 1] = totalScore;
		display.updateScorecard(TOTAL, player, totalScore);
		return totalScore;
	}

	// calculates lower score
	private int getLowerScore(int lowerScore, int player) {
		for (int i = THREE_OF_A_KIND; i <= CHANCE; i++) {
			lowerScore += scoreCard[player - 1][i - 1];
			display.updateScorecard(LOWER_SCORE, player, lowerScore);
		}
		return lowerScore;
	}

	// calculates upper score
	private int getUpperScore(int upperScore, int player) {
		for (int i = ONES; i <= SIXES; i++) {
			upperScore += scoreCard[player - 1][i - 1];
			display.updateScorecard(UPPER_SCORE, player, upperScore);
		}
		return upperScore;
	}

	// checks if it should add bonus
	private int getBonus(int upperScore) {
		if (upperScore > 63) {
			return 35;
		} else {
			return 0;
		}
	}

	/* Private instance variables */
	private int[][] categories;
	private int[][] scoreCard;
	private int[] dice = new int[N_DICE];
	private int category;

	private int nPlayers;
	private String[] playerNames;
	private YahtzeeDisplay display;
	private RandomGenerator rgen = new RandomGenerator();
}
