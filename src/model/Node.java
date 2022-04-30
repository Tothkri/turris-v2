package model;

public class Node {

    private int x;
    private int y;
    private Node parent;

    /**
     * szülővel (előző mező) rendelkező elem
     * @param x
     * @param y
     * @param parent
     */
    public Node(int x, int y, Node parent) {
        this.x = x;
        this.y = y;
        this.parent = parent;
    }

    /**
     * szülő nélkül rendelkező elem
     * @param x
     * @param y
     */
    public Node(int x, int y) {
        this.x = x;
        this.y = y;
        this.parent = null;
    }

    /**
     * getterek, setterek
     * @return
     */
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

    public Node getParent() {
        return parent;
    }
    
    @Override
    public String toString() {
        return x + ";" + y;
    }
}
