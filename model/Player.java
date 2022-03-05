package model;

import java.util.ArrayList;

public class Player {
    private int money;
    private String name;
    private ArrayList<Tower> towers;
    private ArrayList<Unit> units;
    
    
    public Player() {
        
    }
    
    
    public void build(int x, int y, String type) {
        
    }
    
    public void upgrade(Tower t) {
        
    }
    
    public void sendUnits(String type) {
        
    }
    
    public void demolish(Tower t) {
        
    }
    

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Tower> getTowers() {
        return towers;
    }

    public void setTowers(ArrayList<Tower> towers) {
        this.towers = towers;
    }

    public ArrayList<Unit> getUnits() {
        return units;
    }

    public void setUnits(ArrayList<Unit> units) {
        this.units = units;
    }
    
}
