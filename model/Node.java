package model;

public class Node {

    private int x;
    private int y;
    Node parent;

    Node(int x, int y, Node parent) {
        this.x = x;
        this.y = y;
        this.parent = parent;
    }

    Node(int x, int y) {
        this.x = x;
        this.y = y;
        this.parent = null;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return x + ";" + y;
    }
}
