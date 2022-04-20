package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import model.*;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import model.Model;
import model.Player;

public class GameWindow extends JPanel implements ActionListener {

    private Timer timer;
    private Timer animationTimer;
    private int current;
    private Board board;
    private JButton exitButton;
    private JButton newRoundButton;
    private JButton saveButton;
    private JButton[] p1TowerButtons;
    private JButton[] p1UnitButtons;
    private JButton[] p2TowerButtons;
    private JButton[] p2UnitButtons;
    private JLabel p1Data;
    private JLabel p2Data;
    private JLabel timeAndRoundLabel = new JLabel();
    private String selectedTower;
    private String buttonAction;
    private int ticks;
    private boolean simulationTime = false;
    private ArrayList<Integer> player1distances;
    private ArrayList<Integer> player2distances;
    private final int timerInterval = 250;
    private Model model;
    private int simulationticks;
    private int RNDPROTECTION;
    private boolean testMode = false;

    public GameWindow() {
        super();
    }

    public GameWindow(int width, int height, String p1Name, String p2Name, int selectedMap) {
        super();
        board = new Board(selectedMap, p1Name, p2Name, width, height);
        this.model = board.getModel();
        model.setRound(1);

        //players have 50% chance to start the game
        Random rand = new Random();

        model.setActivePlayer(rand.nextInt(2));
        constructor(width, height);
    }

