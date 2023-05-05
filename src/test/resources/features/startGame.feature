Feature: Start of the game
  As a user
  I want to start playing with a bot

  Scenario: start the game
    Given I have a game board
    When I move my first checker from a3 to b4
    And I move my second checker from b2 to a3
    Then the result should be the board with moved checker from the cell a3 to b4 and from b2 to a3