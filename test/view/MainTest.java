package view;

import java.io.IOException;
import java.util.ArrayList;
import model.*;
import view.*;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class MainTest {
    
    private GameWindow gw;
    private Model m;
    private Player p1;
    private Player p2;
    
    public MainTest() {
    }
    
    public void Initialize(String fileName){
        StartScreen ss = new StartScreen(1920, 1080);
        try{
            gw = new GameWindow();
            ss.loadGame(gw,"test/testTxts/" +fileName);
            gw.constructor(1920,1080);
            m = gw.getBoard().getModel();
            p1 = m.getPlayers()[0];
            p2 = m.getPlayers()[1];
        }
        catch(IOException e){
        }
    }
    
    /**
     * Both players start with the correct amount of money
     * Both castles start with the correct amount of health
     * Not current player can't use his buttons
     */   
    @Test
    public void testStart() {
        Initialize("plain.txt");
        int activePlayer = m.getActivePlayer();
        
        assertTrue(p1.getMoney() == 1000 && p2.getMoney() == 1000);
        assertTrue(p1.getCastle().getHp() == 300 && p2.getCastle().getHp() == 300);
        if(activePlayer == 0){
            for(var button : gw.getP2TowerButtons()){
                assertTrue(!button.isEnabled());
            }
            for(var button : gw.getP2UnitButtons()){
                assertTrue(!button.isEnabled());
            }
        }else{
            for(var button : gw.getP1TowerButtons()){
                assertTrue(!button.isEnabled());
            }
            for(var button : gw.getP1UnitButtons()){
                assertTrue(!button.isEnabled());
            }
        }
    }
    /**
     * Checks if the units, towers, castles,other stats are correct after loading
     */
    @Test
    public void testLoad(){
        Initialize("plain1U1T.txt");
        assertTrue(m.getPosition()[2][2] == 'C');
        assertTrue(m.getPosition()[2][3] == 'C');
        assertTrue(m.getPosition()[3][2] == 'C');
        assertTrue(m.getPosition()[3][3] == 'C');
        
        assertTrue(m.getPosition()[12][26] == 'C');
        assertTrue(m.getPosition()[13][26] == 'C');
        assertTrue(m.getPosition()[12][27] == 'C');
        assertTrue(m.getPosition()[13][27] == 'C');
                
        assertTrue(m.getPosition()[27][3] == 'T');
        assertTrue(m.getPlayers()[0].getTowers().size() == 1);
        assertTrue(m.getPlayers()[0].getTowers().get(0).getLevel() == 1);
        assertTrue(m.getPlayers()[0].getTowers().get(0).getHp() == 100);
        assertTrue(m.getPlayers()[0].getTowers().get(0).getType().equals("Fortified"));
        
        assertTrue(m.getPlayers()[1].getUnits().get(0).getHp() == 10);
        assertTrue(m.getPlayers()[1].getUnits().get(0).getType().equals("General"));
        
        assertTrue(m.getActivePlayer() == 1);
        assertTrue(m.getRound() == 18);
    }
    /**
     * Test the amount of mountains and lakes generated on different maps
     */
    @Test
    public void testSelectedMap(){
        Initialize("plain.txt");
        gw = new GameWindow(1, 1, "Player 1", "Player 2", 1);
        m = gw.getBoard().getModel();
        assertTrue(Math.abs(m.getMcount() - m.getLcount()) >= 0 && Math.abs(m.getMcount() - m.getLcount()) <= 9);
        
        gw = new GameWindow(1, 1, "Player 1", "Player 2", 2);
        m = gw.getBoard().getModel();
        assertTrue(m.getMcount() - m.getLcount() >= 21 && m.getMcount() - m.getLcount() <= 29);
        
        gw = new GameWindow(1, 1, "Player 1", "Player 2", 3);
        m = gw.getBoard().getModel();
        assertTrue(m.getLcount() - m.getMcount() >= 21 && m.getLcount() - m.getMcount() <= 29);
    }
    /**
     * Checks if tower is correctly built
     * Checks if the money is deducted
     * Checks if it appears at the position
     * Checks if player has the tower
     * Won't build if there's no place for it, or has no money for it
     * Won't build if the tower would block the castle from units
     * Checks if selects are correct
     */
    @Test
    public void testTowerBuild(){
        Initialize("plain6T.txt");
        char[][] position = m.getPosition();
        int x = 0, y = 0;
        m.setActivePlayer(0);
        
        m.getPlayers()[0].build(x, y, "Fortified", m);
        assertTrue(m.getPlayers()[0].getMoney() == 800);
        assertTrue(m.getPosition()[x][y] == 'T');
        assertTrue(m.getPlayers()[0].getTowers().size() == 8);
        
        m.getPlayers()[0].build(x + 1, y + 1, "Sniper", m);
        assertTrue(m.getPlayers()[0].getMoney() == 500);
        assertTrue(m.getPosition()[x + 1][y + 1] == 'T');
        assertTrue(m.getPlayers()[0].getTowers().size() == 9);
        
        m.getPlayers()[0].build(x + 2, y, "Rapid", m);
        assertTrue(m.getPlayers()[0].getMoney() == 250);
        assertTrue(m.getPosition()[x + 2][y] == 'T');
        assertTrue(m.getPlayers()[0].getTowers().size() == 10);
       
        m.getPlayers()[0].build(x, y, "Fortified", m);
        assertTrue(m.getPlayers()[0].getMoney() == 250);
        assertTrue(m.getPlayers()[0].getTowers().size() == 10);
        
        m.getPlayers()[0].build(x + 3, y, "Sniper", m);
        assertTrue(m.getPlayers()[0].getMoney() == 250);
        assertTrue(m.getPlayers()[0].getTowers().size() == 10);
        
        x = 4;
        y = 3;
        m.getPlayers()[0].build(x, y, "Fortified", m);
        assertTrue(m.getPlayers()[0].getMoney() == 250);
        assertTrue(m.getPlayers()[0].getTowers().size() == 10);
        
        m.setSelectables();
        assertTrue(m.getSelectables().size() == 435);
    }
    /**
     * Check if player can or cannot upgrade
     */
    @Test
    public void upgradeTower(){
        Initialize("plain3T.txt");
        int size = 1080 - 150;
        m.getPlayers()[0].upgrade(28, 3, size, m.getPosition()[28][3]);
        assertTrue(m.getPlayers()[0].getMoney() == 450);
        
        m.setSelectableTowersToUpgrade(m.getPlayers()[0].getMoney());
        assertTrue(m.getSelectables().size() == 1);
        m.setSelectables(new ArrayList<>());
        
        m.getPlayers()[0].upgrade(28, 3, size, m.getPosition()[28][3]);
        assertTrue(m.getPlayers()[0].getMoney() == 450);
        
        m.getPlayers()[0].setMoney(550);
        m.setSelectableTowersToUpgrade(m.getPlayers()[0].getMoney());
        assertTrue(m.getSelectables().get(2).getX() / 30 == 28 && m.getSelectables().get(2).getY() / 30 == 3);
        m.setSelectables(new ArrayList<>());
        
        m.getPlayers()[0].upgrade(28, 3, size, m.getPosition()[28][3]);
        assertTrue(m.getPlayers()[0].getMoney() == 0);
        assertTrue(m.getPlayers()[0].getTowers().get(0).getLevel() == 3);
        
        m.getPlayers()[0].setMoney(5250);
        m.getPlayers()[0].upgrade(28, 3, size, m.getPosition()[28][3]);
        assertTrue(m.getPlayers()[0].getTowers().get(0).getLevel() == 3);
        
        m.getPlayers()[0].setMoney(0);
        m.getPlayers()[0].upgrade(27,3,size, m.getPosition()[27][3]);
        assertTrue(m.getPlayers()[0].getMoney() == 0);
        
        m.getPlayers()[0].setMoney(420);
        m.setSelectableTowersToUpgrade(m.getPlayers()[0].getMoney());
        assertTrue(m.getSelectables().get(0).getX() / 30 == 27 && m.getSelectables().get(0).getY() / 30 == 3);
        m.setSelectables(new ArrayList<>());
        m.getPlayers()[0].upgrade(27,3,size, m.getPosition()[27][3]);
        assertTrue(m.getPlayers()[0].getMoney() == 20);
        
        m.getPlayers()[0].setMoney(570);
        m.setSelectableTowersToUpgrade(m.getPlayers()[0].getMoney());
        assertTrue(m.getSelectables().get(1).getX() / 30 == 27 && m.getSelectables().get(1).getY() / 30 == 3);
        m.setSelectables(new ArrayList<>());
        m.getPlayers()[0].upgrade(27,3,size, m.getPosition()[26][3]);
        assertTrue(m.getPlayers()[0].getMoney() == 20);
        
        m.getPlayers()[0].upgrade(26,3,size, m.getPosition()[26][3]);
        assertTrue(m.getPlayers()[0].getMoney() == 20);
        assertTrue(m.getPlayers()[0].getTowers().get(2).getLevel() == 1);
        
        m.getPlayers()[0].setMoney(520);
        m.setSelectableTowersToUpgrade(m.getPlayers()[0].getMoney());
        assertTrue(m.getSelectables().get(0).getX() / 30 == 26 && m.getSelectables().get(0).getY() / 30 == 3);
        m.setSelectables(new ArrayList<>());
        m.getPlayers()[0].upgrade(26,3,size, m.getPosition()[26][3]);
        assertTrue(m.getPlayers()[0].getMoney() == 20);
        
        m.getPlayers()[0].setMoney(720);
        m.setSelectableTowersToUpgrade(m.getPlayers()[0].getMoney());
        assertTrue(m.getSelectables().get(0).getX() / 30 == 26 && m.getSelectables().get(0).getY() / 30 == 3);
        m.setSelectables(new ArrayList<>());
        m.getPlayers()[0].upgrade(26,3,size, 'T');
        assertTrue(m.getPlayers()[0].getMoney() == 20);
        assertTrue(m.getPlayers()[0].getTowers().get(2).getLevel() == 3);
        assertTrue(m.getPlayers()[0].getTowers().get(2).getRange() == 3);
        assertTrue(m.getPlayers()[0].getTowers().get(2).getPower() == 10);
        assertTrue(m.getPlayers()[0].getTowers().get(2).getAttack_speed() == 1.0);
        assertTrue(m.getPlayers()[0].getTowers().get(2).getHp() == 35);
    }
    /**
     * Checks is destroyed tower gives money back
     * Cannot destroy a destroyed tower
     * Cannot destroy a non-tower
     */
    @Test
    public void testDemolishTower(){
        Initialize("plain3T.txt");
        int size = 1080 - 150;
        
        m.getPlayers()[0].demolish(28, 3, size, m.getPosition()[28][3]);
        assertTrue(m.getPlayers()[0].getMoney() == 900);
        
        m.getPosition()[28][3] = 'D';
        m.getPlayers()[0].upgrade(28,3,size, m.getPosition()[28][3]);
        assertTrue(m.getPlayers()[0].getMoney() == 900);
        
        m.getPlayers()[0].demolish(27, 3, size, m.getPosition()[27][3]);
        assertTrue(m.getPlayers()[0].getMoney() == 1025);
        m.getPosition()[27][3] = 'D';
        
        m.getPlayers()[0].demolish(27, 3, size, m.getPosition()[27][3]);
        assertTrue(m.getPlayers()[0].getMoney() == 1025);
        
        m.getPlayers()[0].setMoney(2000);
        m.getPlayers()[0].upgrade(26,3,size, 'T');
        m.getPlayers()[0].upgrade(26,3,size, 'T');
        assertTrue(m.getPlayers()[0].getTowers().get(2).getLevel() == 3);
        
        m.getPlayers()[0].demolish(26, 3, size, m.getPosition()[26][3]);
        assertTrue(m.getPlayers()[0].getMoney() == 1150);
        
        m.getPlayers()[0].demolish(5, 5, size, m.getPosition()[5][5]);
        assertTrue(m.getPlayers()[0].getMoney() == 1150);
    }
    /**
     * Checks, if unit damages the castle
     */
    @Test
    public void testUnitsDamgeToCastle(){
        Initialize("plain.txt");
        
        m.getPlayers()[0].sendUnits("General", "red", 10, m);
        assertTrue(m.getPlayers()[0].getMoney() == 800);
        
        for (int q = 0; q < 2; q++) {
            int defender = Math.abs(q * 4 - 4);
            ArrayList<Unit> updateUnits = m.getPlayers()[q].getUnits();
            for (Unit u : updateUnits) {
                if (q == 0) {
                    gw.getPlayer1distances().add(u.getDistance());
                } else {
                    gw.getPlayer2distances().add(u.getDistance());
                }
                int minWayDiff = 10000;
                ArrayList<Node> bestway = new ArrayList<>();

                for (int i = 0; i < 4; i++) {
                    ArrayList<String> wayString = m.getPlayers()[q].findWay(u.getX() / (m.getSize() / 30),
                            u.getY() / (m.getSize() / 30), m.getCastleCoordinates()[i + defender][0],
                            m.getCastleCoordinates()[i + defender][1], m.getPlayers()[q].getDifficulty(m, u.getType(), q));

                    if (minWayDiff > m.wayDiff(q, wayString, u.getType())) {
                        bestway = m.getPlayers()[q].convertWay(wayString);
                        minWayDiff = m.wayDiff(q, wayString, u.getType());

                    }
                }
                if (!bestway.isEmpty()) {
                    u.setWay(bestway);
                }

                m.getPlayers()[q].setUnits(updateUnits);
            }
        }      

        gw.simulation();
        gw.simulation();
        gw.simulation();
        gw.simulation();
        gw.simulation();
        gw.simulation();
        gw.getPlayer1distances().clear();
        gw.getPlayer2distances().clear();
        
        for(int k = 0; k < 6; k++){
            for (int q = 0; q < 2; q++) {
            ArrayList<Unit> updateUnits = m.getPlayers()[q].getUnits();
            for (Unit u : updateUnits) {
                    if (q == 0) {
                        gw.getPlayer1distances().add(u.getDistance());
                    } else {
                        gw.getPlayer2distances().add(u.getDistance());
                    }
                }
            }
            gw.simulation();
            gw.simulation();
            gw.simulation();
            gw.simulation();
            gw.simulation();
            gw.simulation();
            gw.getPlayer1distances().clear();
            gw.getPlayer2distances().clear();
        }
        
        assertTrue(m.getPlayers()[1].getCastle().getHp() == 300 - (10 * 5));
        
        m.getPlayers()[0].sendUnits("General", "red", 50, m);
        assertTrue(m.getPlayers()[0].getMoney() == 800);
    }
    
    @Test
    public void testUnitDistance(){
        Initialize("plain.txt");
        
        m.getPlayers()[0].sendUnits("General", "red", 1, m);
        
        for (int q = 0; q < 2; q++) {
            int defender = Math.abs(q * 4 - 4);
            ArrayList<Unit> updateUnits = m.getPlayers()[q].getUnits();
            for (Unit u : updateUnits) {
                if (q == 0) {
                    gw.getPlayer1distances().add(u.getDistance());
                } else {
                    gw.getPlayer2distances().add(u.getDistance());
                }
                int minWayDiff = 10000;
                ArrayList<Node> bestway = new ArrayList<>();

                for (int i = 0; i < 4; i++) {
                    ArrayList<String> wayString = m.getPlayers()[q].findWay(u.getX() / (m.getSize() / 30),
                            u.getY() / (m.getSize() / 30), m.getCastleCoordinates()[i + defender][0],
                            m.getCastleCoordinates()[i + defender][1], m.getPlayers()[q].getDifficulty(m, u.getType(), q));

                    if (minWayDiff > m.wayDiff(q, wayString, u.getType())) {
                        bestway = m.getPlayers()[q].convertWay(wayString);
                        minWayDiff = m.wayDiff(q, wayString, u.getType());

                    }
                }
                if (!bestway.isEmpty()) {
                    u.setWay(bestway);
                }

                m.getPlayers()[q].setUnits(updateUnits);
            }
        } 
        gw.setModel(m);
        gw.simulation();
        gw.simulation();
        gw.simulation();
        gw.simulation();
        gw.simulation();
        int x = gw.getBoard().getModel().getPlayers()[0].getUnits().get(0).getX();
        int y = gw.getBoard().getModel().getPlayers()[0].getUnits().get(0).getY();
        gw.simulation();
        assertTrue(x == gw.getBoard().getModel().getPlayers()[0].getUnits().get(0).getX() 
                && y == gw.getBoard().getModel().getPlayers()[0].getUnits().get(0).getY());
    }
    /**
     * Checks two fighter unit damage
     */
    @Test
    public void testFighterUnit(){
        Initialize("plain2F.txt");
        
        for (int q = 0; q < 2; q++) {
            int defender = Math.abs(q * 4 - 4);
            ArrayList<Unit> updateUnits = m.getPlayers()[q].getUnits();
            for (Unit u : updateUnits) {
                if (q == 0) {
                    gw.getPlayer1distances().add(u.getDistance());
                } else {
                    gw.getPlayer2distances().add(u.getDistance());
                }
                int minWayDiff = 10000;
                ArrayList<Node> bestway = new ArrayList<>();

                for (int i = 0; i < 4; i++) {
                    ArrayList<String> wayString = m.getPlayers()[q].findWay(u.getX() / (m.getSize() / 30),
                            u.getY() / (m.getSize() / 30), m.getCastleCoordinates()[i + defender][0],
                            m.getCastleCoordinates()[i + defender][1], m.getPlayers()[q].getDifficulty(m, u.getType(), q));

                    if (minWayDiff > m.wayDiff(q, wayString, u.getType())) {
                        bestway = m.getPlayers()[q].convertWay(wayString);
                        minWayDiff = m.wayDiff(q, wayString, u.getType());

                    }
                }
                if (!bestway.isEmpty()) {
                    u.setWay(bestway);
                }

                m.getPlayers()[q].setUnits(updateUnits);
            }
        }
        assertTrue(m.getPlayers()[0].getUnits().get(0).getHp() == m.getPlayers()[0].getUnits().get(0).getMaxHp());
        assertTrue(m.getPlayers()[1].getUnits().get(0).getHp() == m.getPlayers()[1].getUnits().get(0).getMaxHp());
        gw.simulation();
        m = gw.getBoard().getModel();
        assertTrue(m.getPlayers()[0].getUnits().get(0).getHp() == m.getPlayers()[0].getUnits().get(0).getMaxHp() - m.getPlayers()[0].getUnits().get(0).getPower());
        assertTrue(m.getPlayers()[1].getUnits().get(0).getHp() == m.getPlayers()[1].getUnits().get(0).getMaxHp() - m.getPlayers()[1].getUnits().get(0).getPower());
    }
    
    @Test
    public void testDestroyerUnit(){
        Initialize("plain1T1D.txt");
        for (int q = 0; q < 2; q++) {
            int defender = Math.abs(q * 4 - 4);
            ArrayList<Unit> updateUnits = m.getPlayers()[q].getUnits();
            for (Unit u : updateUnits) {
                if (q == 0) {
                    gw.getPlayer1distances().add(u.getDistance());
                } else {
                    gw.getPlayer2distances().add(u.getDistance());
                }
                int minWayDiff = 10000;
                ArrayList<Node> bestway = new ArrayList<>();

                for (int i = 0; i < 4; i++) {
                    ArrayList<String> wayString = m.getPlayers()[q].findWay(u.getX() / (m.getSize() / 30),
                            u.getY() / (m.getSize() / 30), m.getCastleCoordinates()[i + defender][0],
                            m.getCastleCoordinates()[i + defender][1], m.getPlayers()[q].getDifficulty(m, u.getType(), q));

                    if (minWayDiff > m.wayDiff(q, wayString, u.getType())) {
                        bestway = m.getPlayers()[q].convertWay(wayString);
                        minWayDiff = m.wayDiff(q, wayString, u.getType());

                    }
                }
                if (!bestway.isEmpty()) {
                    u.setWay(bestway);
                }

                m.getPlayers()[q].setUnits(updateUnits);
            }
        }
        gw.setRNDPROTECTION(1);
        gw.setModel(m);
        
        assertTrue(m.getPlayers()[1].getTowers().get(0).getHp() == m.getPlayers()[1].getTowers().get(0).getMaxHp());
        gw.simulation();
        gw.simulation();
        gw.simulation();
        m = gw.getBoard().getModel();
        
        assertTrue(m.getPlayers()[1].getTowers().get(0).getHp() == m.getPlayers()[1].getTowers().get(0).getMaxHp() - 50);
        assertTrue(m.getPlayers()[0].getUnits().isEmpty());
    }
  
    /**
     * Destroyer cannot destroy demolished tower
     */
    @Test
    public void testDestroyerDemolishedTower(){
        Initialize("plain1TD1D.txt");
        for (int q = 0; q < 2; q++) {
            int defender = Math.abs(q * 4 - 4);
            ArrayList<Unit> updateUnits = m.getPlayers()[q].getUnits();
            for (Unit u : updateUnits) {
                if (q == 0) {
                    gw.getPlayer1distances().add(u.getDistance());
                } else {
                    gw.getPlayer2distances().add(u.getDistance());
                }
                int minWayDiff = 10000;
                ArrayList<Node> bestway = new ArrayList<>();

                for (int i = 0; i < 4; i++) {
                    ArrayList<String> wayString = m.getPlayers()[q].findWay(u.getX() / (m.getSize() / 30),
                            u.getY() / (m.getSize() / 30), m.getCastleCoordinates()[i + defender][0],
                            m.getCastleCoordinates()[i + defender][1], m.getPlayers()[q].getDifficulty(m, u.getType(), q));

                    if (minWayDiff > m.wayDiff(q, wayString, u.getType())) {
                        bestway = m.getPlayers()[q].convertWay(wayString);
                        minWayDiff = m.wayDiff(q, wayString, u.getType());

                    }
                }
                if (!bestway.isEmpty()) {
                    u.setWay(bestway);
                }

                m.getPlayers()[q].setUnits(updateUnits);
            }
        }
        gw.setRNDPROTECTION(1);
        gw.setModel(m);
        gw.simulation();
        gw.simulation();
        gw.simulation();
        m = gw.getBoard().getModel();
        assertTrue(!m.getPlayers()[0].getUnits().isEmpty());
    }
    /**
     * Checks in an extreme map, if climber and diver can move
     */
    @Test
    public void testClimberDiver(){
        Initialize("plainCD.txt");
        for (int q = 0; q < 2; q++) {
            int defender = Math.abs(q * 4 - 4);
            ArrayList<Unit> updateUnits = m.getPlayers()[q].getUnits();
            for (Unit u : updateUnits) {
                if (q == 0) {
                    gw.getPlayer1distances().add(u.getDistance());
                } else {
                    gw.getPlayer2distances().add(u.getDistance());
                }
                int minWayDiff = 10000;
                ArrayList<Node> bestway = new ArrayList<>();

                for (int i = 0; i < 4; i++) {
                    ArrayList<String> wayString = m.getPlayers()[q].findWay(u.getX() / (m.getSize() / 30),
                            u.getY() / (m.getSize() / 30), m.getCastleCoordinates()[i + defender][0],
                            m.getCastleCoordinates()[i + defender][1], m.getPlayers()[q].getDifficulty(m, u.getType(), q));

                    if (minWayDiff > m.wayDiff(q, wayString, u.getType())) {
                        bestway = m.getPlayers()[q].convertWay(wayString);
                        minWayDiff = m.wayDiff(q, wayString, u.getType());

                    }
                }
                if (!bestway.isEmpty()) {
                    u.setWay(bestway);
                }

                m.getPlayers()[q].setUnits(updateUnits);
            }
        }
        gw.setModel(m);
        int dX = m.getPlayers()[0].getUnits().get(0).getX();
        int dY = m.getPlayers()[0].getUnits().get(0).getY();
        int cX = m.getPlayers()[1].getUnits().get(0).getX();
        int cY = m.getPlayers()[1].getUnits().get(0).getY();
        
        gw.simulation();
        
        m = gw.getBoard().getModel();
        
        assertTrue(cY != m.getPlayers()[0].getUnits().get(0).getY() &&
                dX != m.getPlayers()[1].getUnits().get(0).getX());
    }
    /**
     * Checks castles health and the winner
     */
    @Test
    public void testWinner(){
        Initialize("plain1U.txt");
        for (int q = 0; q < 2; q++) {
            int defender = Math.abs(q * 4 - 4);
            ArrayList<Unit> updateUnits = m.getPlayers()[q].getUnits();
            for (Unit u : updateUnits) {
                if (q == 0) {
                    gw.getPlayer1distances().add(u.getDistance());
                } else {
                    gw.getPlayer2distances().add(u.getDistance());
                }
                int minWayDiff = 10000;
                ArrayList<Node> bestway = new ArrayList<>();

                for (int i = 0; i < 4; i++) {
                    ArrayList<String> wayString = m.getPlayers()[q].findWay(u.getX() / (m.getSize() / 30),
                            u.getY() / (m.getSize() / 30), m.getCastleCoordinates()[i + defender][0],
                            m.getCastleCoordinates()[i + defender][1], m.getPlayers()[q].getDifficulty(m, u.getType(), q));

                    if (minWayDiff > m.wayDiff(q, wayString, u.getType())) {
                        bestway = m.getPlayers()[q].convertWay(wayString);
                        minWayDiff = m.wayDiff(q, wayString, u.getType());

                    }
                }
                if (!bestway.isEmpty()) {
                    u.setWay(bestway);
                }

                m.getPlayers()[q].setUnits(updateUnits);
            }
        }
        gw.setModel(m);
        
        gw.getBoard().getModel().getPlayers()[1].getCastle().setHp(1);
        gw.setTestMode(true);
        gw.simulation();
        gw.simulation();
        gw.simulation();
        gw.simulation();
        assertTrue(gw.getBoard().getModel().getPlayers()[1].getCastle().getHp() == 0);
    }
    
    @Test
    public void testDraw(){
        Initialize("plain2U.txt");
        for (int q = 0; q < 2; q++) {
            int defender = Math.abs(q * 4 - 4);
            ArrayList<Unit> updateUnits = m.getPlayers()[q].getUnits();
            for (Unit u : updateUnits) {
                if (q == 0) {
                    gw.getPlayer1distances().add(u.getDistance());
                } else {
                    gw.getPlayer2distances().add(u.getDistance());
                }
                int minWayDiff = 10000;
                ArrayList<Node> bestway = new ArrayList<>();

                for (int i = 0; i < 4; i++) {
                    ArrayList<String> wayString = m.getPlayers()[q].findWay(u.getX() / (m.getSize() / 30),
                            u.getY() / (m.getSize() / 30), m.getCastleCoordinates()[i + defender][0],
                            m.getCastleCoordinates()[i + defender][1], m.getPlayers()[q].getDifficulty(m, u.getType(), q));

                    if (minWayDiff > m.wayDiff(q, wayString, u.getType())) {
                        bestway = m.getPlayers()[q].convertWay(wayString);
                        minWayDiff = m.wayDiff(q, wayString, u.getType());

                    }
                }
                if (!bestway.isEmpty()) {
                    u.setWay(bestway);
                }

                m.getPlayers()[q].setUnits(updateUnits);
            }
        }
        gw.setModel(m);
        
        gw.getBoard().getModel().getPlayers()[1].getCastle().setHp(1);
        gw.getBoard().getModel().getPlayers()[0].getCastle().setHp(1);
        gw.setTestMode(true);
        gw.simulation();
        gw.simulation();
        gw.simulation();
        gw.simulation();
        assertTrue(gw.getBoard().getModel().getPlayers()[1].getCastle().getHp() == 0);
        assertTrue(gw.getBoard().getModel().getPlayers()[0].getCastle().getHp() == 0);
    }
    
    /**
     * Checks if tower is shooting correctly
     * Checks if destroyed tower shoots or not
     */
    @Test
    public void testTowerShooting(){
        Initialize("plain1U2T.txt");
        for (int q = 0; q < 2; q++) {
            int defender = Math.abs(q * 4 - 4);
            ArrayList<Unit> updateUnits = m.getPlayers()[q].getUnits();
            for (Unit u : updateUnits) {
                if (q == 0) {
                    gw.getPlayer1distances().add(u.getDistance());
                } else {
                    gw.getPlayer2distances().add(u.getDistance());
                }
                int minWayDiff = 10000;
                ArrayList<Node> bestway = new ArrayList<>();

                for (int i = 0; i < 4; i++) {
                    ArrayList<String> wayString = m.getPlayers()[q].findWay(u.getX() / (m.getSize() / 30),
                            u.getY() / (m.getSize() / 30), m.getCastleCoordinates()[i + defender][0],
                            m.getCastleCoordinates()[i + defender][1], m.getPlayers()[q].getDifficulty(m, u.getType(), q));

                    if (minWayDiff > m.wayDiff(q, wayString, u.getType())) {
                        bestway = m.getPlayers()[q].convertWay(wayString);
                        minWayDiff = m.wayDiff(q, wayString, u.getType());

                    }
                }
                if (!bestway.isEmpty()) {
                    u.setWay(bestway);
                }

                m.getPlayers()[q].setUnits(updateUnits);
            }
        }
        gw.setModel(m);
        int simulationticks = 0;
        assertTrue(m.getPlayers()[0].getUnits().get(0).getHp() == m.getPlayers()[0].getUnits().get(0).getMaxHp());
        
        gw.simulation();
        for (int i = 0; i < 2; i++) {
        ArrayList<Tower> towers = gw.getBoard().getModel().getPlayers()[i].getNotDemolishedTowers();
        for (int j = 0; j < towers.size(); j++) {
            double tickPerAttack = towers.get(j).getAttack_speed() / 0.25;
            if (simulationticks % tickPerAttack == 0) {
                ArrayList<Unit> enemyUnitsNearby = gw.getBoard().getModel().enemyUnitsNearby(i, towers.get(j));
                if (enemyUnitsNearby.size() > 0) {
                    int rand = (int) (Math.random() * enemyUnitsNearby.size());
                    towers.get(j).setShootCords(enemyUnitsNearby.get(rand).getX(), enemyUnitsNearby.get(rand).getY());
                    if (enemyUnitsNearby.get(rand).getHp() > towers.get(j).getPower()) {
                        enemyUnitsNearby.get(rand).setHp(enemyUnitsNearby.get(rand).getHp() - towers.get(j).getPower());
                    } else {
                            gw.getBoard().getModel().getPlayers()[(i + 1) % 2].deleteUnit(enemyUnitsNearby.get(rand));
                            gw.getBoard().getModel().getPlayers()[i].setMoney(gw.getBoard().getModel().getPlayers()[i].getMoney() + enemyUnitsNearby.get(rand).getMaxHp() * 2);
                        }
                    }
                }
            }
        }
        simulationticks++;
        gw.simulation();
        for (int i = 0; i < 2; i++) {
        ArrayList<Tower> towers = gw.getBoard().getModel().getPlayers()[i].getNotDemolishedTowers();
        for (int j = 0; j < towers.size(); j++) {
            double tickPerAttack = towers.get(j).getAttack_speed() / 0.25;
            if (simulationticks % tickPerAttack == 0) {
                ArrayList<Unit> enemyUnitsNearby = gw.getBoard().getModel().enemyUnitsNearby(i, towers.get(j));
                if (enemyUnitsNearby.size() > 0) {
                    int rand = (int) (Math.random() * enemyUnitsNearby.size());
                    towers.get(j).setShootCords(enemyUnitsNearby.get(rand).getX(), enemyUnitsNearby.get(rand).getY());
                    if (enemyUnitsNearby.get(rand).getHp() > towers.get(j).getPower()) {
                        enemyUnitsNearby.get(rand).setHp(enemyUnitsNearby.get(rand).getHp() - towers.get(j).getPower());
                    } else {
                            gw.getBoard().getModel().getPlayers()[(i + 1) % 2].deleteUnit(enemyUnitsNearby.get(rand));
                            gw.getBoard().getModel().getPlayers()[i].setMoney(gw.getBoard().getModel().getPlayers()[i].getMoney() + enemyUnitsNearby.get(rand).getMaxHp() * 2);
                        }
                    }
                }
            }
        }
        simulationticks++;
        gw.simulation();
        for (int i = 0; i < 2; i++) {
        ArrayList<Tower> towers = gw.getBoard().getModel().getPlayers()[i].getNotDemolishedTowers();
        for (int j = 0; j < towers.size(); j++) {
            double tickPerAttack = towers.get(j).getAttack_speed() / 0.25;
            if (simulationticks % tickPerAttack == 0) {
                ArrayList<Unit> enemyUnitsNearby = gw.getBoard().getModel().enemyUnitsNearby(i, towers.get(j));
                if (enemyUnitsNearby.size() > 0) {
                    int rand = (int) (Math.random() * enemyUnitsNearby.size());
                    towers.get(j).setShootCords(enemyUnitsNearby.get(rand).getX(), enemyUnitsNearby.get(rand).getY());
                    if (enemyUnitsNearby.get(rand).getHp() > towers.get(j).getPower()) {
                        enemyUnitsNearby.get(rand).setHp(enemyUnitsNearby.get(rand).getHp() - towers.get(j).getPower());
                    } else {
                            gw.getBoard().getModel().getPlayers()[(i + 1) % 2].deleteUnit(enemyUnitsNearby.get(rand));
                            gw.getBoard().getModel().getPlayers()[i].setMoney(gw.getBoard().getModel().getPlayers()[i].getMoney() + enemyUnitsNearby.get(rand).getMaxHp() * 2);
                        }
                    }
                }
            }
        }
        simulationticks++;
        gw.simulation();
        for (int i = 0; i < 2; i++) {
        ArrayList<Tower> towers = gw.getBoard().getModel().getPlayers()[i].getNotDemolishedTowers();
        for (int j = 0; j < towers.size(); j++) {
            double tickPerAttack = towers.get(j).getAttack_speed() / 0.25;
            if (simulationticks % tickPerAttack == 0) {
                ArrayList<Unit> enemyUnitsNearby = gw.getBoard().getModel().enemyUnitsNearby(i, towers.get(j));
                if (enemyUnitsNearby.size() > 0) {
                    int rand = (int) (Math.random() * enemyUnitsNearby.size());
                    towers.get(j).setShootCords(enemyUnitsNearby.get(rand).getX(), enemyUnitsNearby.get(rand).getY());
                    if (enemyUnitsNearby.get(rand).getHp() > towers.get(j).getPower()) {
                        enemyUnitsNearby.get(rand).setHp(enemyUnitsNearby.get(rand).getHp() - towers.get(j).getPower());
                    } else {
                            gw.getBoard().getModel().getPlayers()[(i + 1) % 2].deleteUnit(enemyUnitsNearby.get(rand));
                            gw.getBoard().getModel().getPlayers()[i].setMoney(gw.getBoard().getModel().getPlayers()[i].getMoney() + enemyUnitsNearby.get(rand).getMaxHp() * 2);
                        }
                    }
                }
            }
        }
        simulationticks++;
        gw.simulation();
        for (int i = 0; i < 2; i++) {
        ArrayList<Tower> towers = gw.getBoard().getModel().getPlayers()[i].getNotDemolishedTowers();
        for (int j = 0; j < towers.size(); j++) {
            double tickPerAttack = towers.get(j).getAttack_speed() / 0.25;
            if (simulationticks % tickPerAttack == 0) {
                ArrayList<Unit> enemyUnitsNearby = gw.getBoard().getModel().enemyUnitsNearby(i, towers.get(j));
                if (enemyUnitsNearby.size() > 0) {
                    int rand = (int) (Math.random() * enemyUnitsNearby.size());
                    towers.get(j).setShootCords(enemyUnitsNearby.get(rand).getX(), enemyUnitsNearby.get(rand).getY());
                    if (enemyUnitsNearby.get(rand).getHp() > towers.get(j).getPower()) {
                        enemyUnitsNearby.get(rand).setHp(enemyUnitsNearby.get(rand).getHp() - towers.get(j).getPower());
                    } else {
                            gw.getBoard().getModel().getPlayers()[(i + 1) % 2].deleteUnit(enemyUnitsNearby.get(rand));
                            gw.getBoard().getModel().getPlayers()[i].setMoney(gw.getBoard().getModel().getPlayers()[i].getMoney() + enemyUnitsNearby.get(rand).getMaxHp() * 2);
                        }
                    }
                }
            }
        }
        simulationticks++;
        m = gw.getBoard().getModel();
        assertTrue(m.getPlayers()[0].getUnits().get(0).getHp() == m.getPlayers()[0].getUnits().get(0).getMaxHp() - m.getPlayers()[1].getTowers().get(0).getPower());
        
        gw.getPlayer1distances().clear();
        gw.getPlayer2distances().clear();
        
        for(int k = 0; k < 2; k++){
            simulationticks = 0;
            for (int q = 0; q < 2; q++) {
            ArrayList<Unit> updateUnits = m.getPlayers()[q].getUnits();
            for (Unit u : updateUnits) {
                    if (q == 0) {
                        gw.getPlayer1distances().add(u.getDistance());
                    } else {
                        gw.getPlayer2distances().add(u.getDistance());
                    }
                }
            }
            gw.simulation();
            for (int i = 0; i < 2; i++) {
                ArrayList<Tower> towers = gw.getBoard().getModel().getPlayers()[i].getNotDemolishedTowers();
                for (int j = 0; j < towers.size(); j++) {
                    double tickPerAttack = towers.get(j).getAttack_speed() / 0.25;
                    if (simulationticks % tickPerAttack == 0) {
                        ArrayList<Unit> enemyUnitsNearby = gw.getBoard().getModel().enemyUnitsNearby(i, towers.get(j));
                        if (enemyUnitsNearby.size() > 0) {
                            int rand = (int) (Math.random() * enemyUnitsNearby.size());
                            towers.get(j).setShootCords(enemyUnitsNearby.get(rand).getX(), enemyUnitsNearby.get(rand).getY());
                            if (enemyUnitsNearby.get(rand).getHp() > towers.get(j).getPower()) {
                                enemyUnitsNearby.get(rand).setHp(enemyUnitsNearby.get(rand).getHp() - towers.get(j).getPower());
                            } else {
                                gw.getBoard().getModel().getPlayers()[(i + 1) % 2].deleteUnit(enemyUnitsNearby.get(rand));
                                gw.getBoard().getModel().getPlayers()[i].setMoney(gw.getBoard().getModel().getPlayers()[i].getMoney() + enemyUnitsNearby.get(rand).getMaxHp() * 2);
                            }
                        }
                    }
                }
            }
            simulationticks++;
            gw.simulation();
            for (int i = 0; i < 2; i++) {
                ArrayList<Tower> towers = gw.getBoard().getModel().getPlayers()[i].getNotDemolishedTowers();
                for (int j = 0; j < towers.size(); j++) {
                    double tickPerAttack = towers.get(j).getAttack_speed() / 0.25;
                    if (simulationticks % tickPerAttack == 0) {
                        ArrayList<Unit> enemyUnitsNearby = gw.getBoard().getModel().enemyUnitsNearby(i, towers.get(j));
                        if (enemyUnitsNearby.size() > 0) {
                            int rand = (int) (Math.random() * enemyUnitsNearby.size());
                            towers.get(j).setShootCords(enemyUnitsNearby.get(rand).getX(), enemyUnitsNearby.get(rand).getY());
                            if (enemyUnitsNearby.get(rand).getHp() > towers.get(j).getPower()) {
                                enemyUnitsNearby.get(rand).setHp(enemyUnitsNearby.get(rand).getHp() - towers.get(j).getPower());
                            } else {
                                gw.getBoard().getModel().getPlayers()[(i + 1) % 2].deleteUnit(enemyUnitsNearby.get(rand));
                                gw.getBoard().getModel().getPlayers()[i].setMoney(gw.getBoard().getModel().getPlayers()[i].getMoney() + enemyUnitsNearby.get(rand).getMaxHp() * 2);
                            }
                        }
                    }
                }
            }
            simulationticks++;
            gw.simulation();
            for (int i = 0; i < 2; i++) {
                ArrayList<Tower> towers = gw.getBoard().getModel().getPlayers()[i].getNotDemolishedTowers();
                for (int j = 0; j < towers.size(); j++) {
                    double tickPerAttack = towers.get(j).getAttack_speed() / 0.25;
                    if (simulationticks % tickPerAttack == 0) {
                        ArrayList<Unit> enemyUnitsNearby = gw.getBoard().getModel().enemyUnitsNearby(i, towers.get(j));
                        if (enemyUnitsNearby.size() > 0) {
                            int rand = (int) (Math.random() * enemyUnitsNearby.size());
                            towers.get(j).setShootCords(enemyUnitsNearby.get(rand).getX(), enemyUnitsNearby.get(rand).getY());
                            if (enemyUnitsNearby.get(rand).getHp() > towers.get(j).getPower()) {
                                enemyUnitsNearby.get(rand).setHp(enemyUnitsNearby.get(rand).getHp() - towers.get(j).getPower());
                            } else {
                                gw.getBoard().getModel().getPlayers()[(i + 1) % 2].deleteUnit(enemyUnitsNearby.get(rand));
                                gw.getBoard().getModel().getPlayers()[i].setMoney(gw.getBoard().getModel().getPlayers()[i].getMoney() + enemyUnitsNearby.get(rand).getMaxHp() * 2);
                            }
                        }
                    }
                }
            }
            simulationticks++;
            gw.simulation();
            for (int i = 0; i < 2; i++) {
                ArrayList<Tower> towers = gw.getBoard().getModel().getPlayers()[i].getNotDemolishedTowers();
                for (int j = 0; j < towers.size(); j++) {
                    double tickPerAttack = towers.get(j).getAttack_speed() / 0.25;
                    if (simulationticks % tickPerAttack == 0) {
                        ArrayList<Unit> enemyUnitsNearby = gw.getBoard().getModel().enemyUnitsNearby(i, towers.get(j));
                        if (enemyUnitsNearby.size() > 0) {
                            int rand = (int) (Math.random() * enemyUnitsNearby.size());
                            towers.get(j).setShootCords(enemyUnitsNearby.get(rand).getX(), enemyUnitsNearby.get(rand).getY());
                            if (enemyUnitsNearby.get(rand).getHp() > towers.get(j).getPower()) {
                                enemyUnitsNearby.get(rand).setHp(enemyUnitsNearby.get(rand).getHp() - towers.get(j).getPower());
                            } else {
                                gw.getBoard().getModel().getPlayers()[(i + 1) % 2].deleteUnit(enemyUnitsNearby.get(rand));
                                gw.getBoard().getModel().getPlayers()[i].setMoney(gw.getBoard().getModel().getPlayers()[i].getMoney() + enemyUnitsNearby.get(rand).getMaxHp() * 2);
                            }
                        }
                    }
                }
            }
            simulationticks++;
            gw.simulation();
            for (int i = 0; i < 2; i++) {
                ArrayList<Tower> towers = gw.getBoard().getModel().getPlayers()[i].getNotDemolishedTowers();
                for (int j = 0; j < towers.size(); j++) {
                    double tickPerAttack = towers.get(j).getAttack_speed() / 0.25;
                    if (simulationticks % tickPerAttack == 0) {
                        ArrayList<Unit> enemyUnitsNearby = gw.getBoard().getModel().enemyUnitsNearby(i, towers.get(j));
                        if (enemyUnitsNearby.size() > 0) {
                            int rand = (int) (Math.random() * enemyUnitsNearby.size());
                            towers.get(j).setShootCords(enemyUnitsNearby.get(rand).getX(), enemyUnitsNearby.get(rand).getY());
                            if (enemyUnitsNearby.get(rand).getHp() > towers.get(j).getPower()) {
                                enemyUnitsNearby.get(rand).setHp(enemyUnitsNearby.get(rand).getHp() - towers.get(j).getPower());
                            } else {
                                gw.getBoard().getModel().getPlayers()[(i + 1) % 2].deleteUnit(enemyUnitsNearby.get(rand));
                                gw.getBoard().getModel().getPlayers()[i].setMoney(gw.getBoard().getModel().getPlayers()[i].getMoney() + enemyUnitsNearby.get(rand).getMaxHp() * 2);
                            }
                        }
                    }
                }
            }
            simulationticks++;
            gw.simulation();
            for (int i = 0; i < 2; i++) {
                ArrayList<Tower> towers = gw.getBoard().getModel().getPlayers()[i].getNotDemolishedTowers();
                for (int j = 0; j < towers.size(); j++) {
                    double tickPerAttack = towers.get(j).getAttack_speed() / 0.25;
                    if (simulationticks % tickPerAttack == 0) {
                        ArrayList<Unit> enemyUnitsNearby = gw.getBoard().getModel().enemyUnitsNearby(i, towers.get(j));
                        if (enemyUnitsNearby.size() > 0) {
                            int rand = (int) (Math.random() * enemyUnitsNearby.size());
                            towers.get(j).setShootCords(enemyUnitsNearby.get(rand).getX(), enemyUnitsNearby.get(rand).getY());
                            if (enemyUnitsNearby.get(rand).getHp() > towers.get(j).getPower()) {
                                enemyUnitsNearby.get(rand).setHp(enemyUnitsNearby.get(rand).getHp() - towers.get(j).getPower());
                            } else {
                                gw.getBoard().getModel().getPlayers()[(i + 1) % 2].deleteUnit(enemyUnitsNearby.get(rand));
                                gw.getBoard().getModel().getPlayers()[i].setMoney(gw.getBoard().getModel().getPlayers()[i].getMoney() + enemyUnitsNearby.get(rand).getMaxHp() * 2);
                            }
                        }
                    }
                }
            }
            simulationticks++;
            gw.getPlayer1distances().clear();
            gw.getPlayer2distances().clear();
        }
        m = gw.getBoard().getModel();
        assertTrue(m.getPlayers()[0].getUnits().get(0).getHp() == m.getPlayers()[0].getUnits().get(0).getMaxHp() - m.getPlayers()[1].getTowers().get(0).getPower());
    }
}
