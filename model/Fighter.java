package model;

import java.awt.Image;
import java.util.ArrayList;

public class Fighter extends Unit {

    //constructor for loaded units
    public Fighter(String type, String scolor, int distance, int power, int hp, int price, int x, int y, int height, int width, Image img) {
        super(scolor, x, y, height, width, img);
        this.maxHp = 15;
    }

    //constructor for creating new unit
    public Fighter(String scolor, int x, int y, int height, int width, Image img, ArrayList<Node> way) {
        super(scolor, x, y, height, width, img);
        this.distance = 3;
        this.power = 6;
        this.hp = 15;
        this.price = 40;
        this.type = "Fighter";
        this.maxHp = 15;
    }
}
