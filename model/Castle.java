
package model;

import java.awt.Image;
import javax.swing.ImageIcon;


public class Castle extends Sprite{
    
    
    private int hp;
    private String color;

    public Castle(String scolor,int x, int y, int height, int width, Image img, int hp) {
        super(x, y, height, width, img);
        this.hp = hp;
        this.color=scolor;
        if(scolor.equals("red")){
            this.img = new ImageIcon("src/res/Castlered.png").getImage();
        }
        else{
             this.img = new ImageIcon("src/res/Castleblue.png").getImage();
        }
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
    public Castle(){super(); hp = 500;}

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }
    
    
}