    public void constructor(int width, int height) {
        buttonAction = "";
        RNDPROTECTION = 0;
        setSize(width, height);
        player1distances = new ArrayList<>();
        player2distances = new ArrayList<>();
        this.setPreferredSize(new Dimension(width, height));
        exitButton = new JButton("Exit game");
        exitButton.addActionListener((event) -> System.exit(0));
        newRoundButton = new JButton("Finish round");
        newRoundButton.addActionListener((event) -> { newRound(); });

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {  }

                JTextPane text = new JTextPane() {
                    @Override
                    public String getToolTipText() {
                        return ((JComponent) getParent()).getToolTipText();
                    }

                    @Override
                    public String getToolTipText(MouseEvent event) {
                        return ((JComponent) getParent()).getToolTipText(event);
                    }
                };

                try {
                    text.getStyledDocument().insertString(0, ".", null);
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }

                ToolTipManager.sharedInstance().registerComponent(text);
            }
        });

        saveButton = new JButton("Save game");
        saveButton.addActionListener((event) -> saveGame());

        this.setLayout(new GridBagLayout());

        p1TowerButtons = new JButton[5];
        p1UnitButtons = new JButton[5];
        p2TowerButtons = new JButton[5];
        p2UnitButtons = new JButton[5];

        setPanels();

        //PLAYER BUTTONS
        /**
         * PLAYER1 Tower típusok
         */
        p1TowerButtons[0].addActionListener(ae -> { towerPlaceAction("Fortified"); });
        p1TowerButtons[1].addActionListener(ae -> { towerPlaceAction("Rapid"); });
        p1TowerButtons[2].addActionListener(ae -> { towerPlaceAction("Sniper"); });

        /**
         * PLAYER1 Tower management
         */
        p1TowerButtons[3].addActionListener(ae -> {
            if (!buttonAction.equals("")) {
                model.setSelectables(new ArrayList<>());
                buttonAction = "";
            } else {
                buttonAction = "upgrade";
                model.setSelectableTowersToUpgrade(model.getPlayers()[model.getActivePlayer()].getMoney());

            }
        });
        p1TowerButtons[4].addActionListener(ae -> {
            if (!buttonAction.equals("")) {
                model.setSelectables(new ArrayList<>());
                buttonAction = "";
            } else {
                buttonAction = "demolish";
                model.setSelectableTowersToDemolish();
            }
        });

        /**
         * PLAYER1 Unit típusok
         */
        p1UnitButtons[0].addActionListener(ae -> {
            board.setModel(model.getPlayers()[0].sendUnits("General", "blue", 1, model));
            playerDataUpdate();
        });
        p1UnitButtons[1].addActionListener(ae -> {
            board.setModel(model.getPlayers()[0].sendUnits("Fighter", "blue", 1, model));
            playerDataUpdate();
        });
        p1UnitButtons[2].addActionListener(ae -> {
            board.setModel(model.getPlayers()[0].sendUnits("Climber", "blue", 1, model));
            playerDataUpdate();
        });
        p1UnitButtons[3].addActionListener(ae -> {
            board.setModel(model.getPlayers()[0].sendUnits("Diver", "blue", 1, model));
            playerDataUpdate();
        });
        p1UnitButtons[4].addActionListener(ae -> {
            board.setModel(model.getPlayers()[0].sendUnits("Destroyer", "blue", 1, model));
            playerDataUpdate();
        });
        ////////////////////////////////////////////////////////////////////////

        /**
         * PLAYER2 Tower típusok
         */
        p2TowerButtons[0].addActionListener(ae -> { towerPlaceAction("Fortified"); });
        p2TowerButtons[1].addActionListener(ae -> { towerPlaceAction("Rapid"); });
        p2TowerButtons[2].addActionListener(ae -> { towerPlaceAction("Sniper"); });

        /**
         * PLAYER2 Tower management
         */
        p2TowerButtons[3].addActionListener(ae -> {
            if (!buttonAction.equals("")) {
                model.setSelectables(new ArrayList<>());
                buttonAction = "";
            } else {
                buttonAction = "upgrade";
                model.setSelectableTowersToUpgrade(model.getPlayers()[model.getActivePlayer()].getMoney());
            }
        });
        p2TowerButtons[4].addActionListener(ae -> {
            if (!buttonAction.equals("")) {
                model.setSelectables(new ArrayList<>());
                buttonAction = "";
            } else {
                buttonAction = "demolish";
                model.setSelectableTowersToDemolish();
            }
        });

        /**
         * PLAYER2 Unit típusok
         */
        p2UnitButtons[0].addActionListener(ae -> {
            board.setModel(model.getPlayers()[1].sendUnits("General", "red", 1, model));
            playerDataUpdate();
        });
        p2UnitButtons[1].addActionListener(ae -> {
            board.setModel(model.getPlayers()[1].sendUnits("Fighter", "red", 1, model));
            playerDataUpdate();
        });
        p2UnitButtons[2].addActionListener(ae -> {
            board.setModel(model.getPlayers()[1].sendUnits("Climber", "red", 1, model));
            playerDataUpdate();
        });
        p2UnitButtons[3].addActionListener(ae -> {
            board.setModel(model.getPlayers()[1].sendUnits("Diver", "red", 1, model));
            playerDataUpdate();
        });
        p2UnitButtons[4].addActionListener(ae -> {
            board.setModel(model.getPlayers()[1].sendUnits("Destroyer", "red", 1, model));
            playerDataUpdate();
        });

        // after selecting tower type, players must click where they want to place it
        board.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX() / (model.getSize() / 30);
                int y = e.getY() / (model.getSize() / 30);

                if (buttonAction.equals("placeTower")) {
                    model = (model.getPlayers()[model.getActivePlayer()].build(x, y, selectedTower, model));
                    buttonAction = "";
                } else if (buttonAction.equals("upgrade")) {
                    if (model.getTowerFromPosition(x, y) != null && model.getPosition()[x][y] == 'T') {
                        model.getPlayers()[model.getActivePlayer()].upgrade(x, y, model.getSize(),'T');
                        model.setSelectables(new ArrayList<>());
                    }
                    buttonAction = "";
                } else if (buttonAction.equals("demolish")) {
                    if (model.getTowerFromPosition(x, y) != null) {
                        model.getPlayers()[model.getActivePlayer()].demolish(x, y, model.getSize(), model.getPosition()[x][y]);
                        model.setPosition(x, y, 'D');
                        model.setSelectables(new ArrayList<>());
                    }
                    buttonAction = "";
                }
                board.repaint();
                playerDataUpdate();
            }
        });

        //ticks has been set while loading game
        if (!(ticks > 0)) { ticks = (1000 / timerInterval) * 60; }

        timer = new Timer(timerInterval, (ActionEvent ae) -> {
            //showWays();
            // printDiffMatrices();
            PointerInfo a = MouseInfo.getPointerInfo();

            Point b = a.getLocation();
            int x = (int) b.getX() - 500;
            x /= (board.getModel().getSize() / 30);
            int y = (int) b.getY() - 75;
            y /= (board.getModel().getSize() / 30);

            /*
            //helper
            boolean showCoordinates=true;
            if (showCoordinates) {
                ToolTipManager.sharedInstance().setEnabled(true);
                board.setToolTipText(x+";"+y);
            } 
            else */
            if (!model.getInfo(x, y).equals("")) {
                ToolTipManager.sharedInstance().setEnabled(true);
                board.setToolTipText(model.getInfo(x, y));
            } else {
                ToolTipManager.sharedInstance().setEnabled(false);
            }

            if (simulationTime) {
                simulationTime = simulation();

                for (int i = 0; i < 2; i++) {
                    ArrayList<Tower> towers = model.getPlayers()[i].getNotDemolishedTowers();
                    for (int j = 0; j < towers.size(); j++) {
                        double tickPerAttack = towers.get(j).getAttack_speed() / 0.25;
                        if (simulationticks % tickPerAttack == 0) {
                            ArrayList<Unit> enemyUnitsNearby = model.enemyUnitsNearby(i, towers.get(j));
                            if (enemyUnitsNearby.size() > 0) {
                                int rand = (int) (Math.random() * enemyUnitsNearby.size());
                                towers.get(j).setShootCords(enemyUnitsNearby.get(rand).getX(), enemyUnitsNearby.get(rand).getY());
                                if (enemyUnitsNearby.get(rand).getHp() > towers.get(j).getPower()) {
                                    enemyUnitsNearby.get(rand).setHp(enemyUnitsNearby.get(rand).getHp() - towers.get(j).getPower());
                                } else {
                                    model.getPlayers()[(i + 1) % 2].deleteUnit(enemyUnitsNearby.get(rand));
                                    model.getPlayers()[i].setMoney(model.getPlayers()[i].getMoney() + enemyUnitsNearby.get(rand).getMaxHp() * 2);
                                }
                            }
                        }
                    }
                }
                simulationticks++;
                if (model.getRound() == 1 || model.getRound() == 2) {
                    ticks = (1000 / timerInterval) * 60;
                } else {
                    ticks = (1000 / timerInterval) * 30;
                }
            } else {
                for (int i = 0; i < 5; i++) {
                    if (model.getActivePlayer() == 0) {
                        p1TowerButtons[i].setEnabled(true);
                        p1UnitButtons[i].setEnabled(true);
                    } else {
                        p2TowerButtons[i].setEnabled(true);
                        p2UnitButtons[i].setEnabled(true);
                    }

                }
                newRoundButton.setEnabled(true);
                player1distances.clear();
                player2distances.clear();

                for (int q = 0; q < 2; q++) {
                    int defender = Math.abs(q * 4 - 4);
                    ArrayList<Unit> updateUnits = model.getPlayers()[q].getUnits();
                    for (Unit u : updateUnits) {
                        if (q == 0) {
                            player1distances.add(u.getDistance());
                        } else {
                            player2distances.add(u.getDistance());
                        }
                        int minWayDiff = 10000;
                        ArrayList<Node> bestway = new ArrayList<>();

                        for (int i = 0; i < 4; i++) {
                            ArrayList<String> wayString = model.getPlayers()[q].findWay(u.getX() / (model.getSize() / 30),
                                    u.getY() / (model.getSize() / 30), model.getCastleCoordinates()[i + defender][0],
                                    model.getCastleCoordinates()[i + defender][1], model.getPlayers()[q].getDifficulty(model, u.getType(), q));

                            if (minWayDiff > model.wayDiff(q, wayString, u.getType())) {
                                bestway = model.getPlayers()[q].convertWay(wayString);
                                minWayDiff = model.wayDiff(q, wayString, u.getType());

                            }
                        }
                        if (!bestway.isEmpty()) {
                            u.setWay(bestway);
                        }

                        model.getPlayers()[q].setUnits(updateUnits);
                    }

                }
            }

            ticks--;

            int newtime = (ticks + 1) / 2;
            if (newtime == 0) {
                newRound();
            } else {
                timeAndRoundLabel.setText("Time left: " + (ticks + 1) / (1000 / timerInterval)
                        + " sec // Round: " + (model.getRound() + 1) / 2);
            }
            board.repaint();

        }
        );

        timer.start();
        activePlayerPanelSetter();
    }

    public boolean simulation() {
        //first the units move

        boolean moreDistance = false;
        boolean isOver = false;
        for (int q = 0; q < 2; q++) {
            if (!model.getPlayers()[q].getUnits().isEmpty()) {
                ArrayList<Unit> units = model.getPlayers()[q].getUnits();
                for (int i = 0; i < units.size(); i++) {
                    if (q == 0) {
                        if (player1distances.get(i) <= 0) {
                            continue;
                        }
                    } else {
                        if (player2distances.get(i) <= 0) {
                            continue;
                        }
                    }

                    if (!units.get(i).getWay().isEmpty()) {
                        ArrayList<Node> way = units.get(i).getWay();
                        //System.out.println("simulation bestway: " + way);
                        Node next = way.get(0);
                        units.get(i).setX(next.getX() * (model.getSize() / 30));
                        units.get(i).setY(next.getY() * (model.getSize() / 30));
                        board.repaint();
                        way.remove(0);
                        units.get(i).setWay(way);
                    }

                    if (q == 0) {
                        player1distances.set(i, player1distances.get(i) - 1);
                        if (player1distances.get(i) > 0) {
                            moreDistance = true;
                        }
                    } else {
                        player2distances.set(i, player2distances.get(i) - 1);
                        if (player2distances.get(i) > 0) {
                            moreDistance = true;
                        }
                    }

                    //then fighter deal damage to enemy units if they are in the same position
                    ArrayList<Unit> enemyUnitsNearby = model.enemyUnitsNearby(q, units.get(i));
                    if ("Fighter".equals(units.get(i).getType()) && enemyUnitsNearby.size() > 0) {
                        int rand = (int) (Math.random() * enemyUnitsNearby.size());
                        units.get(i).setBlood(true);
                        if (enemyUnitsNearby.get(rand).getHp() > units.get(i).getPower()) {
                            enemyUnitsNearby.get(rand).setHp(enemyUnitsNearby.get(rand).getHp() - units.get(i).getPower());
                            if (units.get(i).getHp() > enemyUnitsNearby.get(rand).getPower() && "Fighter".equals(enemyUnitsNearby.get(rand).getType())) {
                                units.get(i).setHp(units.get(i).getHp() - enemyUnitsNearby.get(rand).getPower());
                            } else if ("Fighter".equals(enemyUnitsNearby.get(rand).getType())) {
                                model.getPlayers()[q].deleteUnit(units.get(i));
                                model.getPlayers()[Math.abs(q - 1)].setMoney(model.getPlayers()[Math.abs(q - 1)].getMoney() + units.get(i).getMaxHp() * 2);
                            }
                        } else {
                            model.getPlayers()[q].setMoney(model.getPlayers()[q].getMoney() + enemyUnitsNearby.get(rand).getMaxHp() * 2);
                            if (units.get(i).getHp() > enemyUnitsNearby.get(rand).getPower() && "Fighter".equals(enemyUnitsNearby.get(rand).getType())) {
                                units.get(i).setHp(units.get(i).getHp() - enemyUnitsNearby.get(rand).getPower());
                            } else if ("Fighter".equals(enemyUnitsNearby.get(rand).getType())) {
                                model.getPlayers()[q].deleteUnit(units.get(i));
                                model.getPlayers()[Math.abs(q - 1)].setMoney(model.getPlayers()[Math.abs(q - 1)].getMoney() + units.get(i).getMaxHp() * 2);
                            }
                            model.getPlayers()[(q + 1) % 2].deleteUnit(enemyUnitsNearby.get(rand));
                        }
                    }

                    ArrayList<Tower> towersNearby = model.towersNearby(q, units.get(i));
                    //System.out.println(towersNearby.toString());

                    //then destroyer attacks 
                    if ("Destroyer".equals(units.get(i).getType()) && towersNearby.size() > 0) {
                        int rand = (int) (Math.random() * 2); //the chance to attack towers is 50%
                        if(RNDPROTECTION != 0) rand = RNDPROTECTION;
                        //destroyer attacks nearby towers with 50% chance (direct next to it) with full power, then disappears
                        if (rand == 1) {
                            //destroyer deals 50 damage when attacking towers, removes towers if their hp reaches 0
                            for (int j = 0; j < towersNearby.size(); j++) {
                                
                                if(towersNearby.get(j).getDemolishedIn() != -1) continue;

                                if (towersNearby.get(j).getHp() > 50) {
                                    towersNearby.get(j).setHp(towersNearby.get(j).getHp() - 50);
                                    int index = model.getPlayers()[Math.abs(q - 1)].getTowerIndex(towersNearby.get(j));
                                    if (index != -1) {
                                        model.getPlayers()[Math.abs(q - 1)].getTowers().get(index).setExploded(true);
                                    }
                                    model.getPlayers()[q].deleteUnit(units.get(i));
                                    break;
                                } else if (towersNearby.get(j).getHp() > 0) {
                                    towersNearby.get(j).setHp(0);
                                    towersNearby.get(j).setPower(0);
                                    towersNearby.get(j).setRange(0);
                                    towersNearby.get(j).setAttack_speed(0);
                                    model.getPlayers()[(q + 1) % 2].demolish(towersNearby.get(j).getX() / (model.getSize() / 30),
                                            towersNearby.get(j).getY() / (model.getSize() / 30), model.getSize(), 'T');
                                    model.getPlayers()[q].setMoney(model.getPlayers()[q].getMoney() + towersNearby.get(j).getMaxHp() * 3);
                                    int index = model.getPlayers()[q].getTowerIndex(towersNearby.get(j));

                                    if (index != -1) { model.getPlayers()[q].getTowers().get(index).setExploded(true); }

                                    model.getPlayers()[q].deleteUnit(units.get(i));
                                    break;
                                }
                            }
                        }
                    }
                    board.repaint();
                    startAnimation();
                }
            }

            Player currentPlayer = model.getPlayers()[q];
            ArrayList<Unit> currentPlayerUnits = currentPlayer.getUnits();
            Player enemyPlayer = model.getPlayers()[Math.abs(q - 1)];
            
            for (int i = 0; i < currentPlayerUnits.size();) {
                Unit cpu = currentPlayerUnits.get(i);
                //ArrayList<Node> way = cpu.getWay();
                if (model.isItEnemyCastleCoordinate(q, cpu.getX() / (model.getSize() / 30), cpu.getY() / (model.getSize() / 30))) {
                    if (enemyPlayer.getCastle().getHp() - cpu.getPower() > 0) {
                        enemyPlayer.getCastle().setHp(enemyPlayer.getCastle().getHp() - cpu.getPower());
                    } else {
                        enemyPlayer.getCastle().setHp(0);
                        isOver = true;
                    }
                    model.getPlayers()[q].deleteUnit(cpu);
                    board.repaint();
                    //remove units who reached Castle after dealing damage to it
                } else {
                    i++;
                }
            }
        }

        if (isOver) {
            simulationTime = false;
            board.repaint();
            if(!testMode)
                gameOver();
        }

        return moreDistance;
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    public void startAnimation() {
        if (animationTimer == null) {
            current = 0;
            animationTimer = new Timer(250, e -> timerActionPerformed(e));
            animationTimer.start();
        } else if (!animationTimer.isRunning()) {
            animationTimer.restart();
        }
    }

    private void timerActionPerformed(ActionEvent e) {
        // TODO repeated code goes here
    }

    public void towerPlaceAction(String sc) {
        if (!buttonAction.equals("")) {
            buttonAction = "";
            model.setSelectables(new ArrayList<>());
        } else {
            selectedTower = sc;
            buttonAction = "placeTower";
            model.setSelectables();
        }
    }

    //helper
    private void showWays() {
        System.out.print("p1: ");
        for (Unit u : model.getPlayers()[0].getUnits()) {
            System.out.println(u.getWay());
        }
        System.out.print("p2: ");
        for (Unit u : model.getPlayers()[1].getUnits()) {
            System.out.println(u.getWay());
        }
    }

    private void printDiffMatrices() {
        ArrayList<Unit> units1 = model.getPlayers()[0].getUnits();
        ArrayList<Unit> units2 = model.getPlayers()[1].getUnits();
        for (Unit u : units1) {
            int diffM[][] = model.getPlayers()[0].getDifficulty(model, u.getType(), 0);
            for (int i = 0; i < 30; i++) {
                for (int j = 0; j < 30; j++) {
                    System.out.print(diffM[i][j] + " ");
                }
                System.out.println();
            }
            System.out.println();
        }

        for (Unit u : units2) {
            int diffM[][] = model.getPlayers()[1].getDifficulty(model, u.getType(), 1);
            for (int i = 0; i < 30; i++) {
                for (int j = 0; j < 30; j++) {
                    System.out.print(diffM[i][j] + " ");
                }
                System.out.println();
            }
            System.out.println();
        }
    }

    private void printWayDiff() {
        ArrayList<Unit> units1 = model.getPlayers()[0].getUnits();

        ArrayList<Unit> units2 = model.getPlayers()[1].getUnits();
        for (Unit u : units1) {
            ArrayList<Node> way = u.getWay();
            int diffM[][] = model.getPlayers()[0].getDifficulty(model, u.getType(), 0);
            for (Node n : way) {
                System.out.print(diffM[n.getX()][n.getY()] + ", ");
            }
            System.out.println();
        }

        for (Unit u : units2) {
            ArrayList<Node> way = u.getWay();
            int diffM[][] = model.getPlayers()[1].getDifficulty(model, u.getType(), 1);
            for (Node n : way) {
                System.out.print(diffM[n.getX()][n.getY()] + ", ");
            }
            System.out.println();
        }
    }

    /**
     * now it's the other player's round both players get 100 coins after every
     * 2 rounds, the simulation starts
     */
    public void newRound() {
        model.setSelectables(new ArrayList<>());
        model.setRound(model.getRound() + 1);

        if (model.getRound() == 1 || model.getRound() == 2) {
            ticks = (1000 / timerInterval) * 60;
        } else {
            ticks = (1000 / timerInterval) * 30;
        }

        model.setActivePlayer((1 + model.getActivePlayer()) % 2);
        playerDataUpdate();
        activePlayerPanelSetter();

        if (model.getRound() % 2 == 1) {
            for (int i = 0; i < 5; i++) {
                p1TowerButtons[i].setEnabled(false);
                p1UnitButtons[i].setEnabled(false);
                p2TowerButtons[i].setEnabled(false);
                p2UnitButtons[i].setEnabled(false);
            }
            newRoundButton.setEnabled(false);
            simulationticks = 0;
            simulationTime = true;

            model.getPlayers()[0].setMoney(model.getPlayers()[0].getMoney() + 100);
            model.getPlayers()[1].setMoney(model.getPlayers()[1].getMoney() + 100);
            playerDataUpdate();

        }

        ArrayList<Tower> p1Towers = model.getPlayers()[0].getTowers();
        for (int i = p1Towers.size() - 1; i >= 0; i--) {
            if (p1Towers.get(i).getDemolishedIn() != -1) {
                p1Towers.get(i).setDemolishedIn(1);//demolishedIn - 1
                if (p1Towers.get(i).getDemolishedIn() == 0) {
                    model.getTerrain().remove(p1Towers.get(i));
                    model.getPosition()[(p1Towers.get(i).getX() / 30)][(p1Towers.get(i).getY() / 30)] = 'F';
                    p1Towers.remove(i);
                }
            }
        }

        model.getPlayers()[0].setTowers(p1Towers);
        ArrayList<Tower> p2Towers = model.getPlayers()[1].getTowers();
        for (int i = p2Towers.size() - 1; i >= 0; i--) {
            if (p2Towers.get(i).getDemolishedIn() != -1) {
                p2Towers.get(i).setDemolishedIn(1);//demolishedIn - 1
                if (p2Towers.get(i).getDemolishedIn() == 0) {
                    model.getTerrain().remove(p2Towers.get(i));
                    model.getPosition()[(p2Towers.get(i).getX() / 30)][(p2Towers.get(i).getY() / 30)] = 'F';
                    p2Towers.remove(i);
                }
            }
        }
        model.getPlayers()[1].setTowers(p2Towers);
    }

    public void gameOver() {
        JFrame f = new JFrame();
        if (model.getPlayers()[0].getCastle().getHp() == 0 && model.getPlayers()[1].getCastle().getHp() == 0) {
            JOptionPane.showMessageDialog(f, "The game ended in a draw.");
        } else {
            if (model.getPlayers()[0].getCastle().getHp() == 0) {
                JOptionPane.showMessageDialog(f, model.getPlayers()[0].getName() + " won, congratulations!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(f, model.getPlayers()[1].getName() + " won, congratulations!", "Game Over", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        System.exit(0);
    }

    public void saveGame() {
        String filename;
        filename = JOptionPane.showInputDialog("Filename:");
        if (filename == null || filename.length() == 0) {
            return;
        }
        model.saveData(filename, ticks);
    }

    /**
     * Outlines
     */
    @Override
    protected void paintComponent(Graphics grph) {
        super.paintComponent(grph);
        Graphics2D grphcs2 = (Graphics2D) grph;
        
        Color sandBrown = new Color(210, 180, 140);
        Color lightGray = new Color(214, 196, 194);
        Color cinnabar = new Color(227,66,52);      //red: ON round
        Color pastelRed = new Color(255,105,97);    //red: NOT ON round
        Color royalBlue = new Color(65,105,225);    //blue: ON round
        Color blueGray = new Color(102,153,204);    //blue: NOT ON round
        Color darkHoneyBrown = new Color(184, 151, 128);
        
        //1st layer UI (bottom)
        grph.setColor(Color.DARK_GRAY);
        grphcs2.fillRoundRect(27, 113, 437, 891, 25, 25);   //left
        grphcs2.fillRoundRect(1456, 113, 437, 891, 25, 25); //right
        grphcs2.fillRoundRect(480, 10, 959, 1062, 22, 22);  //center
        grphcs2.fillRoundRect(27, 23, 437, 70, 25, 25);     //save
        grphcs2.fillRoundRect(1456, 23, 437, 70, 25, 25);   //exit
        
        //2nd layer UI (middle - coloring)
        if(model.getActivePlayer() == 0){
            grph.setColor(royalBlue);
            grphcs2.fillRoundRect(35, 121, 421, 875, 25, 25);   //left
            grph.setColor(pastelRed);
            grphcs2.fillRoundRect(1465, 121, 421, 875, 25, 25); //right
        } else {
            grph.setColor(blueGray);
            grphcs2.fillRoundRect(35, 121, 421, 875, 25, 25);   //left
            grph.setColor(cinnabar);
            grphcs2.fillRoundRect(1465, 121, 421, 875, 25, 25); //right
        }
        grph.setColor(sandBrown);
        grphcs2.fillRoundRect(34, 30, 423, 56, 22, 22);     //save
        grphcs2.fillRoundRect(1463, 30, 423, 56, 22, 22);   //exit
        grphcs2.fillRoundRect(489, 18, 942, 985 , 22, 22);  //center
        
        //3rd layer UI (top)
        grph.setColor(Color.DARK_GRAY);
        grphcs2.fillRoundRect(41, 126, 408, 865, 22, 22);   //left
        grphcs2.fillRoundRect(1471, 126, 408, 865, 22, 22); //right
        grphcs2.fillRect(114, 35, 266, 46);                 //save
        grphcs2.fillRect(1539, 35, 266, 46);                //exit
    }

    /**
     * UI-on megjelenő gombok elhelyezése
     */
    public final void setPanels() {
        Color algaeGreen = new Color(105, 168, 120);
        Color veryLightGray = new Color(220, 220, 220);
        Color transp = new Color(1f, 0f, 0f, .5f);

        JPanel p1Panel          = new JPanel(); //whole panel
        JPanel p1Stats          = new JPanel(); //legfelső (name/cas hp/money)
        JPanel p1TwrLabelRow    = new JPanel(); //tower text
        JPanel p1TwrOptRow      = new JPanel(); //tower button options
        JPanel p1TwrManLabelRow = new JPanel(); //tower management text
        JPanel p1TwrManOptRow   = new JPanel(); //tower management button options
        JPanel p1UnitLabelRow   = new JPanel(); //unit text
        JPanel p1UnitOptRow     = new JPanel(); //unit button options

        p1Data = new JLabel();
        JLabel p1Twr        = new JLabel("Towers");
        JLabel p1FortStat   = new JLabel("200 coins");
        JLabel p1RapStat    = new JLabel("250 coins");
        JLabel p1SnipStat   = new JLabel("300 coins");
        JLabel p1TwrMan     = new JLabel("Tower Management");
        JLabel p1UnitLab    = new JLabel("Units");
        JLabel p1GenStat    = new JLabel("20 coins");
        JLabel p1DivStat    = new JLabel("30 coins");
        JLabel p1ClimStat   = new JLabel("30 coins");
        JLabel p1FigStat    = new JLabel("30 coins");
        JLabel p1DesStat    = new JLabel("30 coins");

        JPanel p2Panel          = new JPanel();
        JPanel p2Stats          = new JPanel();
        JPanel p2TwrLabelRow    = new JPanel();
        JPanel p2TwrOptRow      = new JPanel();
        JPanel p2TwrManLabelRow = new JPanel();
        JPanel p2TwrManOptRow   = new JPanel();
        JPanel p2UnitLabelRow   = new JPanel();
        JPanel p2UnitOptRow     = new JPanel();

        p2Data = new JLabel();
        JLabel p2Twr        = new JLabel("Towers");
        JLabel p2FortStat   = new JLabel("200 coins");
        JLabel p2RapStat    = new JLabel("250 coins");
        JLabel p2SnipStat   = new JLabel("300 coins");
        JLabel p2TwrMan     = new JLabel("Tower Management");
        JLabel p2UnitLab    = new JLabel("Units");
        JLabel p2GenStat    = new JLabel("20 coins");
        JLabel p2DivStat    = new JLabel("30 coins");
        JLabel p2ClimStat   = new JLabel("30 coins");
        JLabel p2FigStat    = new JLabel("30 coins");
        JLabel p2DesStat    = new JLabel("30 coins");

        playerDataUpdate();

        p1TowerButtons[0] = new JButton("Fortified");
        p1TowerButtons[1] = new JButton("Rapid");
        p1TowerButtons[2] = new JButton("Sniper");
        p1TowerButtons[3] = new JButton("Upgrade");
        p1TowerButtons[4] = new JButton("Demolish");
        p1UnitButtons[0] = new JButton("General");
        p1UnitButtons[1] = new JButton("Fighter");
        p1UnitButtons[2] = new JButton("Climber");
        p1UnitButtons[3] = new JButton("Diver");
        p1UnitButtons[4] = new JButton("Destroyer");

        p2TowerButtons[0] = new JButton("Fortified");
        p2TowerButtons[1] = new JButton("Rapid");
        p2TowerButtons[2] = new JButton("Sniper");
        p2TowerButtons[3] = new JButton("Upgrade");
        p2TowerButtons[4] = new JButton("Demolish");
        p2UnitButtons[0] = new JButton("General");
        p2UnitButtons[1] = new JButton("Fighter");
        p2UnitButtons[2] = new JButton("Climber");
        p2UnitButtons[3] = new JButton("Diver");
        p2UnitButtons[4] = new JButton("Destroyer");

        this.setBackground(algaeGreen);

        //p1 panels
        p1Panel.setLayout(new GridBagLayout());
        p1TwrOptRow.setLayout(new GridBagLayout());
        p1TwrManOptRow.setLayout(new GridBagLayout());
        p1UnitOptRow.setLayout(new GridBagLayout());

        //p2 panels
        p2Panel.setLayout(new GridBagLayout());
        p2TwrOptRow.setLayout(new GridBagLayout());
        p2TwrManOptRow.setLayout(new GridBagLayout());
        p2UnitOptRow.setLayout(new GridBagLayout());

        //Elhelyezés
        GridBagConstraints gbc = new GridBagConstraints();

        GridBagConstraints gbl = new GridBagConstraints();
        GridBagConstraints gbtl = new GridBagConstraints();
        GridBagConstraints gbml = new GridBagConstraints();
        GridBagConstraints gbul = new GridBagConstraints();

        GridBagConstraints gbr = new GridBagConstraints();
        GridBagConstraints gbtr = new GridBagConstraints();
        GridBagConstraints gbmr = new GridBagConstraints();
        GridBagConstraints gbur = new GridBagConstraints();

        /**
         * Játéktér
         */
        gbc.anchor = GridBagConstraints.CENTER; //nem csinál semmit..
        gbc.insets = new Insets(0, 45, 0, 45);
        gbc.gridx = 1;
        gbc.gridy = 1;
        this.add(board, gbc);
        
        
        /**
         * Save game BUTTON
         */
        saveButton.setPreferredSize(new Dimension(260, 40));
        saveButton.setFont(new Font("Calibri", Font.PLAIN, 25));
        saveButton.setMargin(new Insets(8, 0, 0, 0));
        saveButton.setFocusPainted(false);
        gbc.insets = new Insets(0, 0, -22, 0);    //0, 0, 0, 0
        gbc.gridx = 0;
        gbc.gridy = 0;
        this.add(saveButton, gbc);

        /**
         * Exit game BUTTON
         */
        exitButton.setPreferredSize(new Dimension(260, 40));
        exitButton.setFont(new Font("Calibri", Font.PLAIN, 25));
        exitButton.setMargin(new Insets(8, 0, 0, 0));
        exitButton.setFocusPainted(false);
        gbc.insets = new Insets(0, 0, -22, 0);
        gbc.gridx = 2;
        gbc.gridy = 0;
        this.add(exitButton, gbc);

        /**
         * Finish round BUTTON
         */
        newRoundButton.setPreferredSize(new Dimension(932, 50));
        newRoundButton.setFont(new Font("Calibri", Font.PLAIN, 32));
        newRoundButton.setMargin(new Insets(12, 0, 0, 0));
        newRoundButton.setFocusPainted(false);
        gbc.insets = new Insets(15, 0, -10, 0);  //10, 0, -5, 0
        gbc.gridx = 1;
        gbc.gridy = 2;
        this.add(newRoundButton, gbc);

        /**
         * Round time & count LABEL
         */
        timeAndRoundLabel.setFont(new Font("Calibri", Font.BOLD, 30));
        gbc.insets = new Insets(2, 0, 0, 0);    //5, 0, 8, 0
        gbc.gridx = 1;
        gbc.gridy = 0;
        this.add(timeAndRoundLabel, gbc);
        
        /**
         * Player 1 UI - p1Panel MAIN
         */
        p1Panel.setPreferredSize(new Dimension(400, 858));
        p1Panel.setBackground(Color.DARK_GRAY);

        //STATOK: név + pénz + kastély hp
        p1Stats.add(p1Data);
        p1Stats.setBackground(veryLightGray);

        p1Data.setPreferredSize(new Dimension(380, 72));
        p1Data.setFont(new Font("Calibri", Font.PLAIN, 35));
        p1Data.setBorder(new EmptyBorder(0, 5, 8, 0));
        
        gbl.insets = new Insets(-210, 0, 75, 0);   //-210, 0, 100, 0
        gbl.gridx = 0;
        gbl.gridy = 0;
        p1Panel.add(p1Stats, gbl);

        
        //Tower LABEL
        p1TwrLabelRow.add(p1Twr);
        p1TwrLabelRow.setBorder(new EmptyBorder(8, -245, 0, 0));
        p1TwrLabelRow.setBackground(veryLightGray);
        p1TwrLabelRow.setPreferredSize(new Dimension(390, 60));

        p1Twr.setFont(new Font("Calibri", Font.PLAIN, 40));

        gbl.insets = new Insets(-100, 0, 0, 0);
        gbl.gridx = 0;
        gbl.gridy = 1;
        p1Panel.add(p1TwrLabelRow, gbl);
        
        
        //Tower BUTTONS
        p1TowerButtons[0].setPreferredSize(new Dimension(180, 40)); //260, 40
        p1TowerButtons[0].setFont(new Font("Calibri", Font.PLAIN, 25));
        p1TowerButtons[0].setMargin(new Insets(8, 0, 0, 0));
        p1TowerButtons[0].setFocusPainted(false);
        gbtl.insets = new Insets(2, -170, 3, 0);    //2, -220, 3, 0
        gbtl.gridx = 0;
        gbtl.gridy = 0;
        p1TwrOptRow.add(p1TowerButtons[0], gbtl);
        
        p1FortStat.setFont(new Font("Calibri", Font.PLAIN, 30));
        gbtl.insets = new Insets(8, 12, 0, -180);
        gbtl.gridx = 1;
        gbtl.gridy = 0;
        p1TwrOptRow.add(p1FortStat, gbtl);

        p1TowerButtons[1].setPreferredSize(new Dimension(180, 40));
        p1TowerButtons[1].setFont(new Font("Calibri", Font.PLAIN, 25));
        p1TowerButtons[1].setMargin(new Insets(8, 0, 0, 0));
        p1TowerButtons[1].setFocusPainted(false);
        gbtl.insets = new Insets(2, -170, 3, 0);
        gbtl.gridx = 0;
        gbtl.gridy = 1;
        p1TwrOptRow.add(p1TowerButtons[1], gbtl);
        
        p1RapStat.setFont(new Font("Calibri", Font.PLAIN, 30));
        gbtl.insets = new Insets(8, 12, 0, -180);
        gbtl.gridx = 1;
        gbtl.gridy = 1;
        p1TwrOptRow.add(p1RapStat, gbtl);

        p1TowerButtons[2].setPreferredSize(new Dimension(180, 40));
        p1TowerButtons[2].setFont(new Font("Calibri", Font.PLAIN, 25));
        p1TowerButtons[2].setMargin(new Insets(8, 0, 0, 0));
        p1TowerButtons[2].setFocusPainted(false);
        gbtl.insets = new Insets(2, -170, 3, 0);
        gbtl.gridx = 0;
        gbtl.gridy = 2;
        p1TwrOptRow.add(p1TowerButtons[2], gbtl);
        
        p1SnipStat.setFont(new Font("Calibri", Font.PLAIN, 30));
        gbtl.insets = new Insets(8, 12, 0, -180);
        gbtl.gridx = 1;
        gbtl.gridy = 2;
        p1TwrOptRow.add(p1SnipStat, gbtl);

        p1TwrOptRow.setBackground(Color.LIGHT_GRAY);
        p1TwrOptRow.setPreferredSize(new Dimension(390, 160));

        gbl.insets = new Insets(-20, 0, 20, 0);
        gbl.gridx = 0;
        gbl.gridy = 2;
        p1Panel.add(p1TwrOptRow, gbl);

        
        //Tower management LABEL
        p1TwrManLabelRow.add(p1TwrMan);
        p1TwrManLabelRow.setBorder(new EmptyBorder(8, 0, 0, 30));
        p1TwrManLabelRow.setBackground(veryLightGray);
        p1TwrManLabelRow.setPreferredSize(new Dimension(390, 60));

        p1TwrMan.setFont(new Font("Calibri", Font.PLAIN, 40));

        gbl.insets = new Insets(0, 0, 0, 0);
        gbl.gridx = 0;
        gbl.gridy = 3;
        p1Panel.add(p1TwrManLabelRow, gbl);
        

        //Tower management BUTTONS
        p1TowerButtons[3].setPreferredSize(new Dimension(260, 40));
        p1TowerButtons[3].setFont(new Font("Calibri", Font.PLAIN, 25));
        p1TowerButtons[3].setMargin(new Insets(8, 0, 0, 0));
        p1TowerButtons[3].setFocusPainted(false);
        gbml.insets = new Insets(4, 0, 3, 0);
        gbml.gridx = 0;
        gbml.gridy = 0;
        p1TwrManOptRow.add(p1TowerButtons[3], gbml);

        p1TowerButtons[4].setPreferredSize(new Dimension(260, 40));
        p1TowerButtons[4].setFont(new Font("Calibri", Font.PLAIN, 25));
        p1TowerButtons[4].setMargin(new Insets(8, 0, 0, 0));
        p1TowerButtons[4].setFocusPainted(false);
        gbml.insets = new Insets(3, 0, 4, 0);
        gbml.gridx = 0;
        gbml.gridy = 1;
        p1TwrManOptRow.add(p1TowerButtons[4], gbml);

        p1TwrManOptRow.setBackground(Color.LIGHT_GRAY);
        p1TwrManOptRow.setPreferredSize(new Dimension(390, 110));

        gbl.insets = new Insets(0, 0, 20, 0);
        gbl.gridx = 0;
        gbl.gridy = 4;
        p1Panel.add(p1TwrManOptRow, gbl);

        
        //Units LABEL
        p1UnitLabelRow.add(p1UnitLab);
        p1UnitLabelRow.setBorder(new EmptyBorder(8, -280, 0, 0));
        p1UnitLabelRow.setBackground(veryLightGray);
        p1UnitLabelRow.setPreferredSize(new Dimension(390, 60));

        p1UnitLab.setFont(new Font("Calibri", Font.PLAIN, 40));

        gbl.insets = new Insets(0, 0, 0, 0);    //másik: -100, 0, 0, 0
        gbl.gridx = 0;
        gbl.gridy = 5;  //5
        p1Panel.add(p1UnitLabelRow, gbl);
        

        //Units BUTTONS
        p1UnitButtons[0].setPreferredSize(new Dimension(180, 40));
        p1UnitButtons[0].setFont(new Font("Calibri", Font.PLAIN, 25));
        p1UnitButtons[0].setMargin(new Insets(8, 0, 0, 0));
        p1UnitButtons[0].setFocusPainted(false);
        gbul.insets = new Insets(2, -170, 3, 0);   //3, -20, 3, 0
        gbul.gridx = 0;
        gbul.gridy = 0;
        p1UnitOptRow.add(p1UnitButtons[0], gbul);
        
        p1GenStat.setFont(new Font("Calibri", Font.PLAIN, 30));
        gbul.insets = new Insets(8, 12, 0, -180);
        gbul.gridx = 1;
        gbul.gridy = 0;
        p1UnitOptRow.add(p1GenStat, gbul);        

        p1UnitButtons[1].setPreferredSize(new Dimension(180, 40));
        p1UnitButtons[1].setFont(new Font("Calibri", Font.PLAIN, 25));
        p1UnitButtons[1].setMargin(new Insets(8, 0, 0, 0));
        p1UnitButtons[1].setFocusPainted(false);
        gbul.insets = new Insets(2, -170, 3, 0);
        gbul.gridx = 0;
        gbul.gridy = 1;
        p1UnitOptRow.add(p1UnitButtons[1], gbul);
        
        p1FigStat.setFont(new Font("Calibri", Font.PLAIN, 30));
        gbul.insets = new Insets(8, 12, 0, -180);
        gbul.gridx = 1;
        gbul.gridy = 1;
        p1UnitOptRow.add(p1FigStat, gbul);

        p1UnitButtons[2].setPreferredSize(new Dimension(180, 40));
        p1UnitButtons[2].setFont(new Font("Calibri", Font.PLAIN, 25));
        p1UnitButtons[2].setMargin(new Insets(8, 0, 0, 0));
        p1UnitButtons[2].setFocusPainted(false);
        gbul.insets = new Insets(2, -170, 3, 0);
        gbul.gridx = 0;
        gbul.gridy = 2;
        p1UnitOptRow.add(p1UnitButtons[2], gbul);
        
        p1ClimStat.setFont(new Font("Calibri", Font.PLAIN, 30));
        gbul.insets = new Insets(8, 12, 0, -180);
        gbul.gridx = 1;
        gbul.gridy = 2;
        p1UnitOptRow.add(p1ClimStat, gbul);

        p1UnitButtons[3].setPreferredSize(new Dimension(180, 40));
        p1UnitButtons[3].setFont(new Font("Calibri", Font.PLAIN, 25));
        p1UnitButtons[3].setMargin(new Insets(8, 0, 0, 0));
        p1UnitButtons[3].setFocusPainted(false);
        gbul.insets = new Insets(2, -170, 3, 0);
        gbul.gridx = 0;
        gbul.gridy = 3;
        p1UnitOptRow.add(p1UnitButtons[3], gbul);
        
        p1DivStat.setFont(new Font("Calibri", Font.PLAIN, 30));
        gbul.insets = new Insets(8, 12, 0, -180);
        gbul.gridx = 1;
        gbul.gridy = 3;
        p1UnitOptRow.add(p1DivStat, gbul);

        p1UnitButtons[4].setPreferredSize(new Dimension(180, 40));
        p1UnitButtons[4].setFont(new Font("Calibri", Font.PLAIN, 25));
        p1UnitButtons[4].setMargin(new Insets(8, 0, 0, 0));
        p1UnitButtons[4].setFocusPainted(false);
        gbul.insets = new Insets(2, -170, 3, 0);
        gbul.gridx = 0;
        gbul.gridy = 4;
        p1UnitOptRow.add(p1UnitButtons[4], gbul);
        
        p1DesStat.setFont(new Font("Calibri", Font.PLAIN, 30));
        gbul.insets = new Insets(8, 12, 0, -180);
        gbul.gridx = 1;
        gbul.gridy = 4;
        p1UnitOptRow.add(p1DesStat, gbul);

        p1UnitOptRow.setBackground(Color.LIGHT_GRAY);
        p1UnitOptRow.setPreferredSize(new Dimension(390, 250));

        gbl.insets = new Insets(0, 0, -185, 0); //0, 0, -210, 0
        gbl.gridx = 0;
        gbl.gridy = 6;  //6
        p1Panel.add(p1UnitOptRow, gbl);

        //p1Panel elhelyezkedése
        gbc.insets = new Insets(0, 0, -53, 5);    //0, 0, -40, 5
        gbc.gridx = 0;
        gbc.gridy = 1;
        this.add(p1Panel, gbc);

        /**
         * Player 2 UI
         */
        p2Panel.setPreferredSize(new Dimension(400, 858)); //420, 390
        p2Panel.setBackground(Color.DARK_GRAY);

        //STATOK: név + pénz + kastély hp
        p2Stats.add(p2Data);
        p2Stats.setBackground(veryLightGray);

        p2Data.setPreferredSize(new Dimension(380, 72));
        p2Data.setFont(new Font("Calibri", Font.PLAIN, 35));
        p2Data.setBorder(new EmptyBorder(0, 5, 8, 0));

        gbr.insets = new Insets(-210, 0, 75, 0);
        gbr.gridx = 0;
        gbr.gridy = 0;
        p2Panel.add(p2Stats, gbr);

        
        //Tower LABEL
        p2TwrLabelRow.add(p2Twr);
        p2TwrLabelRow.setBorder(new EmptyBorder(8, -245, 0, 0));    //8, -245, 0, 0
        p2TwrLabelRow.setBackground(veryLightGray);
        p2TwrLabelRow.setPreferredSize(new Dimension(390, 60));

        p2Twr.setFont(new Font("Calibri", Font.PLAIN, 40));

        gbr.insets = new Insets(-100, 0, 0, 0);
        gbr.gridx = 0;
        gbr.gridy = 1;
        p2Panel.add(p2TwrLabelRow, gbr);

        
        //Tower BUTTONS
        p2TowerButtons[0].setPreferredSize(new Dimension(180, 40));
        p2TowerButtons[0].setFont(new Font("Calibri", Font.PLAIN, 25));
        p2TowerButtons[0].setMargin(new Insets(8, 0, 0, 0));
        p2TowerButtons[0].setFocusPainted(false);
        gbtr.insets = new Insets(2, -170, 3, 0);
        gbtr.gridx = 0;
        gbtr.gridy = 0;
        p2TwrOptRow.add(p2TowerButtons[0], gbtr);
        
        p2FortStat.setFont(new Font("Calibri", Font.PLAIN, 30));
        gbtr.insets = new Insets(8, 12, 0, -180);
        gbtr.gridx = 1;
        gbtr.gridy = 0;
        p2TwrOptRow.add(p2FortStat, gbtr);

        p2TowerButtons[1].setPreferredSize(new Dimension(180, 40));
        p2TowerButtons[1].setFont(new Font("Calibri", Font.PLAIN, 25));
        p2TowerButtons[1].setMargin(new Insets(8, 0, 0, 0));
        p2TowerButtons[1].setFocusPainted(false);
        gbtr.insets = new Insets(2, -170, 3, 0);
        gbtr.gridx = 0;
        gbtr.gridy = 1;
        p2TwrOptRow.add(p2TowerButtons[1], gbtr);
        
        p2RapStat.setFont(new Font("Calibri", Font.PLAIN, 30));
        gbtr.insets = new Insets(8, 12, 0, -180);
        gbtr.gridx = 1;
        gbtr.gridy = 1;
        p2TwrOptRow.add(p2RapStat, gbtr);

        p2TowerButtons[2].setPreferredSize(new Dimension(180, 40));
        p2TowerButtons[2].setFont(new Font("Calibri", Font.PLAIN, 25));
        p2TowerButtons[2].setMargin(new Insets(8, 0, 0, 0));
        p2TowerButtons[2].setFocusPainted(false);
        gbtr.insets = new Insets(2, -170, 3, 0);
        gbtr.gridx = 0;
        gbtr.gridy = 2;
        p2TwrOptRow.add(p2TowerButtons[2], gbtr);
        
        p2SnipStat.setFont(new Font("Calibri", Font.PLAIN, 30));
        gbtr.insets = new Insets(8, 12, 0, -180);
        gbtr.gridx = 1;
        gbtr.gridy = 2;
        p2TwrOptRow.add(p2SnipStat, gbtr);

        p2TwrOptRow.setBackground(Color.LIGHT_GRAY);
        p2TwrOptRow.setPreferredSize(new Dimension(390, 160));

        gbr.insets = new Insets(-20, 0, 20, 0);
        gbr.gridx = 0;
        gbr.gridy = 2;
        p2Panel.add(p2TwrOptRow, gbr);

        
        //Tower management LABEL
        p2TwrManLabelRow.add(p2TwrMan);
        p2TwrManLabelRow.setBorder(new EmptyBorder(8, 0, 0, 30));
        p2TwrManLabelRow.setBackground(veryLightGray);
        p2TwrManLabelRow.setPreferredSize(new Dimension(390, 60));

        p2TwrMan.setFont(new Font("Calibri", Font.PLAIN, 40));

        gbr.insets = new Insets(0, 0, 0, 0);
        gbr.gridx = 0;
        gbr.gridy = 3;
        p2Panel.add(p2TwrManLabelRow, gbr);

        
        //Tower management BUTTONS
        p2TowerButtons[3].setPreferredSize(new Dimension(260, 40));
        p2TowerButtons[3].setFont(new Font("Calibri", Font.PLAIN, 25));
        p2TowerButtons[3].setMargin(new Insets(8, 0, 0, 0));
        p2TowerButtons[3].setFocusPainted(false);
        gbmr.insets = new Insets(4, 0, 3, 0);
        gbmr.gridx = 0;
        gbmr.gridy = 0;
        p2TwrManOptRow.add(p2TowerButtons[3], gbmr);

        p2TowerButtons[4].setPreferredSize(new Dimension(260, 40));
        p2TowerButtons[4].setFont(new Font("Calibri", Font.PLAIN, 25));
        p2TowerButtons[4].setMargin(new Insets(8, 0, 0, 0));
        p2TowerButtons[4].setFocusPainted(false);
        gbmr.insets = new Insets(3, 0, 4, 0);
        gbmr.gridx = 0;
        gbmr.gridy = 1;
        p2TwrManOptRow.add(p2TowerButtons[4], gbmr);

        p2TwrManOptRow.setBackground(Color.LIGHT_GRAY);
        p2TwrManOptRow.setPreferredSize(new Dimension(390, 110));

        gbr.insets = new Insets(0, 0, 20, 0);
        gbr.gridx = 0;
        gbr.gridy = 4;
        p2Panel.add(p2TwrManOptRow, gbr);

        
        //Units LABEL
        p2UnitLabelRow.add(p2UnitLab);
        p2UnitLabelRow.setBorder(new EmptyBorder(8, -280, 0, 0));
        p2UnitLabelRow.setBackground(veryLightGray);
        p2UnitLabelRow.setPreferredSize(new Dimension(390, 60));

        p2UnitLab.setFont(new Font("Calibri", Font.PLAIN, 40));

        gbr.insets = new Insets(0, 0, 0, 0);
        gbr.gridx = 0;
        gbr.gridy = 5;
        p2Panel.add(p2UnitLabelRow, gbr);
        

        //Units BUTTONS
        p2UnitButtons[0].setPreferredSize(new Dimension(180, 40));
        p2UnitButtons[0].setFont(new Font("Calibri", Font.PLAIN, 25));
        p2UnitButtons[0].setMargin(new Insets(8, 0, 0, 0));
        p2UnitButtons[0].setFocusPainted(false);
        gbur.insets = new Insets(2, -170, 3, 0);
        gbur.gridx = 0;
        gbur.gridy = 0;
        p2UnitOptRow.add(p2UnitButtons[0], gbur);
        
        p2GenStat.setFont(new Font("Calibri", Font.PLAIN, 30));
        gbur.insets = new Insets(8, 12, 0, -180);
        gbur.gridx = 1;
        gbur.gridy = 0;
        p2UnitOptRow.add(p2GenStat, gbur);

        p2UnitButtons[1].setPreferredSize(new Dimension(180, 40));
        p2UnitButtons[1].setFont(new Font("Calibri", Font.PLAIN, 25));
        p2UnitButtons[1].setMargin(new Insets(8, 0, 0, 0));
        p2UnitButtons[1].setFocusPainted(false);
        gbur.insets = new Insets(2, -170, 3, 0);
        gbur.gridx = 0;
        gbur.gridy = 1;
        p2UnitOptRow.add(p2UnitButtons[1], gbur);
        
        p2FigStat.setFont(new Font("Calibri", Font.PLAIN, 30));
        gbur.insets = new Insets(8, 12, 0, -180);
        gbur.gridx = 1;
        gbur.gridy = 1;
        p2UnitOptRow.add(p2FigStat, gbur);

        p2UnitButtons[2].setPreferredSize(new Dimension(180, 40));
        p2UnitButtons[2].setFont(new Font("Calibri", Font.PLAIN, 25));
        p2UnitButtons[2].setMargin(new Insets(8, 0, 0, 0));
        p2UnitButtons[2].setFocusPainted(false);
        gbur.insets = new Insets(2, -170, 3, 0);
        gbur.gridx = 0;
        gbur.gridy = 2;
        p2UnitOptRow.add(p2UnitButtons[2], gbur);
        
        p2ClimStat.setFont(new Font("Calibri", Font.PLAIN, 30));
        gbur.insets = new Insets(8, 12, 0, -180);
        gbur.gridx = 1;
        gbur.gridy = 2;
        p2UnitOptRow.add(p2ClimStat, gbur);

        p2UnitButtons[3].setPreferredSize(new Dimension(180, 40));
        p2UnitButtons[3].setFont(new Font("Calibri", Font.PLAIN, 25));
        p2UnitButtons[3].setMargin(new Insets(8, 0, 0, 0));
        p2UnitButtons[3].setFocusPainted(false);
        gbur.insets = new Insets(2, -170, 3, 0);
        gbur.gridx = 0;
        gbur.gridy = 3;
        p2UnitOptRow.add(p2UnitButtons[3], gbur);
        
        p2DivStat.setFont(new Font("Calibri", Font.PLAIN, 30));
        gbur.insets = new Insets(8, 12, 0, -180);
        gbur.gridx = 1;
        gbur.gridy = 3;
        p2UnitOptRow.add(p2DivStat, gbur);

        p2UnitButtons[4].setPreferredSize(new Dimension(180, 40));
        p2UnitButtons[4].setFont(new Font("Calibri", Font.PLAIN, 25));
        p2UnitButtons[4].setMargin(new Insets(8, 0, 0, 0));
        p2UnitButtons[4].setFocusPainted(false);
        gbur.insets = new Insets(2, -170, 3, 0);
        gbur.gridx = 0;
        gbur.gridy = 4;
        p2UnitOptRow.add(p2UnitButtons[4], gbur);
        
        p2DesStat.setFont(new Font("Calibri", Font.PLAIN, 30));
        gbur.insets = new Insets(8, 12, 0, -180);
        gbur.gridx = 1;
        gbur.gridy = 4;
        p2UnitOptRow.add(p2DesStat, gbur);

        p2UnitOptRow.setBackground(Color.LIGHT_GRAY);
        p2UnitOptRow.setPreferredSize(new Dimension(390, 250));

        gbr.insets = new Insets(0, 0, -185, 0);
        gbr.gridx = 0;
        gbr.gridy = 6;  //6
        p2Panel.add(p2UnitOptRow, gbr);

        //p2Panel elhelyezkedése
        gbc.insets = new Insets(0, 5, -53, 0);  //0, 5, -40, 0
        gbc.gridx = 2;
        gbc.gridy = 1;
        this.add(p2Panel, gbc);
    }

    /**
     * Aktiválja a soron levő játékos gombjait
     */
    public final void activePlayerPanelSetter() {
        if (board.getModel().getActivePlayer() == 0) {
            for (var button : p2TowerButtons)   { button.setEnabled(false); }
            for (var button : p2UnitButtons)    { button.setEnabled(false); }
            for (var button : p1TowerButtons)   { button.setEnabled(true); }
            for (var button : p1UnitButtons)    { button.setEnabled(true); }
        } else {
            for (var button : p1TowerButtons)   { button.setEnabled(false); }
            for (var button : p1UnitButtons)    { button.setEnabled(false); }
            for (var button : p2TowerButtons)   { button.setEnabled(true); }
            for (var button : p2UnitButtons)    { button.setEnabled(true); }
        }
    }

    /**
     * Frissíti a játékos UI-on megjelenő adatait
     */
    public void playerDataUpdate() {
        Player p1 = board.getModel().getPlayers()[0];
        Player p2 = board.getModel().getPlayers()[1];
        p1Data.setText(
            "<html>"
            + p1.getName()
            + "<br>Money - " + p1.getMoney()
            + "</html>"
        );

        p2Data.setText(
            "<html>"
            + p2.getName()
            + "<br>Money - " + p2.getMoney()
            + "</html>"
        );
    }

    /**
     * Getterek, setterek
     */
    public Board getBoard()                 { return board; }
    public void setBoard(Board bd)          { board = bd; }
    public int getTicks()                   { return ticks; }
    public void setTicks(int ticks)         { this.ticks = ticks; }
    public void setModel(Model model)       { this.model = model; }
    public JLabel getTimeAndRoundLabel()    { return timeAndRoundLabel; }
    public JButton[] getP1TowerButtons()    { return p1TowerButtons; }
    public JButton[] getP1UnitButtons()     { return p1UnitButtons; }
    public JButton[] getP2TowerButtons()    { return p2TowerButtons; }
    public JButton[] getP2UnitButtons()     { return p2UnitButtons; }
    public ArrayList<Integer> getPlayer1distances() { return player1distances; }
    public ArrayList<Integer> getPlayer2distances() { return player2distances; }
    public void setRNDPROTECTION(int RNDPROTECTION) { this.RNDPROTECTION = RNDPROTECTION; }
    public void setTestMode(boolean b){
        testMode = b;
    }
}