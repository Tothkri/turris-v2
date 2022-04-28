package model;

import java.awt.Image;

public class Fortified extends Tower {

    /**
     *újonnan létrehozott
     * @param scolor
     * @param x
     * @param y
     * @param height
     * @param width
     * @param img
     */
    public Fortified(String scolor, int x, int y, int height, int width, Image img) {
        super(scolor, x, y, height, width, img);
        this.attackFrequency = 1.0;
        this.hp = 100;
        this.maxHp = 100;
        this.level = 1;
        this.price = 200;
        this.range = 1;
        this.power = 2;
        this.demolishedIn = -1;
        this.type = "Fortified";
        this.upgradePrice = price + 150;
        this.moneySpentOn = price;
    }

    /**
     *fájból betöltött
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
    public Fortified(String type, String scolor, int power, int range, double attackFrequency, int hp, int level, int x, int y, int height, int width, Image img) {
        super(scolor, x, y, height, width, img);
        this.type = type;
        this.power = power;
        this.range = range;
        this.attackFrequency = attackFrequency;
        this.hp = hp;
        this.level = level;
        this.price = 50 + level * 150;
        this.upgradePrice = price + 150;
        this.demolishedIn = -1;
        this.maxHp = 75 + level * 25;
        if (level == 1) {
            this.moneySpentOn = 200;
        } else if (level == 2) {
            this.moneySpentOn = 550;
        } else {
            this.moneySpentOn = 1050;
        }
    }

    /**
     * fejlesztés
     */
    @Override
    public void upgrade() {
        if (level == 2) {
            attackFrequency = 0.75;
        }
        power = 3;
        hp += 25;
        level++;
        price += 150;
        upgradePrice += 150;
        maxHp = 75 + level * 25;
        moneySpentOn += price;
    }

}
