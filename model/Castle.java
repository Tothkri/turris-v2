
package model;

import java.awt.Image;


public class Castle extends Sprite{
    
    
    private int hp;

    public Castle(int x, int y, int height, int width, Image img, int hp) {
        super(x, y, height, width, img);
        this.hp = hp;
    }
    public Castle(){super(); hp = 500;}

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }
    
    
}
