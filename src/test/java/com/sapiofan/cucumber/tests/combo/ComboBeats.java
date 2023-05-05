package com.sapiofan.cucumber.tests.combo;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import engine.Computer;
import engine.Game;
import entities.Board;
import entities.Checker;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;
import java.util.List;

import static utils.TestUtils.readFile;

public class ComboBeats {
    private Board board;
    private Game game;
    Computer computer;

    @Given("^I have a game board with 4 checkers and the bot has one checker respectively$")
    public void initializeGame() throws Throwable {
        board = initBoard();
        game = new Game();
        computer = new Computer();
    }

    private Board initBoard() {
        Board boardInit = new Board();
        List<Checker> whiteCheckers = new ArrayList<>();
        List<Checker> blackCheckers = new ArrayList<>();

        whiteCheckers.add(new Checker(2, 2, "w"));
        whiteCheckers.add(new Checker(2, 6, "w"));
        whiteCheckers.add(new Checker(4, 6, "w"));
        whiteCheckers.add(new Checker(6, 6, "w"));

        Checker checker = new Checker(4, 2, "b");
        checker.setKing(true);
        blackCheckers.add(checker);

        boardInit.setWhiteCheckers(whiteCheckers);
        boardInit.setBlackCheckers(blackCheckers);

        return boardInit;
    }

    @When("^I move my checker from ([a-h][1-8]) to ([a-h][1-8])$")
    public void testMakeMyFirstMoveBeforeMistake(String fromMove, String toMove) throws Throwable {
        Assertions.assertEquals(readFile("integration/3/init.txt"), board.toString());
        game.makePlayerMove(board, fromMove, toMove);
    }

    @And("^Enemy King checker is moved from d2 to c3$")
    public void testMakeEnemyMove() throws Throwable {
        System.out.println(board.toString());
        computer.makeMove(4, board);
        System.out.println(board.toString());
        Assertions.assertEquals("c3", board.getBlackCheckers().get(0).getCheckerCell());
    }

    @And("^I move the same checker from ([a-h][1-8]) to ([a-h][1-8])$")
    public void testBadSecondMove(String from, String to) throws Throwable {
        game.makePlayerMove(board, from, to);
    }

    @And("^Enemy King beats all my checkers$")
    public void testKingBeat() throws Throwable {
        computer.makeMove(4, board);
    }

    @Then("^I lose and King checker is on position g7$")
    public void validateResult() throws Throwable {
        Assertions.assertEquals(0, board.getWhiteCheckers().size());
        Assertions.assertEquals(1, board.getBlackCheckers().size());
        Assertions.assertEquals("g7", board.getBlackCheckers().get(0).getCheckerCell());
        Assertions.assertEquals("Computer won", computer.checkIfWinnerExists(board));
        Assertions.assertEquals(readFile("integration/3/result.txt"), board.toString());
    }
}
