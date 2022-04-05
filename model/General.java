package model;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;

public class General extends Unit{

    //constructor for loaded units
    public General(String type, String scolor, int distance, int power, int hp, int price, int x, int y, int height, int width, Image img) {
        super(scolor, x, y, height, width, img);
        this.maxHp=10;
    }

    //constructor for creating new unit
    public General(String scolor, int x, int y, int height, int width, Image img, ArrayList<Node> way) {
        super(scolor, x, y, height, width, img);
        this.distance = 5;
        this.power = 5;
        this.hp = 10;
        this.price = 20;
        this.type="General";
        this.maxHp=10;
    }
}
