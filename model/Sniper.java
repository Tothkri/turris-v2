package model;

import java.awt.Image;

public class Sniper extends Tower {

    //constructor for creating new Tower
    public Sniper(String scolor, int x, int y, int height, int width, Image img) {
        super(scolor, x, y, height, width, img);
        this.attack_speed = 1.5;
        this.hp = 25;
        this.level = 1;
        this.price = 300;
        this.range = 1;
        this.power = 2;
        this.demolishedIn = -1;
        this.type = "Sniper";
        this.maxHp=25;
        this.upgradePrice=price+200;
    }

    //constructor for loaded towers
    public Sniper(String type, String sColor, int power, int range, double attack_speed, int hp, int level, int x, int y, int height, int width, Image img) {
        super(sColor, x, y, height, width, img);
        this.type = type;
        this.power = power;
        this.range = range;
        this.attack_speed = attack_speed;
        this.hp = hp;
        this.level=level;
        this.price = 100+level*200;
        this.upgradePrice=price+200;
        this.maxHp = 75+level*25;
        this.demolishedIn = -1;
        this.maxHp = 20+level*5;
    }

    @Override
    public void upgrade() {
        attack_speed -= 0.25;
        power = 10;
        hp += 5;
        level++; 
        price =100+level*200;
        upgradePrice=price+200;
        maxHp = 20+level*5;
        range++;
    }

    
}
