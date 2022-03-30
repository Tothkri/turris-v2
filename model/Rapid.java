package model;

import java.awt.Image;

public class Rapid extends Tower{

    public Rapid(String type, int power, int range, double attack_speed, int hp, int price, int x, int y, int height, int width, Image img) {
        super(type, power, range, attack_speed, hp, price, x, y, height, width, img);
    }
    public void upgrade(){
        level++;
        if(level == 2){
            attack_speed = 0.25;
            power = 3;
        }else{
            range = 2;
            hp += 15;
        }
    }
}
