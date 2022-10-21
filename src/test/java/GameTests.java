import engine.Computer;
import engine.Game;
import entities.Board;
import entities.Checker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static engine.Game.checkIfPlayerMustBeat;

public class GameTests {

    @Test
    public void checkInitialBoard() throws IOException {
        Assertions.assertEquals(readFile("1/init.txt"), new Board().toString());
    }

    @Test
    public void checkPlayerMove() throws IOException {
        Board board = new Board();
        Game game = new Game();
        game.makePlayerMove(board, "a3", "b4");
        Board temp = getTempBoard(board);
        String state = readFile("2/1/result.txt");
        Assertions.assertEquals(state, board.toString());
        Assertions.assertTrue(game.checkIfCheckerCanMove(board, "b4", "a5"));
        board = getTempBoard(temp);
        Assertions.assertTrue(game.checkIfCheckerCanMove(board, "b4", "c5"));
        board = getTempBoard(temp);
        Assertions.assertFalse(game.checkIfCheckerCanMove(board, "b4", "b4"));
        Assertions.assertFalse(game.checkIfCheckerCanMove(board, "b4", "b5"));
        Assertions.assertFalse(game.checkIfCheckerCanMove(board, "b4", "c4"));
        Assertions.assertFalse(game.checkIfCheckerCanMove(board, "b4", "c3"));
        Assertions.assertFalse(game.checkIfCheckerCanMove(board, "b4", "b3"));
        Assertions.assertFalse(game.checkIfCheckerCanMove(board, "b4", "a3"));

        Assertions.assertFalse(game.checkIfCheckerCanMove(board, "e3", "c5"));
        Assertions.assertFalse(game.checkIfCheckerCanMove(board, "e3", "g5"));
        Assertions.assertFalse(game.checkIfCheckerCanMove(board, "a1", "b2"));
        Assertions.assertFalse(game.checkIfCheckerCanMove(board, "b4", "a3"));
        Assertions.assertFalse(game.checkIfCheckerCanMove(board, "b4", "a3"));


    }

    @Test
    public void checkMoveByKing() {
        Board board = new Board();
        Game game = new Game();
        Checker checker = new Checker(6, 4, "w");
        checker.setKing(true);
        List<Checker> checkers = new ArrayList<>();
        checkers.add(checker);
        board.setWhiteCheckers(checkers);
        board.setBlackCheckers(new ArrayList<>());
        Board temp = getTempBoard(board);

        Assertions.assertTrue(game.checkIfCheckerCanMove(board, "f4", "e5"));
        board = getTempBoard(temp);
        Assertions.assertTrue(game.checkIfCheckerCanMove(board, "f4", "g5"));
        board = getTempBoard(temp);
        Assertions.assertTrue(game.checkIfCheckerCanMove(board, "f4", "e3"));
        board = getTempBoard(temp);
        Assertions.assertTrue(game.checkIfCheckerCanMove(board, "f4", "g3"));
        board = getTempBoard(temp);

        Assertions.assertFalse(game.checkIfCheckerCanMove(board, "f4", "d6"));
    }

    @Test
    public void checkPlayerBeat() throws IOException {
        Board board = new Board();
        Board temp;
        Game game = new Game();
        List<Checker> whiteCheckers = new ArrayList<>();
        whiteCheckers.add(new Checker(6, 4, "w"));

        List<Checker> blackCheckers = new ArrayList<>();
        blackCheckers.add(new Checker(5, 5, "b"));
        blackCheckers.add(new Checker(2, 2, "b"));

        board.setWhiteCheckers(whiteCheckers);
        board.setBlackCheckers(blackCheckers);

        temp = getTempBoard(board);

        Map<Checker, List<String>> mustBeat = checkIfPlayerMustBeat(board);
        Assertions.assertTrue(mustBeat.size() != 0);

        game.checkIfCheckerCanMove(board, "f4", "d6");
        Assertions.assertEquals(readFile("3/1/result.txt"), board.toString());

        board = getTempBoard(temp);
        board.getBlackCheckers().add(new Checker(7, 5, "b"));

        game.checkIfCheckerCanMove(board, "f4", "h6");
        Assertions.assertEquals(readFile("3/2/result.txt"), board.toString());

        board = getTempBoard(temp);
        Assertions.assertFalse(game.checkIfCheckerCanMove(board, "f4", "g5"));
        Assertions.assertEquals(readFile("3/1/init.txt"), board.toString());

    }

