package view;

import java.awt.Dimension;
import java.awt.EventQueue;
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
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
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
    private String winner = "";
    private boolean simulationTime = false;
    private ArrayList<Integer> distances;
    private final int timerInterval = 250;
    private Model model;
    private int simulationticks;

    public GameWindow() {
        super();
    }

    public void setModel(Model model) {
        this.model = model;
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
        setSize(width, height);
        distances = new ArrayList<>();
        this.setPreferredSize(new Dimension(width, height));
        exitButton = new JButton("Exit game");
        exitButton.addActionListener((event) -> System.exit(0));
        newRoundButton = new JButton("Finish round");
        newRoundButton.addActionListener((event) -> {
            newRound();
        });

        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                }

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

        this.add(saveButton);
        this.setLayout(new GridBagLayout());

        p1TowerButtons = new JButton[5];
        p1UnitButtons = new JButton[5];
        p2TowerButtons = new JButton[5];
        p2UnitButtons = new JButton[5];

        //rounds = new JLabel("Round: 1");
        setPanels();

        p1TowerButtons[0].addActionListener(ae -> {
            towerPlaceAction("Fortified");
        });
        p1TowerButtons[1].addActionListener(ae -> {
            towerPlaceAction("Rapid");
        });
        p1TowerButtons[2].addActionListener(ae -> {
            towerPlaceAction("Sniper");
        });
        p1TowerButtons[3].addActionListener(ae -> {
            buttonAction = "upgrade";
            model.setSelectableTowers(true);
        });
        p1TowerButtons[4].addActionListener(ae -> {
            buttonAction = "demolish";
            model.setSelectableTowers(false);
        });
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
        p2TowerButtons[0].addActionListener(ae -> {
            towerPlaceAction("Fortified");
        });
        p2TowerButtons[1].addActionListener(ae -> {
            towerPlaceAction("Rapid");
        });
        p2TowerButtons[2].addActionListener(ae -> {
            towerPlaceAction("Sniper");
        });

        p2TowerButtons[3].addActionListener(ae -> {
            buttonAction = "upgrade";
            model.setSelectableTowers(true);
        });
        p2TowerButtons[4].addActionListener(ae -> {
            buttonAction = "demolish";
            model.setSelectableTowers(false);
        });

        board.addMouseListener(new MouseAdapter() // after selecting tower type, players must click where they want to place it
        {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX() / (model.getSize() / 30);
                int y = e.getY() / (model.getSize() / 30);

                if (buttonAction.equals("placeTower")) {
                    board.setModel(model.getPlayers()[model.getActivePlayer()].build(x, y, selectedTower, model));
                    buttonAction = "";
                } else if (buttonAction.equals("upgrade")) {
                    model.getPlayers()[model.getActivePlayer()].upgrade(x, y, model.getSize());
                    model.setSelectables(new ArrayList<>());
                    buttonAction = "";
                } else if (buttonAction.equals("demolish")) {
                    model.getPosition()[x][y] = 'D';
                    
                    model.getPlayers()[model.getActivePlayer()].demolish(x, y, model.getSize());
                    model.getPlayers()[model.getActivePlayer()].setMoney( model.getPlayers()
                            [model.getActivePlayer()].getMoney()+model.getTower(x, y).getMaxHp());
                    model.getPlayers()[model.getActivePlayer()].demolish(x, y, model.getSize());
                    model.setSelectables(new ArrayList<>());
                    buttonAction = "";
                }

                board.repaint();
                playerDataUpdate();

            }
        });

        ticks = (1000 / timerInterval) * 60;

        timer = new Timer(timerInterval, (ActionEvent ae) -> {

            PointerInfo a = MouseInfo.getPointerInfo();

            Point b = a.getLocation();
            int x = (int) b.getX() - 500;
            x /= (board.getModel().getSize() / 30);
            int y = (int) b.getY() - 75;
            y /= (board.getModel().getSize() / 30);

            if (!model.getInfo(x, y).equals("")) {
                ToolTipManager.sharedInstance().setEnabled(true);
                board.setToolTipText(model.getInfo(x, y));
            } else {
                ToolTipManager.sharedInstance().setEnabled(false);
            }

            if (simulationTime) {
                simulationTime = simulation();
                for (int i = 0; i < 2; i++) {
                    ArrayList<Tower> towers = model.getPlayers()[i].getTowers();
                    for (int j = 0; j < towers.size(); j++) {
                        double tickPerAttack = (1 / towers.get(j).getAttack_speed());
                        if (simulationticks % tickPerAttack == 0) {
                            ArrayList<Unit> enemyUnitsNearby = model.enemyUnitsNearby(i, towers.get(j));
                            for (int k = 0; k < enemyUnitsNearby.size(); k++) {
                                if (enemyUnitsNearby.get(k).getHp() > towers.get(j).getPower()) {
                                    enemyUnitsNearby.get(k).setHp(enemyUnitsNearby.get(k).getHp() - towers.get(j).getPower());
                                } else {

                                    model.getPlayers()[(i + 1) % 2].deleteUnit(enemyUnitsNearby.get(k));
                                    model.getPlayers()[i].setMoney(model.getPlayers()[i].getMoney()+enemyUnitsNearby.get(k).getMaxHp()*2);
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
                distances.clear();
                // showWays();
                for (int q = 0; q < 2; q++) {
                    int defender = Math.abs(q * 4 - 4);
                    ArrayList<Unit> updateUnits = model.getPlayers()[q].getUnits();
                    for (Unit u : updateUnits) {
                        distances.add(u.getDistance());
                        int minWayDiff = 10000;
                        ArrayList<Node> bestway = new ArrayList<>();

                        for (int i = 0; i < 4; i++) {
                            ArrayList<String> wayString = model.getPlayers()[q].findWay(u.getX() / (model.getSize() / 30), u.getY() / (model.getSize() / 30),
                                    model.getCastleCoordinates()[i + defender][0], model.getCastleCoordinates()[i + defender][1], model.getPlayers()[q].getDifficulty(model, u.getType()));

                            if (minWayDiff > model.wayDiff(q, wayString, u.getType())) {
                                bestway = model.getPlayers()[q].convertWay(wayString);
                                minWayDiff = model.wayDiff(q, wayString, u.getType());

                            }
                        }
                        u.setWay(bestway);
                        //System.out.println("timer bestway: " + bestway);
                        model.getPlayers()[q].setUnits(updateUnits);

                    }
                   
                }
            }

                    ticks--;
                    if (!model.isOver()) {
                        int newtime = (ticks + 1) / 2;
                        if (newtime == 0) {
                            newRound();
                        } else {
                            //timeSec = (int) (System.currentTimeMillis() - time) / 1000;
                            //timeLabel.setText("Time " + timeSec + " s,");
                            timeAndRoundLabel.setText("Time left: " + (ticks + 1) / (1000 / timerInterval) + " s, round: " + (model.getRound() + 1) / 2);
                        }

                        board.repaint();

                    } else {
                        gameOver();
                    }

                }
                );

        timer.start();

                activePlayerPanelSetter();
            }

    

    public int getTicks() {
        return ticks;
    }

    public boolean simulation() {
        //first the units move
        boolean moreDistance = false;
        for (int q = 0; q < 2; q++) {
            if (!model.getPlayers()[q].getUnits().isEmpty()) {
                ArrayList<Unit> units = model.getPlayers()[q].getUnits();
                for (int i = 0; i < units.size(); i++) {
                    if (distances.get(i) < 0) {
                        continue;
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

                    distances.set(i, distances.get(i) - 1);
                    if (distances.get(i) >= 0) {
                        moreDistance = true;
                    }

                    //then fighter deal damage to enemy units if they are in the same position
                    ArrayList<Unit> enemyUnitsNearby = model.enemyUnitsNearby(q, units.get(i));
                    if ("Fighter".equals(units.get(i).getType()) && enemyUnitsNearby.size() > 0) {
                        for (int j = 0; j < enemyUnitsNearby.size(); j++) {
                            if (enemyUnitsNearby.get(j).getHp() > units.get(i).getPower()) {
                                enemyUnitsNearby.get(j).setHp(enemyUnitsNearby.get(j).getHp() - units.get(i).getPower());
                            } else {

                                model.getPlayers()[(q + 1) % 2].deleteUnit(enemyUnitsNearby.get(j));
                                model.getPlayers()[q].setMoney(model.getPlayers()[i].getMoney()+enemyUnitsNearby.get(j).getMaxHp()*2);
                                if (j > 0) {
                                    j--;
                                }
                            }
                        }
                    }

                    ArrayList<Tower> towersNearby = model.towersNearby(q, units.get(i));
                    //System.out.println(towersNearby.toString());

                    //then destroyer attacks 
                    if ("Destroyer".equals(units.get(i).getType()) && towersNearby.size() > 0) {
                        int rand = (int) (Math.random() * 2); //the chance to attack towers is 50%
                        if (rand == 1) //destroyer attacks nearby towers with 50% chance (direct next to it) with full power, then disappears
                        {
                            //destroyer deals 50 damage when attacking towers, removes towers if their hp reaches 0
                            for (int j = 0; j < towersNearby.size(); j++) {
                                if (towersNearby.get(j).getHp() > 50) {
                                    towersNearby.get(j).setHp(towersNearby.get(j).getHp() - 50);
                                } else {

                                    towersNearby.get(j).setHp(0);
                                    towersNearby.get(j).setPower(0);
                                    towersNearby.get(j).setRange(0);
                                    towersNearby.get(j).setAttack_speed(0);
                                    model.getPlayers()[(q + 1) % 2].demolish(towersNearby.get(j).getX() / (model.getSize() / 30),
                                            towersNearby.get(j).getY() / (model.getSize() / 30), model.getSize());
                                    model.getPlayers()[q].setMoney(model.getPlayers()[q].getMoney()+towersNearby.get(j).getMaxHp()*3);
                                }
                            }
                            model.getPlayers()[q].deleteUnit(units.get(i));

                        }
                    }
                    board.repaint();
                    startAnimation();
                }

                //u.setWay(model.getPlayers()[q].findWay());
            }
            for (int i = 0; i < model.getPlayers()[q].getUnits().size(); i++) {
                ArrayList<Node> way = model.getPlayers()[q].getUnits().get(i).getWay();
                if (way.isEmpty()) {
                    if (model.getPlayers()[Math.abs(q - 1)].getCastle().getHp()
                            - model.getPlayers()[q].getUnits().get(i).getPower() > 0) {
                        model.getPlayers()[Math.abs(q - 1)].getCastle().setHp(
                                model.getPlayers()[Math.abs(q - 1)].getCastle().getHp()
                                - model.getPlayers()[q].getUnits().get(i).getPower());
                        board.repaint();
                        playerDataUpdate();
                    } else {
                        model.setOver(true);
                    }
                    model.getPlayers()[q].deleteUnit(model.getPlayers()[q].getUnits().get(i));
                    //remove units who reached Castle after dealing damage to it
                    if (i > 0) {
                        i--;
                    }
                }
            }

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

    public Board getBoard() {
        return board;
    }

    public void towerPlaceAction(String sc) {
        selectedTower = sc;
        buttonAction = "placeTower";
        model.setSelectables();
    }

    private void showWays() //helper
    {
        System.out.print("p1: ");
        for (Unit u : model.getPlayers()[0].getUnits()) {
            System.out.println(u.getWay());
        }
        System.out.print("p2: ");
        for (Unit u : model.getPlayers()[1].getUnits()) {
            System.out.println(u.getWay());
        }
    }

    /*
    now it's the other player's round
    both players get 100 coins
    after every 2 rounds, the simulation starts
     */
    public void newRound() {

        model.setSelectables(new ArrayList<>());
        model.setRound(model.getRound() + 1);

        if (model.getRound() == 1 || model.getRound() == 2) {
            ticks = (1000 / timerInterval) * 60;
        } else {
            ticks = (1000 / timerInterval) * 30;
        }
        //rounds.setText("Round: " + model.getRound());
        //timeAndRoundLabel.setText("Time left: " + (ticks + 1) / 2 + " s, round: " + (model.getRound() + 1)/ 2);

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
        JOptionPane.showInputDialog(
                winner + " won, congratulations!");
        System.exit(0);
    }

    public void saveGame() {
        String filename;

        filename = JOptionPane.showInputDialog("Filename:");
        if (filename == null || filename.length() == 0) {
            return;
        }

        model.saveData(filename);

    }

    public final void setPanels() {
        /*this.add(exitButton);
        this.add(saveButton);
         */
        JPanel p1Panel = new JPanel();
        JPanel p2Panel = new JPanel();
        p1Data = new JLabel();
        p2Data = new JLabel();

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

        p1Panel.setLayout(new BoxLayout(p1Panel, BoxLayout.PAGE_AXIS));
        p2Panel.setLayout(new BoxLayout(p2Panel, BoxLayout.PAGE_AXIS));

        p1Panel.add(p1Data);
        p1Panel.add(p1TowerButtons[0]);
        p1Panel.add(p1TowerButtons[1]);
        p1Panel.add(p1TowerButtons[2]);
        p1Panel.add(p1TowerButtons[3]);
        p1Panel.add(p1TowerButtons[4]);
        p1Panel.add(new JLabel("Units"));
        p1Panel.add(p1UnitButtons[0]);
        p1Panel.add(p1UnitButtons[1]);
        p1Panel.add(p1UnitButtons[2]);
        p1Panel.add(p1UnitButtons[3]);
        p1Panel.add(p1UnitButtons[4]);

        p2Panel.add(p2Data);
        p2Panel.add(p2TowerButtons[0]);
        p2Panel.add(p2TowerButtons[1]);
        p2Panel.add(p2TowerButtons[2]);
        p2Panel.add(p2TowerButtons[3]);
        p2Panel.add(p2TowerButtons[4]);
        p2Panel.add(new JLabel("Units"));
        p2Panel.add(p2UnitButtons[0]);
        p2Panel.add(p2UnitButtons[1]);
        p2Panel.add(p2UnitButtons[2]);
        p2Panel.add(p2UnitButtons[3]);
        p2Panel.add(p2UnitButtons[4]);

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 1;
        gbc.gridy = 1;
        this.add(board, gbc);

        gbc.insets = new Insets(0, 0, 0, 200);
        gbc.gridx = 0;
        gbc.gridy = 1;
        this.add(p1Panel, gbc);

        gbc.insets = new Insets(0, 200, 0, 0);
        gbc.gridx = 2;
        gbc.gridy = 1;
        this.add(p2Panel, gbc);

        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.gridx = 1;
        gbc.gridy = 2;
        this.add(newRoundButton, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        this.add(timeAndRoundLabel, gbc);

    }

    public final void activePlayerPanelSetter() {
        if (model.getActivePlayer() == 0) {
            for (var button : p2TowerButtons) {
                button.setEnabled(false);
            }
            for (var button : p2UnitButtons) {
                button.setEnabled(false);
            }
            for (var button : p1TowerButtons) {
                button.setEnabled(true);
            }
            for (var button : p1UnitButtons) {
                button.setEnabled(true);
            }
        } else {
            for (var button : p1TowerButtons) {
                button.setEnabled(false);
            }
            for (var button : p1UnitButtons) {
                button.setEnabled(false);
            }
            for (var button : p2TowerButtons) {
                button.setEnabled(true);
            }
            for (var button : p2UnitButtons) {
                button.setEnabled(true);
            }
        }
    }

    public void playerDataUpdate() {
        Player p1 = board.getModel().getPlayers()[0];
        Player p2 = board.getModel().getPlayers()[1];
        p1Data.setText(
                "<html>"
                + p1.getName()
                + "<br><br>Money: " + p1.getMoney()
                + "<br><br> Towers"
                + "</html>"
        );

        p2Data.setText(
                "<html>"
                + p2.getName()
                + "<br><br>Money: " + p2.getMoney()
                + "<br><br> Towers"
                + "</html>"
        );
    }

    public void setBoard(Board bd) {
        board = bd;
    }

    public JLabel getTimeAndRoundLabel() {
        return timeAndRoundLabel;
    }
}
