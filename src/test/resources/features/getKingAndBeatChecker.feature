Feature: Start of the game
  As a user
  I can get King checker and beat enemies checkers

  Scenario: Getting the King
    Given I have a game board
    When I move my checker from a7 to b8 and get the King checker
    And Enemy moves checker from d8 to c7
    And I beat enemy checker with help of the King checker
    Then I win the game and I have 3 checkers in the end