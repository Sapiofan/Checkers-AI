package engine;

import entities.Board;
import entities.Checker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static engine.Utils.convertNumbersToCell;
import static engine.Utils.getNumbersFromCell;

public class Computer {
    public void makeMove(int mode, Board board) {
        List<String> checkersMustBeBeaten = checkIfComputerMustBeat(board);
        if (checkersMustBeBeaten.size() != 0) {
            // beat checker
        }
        Map<Checker, List<String>> possibleMoves = getCheckersThatCanMove(board.getBlackCheckers(), board);


    }

    private List<String> checkIfComputerMustBeat(Board board) {
        List<String> possibleMoves = new ArrayList<>();
        for (Checker blackChecker : board.getBlackCheckers()) {
            String firstCellLeft = Utils.convertNumbersToCell(blackChecker.getX() - 1, blackChecker.getY() - 1);
            String firstCellRight = Utils.convertNumbersToCell(blackChecker.getX() + 1, blackChecker.getY() - 1);
            String secondCellLeft;
            String secondCellRight;
            if (firstCellLeft != null) {
                Checker checker = board.getCellChecker(firstCellLeft);
                if (checker != null && checker.getColor().equals("w")) {
                    secondCellLeft = Utils.convertNumbersToCell(blackChecker.getX() - 2, blackChecker.getY() - 2);
                    if (secondCellLeft != null) {
                        Checker checker2 = board.getCellChecker(secondCellLeft);
                        if (checker2 == null) {
                            possibleMoves.add(secondCellLeft);
                        }
                    }
                }
            }

            if (firstCellRight != null) {
                Checker checker = board.getCellChecker(firstCellRight);
                if (checker != null && checker.getColor().equals("w")) {
                    secondCellRight = Utils.convertNumbersToCell(blackChecker.getX() + 2, blackChecker.getY() - 2);
                    if (secondCellRight != null) {
                        Checker checker2 = board.getCellChecker(secondCellRight);
                        if (checker2 == null) {
                            possibleMoves.add(secondCellRight);
                        }
                    }
                }
            }
        }

        return possibleMoves;
    }

    private Map<Checker, List<String>> getCheckersThatCanMove(List<Checker> allCheckers, Board board) {
        Map<Checker, List<String>> checkers = new HashMap<>();
        for (Checker checker : allCheckers) {
            List<String> moves = checkIfCheckerCanMove(checker, board);
            if (moves.size() != 0) {
                checkers.put(checker, moves);
            }
        }

        return checkers;
    }

    public List<String> checkIfCheckerCanMove(Checker checker, Board board) {
        List<String> possibleMoves = new ArrayList<>();
        List<Integer> placement = getNumbersFromCell(checker.getCheckerCell());
        if (placement.get(0) - 1 > 0 && placement.get(1) - 1 > 0
                && board.getCellCheckerByNumbers(placement.get(0), placement.get(1)) == null) {
            possibleMoves.add(convertNumbersToCell(placement.get(0) + 1, placement.get(1) + 1));
        }
        if (placement.get(0) + 1 < 8 && placement.get(1) + 1 < 8
                && board.getCellCheckerByNumbers(placement.get(0) + 2, placement.get(1)) == null) {
            possibleMoves.add(convertNumbersToCell(placement.get(0) + 1, placement.get(1) + 1));
        }

        return possibleMoves;
    }

}
