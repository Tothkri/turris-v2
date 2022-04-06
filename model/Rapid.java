package model;

import java.awt.Image;

public class Rapid extends Tower {

    //constructor for creating new Tower
    public Rapid(String scolor, int x, int y, int height, int width, Image img) {
        super(scolor, x, y, height, width, img);
        this.attack_speed = 0.5;
        this.hp = 45;
        this.level = 1;
        this.price = 250;
        this.range = 1;
        this.power = 2;
        this.demolishedIn = -1;
        this.type = "Rapid";
        this.maxHp = 45;
    }

    //constructor for loaded towers
    public Rapid(String type, String scolor, int power, int range, double attack_speed, int hp, int price, int x, int y, int height, int width, Image img) {
        super(scolor, x, y, height, width, img);
        this.type = type;
        this.power = power;
        this.range = range;
        this.attack_speed = attack_speed;
        this.hp = hp;
        this.price = price;
        this.demolishedIn = -1;
        this.maxHp = 40+level*5;
        level = 1;
    }

    @Override
    public void upgrade() {
        if (level == 2) {
            range = 2;
        }
        attack_speed = 0.25;
        power = 3;
        hp += 5;
        price += 150;
        level++;
        maxHp = 40+level*5;
    }
    @Override
    public void setMaxHp(){
        maxHp = 40+level*5;
    }
}