    @Test
    public void checkPlayerBeatByKing() throws IOException {
        Board board = new Board();
        Game game = new Game();
        Board temp;
        List<Checker> whiteCheckers = new ArrayList<>();
        Checker checker = new Checker(6, 4, "w");
        checker.setKing(true);
        whiteCheckers.add(checker);

        List<Checker> blackCheckers = new ArrayList<>();
        blackCheckers.add(new Checker(5, 3, "b"));
        blackCheckers.add(new Checker(7, 3, "b"));
        blackCheckers.add(new Checker(2, 2, "b"));

        board.setWhiteCheckers(whiteCheckers);
        board.setBlackCheckers(blackCheckers);

        temp = getTempBoard(board);

        Assertions.assertTrue(game.checkIfCheckerCanMove(board, "f4", "d2"));
        board = getTempBoard(temp);
        Assertions.assertTrue(game.checkIfCheckerCanMove(board, "f4", "h2"));
    }

    @Test
    public void checkComputerMove() throws IOException {
        Board board = new Board();
        Computer computer = new Computer();
        Board temp;
        List<Checker> whiteCheckers = new ArrayList<>();
        Checker checker = new Checker(1, 3, "w");
        whiteCheckers.add(checker);

        List<Checker> blackCheckers = new ArrayList<>();
        blackCheckers.add(new Checker(4, 6, "b"));

        board.setWhiteCheckers(whiteCheckers);
        board.setBlackCheckers(blackCheckers);
        board.setCurrentPlayer("b");
        temp = getTempBoard(board);

        computer.makeMove(4, board);
        Assertions.assertEquals(readFile("5/1/result.txt"), board.toString());

        board = getTempBoard(temp);
        board.setCurrentPlayer("b");
        board.getBlackCheckers().get(0).setCheckerCell("h2");
        board.getBlackCheckers().add(new Checker(2, 8, "b"));
        computer.makeMove(2, board);
        Assertions.assertEquals(readFile("5/2/result.txt"), board.toString());

        board = getTempBoard(temp);
        board.setCurrentPlayer("b");
        blackCheckers = new ArrayList<>();
        blackCheckers.add(new Checker(4, 6, "b"));
        whiteCheckers = new ArrayList<>();
        checker = new Checker(1, 3, "w");
        whiteCheckers.add(checker);
        board.setBlackCheckers(blackCheckers);
        board.setWhiteCheckers(whiteCheckers);

        temp = getTempBoard(board);
        for (int i = 3; i <= 8; i++) {
            computer.makeMove(i, board);
            Assertions.assertTrue(board.getBlackCheckers().get(0).getCheckerCell().equals("c5"));
            board = getTempBoard(temp);
        }
    }

    @Test
    public void checkComputerBeat() throws IOException {
        Board board = new Board();
        Computer computer = new Computer();
        Board temp;
        List<Checker> whiteCheckers = new ArrayList<>();
        whiteCheckers.add(new Checker(7, 5, "w"));
        whiteCheckers.add(new Checker(1, 3, "w"));

        List<Checker> blackCheckers = new ArrayList<>();
        blackCheckers.add(new Checker(6, 6, "b"));
        blackCheckers.add(new Checker(2, 8, "b"));

        board.setWhiteCheckers(whiteCheckers);
        board.setBlackCheckers(blackCheckers);
        board.setCurrentPlayer("b");
        temp = getTempBoard(board);

        computer.makeMove(3, board);
        Assertions.assertEquals(readFile("6/1/result.txt"), board.toString());

        board = getTempBoard(temp);
        board.getWhiteCheckers().add(new Checker(5, 5, "w"));
        board.getWhiteCheckers().add(new Checker(5, 3, "w"));

        computer.makeMove(2, board);
        Assertions.assertEquals(readFile("6/2/result.txt"), board.toString());
    }

