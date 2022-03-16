package model;

import model.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Player {

    private int money;
    private String name;
    private ArrayList<Tower> towers;
    private ArrayList<Unit> units;
    private Castle castle;

    public Castle getCastle() {
        return castle;
    }

    public void setCastle(Castle castle) {
        this.castle = castle;
    }

    public Player(int money, String name) {
        this.money = money;
        this.name = name;
        this.towers = new ArrayList<Tower>();
        this.units = new ArrayList<Unit>();
    }

    public Model build(int x, int y, String type, Model model) {
        
        
        model.setSelectables(new ArrayList<>());
        
        if (x >= 0 && x < 30 && y >= model.getActivePlayer() * 15
                && y <= ((model.getActivePlayer() + 1) * 15) - 1) /*
                        deciding if the player clicked to the board
                        player 1's y area: 0-14
                         player 2's y area: 15-29
         */ {
            if (model.getPosition()[x][y] == 'F') //deciding if player clicked to a field or not
            {
                //creating chosen tower and adding it to the game (if there's chosen tower)

                if (!type.equals("")) {

                    Tower newTower = switch (type) {
                        case "Fortified" ->
                            new Fortified("Fortified", 2, 1, 0.5, 100, 200, x * (model.getSize() / 30),
                            y * (model.getSize() / 30),
                            model.getSize() / 30, model.getSize() / 30,
                            new ImageIcon("src/res/Tower.png").getImage());
                        case "Sniper" ->
                            new Sniper("Sniper", 9, 1, 1.0, 25, 300, x * (model.getSize() / 30),
                            y * (model.getSize() / 30),
                            model.getSize() / 30, model.getSize() / 30,
                            new ImageIcon("src/res/Tower.png").getImage());
                        case "Rapid" ->
                            new Rapid("Rapid", 2, 1, 0.33, 40, 250, x * (model.getSize() / 30),
                            y * (model.getSize() / 30),
                            model.getSize() / 30, model.getSize() / 30,
                            new ImageIcon("src/res/Tower.png").getImage());
                        default ->
                            new Fortified("Fortified", 2, 1, 0.5, 100, 200, x * (model.getSize() / 30),
                            y * (model.getSize() / 30),
                            model.getSize() / 30, model.getSize() / 30,
                            new ImageIcon("src/res/Tower.png").getImage());
                    };

                    if(newTower.price <= money)
                    {
                        money -= newTower.price;
                        model.setPosition(x, y, 'T');
                        model.addTerrainElement(newTower);
                        addTower(newTower);
                    } else //player gets a warning 
                    {
                        JOptionPane.showMessageDialog(null, "You don't have enough money to build this tower!", "Warning", JOptionPane.INFORMATION_MESSAGE);
                    }

                }
            } else //player gets a warning 
            {
                JOptionPane.showMessageDialog(null, "You can't place a tower here!", "Warning", JOptionPane.INFORMATION_MESSAGE);
            }

        } else //player gets a warning 
        {
            JOptionPane.showMessageDialog(null, "You can't place a tower here!", "Warning", JOptionPane.INFORMATION_MESSAGE);
        }

        return model;

    }
    public void upgrade(Tower t) {

    }

    public void sendUnits(String type, Model model) {

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

    public void addTower(Tower newTower) {
        towers.add(newTower);
    }

    public ArrayList<Unit> getUnits() {
        return units;
    }

    public void setUnits(ArrayList<Unit> units) {
        this.units = units;
    }

}
