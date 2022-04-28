package model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import javax.swing.ImageIcon;

public class Castle extends Sprite {

    private int hp;
    private String color;

    /**
     * új játékhoz konstruktor
     * @param scolor
     * @param x
     * @param y
     * @param height
     * @param width
     * @param img
     * @param hp
     */
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
    
    /**
     * fájlból betöltéshez konstruktor
     */
    public Castle() {
        super();
        hp = 300;
    }
    
    /**
     * hp csík hossza
     */
    private double hpLineLength() {
        double d = (double) hp / 300;
        return d * width;

    }

    /**
     * kastély kirajzolás
     * @param g2
     */
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
            int length=(int)hpLineLength();
            for(int i=2;i<6;i++){
                g2.drawLine(x , y + height - i, x + (int) length , y + height - i);
            }
            
        }

    }
    
    /**
     * hp getterek, setterek
     * @return
     */
   
    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

}
