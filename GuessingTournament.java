import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;

public class GuessingTournament {
    public static void main(String[] args) {
        Scanner consoleReader = new Scanner(System.in);
        GameRound roundEngine = new GameRound(new Random());

        int roundsPlayed = 0;
        int roundsWon = 0;
        int cumulativePoints = 0;
        boolean continuePlaying = true;

        System.out.println("Number Guessing Tournament");
        System.out.println("--------------------------");

        while (continuePlaying) {
            roundsPlayed++;
            GameRound.RoundSummary roundSummary = roundEngine.playSingleRound(consoleReader, roundsPlayed);

            if (roundSummary.isSolved()) {
                roundsWon++;
            }

            cumulativePoints += roundSummary.pointsEarned();

            System.out.println();
            System.out.println("Round " + roundsPlayed + " complete.");
            System.out.println("Round result: " + roundSummary.statusText());
            System.out.println("Attempts used: " + roundSummary.attemptsUsed() + " / " + roundSummary.attemptCap());
            System.out.println("Points earned: " + roundSummary.pointsEarned());
            System.out.println("Current score: " + cumulativePoints);
            System.out.println("Rounds won: " + roundsWon + " / " + roundsPlayed);

            continuePlaying = askToContinue(consoleReader);
        }

        System.out.println();
        System.out.println("Tournament finished.");
        System.out.println("Rounds played: " + roundsPlayed);
        System.out.println("Rounds won: " + roundsWon);
        System.out.println("Final score: " + cumulativePoints);

        consoleReader.close();
    }

    private static boolean askToContinue(Scanner consoleReader) {
        while (true) {
            System.out.print("Play another round? (y/n): ");
            String answerLine = consoleReader.nextLine().trim().toLowerCase();

            if (answerLine.equals("y") || answerLine.equals("yes")) {
                return true;
            }

            if (answerLine.equals("n") || answerLine.equals("no")) {
                return false;
            }

            System.out.println("Please answer with y or n.");
        }
    }
}

class GameRound {
    private static final int LOWER_BOUND = 1;
    private static final int UPPER_BOUND = 100;
    private static final int ATTEMPT_CAP = 7;

    private final Random numberSource;

    GameRound(Random numberSource) {
        this.numberSource = numberSource;
    }

    RoundSummary playSingleRound(Scanner consoleReader, int roundIndex) {
        int targetDigit = numberSource.nextInt(UPPER_BOUND - LOWER_BOUND + 1) + LOWER_BOUND;
        int attemptCounter = 0;
        boolean isMatchFound = false;

        System.out.println();
        System.out.println("Round " + roundIndex + ": I picked a number between " + LOWER_BOUND + " and " + UPPER_BOUND + ".");
        System.out.println("You have " + ATTEMPT_CAP + " attempts.");

        while (attemptCounter < ATTEMPT_CAP && !isMatchFound) {
            Integer userChoice = readValidChoice(consoleReader);

            if (userChoice == null) {
                continue;
            }

            attemptCounter++;

            if (userChoice < targetDigit) {
                System.out.println("Too low. Try a higher number.");
            } else if (userChoice > targetDigit) {
                System.out.println("Too high. Try a lower number.");
            } else {
                isMatchFound = true;
                System.out.println("Correct. You found it.");
            }
        }

        int pointsEarned = isMatchFound ? (ATTEMPT_CAP - attemptCounter + 1) * 10 : 0;
        String statusText = isMatchFound ? "cleared" : "failed";

        if (!isMatchFound) {
            System.out.println("The number was " + targetDigit + ".");
        }

        return new RoundSummary(isMatchFound, attemptCounter, ATTEMPT_CAP, pointsEarned, statusText);
    }

    private Integer readValidChoice(Scanner consoleReader) {
        while (true) {
            System.out.print("Enter your guess: ");

            try {
                int enteredValue = consoleReader.nextInt();
                consoleReader.nextLine();

                if (enteredValue < LOWER_BOUND || enteredValue > UPPER_BOUND) {
                    System.out.println("Enter a number from " + LOWER_BOUND + " to " + UPPER_BOUND + ".");
                    continue;
                }

                return enteredValue;
            } catch (InputMismatchException inputProblem) {
                consoleReader.nextLine();
                System.out.println("That was not a number. Try again.");
            }
        }
    }

    static final class RoundSummary {
        private final boolean solved;
        private final int attemptsUsed;
        private final int attemptCap;
        private final int pointsEarned;
        private final String statusText;

        RoundSummary(boolean solved, int attemptsUsed, int attemptCap, int pointsEarned, String statusText) {
            this.solved = solved;
            this.attemptsUsed = attemptsUsed;
            this.attemptCap = attemptCap;
            this.pointsEarned = pointsEarned;
            this.statusText = statusText;
        }

        boolean isSolved() {
            return solved;
        }

        int attemptsUsed() {
            return attemptsUsed;
        }

        int attemptCap() {
            return attemptCap;
        }

        int pointsEarned() {
            return pointsEarned;
        }

        String statusText() {
            return statusText;
        }
    }
}