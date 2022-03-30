package model;

import static java.lang.Integer.max;
import static java.lang.Integer.parseInt;
import static java.lang.Math.abs;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Player {

    private int money;
    private String name;
    private ArrayList<Tower> towers;
    private ArrayList<Unit> units;
    private Castle castle;
    private final int[] row = {-1, 0, 0, 1};
    private final int[] col = {0, -1, 1, 0};
    private int[][] difficulty = new int[30][30];
    //how you can move from a Node

    public Castle getCastle() {
        return castle;
    }

    public void setCastle(Castle castle) {
        this.castle = castle;
    }

    public Player(int money, String name) {
        this.money = money;
        this.name = name;
        this.towers = new ArrayList<>();
        this.units = new ArrayList<>();
    }

    public Model build(int x, int y, String type, Model model) {

        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                if (model.position[i][j] == 'F' || model.position[i][j] == 'C') {
                    difficulty[i][j] = 1 + model.damagePerHalfSec(model.getActivePlayer(), i, j);
                } else //cant go through so we set a large number
                {
                    difficulty[i][j] = 1000;
                }
            }

        }

        model.setSelectables(new ArrayList<>());

        if (isValid(x, y) && y >= model.getActivePlayer() * 15
                && y < (model.getActivePlayer() + 1) * 15) /*
                        deciding if the player clicked to the board
                        player 1's y area: 0-14
                         player 2's y area: 15-29
         */ {
            if (model.getPosition()[x][y] == 'F' && model.placable(x, y, difficulty)) //deciding if player clicked to a Field or not
            {
                //creating chosen tower and adding it to the game (if there's chosen tower)

                if (!type.equals("")) {
                    Tower newTower;
                    if (type.equals("Fortified")) {
                        newTower = new Fortified("Fortified", 2, 1, 0.5, 100, 200, x * (model.getSize() / 30),
                                y * (model.getSize() / 30),
                                model.getSize() / 30, model.getSize() / 30,
                                new ImageIcon("src/res/Tower.png").getImage());
                    } else if (type.equals("Sniper")) {
                        newTower = new Sniper("Sniper", 9, 1, 1.0, 25, 300, x * (model.getSize() / 30),
                                y * (model.getSize() / 30),
                                model.getSize() / 30, model.getSize() / 30,
                                new ImageIcon("src/res/Tower.png").getImage());
                    } else //type.equals("Sniper")
                    {
                        newTower = new Rapid("Rapid", 2, 1, 0.33, 40, 250, x * (model.getSize() / 30),
                                y * (model.getSize() / 30),
                                model.getSize() / 30, model.getSize() / 30,
                                new ImageIcon("src/res/Tower.png").getImage());
                    }

                    //checking if player's money is enough to buy it
                    if (newTower.price <= money) {
                        money -= newTower.price;

                        model.setPosition(x, y, 'T');
                        model.addTerrainElement(newTower);
                        addTower(newTower);
                    }

                }
            }

        }

        return model;

    }

    public void upgrade(int x, int y, int size) {
        x *= (size / 30);
        y *= (size / 30);
        money -= 100;//will change
        for (var t : towers) {
            if (t.getX() == x && t.getY() == y) {
                t.upgrade();
            }
        }
    }

    public void demolish(int x, int y, int size) {
        x *= (size / 30);
        y *= (size / 30);
        for (var t : towers) {
            if (t.getX() == x && t.getY() == y) {
                t.demolish();
            }
        }
    }
    
   

    private boolean isValid(int x, int y) //determine if it's on the board or not
    {
        return (x >= 0 && x < 30) && (y >= 0 && y < 30);
    }

    public Model sendUnits(String type, int amount, Model model) {

        int minDistance = 10000;
        ArrayList<Node> bestWay = new ArrayList<>();

        /*
        we need to calculate all best ways from each of 4 castle coordinates 
        to the other 4 castle coordinates and find the shortest of them
         */
 /*
            stores how difficult to go through each 
            1 - field
            2 - mountain and lake if unit is Climber/Diver
         */
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                if (type.equals("Diver")) //they can go through lakes
                {
                    if (model.position[i][j] == 'F' || model.position[i][j] == 'C') {
                        difficulty[i][j] = 1 + model.damagePerHalfSec(model.getActivePlayer(), i, j);
                    } else if (model.position[i][j] == 'L') {
                        difficulty[i][j] = 2 + model.damagePerHalfSec(model.getActivePlayer(), i, j);
                    } else //cant go through so we set a large number
                    {
                        difficulty[i][j] = 1000;
                    }
                } else if (type.equals("Climber")) //they can go through mountains
                {
                    if (model.position[i][j] == 'F' || model.position[i][j] == 'C') {
                        difficulty[i][j] = 1 + model.damagePerHalfSec(model.getActivePlayer(), i, j);
                    } else if (model.position[i][j] == 'M') {
                        difficulty[i][j] = 2 + model.damagePerHalfSec(model.getActivePlayer(), i, j);
                    } else //cant go through so we set a large number
                    {
                        difficulty[i][j] = 1000;
                    }
                } else //normal unit
                {
                    if (model.position[i][j] == 'F' || model.position[i][j] == 'C') {
                        difficulty[i][j] = 1 + model.damagePerHalfSec(model.getActivePlayer(), i, j);
                    } else //cant go through so we set a large number
                    {
                        difficulty[i][j] = 1000;
                    }
                }

            }
        }

        for (int i = 0; i < amount; i++) {

            int attacker = model.getActivePlayer() * 4;
            int defender = Math.abs(model.getActivePlayer() * 4 - 4);
            for (int j = 0; j < 4; j++) {
                for (int k = 0; k < 4; k++) {
                    ArrayList<String> wayString = findWay(model.getCastleCoordinates()[j + attacker][0], model.getCastleCoordinates()[j + attacker][1],
                            model.getCastleCoordinates()[k + defender][0], model.getCastleCoordinates()[k + defender][1], difficulty);
                    ArrayList<Node> nodeWay = convertWay(wayString);
                    if (model.wayDiff(model.getActivePlayer(), wayString, type) < minDistance) {
                        bestWay = nodeWay;
                        minDistance = model.wayDiff(model.getActivePlayer(), wayString, type);
                    }
                }
            }

            ArrayList<Node> way = bestWay;

            if (way != null && way.size() > 0) {
                Unit newUnit;
                if (type.equals("General")) {
                    newUnit = new General("General", 5, 3, 10, 20, castle.x, castle.y,
                            model.getSize() / 30, model.getSize() / 30, new ImageIcon("src/res/Unit.png").getImage(),
                            way);
                } else if (type.equals("Climber")) {
                    newUnit = new Climber("Climber", 3, 2, 10, 30, castle.x, castle.y,
                            model.getSize() / 30, model.getSize() / 30, new ImageIcon("src/res/Unit.png").getImage(),
                            way);
                } else if (type.equals("Diver")) {
                    newUnit = new Diver("Diver", 3, 2, 10, 30, castle.x, castle.y,
                            model.getSize() / 30, model.getSize() / 30, new ImageIcon("src/res/Unit.png").getImage(),
                            way);
                } else if (type.equals("Fighter")) {
                    newUnit = new Fighter("Fighter", 4, 5, 15, 30, castle.x, castle.y,
                            model.getSize() / 30, model.getSize() / 30, new ImageIcon("src/res/Unit.png").getImage(),
                            way);
                } else //Destroyer
                {
                    newUnit = new Destroyer("Destroyer", 2, 5, 15, 30, castle.x, castle.y,
                            model.getSize() / 30, model.getSize() / 30, new ImageIcon("src/res/Unit.png").getImage(),
                            way);
                }
                //checking if player's money is enough to buy it
                if (newUnit.price <= money) {
                    money -= newUnit.price;
                    addUnits(newUnit);
                }

            }
        }

        return model;

    }

    public ArrayList<String> findWay(int fromX, int fromY, int toX, int toY, int difficulty[][]) /* 
            stores the best way as String from the home castle to the enemy castle
            step to step as (x, y) pairs
            (units can't move in any diagonal directions)

     */ {

        ArrayList<String> bestway = new ArrayList<>();

        int currentX = fromX;
        int currentY = fromY;
        //begin from own castle

        Queue<Node> q = new ArrayDeque<>();
        Node from = new Node(currentX, currentY, null);
        q.add(from);

        Set<String> visited = new HashSet<>();
        // to know if we went across a Node or not
        visited.add(from.toString());
        while (!q.isEmpty()) {
            Node current = q.poll();
            int i = current.getX();
            int j = current.getY();

            if (i == toX && j == toY) //destination found
            {
                findNodeWay(current, bestway);

                return bestway;
            }

            int currentValue = difficulty[i][j];

            for (int k = 0; k < row.length; k++) {
                currentX = i + (row[k] * currentValue);
                currentY = j + (col[k] * currentValue);

                if (isValid(currentX, currentY)) //check if we're in the board
                {
                    Node next = new Node(currentX, currentY, current);

                    if (!visited.contains(next.toString())) //if node is not visited yet, we add it to the way and flag as visited
                    {
                        q.add(next);
                        visited.add(next.toString());
                    }
                }
            }
        }

        return bestway;
    }

    public ArrayList<Node> convertWay(ArrayList<String> src) {

        ArrayList<Node> way = new ArrayList<Node>();

        if (src.size() == 0 || src == null) {
            return new ArrayList<Node>();
        }

        way.add(new Node(parseInt(src.get(0).split(";")[0]),
                parseInt(src.get(0).split(";")[1])));

        for (int i = 1; i < src.size(); i++) {
            int x1 = parseInt(src.get(i - 1).split(";")[0]);
            int y1 = parseInt(src.get(i - 1).split(";")[1]);
            int x2 = parseInt(src.get(i).split(";")[0]);
            int y2 = parseInt(src.get(i).split(";")[1]);

            if (max(abs(x1 - x2), abs(y1 - y2)) > 1) {

                if (x1 > x2) {
                    for (int j = x1 - 1; j > x2; j--) {
                        way.add(new Node(j, y1));
                    }
                } else if (x2 > x1) {
                    for (int j = x1 + 1; j < x2; j++) {
                        way.add(new Node(j, y1));
                    }
                } else if (y2 > y1) {
                    for (int j = y1 + 1; j < y2; j++) {
                        way.add(new Node(x1, j));
                    }
                } else {
                    for (int j = y1 - 1; j < y2; j--) {
                        way.add(new Node(x1, j));
                    }
                }

            } else {
                way.add(new Node(x2, y2));
            }

        }

        //check if there are same positions after each other
        for (int j = 1; j < way.size(); j++) {
            if (way.get(j - 1).equals(way.get(j))) {
                way.remove(j);
                j--;
            }
        }

        return way;
    }

    private void findNodeWay(Node n, ArrayList<String> way) //find the way between two Nodes
    {
        if (n != null) {
            findNodeWay(n.parent, way);
            way.add(n.toString());
        }
    }

    public void deleteUnit(Unit unitToDelete) {

        units.remove(unitToDelete);

    }

    public int[][] getDifficulty(Model model, String type) {
        for (int i = 0; i < 30; i++) {
            for (int j = 0; j < 30; j++) {
                if (type.equals("Diver")) //they can go through lakes
                {
                    if (model.position[i][j] == 'F' || model.position[i][j] == 'C') {
                        difficulty[i][j] = 1 + model.damagePerHalfSec(model.getActivePlayer(), i, j);
                    } else if (model.position[i][j] == 'L') {
                        difficulty[i][j] = 2 + model.damagePerHalfSec(model.getActivePlayer(), i, j);
                    } else //cant go through so we set a large number
                    {
                        difficulty[i][j] = 1000;
                    }
                } else if (type.equals("Climber")) //they can go through mountains
                {
                    if (model.position[i][j] == 'F' || model.position[i][j] == 'C') {
                        difficulty[i][j] = 1 + model.damagePerHalfSec(model.getActivePlayer(), i, j);
                    } else if (model.position[i][j] == 'M') {
                        difficulty[i][j] = 2 + model.damagePerHalfSec(model.getActivePlayer(), i, j);
                    } else //cant go through so we set a large number
                    {
                        difficulty[i][j] = 1000;
                    }
                } else //normal unit
                {
                    if (model.position[i][j] == 'F' || model.position[i][j] == 'C') {
                        difficulty[i][j] = 1 + model.damagePerHalfSec(model.getActivePlayer(), i, j);
                    } else //cant go through so we set a large number
                    {
                        difficulty[i][j] = 1000;
                    }
                }
            }

        }
        return difficulty;
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

    public void addUnits(Unit newUnit) {
        units.add(newUnit);
    }

    public void setTowers(ArrayList<Tower> t) {
        towers = t;
    }

}
