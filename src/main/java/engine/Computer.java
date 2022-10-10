package engine;

import entities.Board;
import entities.Checker;

import java.util.*;

import static engine.Game.checkIfPlayerMustBeat;
import static engine.Utils.convertNumbersToCell;
import static engine.Utils.getNumbersFromCell;

public class Computer {

    private Map<Checker, String> strategy = new LinkedHashMap<>();

    public void makeMove(int mode, Board board) {
        Map<Checker, List<String>> checkersMustBeBeaten = checkIfComputerMustBeat(board);
        beatCheckers(checkersMustBeBeaten);

        Board tempBoard = new Board();
        tempBoard.setWhiteCheckers(board.getWhiteCheckers());
        tempBoard.setBlackCheckers(board.getBlackCheckers());

//        Map<Checker, List<String>> possibleMoves = getCheckersThatCanMove(tempBoard.getBlackCheckers(), tempBoard);

        // 1. beat the most rival checkers and lose minimum computer checkers
        // 2. remove possibility of getting King by rival
        // 3. get Kings
        List<List<Map<String, Checker>>> allPaths = new ArrayList<>();
        for (int i = 0; i < mode; i++) {
            tempBoard.setWhiteCheckers(board.getWhiteCheckers()); // update initial state
            tempBoard.setBlackCheckers(board.getBlackCheckers());
            for (List<Map<String, Checker>> allPath : allPaths) {
                Board board1 = getBoardState(tempBoard, allPath); // get board state for a certain path
                List<Checker> allCheckers; // get checkers of board state
                Map<String, Checker> move = new HashMap<>();
                if (board.getCurrentPlayer().equals("b")) {
                    allCheckers = board1.getBlackCheckers();
                    Map<Checker, List<String>> botMustBeat = checkIfComputerMustBeat(board1);
                    Checker checker = beatCheckers(botMustBeat);
                    move.put(checker.getCheckerCell(), checker);
                    allPath.add(move);
                } else {
                    allCheckers = board1.getWhiteCheckers();
                    Map<Checker, List<String>> playerMustBeat = checkIfPlayerMustBeat(board1);
                    Checker checker = beatCheckers(playerMustBeat);
                    move.put(checker.getCheckerCell(), checker);
                    allPath.add(move);
                }
                Map<Checker, List<String>> possibleMoves = getCheckersThatCanMove(allCheckers, board1); // get moves of checkers
                for (Map.Entry<Checker, List<String>> checkerListEntry : possibleMoves.entrySet()) {
                    if (board1.getCurrentPlayer().equals("w")) { // make move (add checker and where it should be moved)

                    } else {

                    }
                }
            }
            if (i == 0) {
                continue;
            }
            int lastSize = allPaths.get(allPaths.size() - 1).size();
            for (List<Map<String, Checker>> allPath : allPaths) {
                if (allPath.size() < lastSize) {
                    allPath.remove(allPath);
                }
            }
        }
//        Map<Checker, Map<Checker, String>> possibleStrategies = new HashMap<>();
//        for (Map.Entry<Checker, List<String>> checkerListEntry : possibleMoves.entrySet()) {
//            tempBoard.setWhiteCheckers(board.getWhiteCheckers());
//            tempBoard.setBlackCheckers(board.getBlackCheckers());
//            possibleStrategies.put(checkerListEntry.getKey(),
//                    calculateBestWayForChecker(checkerListEntry.getKey(), checkerListEntry.getValue(), mode, tempBoard, board));
//        }

//        possibleStrategies = sortStrategiesByEffectiveness(possibleStrategies);
//        strategy = possibleStrategies.entrySet().stream().findFirst().map(Map.Entry::getValue).orElse(strategy);

        // choose the best strategy for computer (minimum loses for preventing it)
        Map<Checker, String> strategyForPreventingRivalKing = checkIfUserCanGetKing();

        // choose the best strategy for computer (minimum loses, maximum beats for getting king)
        Map<Checker, String> strategyForGettingKing = checkIfBotCanGetKing();


        // compare all 3 strategies and decide which is the best, assign it to global strategy and make first move

    }

