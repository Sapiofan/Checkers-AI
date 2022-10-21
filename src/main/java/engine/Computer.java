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

        if (!flag) {
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
            counter = allPaths.size();
            for (int j = 0; j < counter; j++) {
                tempBoard = getTempBoard(board); // update initial state
                if (i % 2 == 0) {
                    tempBoard.setCurrentPlayer("b");
                } else {
                    tempBoard.setCurrentPlayer("w");
                }
                List<Map<String, Checker>> allPath = allPaths.get(j);
                Board board1 = getBoardState(tempBoard, allPath); // get board state for a certain path
                List<Checker> allCheckers; // get checkers of board state
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
                if(possibleMoves.isEmpty()) {
                    allPaths.add(new ArrayList<>(allPath));
                    continue;
                }
                for (Map.Entry<Checker, List<String>> checkerListEntry : possibleMoves.entrySet()) {
                    for (String s : checkerListEntry.getValue()) {
                        Map<String, Checker> map = new HashMap<>();
                        map.put(s, checkerListEntry.getKey());
                        List<Map<String, Checker>> path = new ArrayList<>(allPath);
                        path.add(map);
                        allPaths.add(path);
                    }
                }
            }
            for (int i1 = 0; i1 < counter; i1++) {
                allPaths.remove(0);
            }
        }

        strategy = bestStrategy(allPaths, board);
        Checker lastMoved;
        while (true) {
            moveByStrategy(board);
            if (strategy.size() < 2) {
                break;
            }
            lastMoved = strategy.get(0).entrySet().stream().findFirst().get().getValue();
            strategy.remove(0);
            if (!strategy.get(0).entrySet().stream().findFirst().get().getValue().getId()
                    .equals(lastMoved.getId())) {
                break;
            }
        }

        System.out.println(counter);

    }

    private void moveByStrategy(Board init) {
        for (Map<String, Checker> stringCheckerMap : strategy) {
            for (Map.Entry<String, Checker> stringCheckerEntry : stringCheckerMap.entrySet()) {
                for (Checker blackChecker : init.getBlackCheckers()) {
                    if (stringCheckerEntry.getValue().getCheckerCell().equals(blackChecker.getCheckerCell())) {
                        killBeatenWhiteChecker(stringCheckerEntry.getKey(), blackChecker, init);
                        blackChecker.setCheckerCell(stringCheckerEntry.getKey());
                        if (stringCheckerEntry.getKey().charAt(1) == '1') {
                            blackChecker.setKing(true);
                        }
                        break;
                    }
                }
                break;
            }
            break;
        }
    }

    private void killBeatenWhiteChecker(String cell, Checker blackChecker, Board init) {
        if (blackChecker.isKing() && (cell.charAt(0) - blackChecker.getCheckerCell().charAt(0) == -2)
                && (cell.charAt(1) - blackChecker.getCheckerCell().charAt(1) == 2)) {
            String beatenCheckerCell = Character.toString(blackChecker.getCheckerCell().charAt(0) - 1) +
                    Character.toString(blackChecker.getCheckerCell().charAt(1) + 1);
            init.getWhiteCheckers().remove(init.getCellChecker(beatenCheckerCell));
        } else if (blackChecker.isKing() && (cell.charAt(0) - blackChecker.getCheckerCell().charAt(0) == 2)
                && (cell.charAt(1) - blackChecker.getCheckerCell().charAt(1) == 2)) {
            String beatenCheckerCell = Character.toString(blackChecker.getCheckerCell().charAt(0) + 1) +
                    Character.toString(blackChecker.getCheckerCell().charAt(1) + 1);
            init.getWhiteCheckers().remove(init.getCellChecker(beatenCheckerCell));
        } else if (cell.charAt(0) - blackChecker.getCheckerCell().charAt(0) == -2
                && cell.charAt(1) - blackChecker.getCheckerCell().charAt(1) == -2) {
            String beatenCheckerCell = Character.toString(blackChecker.getCheckerCell().charAt(0) - 1) +
                    Character.toString(blackChecker.getCheckerCell().charAt(1) - 1);
            init.getWhiteCheckers().remove(init.getCellChecker(beatenCheckerCell));
        } else if (cell.charAt(0) - blackChecker.getCheckerCell().charAt(0) == 2
                && cell.charAt(1) - blackChecker.getCheckerCell().charAt(1) == -2) {
            String beatenCheckerCell = Character.toString(blackChecker.getCheckerCell().charAt(0) + 1) +
                    Character.toString(blackChecker.getCheckerCell().charAt(1) - 1);
            init.getWhiteCheckers().remove(init.getCellChecker(beatenCheckerCell));
        }
    }

    public void killBeatenBlackChecker(String cell, Checker whiteChecker, Board init) {
        if (whiteChecker.isKing() && (cell.charAt(0) - whiteChecker.getCheckerCell().charAt(0) == -2)
                && (cell.charAt(1) - whiteChecker.getCheckerCell().charAt(1) == -2)) {
            String beatenCheckerCell = Character.toString(whiteChecker.getCheckerCell().charAt(0) - 1) +
                    Character.toString(whiteChecker.getCheckerCell().charAt(1) - 1);
            init.getWhiteCheckers().remove(init.getCellChecker(beatenCheckerCell));
        } else if (whiteChecker.isKing() && (cell.charAt(0) - whiteChecker.getCheckerCell().charAt(0) == 2)
                && (cell.charAt(1) - whiteChecker.getCheckerCell().charAt(1) == -2)) {
            String beatenCheckerCell = Character.toString(whiteChecker.getCheckerCell().charAt(0) + 1) +
                    Character.toString(whiteChecker.getCheckerCell().charAt(1) - 1);
            init.getWhiteCheckers().remove(init.getCellChecker(beatenCheckerCell));
        } else if (cell.charAt(0) - whiteChecker.getCheckerCell().charAt(0) == 2
                && cell.charAt(1) - whiteChecker.getCheckerCell().charAt(1) == 2) {
            String beatenCheckerCell = Character.toString(whiteChecker.getCheckerCell().charAt(0) + 1) +
                    Character.toString(whiteChecker.getCheckerCell().charAt(1) + 1);
            init.getBlackCheckers().remove(init.getCellChecker(beatenCheckerCell));
        } else if (cell.charAt(0) - whiteChecker.getCheckerCell().charAt(0) == -2
                && cell.charAt(1) - whiteChecker.getCheckerCell().charAt(1) == 2) {
            String beatenCheckerCell = Character.toString(whiteChecker.getCheckerCell().charAt(0) - 1) +
                    Character.toString(whiteChecker.getCheckerCell().charAt(1) + 1);
            init.getBlackCheckers().remove(init.getCellChecker(beatenCheckerCell));
        }
    }

    private void addAdditionalBeats(Board board, List<Map<String, Checker>> path, List<List<Map<String, Checker>>> allPaths) {
        Board tempBoard = getTempBoard(board);
        Board state = getBoardState(tempBoard, path);
        Map<Checker, List<String>> mustBeat;
        if (path.get(path.size() - 1).entrySet().stream().findFirst().get().getValue().getColor().equals("b")) {
            mustBeat = checkIfComputerMustBeat(state);
        } else {
            mustBeat = checkIfPlayerMustBeat(state);
        }
        boolean flag = false;
        for (Map.Entry<Checker, List<String>> checkerListEntry : mustBeat.entrySet()) {
            if (path.get(path.size() - 1).entrySet().stream().findFirst().get().getValue().getId()
                    .equals(checkerListEntry.getKey().getId())) {
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
        if (!flag) {
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

            if (blackChecker.isKing()) {
                String firstCellLeftUp = Utils.convertNumbersToCell(blackChecker.getX() - 1, blackChecker.getY() + 1);
                String firstCellRightUp = Utils.convertNumbersToCell(blackChecker.getX() + 1, blackChecker.getY() + 1);
                String secondCellLeftUp;
                String secondCellRightUp;

                if (firstCellLeftUp != null) {
                    Checker checker = board.getCellChecker(firstCellLeftUp);
                    if (checker != null && checker.getColor().equals("w")) {
                        secondCellLeftUp = Utils.convertNumbersToCell(blackChecker.getX() - 2, blackChecker.getY() + 2);
                        if (secondCellLeftUp != null) {
                            Checker checker2 = board.getCellChecker(secondCellLeftUp);
                            if (checker2 == null) {
                                List<String> moves = possibleMoves.get(blackChecker);
                                if (moves == null) {
                                    moves = new ArrayList<>();
                                }
                                moves.add(secondCellLeftUp);
                                possibleMoves.put(blackChecker, moves);
                            }
                        }
                    }
                }

                if (firstCellRightUp != null) {
                    Checker checker = board.getCellChecker(firstCellRightUp);
                    if (checker != null && checker.getColor().equals("w")) {
                        secondCellRightUp = Utils.convertNumbersToCell(blackChecker.getX() + 2, blackChecker.getY() + 2);
                        if (secondCellRightUp != null) {
                            Checker checker2 = board.getCellChecker(secondCellRightUp);
                            if (checker2 == null) {
                                List<String> moves = possibleMoves.get(blackChecker);
                                if (moves == null) {
                                    moves = new ArrayList<>();
                                }
                                moves.add(secondCellRightUp);
                                possibleMoves.put(blackChecker, moves);
                            }
                        }
                    }
                }
            }
        }

        return possibleMoves;
    }

    private Board getBoardState(Board init, List<Map<String, Checker>> path) {
        boolean flag = false;
        for (Map<String, Checker> stringCheckerMap : path) {
            for (Map.Entry<String, Checker> stringCheckerEntry : stringCheckerMap.entrySet()) {
                if (stringCheckerEntry.getValue().getColor().equals("b")) {
                    for (Checker blackChecker : init.getBlackCheckers()) {
                        if (blackChecker.getId().equals(stringCheckerEntry.getValue().getId())) {
                            killBeatenWhiteChecker(stringCheckerEntry.getKey(), blackChecker, init);
                            blackChecker.setCheckerCell(stringCheckerEntry.getKey());
                            if (stringCheckerEntry.getKey().charAt(1) == '1') {
                                blackChecker.setKing(true);
                            }
                            flag = true;
                            break;
                        }
                    }
                } else {
                    for (Checker whiteChecker : init.getWhiteCheckers()) {
                        if (whiteChecker.getId().equals(stringCheckerEntry.getValue().getId())) {
                            killBeatenBlackChecker(stringCheckerEntry.getKey(), whiteChecker, init);
                            whiteChecker.setCheckerCell(stringCheckerEntry.getKey());
                            if (stringCheckerEntry.getKey().charAt(1) == '8') {
                                whiteChecker.setKing(true);
                            }
                            flag = true;
                            break;
                        }
                    }
                }
                if (flag) {
                    flag = false;
                    break;
                }
            }
        }

        return init;
    }

    public Map<Checker, List<String>> getCheckersThatCanMove(List<Checker> allCheckers, Board board) {
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
        for (Map.Entry<Checker, List<String>> checkerListEntry : checkersMustBeBeaten.entrySet()) {
            for (String s : checkerListEntry.getValue()) {
                Map<String, Checker> move = new LinkedHashMap<>();
                move.put(s, checkerListEntry.getKey());
                beatenCheckers.add(move);
            }
        }

        return beatenCheckers;
    }

    public List<String> checkIfCheckerCanMove(Checker checker, Board board) {
        List<String> possibleMoves = new ArrayList<>();
        List<Integer> placement = getNumbersFromCell(checker.getCheckerCell());
        int x = placement.get(0);
        int y = placement.get(1);
        if (board.getCurrentPlayer().equals("b")) {
            if (x - 1 >= 0 && y - 1 >= 0
                    && board.getCellCheckerByNumbers(x, y) == null) {
                possibleMoves.add(convertNumbersToCell(x + 1 - 1, y + 1 - 1));
            }
            if (x + 1 < 8 && y - 1 >= 0
                    && board.getCellCheckerByNumbers(x + 2, y) == null) {
                possibleMoves.add(convertNumbersToCell(x + 1 + 1, y + 1 - 1));
            }
            if (checker.isKing()
                    && ((x - 1 >= 0 && y + 1 < 8 && board.getCellCheckerByNumbers(x, y + 2) == null))) {
                possibleMoves.add(convertNumbersToCell(x + 1 - 1, y + 1 + 1));
            }
            if (checker.isKing()
                    && ((x + 1 < 8 && y + 1 < 8 && board.getCellCheckerByNumbers(x + 2, y + 2) == null))) {
                possibleMoves.add(convertNumbersToCell(x + 1 + 1, y + 1 + 1));
            }
        } else {
            if (x - 1 >= 0 && y + 1 < 8
                    && board.getCellCheckerByNumbers(x, y + 2) == null) {
                possibleMoves.add(convertNumbersToCell(x + 1 - 1, y + 1 + 1));
            }
            if (x + 1 < 8 && y + 1 < 8
                    && board.getCellCheckerByNumbers(x + 2, y + 2) == null) {
                possibleMoves.add(convertNumbersToCell(x + 1 + 1, y + 1 + 1));
            }
            if (checker.isKing() && (x - 1 >= 0 && y - 1 >= 0
                    && board.getCellCheckerByNumbers(x, y) == null)) {
                possibleMoves.add(convertNumbersToCell(x + 1 - 1, y + 1 - 1));
            }
            if (checker.isKing() && (x + 1 < 8 && y - 1 >= 0
                    && board.getCellCheckerByNumbers(x + 2, y) == null)) {
                possibleMoves.add(convertNumbersToCell(x + 1 + 1, y + 1 - 1));
            }
        }

        return possibleMoves;
    }

    // 1. the biggest number of rival beaten checkers (beaten checker = +2)
    // 2. user gets King (getting King = -4)
    // 3. the smallest number of computer beaten checkers and not more than rival (beaten checker = -2)
    // 4. getting of King (getting king = +3)
    private List<Map<String, Checker>> bestStrategy(List<List<Map<String, Checker>>> allPaths, Board board) {
        Board tempBoard = getTempBoard(board);
        Map<List<Map<String, Checker>>, Integer> gradeStrategies = new HashMap<>();
        Set<Checker> potentialKings = getPotentialKings(allPaths, board, tempBoard);
        for (List<Map<String, Checker>> allPath : allPaths) {
            Board state = getBoardState(tempBoard, allPath);
            if(checkIfWinnerExists(state).equals("Computer won")) {
                return allPath;
            }
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
                            ((board.getBlackCheckers().size() - state.getBlackCheckers().size()) * 2) -
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
                .map(blackChecker -> new Checker(blackChecker.getId(), blackChecker.getX(),
                        blackChecker.getY(), "b", blackChecker.isKing()))
                .collect(Collectors.toList());
        List<Checker> tempWhite = board.getWhiteCheckers()
                .stream()
                .map(whiteChecker -> new Checker(whiteChecker.getId(), whiteChecker.getX(),
                        whiteChecker.getY(), "w", whiteChecker.isKing()))
                .collect(Collectors.toList());
        tempBoard.setWhiteCheckers(tempWhite);
        tempBoard.setBlackCheckers(tempBlack);
        tempBoard.setCurrentPlayer(board.getCurrentPlayer());

        return tempBoard;
    }

    private HashMap<List<Map<String, Checker>>, Integer> sortByValue(Map<List<Map<String, Checker>>, Integer> hm) {
        List<Map.Entry<List<Map<String, Checker>>, Integer>> list = new LinkedList<>(hm.entrySet());

        list.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));

        HashMap<List<Map<String, Checker>>, Integer> temp = new LinkedHashMap<>();
        for (Map.Entry<List<Map<String, Checker>>, Integer> aa : list) {
            temp.put(aa.getKey(), aa.getValue());
        }

        return temp;
    }

    public String checkIfWinnerExists(Board board) {
        if (board.getWhiteCheckers().size() == 0) {
            return "Computer won";
        } else if (board.getBlackCheckers().size() == 0) {
            return "You won. Congratulations";
        }

        if (board.getCurrentPlayer().equals("w")) {
            Map<Checker, List<String>> possibleBeats = checkIfPlayerMustBeat(board);
            Map<Checker, List<String>> possibleMoves = getCheckersThatCanMove(board.getWhiteCheckers(), board);

            if (possibleBeats.size() == 0 && possibleMoves.size() == 0) {
                return "Computer won";
            }
        } else {
            Map<Checker, List<String>> possibleBeats = checkIfComputerMustBeat(board);
            Map<Checker, List<String>> possibleMoves = getCheckersThatCanMove(board.getBlackCheckers(), board);
            if (possibleBeats.size() == 0 && possibleMoves.size() == 0) {
                return "You won. Congratulations";
            }
        }

        return "";
    }
}
