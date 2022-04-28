package model;

import java.awt.Image;

public class Sniper extends Tower {

    /**
     * újonnan létrehozott
     * @param scolor
     * @param x
     * @param y
     * @param height
     * @param width
     * @param img
     */
    public Sniper(String scolor, int x, int y, int height, int width, Image img) {
        super(scolor, x, y, height, width, img);
        this.attackFrequency = 1.5;
        this.hp = 25;
        this.level = 1;
        this.price = 300;
        this.range = 1;
        this.power = 2;
        this.demolishedIn = -1;
        this.type = "Sniper";
        this.maxHp = 25;
        this.upgradePrice = price + 200;
        this.moneySpentOn = price;
    }

    /**
     * fájból betöltött
     * @param type
     * @param sColor
     * @param power
     * @param range
     * @param attackFrequency
     * @param hp
     * @param level
     * @param x
     * @param y
     * @param height
     * @param width
     * @param img
     */
    public Sniper(String type, String sColor, int power, int range, double attackFrequency, int hp, int level, int x, int y, int height, int width, Image img) {
        super(sColor, x, y, height, width, img);
        this.type = type;
        this.power = power;
        this.range = range;
        this.attackFrequency = attackFrequency;
        this.hp = hp;
        this.level = level;
        this.price = 100 + level * 200;
        this.upgradePrice = price + 200;
        this.maxHp = 75 + level * 25;
        this.demolishedIn = -1;
        this.maxHp = 20 + level * 5;
        if (level == 1) {
            this.moneySpentOn = 300;
        } else if (level == 2) {
            this.moneySpentOn = 800;
        } else {
            this.moneySpentOn = 1500;
        }
    }

    /**
     * fejlesztés
     */
    @Override
    public void upgrade() {
        attackFrequency -= 0.25;
        power = 10;
        hp += 5;
        level++;
        price = 100 + level * 200;
        upgradePrice = price + 200;
        maxHp = 20 + level * 5;
        range++;
        moneySpentOn += price;
    }

}
