package model;

import java.awt.Image;
import java.util.ArrayList;

public abstract class Unit extends Sprite {

    protected String type;
    protected int distance;
    protected int power;
    protected int hp;
    protected int price;
    protected ArrayList<Node> way;

    protected void move(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean collides(Sprite s) {
        return false;
    }

    public Unit(String type, int distance, int power, int hp, int price, int x, int y, int height, int width, Image img, ArrayList<Node> way) {
        super(x, y, height, width, img);
        this.type = type;
        this.distance = distance;
        this.power = power;
        this.hp = hp;
        this.price = price;
        this.way = way;
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

    public void setDistance(int distance) {
        this.distance = distance;
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
