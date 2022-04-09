package model;

import java.awt.Image;

public class Fortified extends Tower {

    //constructor for creating new Tower
    public Fortified(String scolor, int x, int y, int height, int width, Image img) {
        super(scolor, x, y, height, width, img);
        this.attack_speed = 1.0;
        this.hp = 100;
        this.maxHp = 100;
        this.level = 1;
        this.price = 200;
        this.range = 1;
        this.power = 2;
        this.demolishedIn = -1;
        this.type = "Fortified";
        this.upgradePrice=350;
    }

    //constructor for loaded towers
    public Fortified(String type, String scolor, int power, int range, double attack_speed, int hp, int level, int x, int y, int height, int width, Image img) {
        super(scolor, x, y, height, width, img);
        this.type = type;
        this.power = power;
        this.range = range;
        this.attack_speed = attack_speed;
        this.hp = hp;
        this.level=level;
        this.price = 50+level*150;
        this.upgradePrice=price+150;
        this.demolishedIn = -1;
        this.maxHp = 75+level*25;
    }

    @Override
    public void upgrade() {
        if (level == 2) {
            attack_speed = 0.75;
        }
        power = 3;
        hp += 25;
        level++; 
        price =100+level*150;
        upgradePrice=price+150;
        maxHp=75+level*25;
    }
    
}