    private Map<Checker, List<String>> checkIfComputerMustBeat(Board board) {
//        List<String> possibleMoves = new ArrayList<>();
        Map<Checker, List<String>> possibleMoves = new HashMap<>();
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
                            List<String> moves = possibleMoves.get(blackChecker);
                            if (moves == null) {
                                moves = new ArrayList<>();
                            }
                            moves.add(secondCellLeft);
                            possibleMoves.put(blackChecker, moves);
//                            possibleMoves.add(secondCellLeft);
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
                            List<String> moves = possibleMoves.get(blackChecker);
                            if (moves == null) {
                                moves = new ArrayList<>();
                            }
                            moves.add(secondCellRight);
                            possibleMoves.put(blackChecker, moves);
//                            possibleMoves.add(secondCellRight);
                        }
                    }
                }
            }
        }

        return possibleMoves;
    }

    private Board getBoardState(Board init, List<Map<String, Checker>> path) {
        for (Map<String, Checker> checkerStringMap : path) {
            for (Map.Entry<String, Checker> checkerStringEntry : checkerStringMap.entrySet()) {
                init.getCellChecker(checkerStringEntry.getValue().getCheckerCell())
                        .setCheckerCell(checkerStringEntry.getKey());
            }
        }

        return init;
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

    private Checker beatCheckers(Map<Checker, List<String>> checkersMustBeBeaten) {
        if (checkersMustBeBeaten.size() != 0) {
            if (checkersMustBeBeaten.size() > 1) {
                // where bot should beat checkers
                return null;
            }
            for (Map.Entry<Checker, List<String>> checkerListEntry : checkersMustBeBeaten.entrySet()) {
                if (checkerListEntry.getKey().getCheckerCell().equals("b")) {
                    if (checkerListEntry.getValue().size() > 1) {
                        // decide which checker must be beaten
                        return null;
                    }
                    Checker checker = checkerListEntry.getKey();
                    if (checkerListEntry.getValue().get(0).charAt(0) - checkerListEntry.getKey().getCheckerCell().charAt(0) == 1) {

                        checker.setX(checker.getX() - 2);
                        checker.setY(checker.getY() - 2);
                    } else {
                        checker.setX(checker.getX() + 2);
                        checker.setY(checker.getY() - 2);
                    }

                    return checker;
                } else {
                    if (checkerListEntry.getValue().size() > 1) {
                        // decide which checker must be beaten
                        return null;
                    }
                    Checker checker = checkerListEntry.getKey();
                    if (checkerListEntry.getValue().get(0).charAt(0) - checkerListEntry.getKey().getCheckerCell().charAt(0) == 1) {
                        checker.setX(checker.getX() - 2);
                        checker.setY(checker.getY() + 2);
                    } else {
                        checker.setX(checker.getX() + 2);
                        checker.setY(checker.getY() + 2);
                    }

                    return checker;
                }
            }
        }

        return null;
    }

    public List<String> checkIfCheckerCanMove(Checker checker, Board board) {
        List<String> possibleMoves = new ArrayList<>();
        List<Integer> placement = getNumbersFromCell(checker.getCheckerCell());
        int x = placement.get(0);
        int y = placement.get(1);
        if (board.getCurrentPlayer().equals("b")) {
            if (x - 1 >= 0 && placement.get(1) - 1 >= 0
                    && board.getCellCheckerByNumbers(x, y) == null) {
                possibleMoves.add(convertNumbersToCell(x + 1, y + 1));
            }
            if (x + 1 < 8 && placement.get(1) - 1 >= 0
                    && board.getCellCheckerByNumbers(x + 2, y) == null) {
                possibleMoves.add(convertNumbersToCell(x + 1, y + 1));
            }
        } else {
            if (x - 1 > 0 && y + 1 < 8
                    && board.getCellCheckerByNumbers(x, y + 2) == null) {
                possibleMoves.add(convertNumbersToCell(x + 1, y + 1));
            }
            if (x + 1 < 8 && y + 1 < 8
                    && board.getCellCheckerByNumbers(x + 2, y + 2) == null) {
                possibleMoves.add(convertNumbersToCell(x + 1, y + 1));
            }
        }

        return possibleMoves;
    }

//    private Map<Checker, String> calculateBestWayForChecker(Checker checker, List<String> possibleMoves,
//                                                            int mode, Board board, Board init) {
//
//        Map<Checker, String> strategy1 = null;
//        Map<Checker, String> strategy2 = null;
//
//        for (int i = 0; i < possibleMoves.size(); i++) {
//            String possibleMove = possibleMoves.get(i);
//            if (i == 0) {
//                strategy1 = move(checker, possibleMove, mode, board, init);
//            } else {
//                board.setWhiteCheckers(init.getWhiteCheckers());
//                board.setBlackCheckers(init.getBlackCheckers());
//                strategy2 = move(checker, possibleMove, mode, board, init);
//            }
//        }
//
//        if (strategy2 == null) {
//            return strategy1;
//        }
//
//        return bestStrategyForOneChecker(strategy1, strategy2, board, init);
//    }

    // see the best outcome for first move of a certain checker
//    private Map<Checker, String> move(Checker checker, String move, int mode, Board board, Board init) {
//        if (mode == 0) {
//            return new LinkedHashMap<>();
//        }
//
////        Map<Checker, String> strategy = new LinkedHashMap<>();
//
//        List<Map<Checker, String>> bestWays = new ArrayList<>();
//        Map<Checker, List<String>> whites = getCheckersThatCanMove(board.getWhiteCheckers(), board);
//        Map<Checker, List<String>> blacks = getCheckersThatCanMove(board.getBlackCheckers(), board);
//        if(checker.getColor().equals("b")) {
//            for (Map.Entry<Checker, List<String>> checkerListEntry : whites.entrySet()) {
//                bestWays.add(calculateBestWayForChecker(checkerListEntry.getKey(),
//                        checkerListEntry.getValue(), mode - 1, board, init));
//            }
//        } else {
//            for (Map.Entry<Checker, List<String>> checkerListEntry : blacks.entrySet()) {
//                bestWays.add(calculateBestWayForChecker(checkerListEntry.getKey(),
//                        checkerListEntry.getValue(), mode - 1, board, init));
//            }
//        }
//
//        List<String> beatComputer = checkIfPlayerMustBeat(board);
//
//
//
//        Map<Checker, List<String>> checkersMustBeBeaten = checkIfComputerMustBeat(board);
//        beatCheckers(checkersMustBeBeaten);
//
//
//        return strategy;
//    }

    // module strategy and see what is more effective
    private Map<Checker, String> bestStrategyForOneChecker(Map<Checker, String> strategy1,
                                                           Map<Checker, String> strategy2, Board board, Board init) {

        for (Map.Entry<Checker, String> checkerStringEntry : strategy1.entrySet()) {

        }

        return strategy1;
    }


    // the most effective strategies are first
    private Map<Checker, Map<Checker, String>> sortStrategiesByEffectiveness(Map<Checker, Map<Checker, String>> possibleStr) {
        Map<Checker, Map<Checker, String>> sortedMap = new LinkedHashMap<>();


        return sortedMap;
    }

    private Map<Checker, String> checkIfBotCanGetKing() {

        return null;
    }

    private Map<Checker, String> checkIfUserCanGetKing() {

        return null;
    }
}
