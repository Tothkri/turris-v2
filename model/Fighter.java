package model;

import java.awt.Image;
import java.util.ArrayList;

public class Fighter extends Unit{

    public Fighter(String type,String scolor, int distance, int power, int hp, int price, int x, int y, int height, int width, Image img,ArrayList<Node> way) {
        super(type,scolor, distance, power, hp, price, x, y, height, width, img,way);
    }
}
