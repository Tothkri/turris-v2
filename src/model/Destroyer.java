package model;

import java.awt.Image;
import java.util.ArrayList;

public class Destroyer extends Unit {

     /**
     *újonnan létrehozott
     * @param scolor
     * @param x
     * @param y
     * @param height
     * @param width
     * @param img
     * @param way
     */
    public Destroyer(String scolor, int x, int y, int height, int width, Image img, ArrayList<Node> way) {
        super(scolor, x, y, height, width, img);
        this.distance = 3;
        this.power = 6;
        this.hp = 15;
        this.price = 40;
        this.type = "Destroyer";
        this.maxHp = 15;
        this.way = way;
    }

    /**
     *fájlból betöltött
     * @param scolor
     * @param hp
     * @param x
     * @param y
     * @param height
     * @param width
     * @param img
     */
    public Destroyer(String scolor, int hp, int x, int y, int height, int width, Image img) {
        super(scolor, x, y, height, width, img);
        this.distance = 3;
        this.power = 6;
        this.hp = hp;
        this.price = 40;
        this.type = "Destroyer";
        this.maxHp = 15;
    }

}
