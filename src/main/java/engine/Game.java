package engine;

import entities.Board;
import entities.Checker;

import java.util.*;

public class Game {

    private final Scanner scanner = new Scanner(System.in);

    public void start() {
        Board board = new Board();
        Computer computer = new Computer();
        System.out.println("Choose a mode:\n" +
                "1 >> easy\n" +
                "2 >> medium\n" +
                "3 >> hard\n" +
                "4 >> insane\n");
        int mode = scanner.nextInt() * 2;
        String checker, move, result;

        // fix beating, moving
        // cleaning, optimization
        while (true) {
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
            result = computer.checkIfWinnerExists(board);
            if (!result.equals("")) {
                System.out.println(result);
                return;
            }
            long start = System.currentTimeMillis();
            computer.makeMove(mode, board);
            float elapsedTimeSec = (System.currentTimeMillis() - start) / 1000F;
            System.out.println("Seconds: " + elapsedTimeSec);
            board.setCurrentPlayer("w");
            result = computer.checkIfWinnerExists(board);
            if (!result.equals("")) {
                System.out.println(result);
                return;
            }
        }
    }

    public String checkMoveInput(String move, boolean exitMode) {
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
        if (checkersThatMustBeBeaten.size() != 0) {
            for (Map.Entry<Checker, List<String>> checkerListEntry : checkersThatMustBeBeaten.entrySet()) {
                for (String s : checkerListEntry.getValue()) {
                    if (s.contains(move)) {
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    break;
                }
            }
            if(!flag) {
                System.out.println("You can't move there. You must beat a checker.");
            }
        }

        Checker checker1 = null;
        if (flag) { // add additional beats for user, assign King status to checker if it reached last line
//            for (Map.Entry<Checker, List<String>> checkerListEntry : checkersThatMustBeBeaten.entrySet()) {
//                for (String s : checkerListEntry.getValue()) {

                   checker1 = beatComputerChecker(move, checker, board);
                    if(checker1 != null && checker1.getY() == 8) {
                        checker1.setKing(true);
//                        break;
                    }
//                }
//                if(checker1 != null) {
//                    break;
//                }
//            }
            while (checker1 != null) {
                Checker checker2 = checker1;
                checkersThatMustBeBeaten = checkIfPlayerMustBeat(board);
                for (Map.Entry<Checker, List<String>> checkerListEntry : checkersThatMustBeBeaten.entrySet()) {
                    if(checkerListEntry.getKey().getId().equals(checker1.getId())) {
                        System.out.print("Your next move: ");
                        move = checkMoveInput(scanner.next(), false);
                        boolean flag2 = false;
                        while (!flag2) {
                            for (String s : checkerListEntry.getValue()) {
                                if(s.equals(move)) {
                                    flag2 = true;
                                    break;
                                }
                            }
                            if(!flag2) {
                                String warn = "You must beat. Possible moves: ";
                                for (String s : checkerListEntry.getValue()) {
                                    warn += s + " ";
                                }
                                warn += "\nPlease, input your move from the list about: ";
                                System.out.print(warn);
                                move = checkMoveInput(scanner.next(), false);
                            }
                        }
                        checker1 = beatComputerChecker(move, checker1.getCheckerCell(), board);
                        break;
                    }
                }
                if(checker1.equals(checker2)) {
                    return true;
                }
            }
        }

        if (!checkIfWhiteCell(board, move)) {
            return false;
        }

        return userCanMoveChecker(board, checker, move);
    }

    public Checker beatComputerChecker(String s, String checker, Board board) {
        Checker checker1 = null;
        char beatenCheckerLetter = s.charAt(0);
        char beatenCheckerNumber = s.charAt(1);
        char currentCheckerLetter = checker.charAt(0);
        char currentCheckerNumber = checker.charAt(1);
        if (currentCheckerLetter - 2 == beatenCheckerLetter && currentCheckerNumber + 2 == beatenCheckerNumber) {
            checker1 = board.getCellChecker(checker);
            board.getBlackCheckers().remove(board
                    .getCellCheckerByNumbers(checker1.getX() - 1, checker1.getY() + 1));
            checker1.setX(checker1.getX() - 2);
            checker1.setY(checker1.getY() + 2);

            return checker1;
        } else if (currentCheckerLetter + 2 == beatenCheckerLetter && currentCheckerNumber + 2 == beatenCheckerNumber) {
            checker1 = board.getCellChecker(checker);
            board.getBlackCheckers().remove(board
                    .getCellCheckerByNumbers(checker1.getX() + 1, checker1.getY() + 1));
            checker1.setX(checker1.getX() + 2);
            checker1.setY(checker1.getY() + 2);

            return checker1;
        } else if (board.getCellChecker(checker).isKing() &&
                currentCheckerLetter - 2 == beatenCheckerLetter && currentCheckerNumber - 2 == beatenCheckerNumber) {
            checker1 = board.getCellChecker(checker);
            board.getBlackCheckers().remove(board
                    .getCellCheckerByNumbers(checker1.getX() - 1, checker1.getY() - 1));
            checker1.setX(checker1.getX() - 2);
            checker1.setY(checker1.getY() - 2);

            return checker1;
        } else if (board.getCellChecker(checker).isKing() &&
                currentCheckerLetter + 2 == beatenCheckerLetter && currentCheckerNumber - 2 == beatenCheckerNumber) {
            checker1 = board.getCellChecker(checker);
            board.getBlackCheckers().remove(board
                    .getCellCheckerByNumbers(checker1.getX() + 1, checker1.getY() - 1));
            checker1.setX(checker1.getX() + 2);
            checker1.setY(checker1.getY() - 2);

            return checker1;
        }

        return null;
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
                            if (possibleMoves.get(whiteChecker) == null) {
                                list = new ArrayList<>();
                            }
                            list.add(secondCellRight);
                            possibleMoves.put(whiteChecker, list);
                        }
                    }
                }
            }

            if (whiteChecker.isKing()) {
                String firstCellLeftDown = Utils.convertNumbersToCell(whiteChecker.getX() - 1, whiteChecker.getY() - 1);
                String firstCellRightDown = Utils.convertNumbersToCell(whiteChecker.getX() + 1, whiteChecker.getY() - 1);
                String secondCellLeftDown;
                String secondCellRightDown;

                if (firstCellLeftDown != null) {
                    Checker checker = board.getCellChecker(firstCellLeftDown);
                    if (checker != null && checker.getColor().equals("b")) {
                        secondCellLeftDown = Utils.convertNumbersToCell(whiteChecker.getX() - 2, whiteChecker.getY() - 2);
                        if (secondCellLeftDown != null) {
                            Checker checker2 = board.getCellChecker(secondCellLeftDown);
                            if (checker2 == null) {
                                List<String> list = new ArrayList<>();
                                list.add(secondCellLeftDown);
                                possibleMoves.put(whiteChecker, list);
                            }
                        }
                    }
                }

                if (firstCellRightDown != null) {
                    Checker checker = board.getCellChecker(firstCellRightDown);
                    if (checker != null && checker.getColor().equals("b")) {
                        secondCellRightDown = Utils.convertNumbersToCell(whiteChecker.getX() + 2, whiteChecker.getY() - 2);
                        if (secondCellRightDown != null) {
                            Checker checker2 = board.getCellChecker(secondCellRightDown);
                            if (checker2 == null) {
                                List<String> list = possibleMoves.get(whiteChecker);
                                if (possibleMoves.get(whiteChecker) == null) {
                                    list = new ArrayList<>();
                                }
                                list.add(secondCellRightDown);
                                possibleMoves.put(whiteChecker, list);
                            }
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
        if(checker.equals(move)) {
            return false;
        }

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

            if (board.getCellChecker(checker).isKing()
                    && ((initialPosition.get(0) + 1 == movement.get(0) && initialPosition.get(1) - 1 == movement.get(1))
                    || (initialPosition.get(0) - 1 == movement.get(0) && initialPosition.get(1) - 1 == movement.get(1)))) {
                Checker moveCh = board.getCellChecker(checker);
                moveCh.setX(movement.get(0) + 1);
                moveCh.setY(movement.get(1) + 1);

                return true;
            }
        }
        return false;
    }
}
