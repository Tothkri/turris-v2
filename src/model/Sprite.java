package model;

import java.awt.Graphics2D;
import java.awt.Image;

public abstract class Sprite {

    protected int x;
    protected int y;
    protected int height;
    protected int width;
    protected Image img;

    /**
     * újonnan létrehozáshoz
     * @param x
     * @param y
     * @param height
     * @param width
     * @param img
     */
    public Sprite(int x, int y, int height, int width, Image img) {
        this.x = x;
        this.y = y;
        this.height = height;
        this.width = width;
        this.img = img;
    }

    /**
     * fájlból betöltéshez konstruktor
     */ 
    public Sprite() {
    }
    
    /**
     * elem kirajzolása
     * @param g2
     */
    public void draw(Graphics2D g2) {
        g2.drawImage(img, x, y, height, width, null);
    }

    /**
     * getterek, setterek
     * @return
     */
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public Image getImg() {
        return img;
    }

    public void setImg(Image img) {
        this.img = img;
    }

    
}
