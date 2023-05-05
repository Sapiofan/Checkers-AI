package com.sapiofan.cucumber.tests.start;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import engine.Computer;
import engine.Game;
import entities.Board;
import org.junit.jupiter.api.Assertions;
import utils.TestUtils;

public class StartTheGameTest {

    private Board board;
    private Game game;
    Computer computer;

    @Given("^I have a game board$")
    public void initializeGame() throws Throwable {
        board = new Board();
        game = new Game();
        computer = new Computer();
    }

    @When("^I move my first checker from a3 to b4$")
    public void testOneMove() throws Throwable {
        game.makePlayerMove(board, "a3", "b4");
        computer.makeMove(4, board);
    }

    @And("^I move my second checker from b2 to a3$")
    public void testSecondMove() throws Throwable {
        game.makePlayerMove(board, "b2", "a3");
        computer.makeMove(4, board);
    }

    @Then("^the result should be the board with moved checker from the cell a3 to b4 and from b2 to a3$")
    public void validateResult() throws Throwable {
        String[] lines = board.toString().split("\n");
        boolean flag = false;
        String result = "";
        for (String line : lines) {
            if (line.contains("4") || flag) {
                flag = true;
                result += line + "\n";
            }
        }
        Assertions.assertEquals(TestUtils.readFile("integration/1/result.txt") + "\n", result);
    }
}
