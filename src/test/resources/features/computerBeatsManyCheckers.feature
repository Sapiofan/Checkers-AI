Feature: Computer catch my playing mistakes quickly
  As a user
  I can miss some bad moves for me and the bot can effectively use such mistakes

  Scenario: the players are at height of the game
    Given I have a game board with 4 checkers and the bot has one checker respectively
    When I move my checker from b2 to a3
    And Enemy King checker is moved from d2 to c3
    And I move the same checker from a3 to b4
    And Enemy King beats all my checkers
    Then I lose and King checker is on position g7