    @Test
    public void checkComputerBeatByKing() throws IOException {
        Board board = new Board();
        Computer computer = new Computer();
        Board temp;
        List<Checker> whiteCheckers = new ArrayList<>();
        whiteCheckers.add(new Checker(6, 2, "w"));
        whiteCheckers.add(new Checker(1, 3, "w"));

        List<Checker> blackCheckers = new ArrayList<>();
        Checker checker = new Checker(7, 1, "b");
        checker.setKing(true);
        blackCheckers.add(checker);
        blackCheckers.add(new Checker(2, 8, "b"));

        board.setWhiteCheckers(whiteCheckers);
        board.setBlackCheckers(blackCheckers);
        board.setCurrentPlayer("b");
        temp = getTempBoard(board);

        computer.makeMove(3, board);
        Assertions.assertEquals(readFile("7/1/result.txt"), board.toString());

        board = getTempBoard(temp);
        board.getWhiteCheckers().add(new Checker(4, 2, "w"));

        computer.makeMove(4, board);
        Assertions.assertEquals(readFile("7/2/result.txt"), board.toString());
    }

    @Test
    public void checkIfSomeoneWon() {
        Board board = new Board();
        Game game = new Game();
        Computer computer = new Computer();
        Board temp;
        List<Checker> whiteCheckers = new ArrayList<>();
        whiteCheckers.add(new Checker(2, 2, "w"));

        List<Checker> blackCheckers = new ArrayList<>();
        blackCheckers.add(new Checker(3, 3, "b"));
        board.setWhiteCheckers(whiteCheckers);
        board.setBlackCheckers(blackCheckers);

        temp = getTempBoard(board);

        game.checkIfCheckerCanMove(board, "b2", "d4"); // computer has no checker
        Assertions.assertEquals("You won. Congratulations", computer.checkIfWinnerExists(board));

        board = getTempBoard(temp);
        board.setCurrentPlayer("b");
        computer.makeMove(2, board); // user has no checker
        Assertions.assertEquals("Computer won", computer.checkIfWinnerExists(board));

        List<Checker> whiteCheckers1 = new ArrayList<>();
        whiteCheckers1.add(new Checker(2, 2, "w"));
        whiteCheckers1.add(new Checker(3, 1, "w"));

        List<Checker> blackCheckers1 = new ArrayList<>();
        blackCheckers1.add(new Checker(1, 3, "b"));

        board.setWhiteCheckers(whiteCheckers1);
        board.setBlackCheckers(blackCheckers1); // computer has no moves
        board.setCurrentPlayer("b");
        Assertions.assertEquals("You won. Congratulations", computer.checkIfWinnerExists(board));

        List<Checker> whiteCheckers2 = new ArrayList<>();
        whiteCheckers2.add(new Checker(1, 5, "w"));

        List<Checker> blackCheckers2 = new ArrayList<>();
        blackCheckers2.add(new Checker(2, 6, "b"));
        blackCheckers2.add(new Checker(3, 7, "b"));

        board.setWhiteCheckers(whiteCheckers2);
        board.setBlackCheckers(blackCheckers2);// user has no moves
        board.setCurrentPlayer("w");
        Assertions.assertEquals("Computer won", computer.checkIfWinnerExists(board));
    }

    private String readFile(String fileName) throws IOException {
        String initFile = "";
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("src/test/resources/states/" + fileName))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                initFile += line + "\n";
            }
            initFile = initFile.substring(0, initFile.length() - 1);
        }

        return initFile;
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

        return tempBoard;
    }
}
