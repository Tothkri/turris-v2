
package model;

import java.awt.Image;

public abstract class Tower extends Sprite{
    
    protected String type;
    protected int power;
    protected int range;
    protected double attack_speed;
    protected int hp;
    protected int price;

    public Tower(String type, int power, int range, double attack_speed, int hp, int price, int x, int y, int height, int width, Image img) {
        super(x, y, height, width, img);
        this.type = type;
        this.power = power;
        this.range = range;
        this.attack_speed = attack_speed;
        this.hp = hp;
        this.price = price;
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

    public void setPrice(int price) {
        this.price = price;
    }
    
    
    
    
}
