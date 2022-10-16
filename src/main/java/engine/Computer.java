package engine;

import entities.Board;
import entities.Checker;

import java.util.*;
import java.util.stream.Collectors;

import static engine.Game.checkIfPlayerMustBeat;
import static engine.Utils.convertNumbersToCell;
import static engine.Utils.getNumbersFromCell;

public class Computer {

    private List<Map<String, Checker>> strategy = new ArrayList<>();

    public void makeMove(int mode, Board board) {
        Map<Checker, List<String>> checkersMustBeBeaten = checkIfComputerMustBeat(board);
        List<Map<String, Checker>> beatenCheckers = beatCheckers(checkersMustBeBeaten);
        List<List<Map<String, Checker>>> allPaths = new ArrayList<>();
        boolean flag = false;

        if (beatenCheckers.size() != 0) {
            for (Map<String, Checker> beatenChecker : beatenCheckers) {
                List<Map<String, Checker>> path = new ArrayList<>();
                path.add(beatenChecker);
                addAdditionalBeats(board, path, allPaths);
            }
            flag = true;
        }

        Board tempBoard;
        tempBoard = getTempBoard(board);
        tempBoard.setCurrentPlayer("b");

        Map<Checker, List<String>> possibleMoves = getCheckersThatCanMove(tempBoard.getBlackCheckers(), tempBoard);

        if(!flag) {
            for (Map.Entry<Checker, List<String>> checkerListEntry : possibleMoves.entrySet()) {
                for (String s : checkerListEntry.getValue()) {
                    List<Map<String, Checker>> path = new ArrayList<>();
                    Map<String, Checker> map = new HashMap<>();
                    map.put(s, checkerListEntry.getKey());
                    path.add(map);
                    allPaths.add(path);
                }
            }
        }

        int counter = allPaths.size();

        for (int i = 1; i < mode; i++) { // default step = 1
            int stopFlag = allPaths.get(allPaths.size() - 1).size();
            for (int j = 0; j < allPaths.size(); j++) {
                tempBoard = getTempBoard(board); // update initial state
                if (i % 2 == 0) {
                    tempBoard.setCurrentPlayer("b");
                } else {
                    tempBoard.setCurrentPlayer("w");
                }
                List<Map<String, Checker>> allPath = allPaths.get(j);
                Board board1 = getBoardState(tempBoard, allPath); // get board state for a certain path
                List<Checker> allCheckers; // get checkers of board state
                Map<String, Checker> move = new HashMap<>();
                if (tempBoard.getCurrentPlayer().equals("b")) {
                    allCheckers = board1.getBlackCheckers();
                    Map<Checker, List<String>> botMustBeat = checkIfComputerMustBeat(board1);
                    beatenCheckers = beatCheckers(botMustBeat);
                    if (beatenCheckers.size() != 0) {
                        for (Map<String, Checker> beatenChecker : beatenCheckers) {
                            List<Map<String, Checker>> path = new ArrayList<>(allPath);
                            path.add(beatenChecker);
                            addAdditionalBeats(board, path, allPaths);
                        }
                        continue;
                    }
                } else {
                    allCheckers = board1.getWhiteCheckers();
                    Map<Checker, List<String>> playerMustBeat = checkIfPlayerMustBeat(board1);
                    beatenCheckers = beatCheckers(playerMustBeat);
                    if (beatenCheckers.size() != 0) {
                        for (Map<String, Checker> beatenChecker : beatenCheckers) {
                            List<Map<String, Checker>> path = new ArrayList<>(allPath);
                            path.add(beatenChecker);
                            addAdditionalBeats(board, path, allPaths);
                        }
                        continue;
                    }
                }
                possibleMoves = getCheckersThatCanMove(allCheckers, board1); // get moves of checkers
                for (Map.Entry<Checker, List<String>> checkerListEntry : possibleMoves.entrySet()) {
                    for (String s : checkerListEntry.getValue()) {
                        Map<String, Checker> map = new HashMap<>();
                        map.put(s, checkerListEntry.getKey());
                        List<Map<String, Checker>> path = new ArrayList<>(allPath);
                        path.add(map);
                        allPaths.add(path);
                    }
                }
                if (stopFlag < allPath.size()) {
                    break;
                }
            }
            for (int i1 = 0; i1 < counter; i1++) {
                allPaths.remove(i1);
            }
            counter = allPaths.size();
//            int lastSize = allPaths.get(allPaths.size() - 1).size();
//            allPaths.removeIf(allPath -> allPath.size() < lastSize);
        }

        strategy = bestStrategy(allPaths, board);
        moveByStrategy(board);
//        strategy.forEach(stringCheckerMap -> stringCheckerMap.forEach((key, value) -> value.setCheckerCell(key)));
        strategy.remove(0);

    }

