package com.sapiofan.cucumber.tests.king;

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

public class GetKingAndBeat {
    private Board board;
    private Game game;
    Computer computer;

    @Given("^I have a game board$")
    public void initializeGame() throws Throwable {
        board = initBoard();
        game = new Game();
        computer = new Computer();
    }

    private Board initBoard() {
        Board boardInit = new Board();
        List<Checker> whiteCheckers = new ArrayList<>();
        List<Checker> blackCheckers = new ArrayList<>();

        whiteCheckers.add(new Checker(1, 7, "w"));
        whiteCheckers.add(new Checker(5, 7, "w"));
        whiteCheckers.add(new Checker(6, 6, "w"));

        blackCheckers.add(new Checker(4, 8, "b"));

        boardInit.setWhiteCheckers(whiteCheckers);
        boardInit.setBlackCheckers(blackCheckers);

        return boardInit;
    }

    @When("^I move my checker from a7 to b8 and get the King checker$")
    public void testGetKing() throws Throwable {
        Assertions.assertEquals(readFile("integration/2/init.txt"), board.toString());
        game.makePlayerMove(board, "a7", "b8");
        Assertions.assertTrue(board.getWhiteCheckers().stream().anyMatch(Checker::isKing));
    }

    @And("^Enemy moves checker from d8 to c7$")
    public void testEnemyMove() throws Throwable {
        computer.makeMove(2, board);
        Assertions.assertEquals("c7", board.getBlackCheckers().get(0).getCheckerCell());
    }

    @And("^I beat enemy checker with help of the King checker$")
    public void testKingBeat() throws Throwable {
        game.makePlayerMove(board, "b8", "d6");
    }

    @Then("^I win the game and I have 3 checkers in the end$")
    public void validateResult() throws Throwable {
        Assertions.assertEquals(0, board.getBlackCheckers().size());
        Assertions.assertEquals(3, board.getWhiteCheckers().size());
        Assertions.assertEquals("You won. Congratulations", computer.checkIfWinnerExists(board));
        Assertions.assertEquals(readFile("integration/2/result.txt"), board.toString());
    }
}
