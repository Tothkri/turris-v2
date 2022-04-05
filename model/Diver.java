package model;

import java.awt.Image;
import java.util.ArrayList;

public class Diver extends Unit {

    //constructor for loaded units
    public Diver(String type, String scolor, int distance, int power, int hp, int price, int x, int y, int height, int width, Image img) {
        super(scolor, x, y, height, width, img);
         this.maxHp=10;
    }

    //constructor for creating new unit
    public Diver(String scolor, int x, int y, int height, int width, Image img, ArrayList<Node> way) {
        super(scolor, x, y, height, width, img);
        this.distance = 4;
        this.power = 4;
        this.hp = 10;
        this.price = 20;
        this.type = "Diver";
        this.maxHp=10;
    }
}
