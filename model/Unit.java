package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.ArrayList;
import javax.swing.ImageIcon;

public abstract class Unit extends Sprite {

    protected String type;
    protected int distance;
    protected int power;
    protected int hp;
    protected int price;
    protected ArrayList<Node> way;
    protected Color color;
    protected int maxHp;

    public int getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(int maxHp) {
        this.maxHp = maxHp;
    }

    protected void move(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean collides(Sprite s) {
        return false;
    }

    private double hpLine() {
        double d = (double) hp / maxHp;
            return d * width;

    }

    public Unit(String scolor, int x, int y, int height, int width, Image img) {
        super(x, y, height, width, img);
        if (scolor == "red") {
            this.color = Color.red;
            this.img = new ImageIcon("src/res/Unitred.png").getImage();
        } else {
            this.color = Color.blue;
            this.img = new ImageIcon("src/res/Unitblue.png").getImage();
        }
        this.way=null;
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

    @Override
    public void draw(Graphics2D g2) {

        g2.drawImage(img, x, y, height, width, null);
        g2.setColor(color);
        g2.drawLine(x + 2, y + height - 2, x + (int) hpLine() - 2, y + height - 2);
        g2.drawLine(x + 2, y + height - 3, x + (int) hpLine() - 2, y + height - 3);
        g2.drawLine(x + 2, y + height - 4, x + (int) hpLine() - 2, y + height - 4);
        
    }

}
