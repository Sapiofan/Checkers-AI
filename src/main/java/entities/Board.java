package entities;

import engine.Utils;

import java.util.ArrayList;
import java.util.List;

public class Board {
    private String currentPlayer;

    private final List<Checker> whiteCheckers = new ArrayList<>();
    private final List<Checker> blackCheckers = new ArrayList<>();
    private final List<String> whiteCells = new ArrayList<>();

    public Board() {
        initBoard();
        currentPlayer = "w";
    }

    private void initBoard() {
        for (int i = 0; i < 3; i++) {
            if (i % 2 == 0) {
                for (int j = 1; j <= 4; j++) {
                    blackCheckers.add(new Checker(j * 2, 8 - i, "b"));
                }

                for (int j = 1; j <= 4; j++) {
                    whiteCheckers.add(new Checker(j * 2 - 1, 1 + i, "w"));
                }
            } else {
                for (int j = 1; j <= 4; j++) {
                    blackCheckers.add(new Checker(j * 2 - 1, 8 - i, "b"));
                }

                for (int j = 1; j <= 4; j++) {
                    whiteCheckers.add(new Checker(j * 2, 1 + i, "w"));
                }
            }
        }

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 4; j++) {
                if (i % 2 == 0) {
                    whiteCells.add("" + Utils.convertNumberToLetter(i) + "" + (j * 2 - 1));
                } else {
                    whiteCells.add("" + Utils.convertNumberToLetter(i) + "" + (j * 2));
                }
            }
        }
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public List<Checker> getWhiteCheckers() {
        return whiteCheckers;
    }

    public List<Checker> getBlackCheckers() {
        return blackCheckers;
    }

    public List<String> getWhiteCells() {
        return whiteCells;
    }

    public Checker getCellChecker(String cell) {
        Checker checker = blackCheckers
                .stream().filter(blackChecker -> blackChecker.getCheckerCell().equals(cell))
                .findFirst().orElse(null);

        if (checker != null) {
            return checker;
        }

        return whiteCheckers
                .stream().filter(whiteChecker -> whiteChecker.getCheckerCell().equals(cell))
                .findFirst().orElse(null);
    }

    public Checker getCellCheckerByNumbers(int x, int y) {
        Checker checker = blackCheckers.stream()
                .filter(blackChecker -> blackChecker.getX() == x && blackChecker.getY() == y)
                .findFirst()
                .orElse(null);

        if (checker != null) {
            return checker;
        }

        return whiteCheckers.stream()
                .filter(whiteCheckers -> whiteCheckers.getX() == x && whiteCheckers.getY() == y)
                .findFirst()
                .orElse(null);
    }

    @Override
    public String toString() {
        String[][] boardState = new String[8][8];
        for (Checker blackChecker : blackCheckers) {
            if (blackChecker.getY() % 2 == 0) {
                if (!blackChecker.isKing()) {
                    boardState[8 - blackChecker.getY()][8 - blackChecker.getX() + 1] = "\u26C0";
                } else {
                    boardState[8 - blackChecker.getY()][8 - blackChecker.getX() + 1] = "\u26C1";
                }
            } else {
                if (!blackChecker.isKing()) {
                    boardState[8 - blackChecker.getY()][8 - blackChecker.getX() - 1] = "\u26C0";
                } else {
                    boardState[8 - blackChecker.getY()][8 - blackChecker.getX() - 1] = "\u26C1";
                }
            }
        }

        for (Checker whiteChecker : whiteCheckers) {
            if (whiteChecker.getY() % 2 == 0) {
                if (!whiteChecker.isKing()) {
                    boardState[8 - whiteChecker.getY()][whiteChecker.getX() - 1] = "\u26C2";
                } else {
                    boardState[8 - whiteChecker.getY()][8 - whiteChecker.getX() + 1] = "\u26C3";
                }
            } else {
                if (!whiteChecker.isKing()) {
                    boardState[8 - whiteChecker.getY()][whiteChecker.getX() - 1] = "\u26C2";
                } else {
                    boardState[8 - whiteChecker.getY()][8 - whiteChecker.getX() - 1] = "\u26C3";
                }
            }
        }

        for (int i = 0; i < boardState.length; i++) {
            for (int i1 = 0; i1 < boardState[i].length; i1++) {
                String cell = boardState[i][i1];
                if (cell == null) {
                    if ((i % 2 == 0 && i1 % 2 == 1) || (i % 2 == 1 && i1 % 2 == 0)) {
                        boardState[i][i1] = "\u25AC";
                    } else {
                        boardState[i][i1] = "\u25AD";
                    }
                }
            }
        }

        String board = "";
        int counter = 8;
        for (int i = 0; i < boardState.length; i++) {
            board += counter;
            counter--;
            for (int i1 = 0; i1 < boardState[i].length; i1++) {
                board += boardState[i][i1];
            }
            board += "\n";
        }
        board += " a\u2004b\u2004c\u2004d e\u2004f\u2004g\u2004h";

        return board;
    }
}