    private void moveByStrategy(Board init) {
        for (Map<String, Checker> stringCheckerMap : strategy) {
            for (Map.Entry<String, Checker> stringCheckerEntry : stringCheckerMap.entrySet()) {
                for (Checker blackChecker : init.getBlackCheckers()) {
                    if(stringCheckerEntry.getValue().getCheckerCell().equals(blackChecker.getCheckerCell())) {
                        blackChecker.setCheckerCell(stringCheckerEntry.getKey());
                    }
                }
            }
        }
    }

    private void addAdditionalBeats(Board board, List<Map<String, Checker>> path, List<List<Map<String, Checker>>> allPaths) {
        Board tempBoard = getTempBoard(board);
        Board state = getBoardState(tempBoard, path);
        Map<Checker, List<String>> mustBeat = checkIfComputerMustBeat(state);
        boolean flag = false;
        for (Map.Entry<Checker, List<String>> checkerListEntry : mustBeat.entrySet()) {
            if (path.get(path.size() - 1).entrySet().stream().findFirst().get().getValue().equals(checkerListEntry.getKey())) {
                for (String s : checkerListEntry.getValue()) {
                    Map<String, Checker> move = new HashMap<>();
                    move.put(s, checkerListEntry.getKey());
                    List<Map<String, Checker>> newPath = new ArrayList<>(path);
                    newPath.add(move);
                    addAdditionalBeats(board, newPath, allPaths);
                    allPaths.add(newPath);
                }
                flag = true;
            }
        }
        if(!flag) {
            allPaths.add(path);
        }
    }

