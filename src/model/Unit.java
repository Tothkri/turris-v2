package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;

public abstract class Unit extends Sprite {

    protected String type;
    protected int distance;
    protected int power;
    protected int hp;
    protected int price;
    protected ArrayList<Node> way;
    protected Color color;
    protected int maxHp;
    protected boolean blood;

    /**
     * egység lép adott koordinátára
     *
     * @param x
     * @param y
     */
    public void move(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * hp csík hossza (25 pixeles maximum)
     */
    private double hpLineWidth() {
        return ((double) hp / (double) maxHp) * 25;
    }

    /**
     *
     * @param scolor
     * @param x
     * @param y
     * @param height
     * @param width
     * @param img
     */
    public Unit(String scolor, int x, int y, int height, int width, Image img) {
        super(x, y, height, width, img);
        blood = false;
        if (scolor.equals("red")) {
            this.color = Color.red;
        } else {
            this.color = Color.blue;
        }
        this.img = img;
        this.way = null;
    }

    /**
     * egység kirajzolása
     *
     * @param g2
     */
    @Override
    public void draw(Graphics2D g2) {

        g2.drawImage(img, x, y, height, width, null);
        g2.setColor(color);
        g2.drawLine(x + 3, y + height - 2, x + 3 + (int) hpLineWidth(), y + height - 2);
        g2.drawLine(x + 3, y + height - 3, x + 3 + (int) hpLineWidth(), y + height - 3);
        g2.drawLine(x + 3, y + height - 4, x + 3 + (int) hpLineWidth(), y + height - 4);

    }

    /**
     * getterek, setterek
     *
     * @return
     */
    public int getMaxHp() {
        return maxHp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getDistance() {
        return distance;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public int getPrice() {
        return price;
    }

    public boolean isBlood() {
        return blood;
    }

    public void setBlood(boolean blood) {
        this.blood = blood;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public ArrayList<Node> getWay() {
        return way;
    }

    public void setWay(ArrayList<Node> way) {
        this.way = way;
    }

}
