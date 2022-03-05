package model;

import java.util.ArrayList;

public class Model {
    protected int level;
    protected Player player1;
    protected Player player2;
    protected int round;
    protected ArrayList<Sprite> terrain;
    
    
    public Model(int selectedMap, String p1Name, String p2Name) {
        
    }
    
    
    public boolean isOver() {
        return false;
    }
    
    public boolean roundOver() {
        return false;
    }
    
    public void unitAttack() {
        
    }
    
    public void towerDefense() {
        
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public ArrayList<Sprite> getTerrain() {
        return terrain;
    }

    public void setTerrain(ArrayList<Sprite> terrain) {
        this.terrain = terrain;
    }
}