    private Map<Checker, List<String>> checkIfComputerMustBeat(Board board) {
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
                        }
                    }
                }
            }
        }

        return possibleMoves;
    }

    private Board getBoardState(Board init, List<Map<String, Checker>> path) {
        System.out.println(init);
        for (Map<String, Checker> stringCheckerMap : path) {
            for (Map.Entry<String, Checker> stringCheckerEntry : stringCheckerMap.entrySet()) {
                System.out.println(stringCheckerEntry.getValue().getCheckerCell() + "->" + stringCheckerEntry.getKey());
            }
        }
        boolean flag = false;
        for (Map<String, Checker> stringCheckerMap : path) {
            for (Map.Entry<String, Checker> stringCheckerEntry : stringCheckerMap.entrySet()) {
                for (Checker blackChecker : init.getBlackCheckers()) {
                    if(blackChecker.getId().equals(stringCheckerEntry.getValue().getId())) {
                        blackChecker.setCheckerCell(stringCheckerEntry.getKey());
                        flag = true;
                        break;
                    }
                }
                if(flag) {
                    flag = false;
                    break;
                }
            }
        }

        System.out.println(init + "\nBoard after");

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

    private List<Map<String, Checker>> beatCheckers(Map<Checker, List<String>> checkersMustBeBeaten) {
        List<Map<String, Checker>> beatenCheckers = new ArrayList<>();
        Board tempBoard;
        for (Map.Entry<Checker, List<String>> checkerListEntry : checkersMustBeBeaten.entrySet()) {
            for (String s : checkerListEntry.getValue()) {
                Map<String, Checker> move = new LinkedHashMap<>();
                // just add moves (beats) to strategy
                move.put(s, checkerListEntry.getKey());
                beatenCheckers.add(move);
//                while(true) {
//                    beatenCheckers.add(move);
//                    tempBoard = getTempBoard(init);
//                    Map<Checker, List<String>> additionalBeats = checkIfComputerMustBeat(getBoardState(tempBoard, path));
//                    for (Map.Entry<Checker, List<String>> listEntry : additionalBeats.entrySet()) {
//                        if(listEntry.getKey().equals(checkerListEntry.getKey())) {
//                            move.put(listEntry.)
//                        }
//                    }
//                }
            }
        }

        return beatenCheckers;

//        if (checkersMustBeBeaten.size() != 0) {
//            if (checkersMustBeBeaten.size() > 1) {
//                // where bot should beat checkers
//                return decideWhichCheckerBeatIfMany(checkersMustBeBeaten, steps);
//            }
//            for (Map.Entry<Checker, List<String>> checkerListEntry : checkersMustBeBeaten.entrySet()) {
//                if (checkerListEntry.getKey().getCheckerCell().equals("b")) {
//                    if (checkerListEntry.getValue().size() > 1) {
//                        // decide which checker must be beaten
//                        return decideWhichCheckerBeatIfOne(checkersMustBeBeaten, steps);;
//                    }
//                    Checker checker = checkerListEntry.getKey();
//                    if (checkerListEntry.getValue().get(0).charAt(0) - checkerListEntry.getKey().getCheckerCell().charAt(0) == 1) {
//                        checker.setX(checker.getX() - 2);
//                        checker.setY(checker.getY() - 2);
//                    } else {
//                        checker.setX(checker.getX() + 2);
//                        checker.setY(checker.getY() - 2);
//                    }
//
//                    return checker;
//                } else {
//                    if (checkerListEntry.getValue().size() > 1) {
//                        // decide which checker must be beaten
//                        return null;
//                    }
//                    Checker checker = checkerListEntry.getKey();
//                    if (checkerListEntry.getValue().get(0).charAt(0) - checkerListEntry.getKey().getCheckerCell().charAt(0) == 1) {
//                        checker.setX(checker.getX() - 2);
//                        checker.setY(checker.getY() + 2);
//                    } else {
//                        checker.setX(checker.getX() + 2);
//                        checker.setY(checker.getY() + 2);
//                    }
//
//                    return checker;
//                }
//            }
//        }

//        return null;
    }

    public List<String> checkIfCheckerCanMove(Checker checker, Board board) {
        List<String> possibleMoves = new ArrayList<>();
        List<Integer> placement = getNumbersFromCell(checker.getCheckerCell());
        int x = placement.get(0);
        int y = placement.get(1);
        if (board.getCurrentPlayer().equals("b")) {
            if (x - 1 >= 0 && placement.get(1) - 1 >= 0
                    && board.getCellCheckerByNumbers(x, y) == null) {
                possibleMoves.add(convertNumbersToCell(x + 1 - 1, y + 1 - 1));
            }
            if (x + 1 < 8 && placement.get(1) - 1 >= 0
                    && board.getCellCheckerByNumbers(x + 2, y) == null) {
                possibleMoves.add(convertNumbersToCell(x + 1 + 1, y + 1 - 1));
            }
        } else {
            if (x - 1 > 0 && y + 1 < 8
                    && board.getCellCheckerByNumbers(x, y + 2) == null) {
                possibleMoves.add(convertNumbersToCell(x + 1 - 1, y + 1 + 1));
            }
            if (x + 1 < 8 && y + 1 < 8
                    && board.getCellCheckerByNumbers(x + 2, y + 2) == null) {
                possibleMoves.add(convertNumbersToCell(x + 1 + 1, y + 1 + 1));
            }
        }

        return possibleMoves;
    }

    // 1. the biggest number of rival beaten checkers (beaten checker = +2)
    // 2. not allow to get King (getting King = +4)
    // 3. the smallest number of computer beaten checkers and not more than rival (beaten checker = -2)
    // 4. getting of King (getting king = +3)
    private List<Map<String, Checker>> bestStrategy(List<List<Map<String, Checker>>> allPaths, Board board) {
        Board tempBoard = getTempBoard(board);
        Map<List<Map<String, Checker>>, Integer> gradeStrategies = new HashMap<>();
        Set<Checker> potentialKings = getPotentialKings(allPaths, board, tempBoard);
        for (List<Map<String, Checker>> allPath : allPaths) {
            Board state = getBoardState(tempBoard, allPath);
            int potentialKingsExist = 0;
            for (Checker whiteChecker : state.getWhiteCheckers()) {
                for (Checker potentialKing : potentialKings) {
                    if (whiteChecker.equals(potentialKing)) {
                        potentialKingsExist++;
                    }
                }
            }
            gradeStrategies.put(allPath,
                    ((board.getWhiteCheckers().size() - state.getWhiteCheckers().size()) * 2) -
                            ((board.getBlackCheckers().size() - state.getBlackCheckers().size()) * 2) +
                            (getNumberOfComputerKings(allPath, board) * 3) +
                            (potentialKings.size() - potentialKingsExist) * 4);
        }

        return sortByValue(gradeStrategies).entrySet()
                .stream()
                .findFirst()
                .map(Map.Entry::getKey)
                .orElse(null);

    }

    private int getNumberOfComputerKings(List<Map<String, Checker>> allPath, Board board) {
        Board tempBoard = getTempBoard(board);
        Board state = getBoardState(tempBoard, allPath);

        return (int) state.getBlackCheckers().stream().filter(Checker::isKing).count();
    }

    private Set<Checker> getPotentialKings(List<List<Map<String, Checker>>> allPaths, Board board, Board tempBoard) {
        Set<Checker> checkers = new HashSet<>();
        for (List<Map<String, Checker>> allPath : allPaths) {
            List<Checker> kings = checkIfRivalKingExistInPath(allPath, board);
            checkers.addAll(kings);
        }

        return checkers;
    }

    private List<Checker> checkIfRivalKingExistInPath(List<Map<String, Checker>> allPath, Board init) {
        List<Checker> checkers = new ArrayList<>();
        for (int i = allPath.size() - 1; i >= 0; i--) {
            Board tempBoard = getTempBoard(init);
            Board board = getBoardState(tempBoard, allPath.subList(0, allPath.size() - i));
            board.getWhiteCheckers().stream().filter(Checker::isKing).forEach(checkers::add);
        }

        return checkers;
    }

    private Board getTempBoard(Board board) {
        Board tempBoard = new Board();
        List<Checker> tempBlack = board.getBlackCheckers()
                .stream()
                .map(blackChecker -> new Checker(blackChecker.getId(), blackChecker.getX(), blackChecker.getY(), "b"))
                .collect(Collectors.toList());
        List<Checker> tempWhite = board.getWhiteCheckers()
                .stream()
                .map(whiteChecker -> new Checker(whiteChecker.getId(), whiteChecker.getX(), whiteChecker.getY(), "w"))
                .collect(Collectors.toList());
        tempBoard.setWhiteCheckers(tempWhite);
        tempBoard.setBlackCheckers(tempBlack);

        return tempBoard;
    }

    private HashMap<List<Map<String, Checker>>, Integer> sortByValue(Map<List<Map<String, Checker>>, Integer> hm) {
        List<Map.Entry<List<Map<String, Checker>>, Integer>> list = new LinkedList<>(hm.entrySet());

        list.sort(Map.Entry.comparingByValue());

        HashMap<List<Map<String, Checker>>, Integer> temp = new LinkedHashMap<>();
        for (Map.Entry<List<Map<String, Checker>>, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }

        return temp;
    }
}
