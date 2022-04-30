package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;

public abstract class Tower extends Sprite {

    protected String type;
    protected int power;
    protected int range;
    protected double attackFrequency;
    protected int hp;
    protected int price;
    protected int upgradePrice;
    protected int demolishedIn;
    protected int moneySpentOn;
    protected int level;
    protected int maxHp;
    protected Color color;
    protected Node shootCords;
    protected boolean exploded;

    /**
     *
     * @param scolor
     * @param x
     * @param y
     * @param height
     * @param width
     * @param img
     */
    public Tower(String scolor, int x, int y, int height, int width, Image img) {
        super(x, y, height, width, img);
        exploded = false;
        shootCords = new Node(-1, -1);
        this.demolishedIn = -1;
        level = 1;
        if (scolor == "red") {
            this.color = Color.red;
        } else {
            this.color = Color.blue;
        }
    }

    /**
     * torony fejlesztése
     */
    public abstract void upgrade();

    /**
     * torony lerombolása
     */
    public void demolish() {
        demolishedIn = 4;//2 whole round
        img = new ImageIcon("src/res/Destroyed.png").getImage();
    }

    /**
     * hp csík hossza
     */
    private double hpLineWidth() {
        return ((double) hp / (double) maxHp) * 25;

    }

    /**
     * torony kirajzolása
     *
     * @param g2
     */
    @Override
    public void draw(Graphics2D g2) {
        if (demolishedIn != -1) {
            img = new ImageIcon("src/res/Destroyed.png").getImage();
        }
        g2.drawImage(img, x, y, height, width, null);
        if (demolishedIn <= 0) {
            g2.setColor(color);
            g2.drawLine(x + 3, y + height - 2, x + 3 + (int) hpLineWidth(), y + height - 2);
            g2.drawLine(x + 3, y + height - 3, x + 3 + (int) hpLineWidth(), y + height - 3);
            g2.drawLine(x + 3, y + height - 4, x + 3 + (int) hpLineWidth(), y + height - 4);
        }

    }

    public int getMoneySpentOn() {
        return moneySpentOn;
    }

    public void setMoneySpentOn(int moneySpentOn) {
        this.moneySpentOn = moneySpentOn;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public double getAttackFrequency() {
        return attackFrequency;
    }

    public void setAttackFrequency(double attackFrequency) {
        this.attackFrequency = attackFrequency;
    }

    public boolean isExploded() {
        return exploded;
    }

    public void setExploded(boolean exploded) {
        this.exploded = exploded;
    }

    public void setShootCords(int x, int y) {
        shootCords.setX(x);
        shootCords.setY(y);
    }

    public Node getShootCords() {
        return shootCords;
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

    public void setColor(String colorString) {
        if (colorString.equals("red")) {
            this.color = Color.red;
        } else {
            this.color = Color.blue;
        }
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getLevel() {
        return level;
    }

    public int getMaxHp() {
        return maxHp;
    }

    public int getUpgradePrice() {
        return upgradePrice;
    }

    public void setUpgradePrice(int upgradePrice) {
        this.upgradePrice = upgradePrice;
    }

    public void setLevel(int lvl) {
        level = lvl;
    }

    public int getDemolishedIn() {
        return demolishedIn;
    }

    public void setDemolishedIn(int x) {
        demolishedIn -= x;
    }

}
