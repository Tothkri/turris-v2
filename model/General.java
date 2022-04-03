package model;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;

public class General extends Unit{

    public General(String type,String scolor, int distance, int power, int hp, int price, int x, int y, int height, int width, Image img,ArrayList<Node> way) {
        super(type,scolor, distance, power, hp, price, x, y, height, width, img,way);
    }
}
