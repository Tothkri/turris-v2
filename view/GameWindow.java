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
import java.util.logging.Level;
import java.util.logging.Logger;
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
    //private JLabel rounds;
    //private JLabel timeLabel = new JLabel();
    private JLabel timeAndRoundLabel = new JLabel();
    private JLabel activePlayerLabel = new JLabel();
    private String selectedTower;
    private String buttonAction;
    private int ticks;
    private String winner = "";

    public GameWindow() {
        super();
    }

    public GameWindow(int width, int height, String p1Name, String p2Name, int selectedMap) {
        super();
        board = new Board(selectedMap, p1Name, p2Name, width, height);
        board.getModel().setRound(1);
        //players have 50% chance to start the game
        Random rand = new Random();
        board.getModel().setActivePlayer(rand.nextInt(2));
        constructor(width, height);
    }

    public void constructor(int width, int height) {
        buttonAction = "";
        setSize(width, height);
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
            board.getModel().setSelectableTowers();
        });
        p1TowerButtons[4].addActionListener(ae -> {
            buttonAction = "demolish";
            board.getModel().setSelectableTowers();
        });
        p1UnitButtons[0].addActionListener(ae -> {
            board.setModel(board.getModel().getPlayers()[0].sendUnits("General", 1, board.getModel()));
            playerDataUpdate();
        });
        p1UnitButtons[1].addActionListener(ae -> {
            board.setModel(board.getModel().getPlayers()[0].sendUnits("Fighter", 1, board.getModel()));
            playerDataUpdate();
        });
        p1UnitButtons[2].addActionListener(ae -> {
            board.setModel(board.getModel().getPlayers()[0].sendUnits("Climber", 1, board.getModel()));
            playerDataUpdate();
        });
        p1UnitButtons[3].addActionListener(ae -> {
            board.setModel(board.getModel().getPlayers()[0].sendUnits("Diver", 1, board.getModel()));
            playerDataUpdate();
        });
        p1UnitButtons[4].addActionListener(ae -> {
            board.setModel(board.getModel().getPlayers()[0].sendUnits("Destroyer", 1, board.getModel()));
            playerDataUpdate();
        });
        p2UnitButtons[0].addActionListener(ae -> {
            board.setModel(board.getModel().getPlayers()[1].sendUnits("General", 1, board.getModel()));
            playerDataUpdate();
        });
        p2UnitButtons[1].addActionListener(ae -> {
            board.setModel(board.getModel().getPlayers()[1].sendUnits("Fighter", 1, board.getModel()));
            playerDataUpdate();
        });
        p2UnitButtons[2].addActionListener(ae -> {
            board.setModel(board.getModel().getPlayers()[1].sendUnits("Climber", 1, board.getModel()));
            playerDataUpdate();
        });
        p2UnitButtons[3].addActionListener(ae -> {
            board.setModel(board.getModel().getPlayers()[1].sendUnits("Diver", 1, board.getModel()));
            playerDataUpdate();
        });
        p2UnitButtons[4].addActionListener(ae -> {
            board.setModel(board.getModel().getPlayers()[1].sendUnits("Destroyer", 1, board.getModel()));
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
            board.getModel().setSelectableTowers();
        });
        p2TowerButtons[4].addActionListener(ae -> {
            buttonAction = "demolish";
            board.getModel().setSelectableTowers();
        });

        board.addMouseListener(new MouseAdapter() // after selecting tower type, players must click where they want to place it
        {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX() / (board.getModel().getSize() / 30);
                int y = e.getY() / (board.getModel().getSize() / 30);

                if (buttonAction.equals("placeTower")) {
                    board.setModel(board.getModel().getPlayers()[board.getModel().getActivePlayer()].build(x, y, selectedTower, board.getModel()));
                    buttonAction = "";
                } else if (buttonAction.equals("upgrade")) {
                    board.getModel().getPlayers()[board.getModel().getActivePlayer()].upgrade(x, y, board.getModel().getSize());
                    board.getModel().setSelectables(new ArrayList<>());
                    buttonAction = "";
                } else if (buttonAction.equals("demolish")) {
                    board.getModel().getPosition()[x][y] = 'D';
                    board.getModel().getPlayers()[board.getModel().getActivePlayer()].demolish(x, y, board.getModel().getSize());
                    board.getModel().setSelectables(new ArrayList<>());
                    buttonAction = "";
                }

                board.repaint();
                playerDataUpdate();

            }
        });

        ticks = 120;

        timer = new Timer(500, (ActionEvent ae) -> {
            PointerInfo a = MouseInfo.getPointerInfo();

            Point b = a.getLocation();
            int x = (int) b.getX() - 500;
            x /= (board.getModel().getSize() / 30);
            int y = (int) b.getY() - 75;
            y /= (board.getModel().getSize() / 30);

            if (!board.getModel().getInfo(x, y).equals("")) {
                ToolTipManager.sharedInstance().setEnabled(true);
                board.setToolTipText(board.getModel().getInfo(x, y));
            } else {
                ToolTipManager.sharedInstance().setEnabled(false);
            }

            ticks--;
            if (!board.getModel().isOver()) {
                int newtime = (ticks + 1) / 2;
                if (newtime == 0) {
                    newRound();
                } else {
                    //timeSec = (int) (System.currentTimeMillis() - time) / 1000;
                    //timeLabel.setText("Time " + timeSec + " s,");
                    timeAndRoundLabel.setText("Time left: " + newtime + " s, round: " + board.getModel().getRound());
                    activePlayerLabel.setText("Active player: "
                            + board.getModel().getPlayers()[board.getModel().getActivePlayer()].getName());
                }

                board.repaint();

            } else {
                gameOver();
            }

        });

        timer.start();

        activePlayerPanelSetter();
    }

    public int getTicks() {
        return ticks;
    }

    public void simulation() {
        for (int q = 0; q < 2; q++) {
            if (board.getModel().getPlayers()[q].getUnits().size() != 0) {
                ArrayList<Unit> units=board.getModel().getPlayers()[q].getUnits();
                for (int i = 0; i < units.size(); i++) {
                    ArrayList<Node> way = units.get(i).getWay();
                    for (int j = 0; j < units.get(i).getDistance() && !way.isEmpty(); j++) {
                        Node next = way.get(0);
                        /*while(u.getX()!=next.getX()*(board.getModel().getSize()/30)
                        &&u.getY()!=next.getY()*(board.getModel().getSize()/30)){
                    int dirX=u.getX()<next.getX()*(board.getModel().getSize()/30) ? 1:
                            u.getX()==next.getX()*(board.getModel().getSize()/30) ? 0: -1;
                    int dirY=u.getY()<next.getY()*(board.getModel().getSize()/30) ? 1:
                            u.getY()==next.getY()*(board.getModel().getSize()/30) ? 0: -1;
                    u.setX(u.getX()+dirX);
                    u.setY(u.getY()+dirY);
                }
                         */

                        units.get(i).setX(next.getX() * (board.getModel().getSize() / 30));
                        units.get(i).setY(next.getY() * (board.getModel().getSize() / 30));
                         board.repaint();

                        way.remove(0);
                    }
                        ArrayList<Tower> towersNearby = board.getModel().towersNearby(q, units.get(i));
                        System.out.println(towersNearby.toString());
                        ArrayList<Tower> newEnemyTowerList = new ArrayList<>();

                        if (units.get(i).getType() == "Destroyer" && towersNearby.size() > 0) {
                            int rand = (int) (Math.random() * 2);
                            if (rand == 1) //destroyer attacks neraby towers (direct next to it) with full power, then disappears
                            {
                                //destroyer deals 50 damage when attacking towers
                                for (Tower t : board.getModel().getPlayers()[(q + 1) % 2].getTowers()) {
                                    if (t.getHp() > 50) {
                                        t.setHp(t.getHp() - 50);
                                        newEnemyTowerList.add(t);
                                    }
                                    //adding towers to a temprary list, this list will be the new tower list of enemy player

                                }
                                System.out.println(newEnemyTowerList.toString());
                                board.getModel().getPlayers()[(q + 1) % 2].setTowers(newEnemyTowerList);
                                board.getModel().getPlayers()[q].deleteUnit(units.get(i));
                                if(i>0) {--i;}

                            }
                        }
                       

                        board.repaint();
                        startAnimation();
                    }
                
                //u.setWay(board.getModel().getPlayers()[q].findWay());
            }
            for (int i = 0; i < board.getModel().getPlayers()[q].getUnits().size(); i++) {
                ArrayList<Node> way = board.getModel().getPlayers()[q].getUnits().get(i).getWay();
                if (way.isEmpty()) {
                    if (board.getModel().getPlayers()[Math.abs(q - 1)].getCastle().getHp()
                            - board.getModel().getPlayers()[q].getUnits().get(i).getPower() > 0) {
                        board.getModel().getPlayers()[Math.abs(q - 1)].getCastle().setHp(
                                board.getModel().getPlayers()[Math.abs(q - 1)].getCastle().getHp()
                                - board.getModel().getPlayers()[q].getUnits().get(i).getPower());
                        board.repaint();
                        playerDataUpdate();
                    } else {
                        board.getModel().setOver(true);
                    }
                    board.getModel().getPlayers()[q].deleteUnit(board.getModel().getPlayers()[q].getUnits().get(i));
                    --i;
                }
            }

            //remove units who reached Castle after dealing damage to it
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // your coded here
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
        board.getModel().setSelectables();
    }

    /*
    now it's the other player's round
    both players get 100 coins
    after every 2 rounds, the simulation starts
     */
    public void newRound() {

        Model model = board.getModel();
        model.setSelectables(new ArrayList<>());
        model.setRound(model.getRound() + 1);
        if (model.getRound() == 1 || model.getRound() == 2) {
            ticks = 120;
        } else {
            ticks = 60;
        }
        //rounds.setText("Round: " + model.getRound());
        timeAndRoundLabel.setText("Time left: " + (ticks + 1) / 2 + " s, round: " + board.getModel().getRound());

        model.setActivePlayer((1 + model.getActivePlayer()) % 2);
        playerDataUpdate();
        activePlayerPanelSetter();
        if (model.getRound() % 2 == 1) {
            model.getPlayers()[0].setMoney(model.getPlayers()[0].getMoney() + 100);
            model.getPlayers()[1].setMoney(model.getPlayers()[1].getMoney() + 100);
            playerDataUpdate();
            simulation();

            for (int q = 0; q < 2; q++) {

                int defender = Math.abs(q * 4 - 4);

                for (Unit u : model.getPlayers()[q].getUnits()) {
                    int minDistance = 10000;
                    ArrayList<Node> bestway = new ArrayList<Node>();

                    for (int i = 0; i < 4; i++) {
                        ArrayList<String> wayString = model.getPlayers()[q].findWay(u.getX() / (model.getSize() / 30), u.getY() / (model.getSize() / 30),
                                model.getCastleCoordinates()[i + defender][0], model.getCastleCoordinates()[i + defender][1], model.getPlayers()[q].getDifficulty(model, u.getType()));

                        if (minDistance > board.getModel().wayDiff(q, wayString, u.getType())) {
                            ArrayList<Node> newWay = board.getModel().getPlayers()[q].convertWay(wayString);
                            minDistance = board.getModel().wayDiff(q, wayString, u.getType());
                            bestway = newWay;
                        }
                    }
                    u.setWay(bestway);
                    //System.out.println(bestway);
                }
            }
        }
        ArrayList<Tower> p1Towers = board.getModel().getPlayers()[0].getTowers();
        for (int i = p1Towers.size() - 1; i >= 0; i--) {
            if (p1Towers.get(i).getDemolishedIn() != -1) {
                p1Towers.get(i).setDemolishedIn(1);//demolishedIn - 1
                if (p1Towers.get(i).getDemolishedIn() == 0) {
                    board.getModel().getTerrain().remove(p1Towers.get(i));
                    board.getModel().getPosition()[(p1Towers.get(i).getX() / 30)][(p1Towers.get(i).getY() / 30)] = 'F';
                    p1Towers.remove(i);
                }
            }
        }
        board.getModel().getPlayers()[0].setTowers(p1Towers);
        ArrayList<Tower> p2Towers = board.getModel().getPlayers()[1].getTowers();
        for (int i = p2Towers.size() - 1; i >= 0; i--) {
            if (p2Towers.get(i).getDemolishedIn() != -1) {
                p2Towers.get(i).setDemolishedIn(1);//demolishedIn - 1
                if (p2Towers.get(i).getDemolishedIn() == 0) {
                    board.getModel().getTerrain().remove(p2Towers.get(i));
                    board.getModel().getPosition()[(p2Towers.get(i).getX() / 30)][(p2Towers.get(i).getY() / 30)] = 'F';
                    p2Towers.remove(i);
                }
            }
        }
        board.getModel().getPlayers()[1].setTowers(p2Towers);
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

        board.getModel().saveData(filename);

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
        if (board.getModel().getActivePlayer() == 0) {
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
                + "<br>Money: " + p1.getMoney()
                + "<br>Castlye's health: " + p1.getCastle().getHp()
                + "<br> Towers"
                + "</html>"
        );

        p2Data.setText(
                "<html>"
                + p2.getName()
                + "<br>Money: " + p2.getMoney()
                + "<br>Castle's health: " + p2.getCastle().getHp()
                + "<br> Towers"
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
