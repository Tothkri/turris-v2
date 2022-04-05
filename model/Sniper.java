package model;

import java.awt.Image;

public class Sniper extends Tower {

    //constructor for creating new Tower
    public Sniper(String scolor, int x, int y, int height, int width, Image img) {
        super(scolor, x, y, height, width, img);
        this.attack_speed = 1.5;
        this.hp = 25;
        this.level = 1;
        this.price = 350;
        this.range = 1;
        this.power = 2;
        this.demolishedIn = -1;
        this.type = "Sniper";
        this.maxHp=25;
    }

    //constructor for loaded towers
    public Sniper(String type, String scolor, int power, int range, double attack_speed, int hp, int price, int x, int y, int height, int width, Image img) {
        super(scolor, x, y, height, width, img);
        this.type = type;
        this.power = power;
        this.range = range;
        this.attack_speed = attack_speed;
        this.hp = hp;
        this.price = price;
        this.demolishedIn = -1;
        this.maxHp = 20+level*5;
        level = 1;
    }

    @Override
    public void upgrade() {
        attack_speed -= 0.25;
        power = 10;
        hp += 5;
        price += 200;
        level++;
        maxHp = 20+level*5;

    }
}
