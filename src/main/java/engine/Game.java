package engine;

import entities.Board;
import entities.Checker;

import java.util.*;

public class Game {
    public void start() {
        Scanner scanner = new Scanner(System.in);
        Board board = new Board();
        Computer computer = new Computer();
        System.out.println("Choose a mode:\n" +
                "1 >> easy\n" +
                "2 >> medium\n" +
                "3 >> hard\n" +
                "4 >> insane\n");
        int mode = scanner.nextInt() * 2;
        String checker, move;

        // fix moving
        // add King logic
        // check if winner exist
        while (winnerNotExist(board)) {
            System.out.println(board);
            System.out.println("Choose checker: ");
            checker = checkMoveInput(scanner.next(), false);
            while (!checkIfExistCheckerOnCell(board, checker)) {
                System.out.println("There is no your checker on this cell. Input new checker");
                checker = checkMoveInput(scanner.next(), false);
            }
            System.out.print("Your move: ");
            move = checkMoveInput(scanner.next(), false);
            makePlayerMove(board, checker, move);
            System.out.println(board);
            System.out.println("Computer turn...");
            board.setCurrentPlayer("b");
            computer.makeMove(mode, board);
            board.setCurrentPlayer("w");
        }
    }

    public boolean winnerNotExist(Board board) {
        if (board.getWhiteCheckers().size() == 0 || board.getBlackCheckers().size() == 0) {
            return false;
        }

        return true;
    }

    public String checkMoveInput(String move, boolean exitMode) {
        Scanner scanner = new Scanner(System.in);
        move = move.toLowerCase();
        String[] chars = move.split("");
        while (move.length() != 2 && (chars[0].charAt(0) < 'a' && chars[0].charAt(0) > 'h')
                && (chars[1].charAt(0) < '1' && chars[1].charAt(0) > '9')) {
            System.out.println("Such cell doesn't exist. Example of correct input: a4");
            move = scanner.next().toLowerCase();
            if (move.equals("ex") && exitMode) {
                return move;
            }
        }

        return move;
    }

    public boolean checkIfExistCheckerOnCell(Board board, String cell) {
        return board.getWhiteCheckers().stream().anyMatch(whiteChecker -> whiteChecker.getCheckerCell().equals(cell));
    }

    public void makePlayerMove(Board board, String checker, String move) {
        Scanner scanner = new Scanner(System.in);
        while (!checkIfCheckerCanMove(board, checker, move)) {
            System.out.println("You can't move there. Input other cell for moving or 'ex' to choose other checker");
            move = checkMoveInput(scanner.next(), true);
            if (move.equals("ex")) {
                return;
            }
        }
    }

    public boolean checkIfCheckerCanMove(Board board, String checker, String move) {
        Map<Checker, List<String>> checkersThatMustBeBeaten = checkIfPlayerMustBeat(board);
        boolean flag = false;
        if (checkersThatMustBeBeaten.size() != 0) { // what do when here
            for (Map.Entry<Checker, List<String>> checkerListEntry : checkersThatMustBeBeaten.entrySet()) {
                for (String s : checkerListEntry.getValue()) {
                    if(s.contains(move)) {
                        flag = true;
                        break;
                    }
                }
                if(flag) {
                    break;
                }
            }
            System.out.println("You can't move there. You must beat a checker.");
        }

        if (flag) {
            char currentCheckerLetter = checker.charAt(0);
            char currentCheckerNumber = checker.charAt(1);
            for (Map.Entry<Checker, List<String>> checkerListEntry : checkersThatMustBeBeaten.entrySet()) {
                for (String s : checkerListEntry.getValue()) {
                    char beatenCheckerLetter = s.charAt(0);
                    char beatenCheckerNumber = s.charAt(1);
                    if (currentCheckerLetter - 1 == beatenCheckerLetter && currentCheckerNumber + 1 == beatenCheckerNumber) {
                        Checker checker1 = board.getCellChecker(checker);
                        board.getBlackCheckers().remove(board.getCellChecker(checker));
                        checker1.setX(checker1.getX() - 2);
                        checker1.setY(checker1.getY() + 2);
                        return true;
                    } else if (currentCheckerLetter + 1 == beatenCheckerLetter && currentCheckerNumber + 1 == beatenCheckerNumber) {
                        Checker checker1 = board.getCellChecker(checker);
                        board.getBlackCheckers().remove(board.getCellChecker(checker));
                        checker1.setX(checker1.getX() + 2);
                        checker1.setY(checker1.getY() + 2);
                        return true;
                    }
                }
            }
        }

        if (!checkIfWhiteCell(board, move)) {
            return false;
        }

        return userCanMoveChecker(board, checker, move);
    }

    public static Map<Checker, List<String>> checkIfPlayerMustBeat(Board board) {
        Map<Checker, List<String>> possibleMoves = new HashMap<>();
        for (Checker whiteChecker : board.getWhiteCheckers()) {
            String firstCellLeft = Utils.convertNumbersToCell(whiteChecker.getX() - 1, whiteChecker.getY() + 1);
            String firstCellRight = Utils.convertNumbersToCell(whiteChecker.getX() + 1, whiteChecker.getY() + 1);
            String secondCellLeft;
            String secondCellRight;
            if (firstCellLeft != null) {
                Checker checker = board.getCellChecker(firstCellLeft);
                if (checker != null && checker.getColor().equals("b")) {
                    secondCellLeft = Utils.convertNumbersToCell(whiteChecker.getX() - 2, whiteChecker.getY() + 2);
                    if (secondCellLeft != null) {
                        Checker checker2 = board.getCellChecker(secondCellLeft);
                        if (checker2 == null) {
                            List<String> list = new ArrayList<>();
                            list.add(secondCellLeft);
                            possibleMoves.put(whiteChecker, list);
                        }
                    }
                }
            }

            if (firstCellRight != null) {
                Checker checker = board.getCellChecker(firstCellRight);
                if (checker != null && checker.getColor().equals("b")) {
                    secondCellRight = Utils.convertNumbersToCell(whiteChecker.getX() + 2, whiteChecker.getY() + 2);
                    if (secondCellRight != null) {
                        Checker checker2 = board.getCellChecker(secondCellRight);
                        if (checker2 == null) {
                            List<String> list = possibleMoves.get(whiteChecker);
                            if(possibleMoves.get(whiteChecker) == null) {
                                list = new ArrayList<>();
                            }
                            list.add(secondCellRight);
                            possibleMoves.put(whiteChecker, list);
                        }
                    }
                }
            }
        }

        return possibleMoves;
    }

    public boolean checkIfWhiteCell(Board board, String move) {
        return !board.getWhiteCells().contains(move);
    }

    public boolean userCanMoveChecker(Board board, String checker, String move) {
        Checker cellChecker = board.getCellChecker(move);
        if (cellChecker == null) {
            List<Integer> initialPosition = Utils.getNumbersFromCell(checker);
            List<Integer> movement = Utils.getNumbersFromCell(move);
            if ((initialPosition.get(0) + 1 == movement.get(0) && initialPosition.get(1) + 1 == movement.get(1))
                    || initialPosition.get(0) - 1 == movement.get(0) && initialPosition.get(1) + 1 == movement.get(1)) {
                Checker moveCh = board.getCellChecker(checker);
                moveCh.setX(movement.get(0) + 1);
                moveCh.setY(movement.get(1) + 1);

                return true;
            }
        }
        return false;
    }
}
