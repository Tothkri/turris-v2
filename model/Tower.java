package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;

public abstract class Tower extends Sprite {

    protected String type;
    protected int power;
    protected int range;
    protected double attack_speed;
    protected int hp;
    protected int price;
    protected int upgradePrice;
    protected int demolishedIn;
    protected int level;
    protected int maxHp;
    protected Color color;

    public Tower(String scolor, int x, int y, int height, int width, Image img) {
        super(x, y, height, width, img);

        this.demolishedIn = -1;
        level = 1;
        if (scolor == "red") {
            this.color = Color.red;
            this.img = new ImageIcon("src/res/Towerred.png").getImage();
        } else {
            this.color = Color.blue;
            this.img = new ImageIcon("src/res/Towerblue.png").getImage();
        }
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

    public double getAttack_speed() {
        return attack_speed;
    }

    public void setAttack_speed(double attack_speed) {
        this.attack_speed = attack_speed;
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
    
    public void setColor(String colorString){
        if (colorString == "red") {
            this.color = Color.red;
            this.img = new ImageIcon("src/res/Towerred.png").getImage();
        } else {
            this.color = Color.blue;
            this.img = new ImageIcon("src/res/Towerblue.png").getImage();
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

    public abstract void upgrade();

    public void demolish() {
        demolishedIn = 4;//2 whole round
        img = new ImageIcon("src/res/Destroyed.png").getImage();
    }

    public int getDemolishedIn() {
        return demolishedIn;
    }

    public void setDemolishedIn(int x) {
        demolishedIn -= x;
    }

    private double hpLine() {
        double d = (double) hp / maxHp;
            return d * width;

    }

    @Override
    public void draw(Graphics2D g2) {
        g2.drawImage(img, x, y, height, width, null);
        if (demolishedIn <=0) {
            g2.setColor(color);
            g2.drawLine(x + 2, y + height - 2, x + (int) hpLine() - 2, y + height - 2);
            g2.drawLine(x + 2, y + height - 3, x + (int) hpLine() - 2, y + height - 3);
            g2.drawLine(x + 2, y + height - 4, x + (int) hpLine() - 2, y + height - 4);
        }

    }

}
