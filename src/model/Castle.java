package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Castle extends Sprite {

    private int hp;
    private String color;

    public Castle(String scolor, int x, int y, int height, int width, Image img, int hp) {
        super(x, y, height, width, img);
        this.hp = hp;
        this.color = scolor;
        if (scolor.equals("red")) {
            this.img = new ImageIcon("src/res/Castlered.png").getImage();
        } else {
            this.img = new ImageIcon("src/res/Castleblue.png").getImage();
        }
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Castle() {
        super();
        hp = 500;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    private double hpLine() {
        double d = (double) hp / 300;
        return d * width;

    }

    @Override
    public void draw(Graphics2D g2) {
        Color colorFromString;
        if (color.equals("red")) {
            colorFromString = Color.RED;
        } else {
            colorFromString = Color.blue;
        }
        if (hp != 0) {
            g2.drawImage(img, x, y, height, width, null);
            g2.setColor(colorFromString);
            g2.drawLine(x , y + height - 2, x + (int) hpLine() , y + height - 2);
            g2.drawLine(x , y + height - 3, x + (int) hpLine() , y + height - 3);
            g2.drawLine(x, y + height - 4, x + (int) hpLine() , y + height - 4);
            g2.drawLine(x , y + height - 5, x + (int) hpLine() , y + height - 5);
            g2.drawLine(x , y + height - 6, x + (int) hpLine() , y + height - 6);
        }

    }

}
