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
        blackCheckers.add(new Checker(5, 7, "b"));
        board.setBlackCheckers(blackCheckers);
        game.checkIfCheckerCanMove(board, "f4", "d6");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);
        System.out.flush();
        System.setOut(old);
        Assertions.assertEquals("Your next move: ", baos.toString());

        InputStream sysInBackup = System.in;
        ByteArrayInputStream in = new ByteArrayInputStream("f8".getBytes());
        System.setIn(in);

        Assertions.assertTrue(whiteCheckers.get(0).isKing());
        Assertions.assertEquals(readFile("3/2/result.txt"), board.toString());


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
        board = getTempBoard(temp);

        board.getBlackCheckers().add(new Checker(3, 3, "b"));
        game.checkIfCheckerCanMove(board, "f4", "d2");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        PrintStream old = System.out;
        System.setOut(ps);
        System.out.flush();
        System.setOut(old);
        Assertions.assertEquals("Your next move: ", baos.toString());

        InputStream sysInBackup = System.in;
        ByteArrayInputStream in = new ByteArrayInputStream("b4".getBytes());
        System.setIn(in);

        Assertions.assertEquals(readFile("4/result.txt"), board.toString());
    }

    @Test
    public void checkComputerMove() throws IOException {
        System.out.println("\u26C1");
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
        board.getWhiteCheckers().get(0).setCheckerCell("h2");
        board.getWhiteCheckers().add(new Checker(2, 8, "b"));
        computer.makeMove(2, board);
        Assertions.assertEquals(readFile("5/2/result.txt"), board.toString());

        board = getTempBoard(temp);
        board.setCurrentPlayer("b");
        blackCheckers = new ArrayList<>();
        blackCheckers.add(new Checker(4, 6, "b"));
        board.setBlackCheckers(blackCheckers);
        board.setWhiteCheckers(new ArrayList<>());
        temp = getTempBoard(board);
        for (int i = 1; i <= 5; i++) {
            computer.makeMove(i, board);
            Assertions.assertTrue(board.getBlackCheckers().get(0).getCheckerCell().equals("c5")
                    || board.getBlackCheckers().get(0).getCheckerCell().equals("e5"));
            board = getTempBoard(temp);
        }
    }

    @Test
    public void checkComputerBeat() {

    }

    @Test
    public void checkComputerBeatByKing() {

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
