package engine;

import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static String convertNumbersToCell(int x, int y) {
        if ((x > 0 && x < 9) && (y > 0 && y < 9)) {
            return Character.toString((char) (x + 96)) + y;
        }

        return null;
    }

    public static List<Integer> getNumbersFromCell(String cell) {
        char letter = cell.charAt(0);
        char number = cell.charAt(1);

        List<Integer> numbers = new ArrayList<>();
        numbers.add(Integer.parseInt(Character.toString((char) (letter - 49))));
        numbers.add(Integer.parseInt(Character.toString(number)) - 1);

        return numbers;
    }

    public static int convertLetterToNumber(String letter) {
        return Integer.parseInt(Character.toString((char) (letter.charAt(0) - 49)));
    }

    public static String convertNumberToLetter(int number) {
        return Character.toString((char) (number + 96));
    }
}
