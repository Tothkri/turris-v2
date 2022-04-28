package model;

import java.awt.Image;

public class Rapid extends Tower {

    /**
     * újonnan létrehozott
     * @param scolor
     * @param x
     * @param y
     * @param height
     * @param width
     * @param img
     */
    public Rapid(String scolor, int x, int y, int height, int width, Image img) {
        super(scolor, x, y, height, width, img);
        this.attackFrequency = 0.5;
        this.hp = 45;
        this.level = 1;
        this.price = 250;
        this.range = 1;
        this.power = 2;
        this.demolishedIn = -1;
        this.type = "Rapid";
        this.maxHp = 45;
        this.upgradePrice = price + 150;
        this.moneySpentOn = price;
    }

    /**
     * fájlból betöltött
     * @param type
     * @param scolor
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
    public Rapid(String type, String scolor, int power, int range, double attackFrequency, int hp, int level, int x, int y, int height, int width, Image img) {
        super(scolor, x, y, height, width, img);
        this.type = type;
        this.power = power;
        this.range = range;
        this.level = level;
        this.price = 100 + level * 150;
        this.upgradePrice = price + 150;
        this.attackFrequency = attackFrequency;
        this.hp = hp;
        this.demolishedIn = -1;
        this.maxHp = 40 + level * 5;
        if (level == 1) {
            this.moneySpentOn = 250;
        } else if (level == 2) {
            this.moneySpentOn = 650;
        } else {
            this.moneySpentOn = 1200;
        }
    }

    /**
     * fejlesztés
     */
    @Override
    public void upgrade() {
        if (level == 2) {
            range = 2;
        }
        attackFrequency = 0.25;
        power = 3;
        hp += 5;
        level++;
        price = 100 + level * 150;
        upgradePrice = price + 150;
        maxHp = 40 + level * 5;
        moneySpentOn += price;
    }

}
