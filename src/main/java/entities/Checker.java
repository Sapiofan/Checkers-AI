package entities;

import java.util.UUID;

public class Checker {
    private UUID id;
    private int x;
    private int y;
    private String color;
    private boolean isKing = false;

    public Checker(int x, int y, String color) {
        this.id = UUID.randomUUID();
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public Checker(UUID id, int x, int y, String color) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.color = color;
    }

    public Checker(String x, int y) {
        setStringX(x);
        this.y = y;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setStringX(String x) {
        this.x = Integer.parseInt(Character.toString((char) (x.charAt(0) - 49)));
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        if(color.equals("w")) {
            if(y == 8) {
                this.isKing = true;
            }
        } else {
            if(y == 1) {
                this.isKing = true;
            }
        }
        this.y = y;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public boolean isKing() {
        return isKing;
    }

    public void setKing(boolean king) {
        isKing = king;
    }

    public String getCheckerCell() {
        return Character.toString((char) (x + 96)) + y;
    }

    public void setCheckerCell(String cell) {
        this.x = Integer.parseInt(Character.toString((char) (cell.charAt(0) - 48)));
        this.y = Integer.parseInt(cell.substring(1));
    }
}
