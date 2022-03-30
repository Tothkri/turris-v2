package model;

import java.awt.Image;

public class Sniper extends Tower{

    public Sniper(String type, int power, int range, double attack_speed, int hp, int price, int x, int y, int height, int width, Image img) {
        super(type, power, range, attack_speed, hp, price, x, y, height, width, img);
    }
    public void upgrade(){
        level++;
        if(level == 2){
            attack_speed = 1;
            power = 9;
            range = 2;
        }else{
            attack_speed = 1;
            power = 10;
            hp += 10;
            range = 3;
        }
    }
}
