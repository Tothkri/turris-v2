package view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import model.*;

public class StartScreen extends JPanel {

    private final JButton startButton;
    private final JButton exitButton;
    private final JButton loadButton;
    private final JTextField p1Name;
    private final JTextField p2Name;
    private final JRadioButton map1;
    private final JRadioButton map2;
    private final JRadioButton map3;

    private final int width;
    private final int height;

    private final Image Lake = new ImageIcon("src/res/Lake.png").getImage();
    private final Image Mountain = new ImageIcon("src/res/Mountain.png").getImage();
    private final Image Castle = new ImageIcon("src/res/Castle.png").getImage();

    private String mapVal = "";

    /**
     * Játék kezdőképernyője
     * @param width
     * @param height
     */
    public StartScreen(int width, int height) {
        super();
        this.width = width;
        this.height = height;
        startButton = new JButton("Start");
        exitButton = new JButton("Exit");
        loadButton = new JButton("Load Game");
        p1Name = new JTextField(15);
        p2Name = new JTextField(15);
        map1 = new JRadioButton("Map 1 (Mountains & lakes)");
        map2 = new JRadioButton("Map 2 (Mountains mostly)");
        map3 = new JRadioButton("Map 3 (Lakes mostly)");
        setPanels();
    }

    /**
     * Játék betöltése
     * @param gw
     * @param fileName
     * @throws java.io.IOException
     */
    public void loadGame(GameWindow gw, String fileName) throws IOException {
        int size = height - 150;
        Model model = new Model(size);

        File myObj = new File(fileName);
        Scanner myReader = new Scanner(myObj);

        Castle castle1 = new Castle();
        Castle castle2 = new Castle();
        int castleCount = 0;

        for (int i = 0; i < 30; i++) {
            String data = myReader.nextLine();
            for (int j = 0; j < 30; j++) {
                model.getPosition()[j][i] = data.charAt(i);

                if (data.charAt(j) == 'C') {
                    castleCount++;
                    model.getPosition()[j][i] = 'C';
                }

                if (castleCount == 3) {
                    castle1 = new Castle("blue", j * (size / 30), 2 * (size / 30), (size / 15), (size / 15), Castle, 300);
                }

                if (castleCount == 7) {
                    castle2 = new Castle("red", j * (size / 30), 26 * (size / 30), (size / 15), (size / 15), Castle, 300);
                }

                if (data.charAt(j) == 'F') {
                    model.getPosition()[j][i] = 'F';
                } else if (data.charAt(j) == 'M') {
                    model.getPosition()[j][i] = 'M';
                    model.addTerrainElement(new Mountain(j * (size / 30), i * (size / 30), (size / 30), (size / 30), Mountain));
                } else if (data.charAt(j) == 'L') {
                    model.getPosition()[j][i] = 'L';
                    model.addTerrainElement(new Lake(j * (size / 30), i * (size / 30), (size / 30), (size / 30), Lake));
                } else if (data.charAt(j) == 'T') {
                    model.getPosition()[j][i] = 'T';
                } else if (data.charAt(j) == 'D') {
                    model.getPosition()[j][i] = 'D';
                }
            }
        }

        /**
         * Térkép kiválasztása, kezdő játékos kiválasztása, játékosok neveinek
         * beállítása, játékosok pénzeinek kiosztása, kastélyok lehelyezése,
         * terepakadályok lehelyezése, jelenlegi kör beállítása
         */
        model.setMap(Integer.parseInt(myReader.nextLine()));
        model.setActivePlayer(Integer.parseInt(myReader.nextLine()));
        model.setRound(Integer.parseInt(myReader.nextLine()));
        int ticks = Integer.parseInt(myReader.nextLine());
        model.setCastleCords(castle1, castle2);
        model.addTerrainElement(castle1);
        model.addTerrainElement(castle2);

        myReader.nextLine();
        String name = myReader.nextLine();
        int money = Integer.parseInt(myReader.nextLine());
        model.getPlayers()[0] = new Player(money, name);
        model.getPlayers()[0].setCastle(castle1);
        model.getPlayers()[0].getCastle().setHp(Integer.parseInt(myReader.nextLine()));

        String data = myReader.nextLine();
        while (!data.equals("p:")) {
            if (!data.equals("")) {
                String[] arr;
                arr = data.split(" ");
                if (arr[0].equals("T")) {
                    switch (arr[1]) {
                        case "Rapid":
                            Rapid rt = (Rapid) setTower(arr);
                            model.getPlayers()[0].addTower(rt);
                            model.addTerrainElement(rt);
                            break;
                        case "Sniper":
                            Sniper st = (Sniper) setTower(arr);
                            model.getPlayers()[0].addTower(st);
                            model.addTerrainElement(st);
                            break;
                        default:
                            Fortified ft = (Fortified) setTower(arr);
                            model.getPlayers()[0].addTower(ft);
                            model.addTerrainElement(ft);
                            break;
                    }
                } else if (arr[0].equals("U")) {
                    switch (arr[6]) {
                        case "Fighter":
                            Fighter fu = (Fighter) setUnit(arr);
                            model.getPlayers()[0].addUnits(fu);
                            break;
                        case "Diver":
                            Diver du = (Diver) setUnit(arr);
                            model.getPlayers()[0].addUnits(du);
                            break;
                        case "Climber":
                            Climber cu = (Climber) setUnit(arr);
                            model.getPlayers()[0].addUnits(cu);
                            break;
                        case "Destroyer":
                            Destroyer deu = (Destroyer) setUnit(arr);
                            model.getPlayers()[0].addUnits(deu);
                            break;
                        default:
                            General gu = (General) setUnit(arr);
                            model.getPlayers()[0].addUnits(gu);
                            break;
                    }
                }
            }
            data = myReader.nextLine();
        }
        name = myReader.nextLine();

        money = Integer.parseInt(myReader.nextLine());
        model.getPlayers()[1] = new Player(money, name);
        model.getPlayers()[1].setCastle(castle2);
        model.getPlayers()[1].getCastle().setHp(Integer.parseInt(myReader.nextLine()));
        data = myReader.nextLine();

        while (!data.equals("")) {
            String[] arr;
            arr = data.split(" ");
            if (arr[0].equals("T")) {
                switch (arr[1]) {
                    case "Rapid":
                        Rapid rt = (Rapid) setTower(arr);
                        rt.setColor("red");
                        model.getPlayers()[1].addTower(rt);
                        model.addTerrainElement(rt);
                        break;
                    case "Sniper":
                        Sniper st = (Sniper) setTower(arr);
                        st.setColor("red");
                        model.getPlayers()[1].addTower(st);
                        model.addTerrainElement(st);
                        break;
                    default:
                        Fortified ft = (Fortified) setTower(arr);
                        ft.setColor("red");
                        model.getPlayers()[1].addTower(ft);
                        model.addTerrainElement(ft);
                        break;
                }
            } else if (arr[0].equals("U")) {
                switch (arr[6]) {
                    case "Fighter":
                        Fighter fu = (Fighter) setUnit(arr);
                        model.getPlayers()[1].addUnits(fu);
                        break;
                    case "Diver":
                        Diver du = (Diver) setUnit(arr);
                        model.getPlayers()[1].addUnits(du);
                        break;
                    case "Climber":
                        Climber cu = (Climber) setUnit(arr);
                        model.getPlayers()[1].addUnits(cu);
                        break;
                    case "Destroyer":
                        Destroyer deu = (Destroyer) setUnit(arr);
                        model.getPlayers()[1].addUnits(deu);
                        break;
                    default:
                        General gu = (General) setUnit(arr);
                        model.getPlayers()[1].addUnits(gu);
                        break;
                }
            }
            data = myReader.nextLine();
        }
        Board bd = new Board(width, height);
        bd.setModel(model);
        gw.setBoard(bd);
        gw.setModel(model);
        gw.setTicks(ticks);
    }

    /**
     * tornyok betöltése
     * @param arr
     * @return
     */
    public Tower setTower(String[] arr) {
        String png = arr[1] + arr[7] + arr[2];
        if (!arr[12].equals("-1")) {
            png = "Destroyed";
        }
        Tower t;
        switch (arr[1]) {
            case "Rapid":
                t = new Rapid(arr[1], arr[2], Integer.parseInt(arr[3]), Integer.parseInt(arr[4]), Double.parseDouble(arr[5]),
                        Integer.parseInt(arr[6]), Integer.parseInt(arr[7]), Integer.parseInt(arr[8]), Integer.parseInt(arr[9]), Integer.parseInt(arr[10]),
                        Integer.parseInt(arr[11]), new ImageIcon("src/res/" + png + ".png").getImage());
                break;
            case "Sniper":
                t = new Sniper(arr[1], arr[2], Integer.parseInt(arr[3]), Integer.parseInt(arr[4]), Double.parseDouble(arr[5]),
                        Integer.parseInt(arr[6]), Integer.parseInt(arr[7]), Integer.parseInt(arr[8]), Integer.parseInt(arr[9]), Integer.parseInt(arr[10]),
                        Integer.parseInt(arr[11]), new ImageIcon("src/res/" + png + ".png").getImage());
                break;
            default:    //Fortified
                t = new Fortified(arr[1], arr[2], Integer.parseInt(arr[3]), Integer.parseInt(arr[4]), Double.parseDouble(arr[5]),
                        Integer.parseInt(arr[6]), Integer.parseInt(arr[7]), Integer.parseInt(arr[8]), Integer.parseInt(arr[9]), Integer.parseInt(arr[10]),
                        Integer.parseInt(arr[11]), new ImageIcon("src/res/" + png + ".png").getImage());
                break;
        }
        if (Integer.parseInt(arr[12]) == -1) {
            t.setDemolishedIn(0);//-1
        } else {
            t.setDemolishedIn((Integer.parseInt(arr[12]) + 1) * -1);
        }
        return t;
    }

    /**
     * egységek betöltése
     * @param arr
     * @return
     */
    public Unit setUnit(String[] arr) {
        Unit u;
        int size = height - 150;
        switch (arr[6]) {
            case "Fighter":
                u = new Fighter(arr[1], Integer.parseInt(arr[7]), Integer.parseInt(arr[2]), Integer.parseInt(arr[3]), (size / 30), (size / 30),
                        new ImageIcon("src/res/" + arr[6] + arr[1] + ".png").getImage());
                break;
            case "Diver":
                u = new Diver(arr[1], Integer.parseInt(arr[7]), Integer.parseInt(arr[2]), Integer.parseInt(arr[3]), (size / 30), (size / 30),
                        new ImageIcon("src/res/" + arr[6] + arr[1] + ".png").getImage());
                break;
            case "Climber":
                u = new Climber(arr[1], Integer.parseInt(arr[7]), Integer.parseInt(arr[2]), Integer.parseInt(arr[3]), (size / 30), (size / 30),
                        new ImageIcon("src/res/" + arr[6] + arr[1] + ".png").getImage());
                break;
            case "Destroyer":
                u = new Destroyer(arr[1], Integer.parseInt(arr[7]), Integer.parseInt(arr[2]), Integer.parseInt(arr[3]), (size / 30), (size / 30),
                        new ImageIcon("src/res/" + arr[6] + arr[1] + ".png").getImage());
                break;
            default:
                u = new General(arr[1], Integer.parseInt(arr[7]), Integer.parseInt(arr[2]), Integer.parseInt(arr[3]), (size / 30), (size / 30),
                        new ImageIcon("src/res/" + arr[6] + arr[1] + ".png").getImage());
                break;
        }
        return u;
    }

    /**
     * pálya típus képének beállítása
     * @param mf
     * @param ms
     * @param mt
     */
    public void getMapPickVal(Boolean mf, Boolean ms, Boolean mt) {
        if (mf) {
            mapVal = "src/res/mnl_map.png";
            repaint();
        } else if (ms) {
            mapVal = "src/res/mm_map.png";
            repaint();
        } else {
            mapVal = "src/res/ml_map.png";
            repaint();
        }
    }

    /**
     * grafikus rész
     * @param grph
     */
    @Override
    protected void paintComponent(Graphics grph) {
        super.paintComponent(grph);
        Graphics2D grphcs2 = (Graphics2D) grph;
        grph.drawImage(new ImageIcon("src/res/StartScreen Background.png").getImage(), 0, 0, 1920, 1080, null);
        Color veryLightGray = new Color(220, 220, 220);

        grph.setColor(Color.DARK_GRAY);
        grphcs2.fillRoundRect(570, 12, 780, 157, 50, 50);   //Game name LABEL
        grphcs2.fillRoundRect(135, 209, 750, 860, 50, 50);  //Compl. Left Side
        grphcs2.fillRoundRect(1035, 850, 750, 220, 50, 50); //Start game
        grphcs2.fillRoundRect(1065, 209, 690, 620, 50, 50); //map overview bot.

        grph.setColor(Color.LIGHT_GRAY);
        grphcs2.fillRoundRect(580, 21, 760, 138, 30, 30);   //Game name LABEL
        grphcs2.fillRoundRect(145, 219, 730, 125, 30, 30);  //Player name LABEL 
        grphcs2.fillRoundRect(145, 509, 730, 150, 30, 30);  //Map name LABEL
        grphcs2.fillRoundRect(145, 860, 730, 155, 30, 30);  //Load game LABEL
        grphcs2.fillRoundRect(1045, 860, 730, 150, 30, 30); //Start game LABEL
        
        grph.setColor(veryLightGray);
        grphcs2.fillRect(145, 319, 730, 90);
        grphcs2.fillRoundRect(145, 370, 730, 125, 30, 30);
        grphcs2.fillRect(145, 609, 730, 130);
        grphcs2.fillRoundRect(145, 720, 730, 130, 30, 30);
        grphcs2.fillRect(145, 979, 730, 40);
        grphcs2.fillRoundRect(145, 995, 730, 65, 30, 30);
        grphcs2.fillRect(1045, 979, 730, 40);               //Start game lower
        grphcs2.fillRoundRect(1045, 995, 730, 65, 30, 30);  //Start game lower
        grphcs2.fillRoundRect(1075, 220, 670, 600, 30, 30); //map overview mid

        grph.setColor(Color.DARK_GRAY);
        grphcs2.fillRect(1145, 260, 530, 530);

        grph.drawImage(new ImageIcon(mapVal).getImage(), 1155, 269, 511, 511, null);
    }

    /**
     * Játék kezdőoldalának vizuális beállítása
     */
    public void setPanels() {
        this.setPreferredSize(new Dimension(width, height));
        this.setLayout(new GridBagLayout());
        GridBagConstraints gridBConts       = new GridBagConstraints();
        GridBagConstraints gridBContsPlr    = new GridBagConstraints();
        GridBagConstraints gridBContsMap    = new GridBagConstraints();
        GridBagConstraints gridBContsStex   = new GridBagConstraints();

        JLabel gameName = new JLabel("Tower Defense Game");
        JLabel playerNames = new JLabel("Player Names");
        JLabel p1NameLabel = new JLabel("Player 1    ");
        JLabel p2NameLabel = new JLabel("Player 2    ");
        JLabel pickMap = new JLabel("Choose Map");
        JLabel loadGameLabel = new JLabel("Load Saved Game");
        JLabel startGameLabel = new JLabel("Start Game // Exit Game");

        JPanel gameNameRow = new JPanel();          //0,0
        JPanel playerNameLabelRow = new JPanel();   //0,1
        JPanel playerNameInputRow = new JPanel();   //0,2
        JPanel pickMapLabelRow = new JPanel();      //0,3
        JPanel pickMapRadioRow = new JPanel();      //0,4
        JPanel loadGameLabelRow = new JPanel();     //0,5
        JPanel loadGameButtonRow = new JPanel();    //0,6
        JPanel stexLabelRow = new JPanel();         //1,5
        JPanel stexButtonRow = new JPanel();        //1,6

        playerNameInputRow.setLayout(new GridBagLayout());
        pickMapRadioRow.setLayout(new GridBagLayout());

        Color veryLightGray = new Color(220, 220, 220);
        Color algaeGreen = new Color(105, 168, 120);
        Color cinnabar = new Color(227, 66, 52);
        
        try {
            //számoknak: poppins
            //szavaknak: nunito
            Font poppinsLight = Font.createFont(Font.TRUETYPE_FONT, new File("src/res/fonts/Poppins-Light.ttf"));
            Font nunitoLight = Font.createFont(Font.TRUETYPE_FONT, new File("src/res/fonts/Nunito-Light.ttf"));
            
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("src/res/fonts/Poppins-Light.ttf")));
            ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("src/res/fonts/Nunito-Light.ttf")));
            
            
            //Game Name LABEL
            gameNameRow.add(gameName);
            gameNameRow.setBorder(new EmptyBorder(30, 0, 0, 0));
            gameNameRow.setBackground(Color.LIGHT_GRAY);
            gameName.setFont(new Font("Calibri", Font.BOLD, 80));

            gridBConts.insets = new Insets(0, 0, 60, -705);
            gridBConts.gridx = 0;
            gridBConts.gridy = 0;
            this.add(gameNameRow, gridBConts);
            
            
            //Player Name LABEL
            playerNames.setFont(nunitoLight.deriveFont(70f));
            playerNameLabelRow.add(playerNames);
            playerNameLabelRow.setPreferredSize(new Dimension(700, 100));  //LABEL HEIGHT: 100
            playerNameLabelRow.setBorder(new EmptyBorder(0, -240, 0, 0));
            playerNameLabelRow.setBackground(Color.LIGHT_GRAY);

            gridBConts.insets = new Insets(0, -100, 0, 100);
            gridBConts.gridx = 0;
            gridBConts.gridy = 1;
            this.add(playerNameLabelRow, gridBConts);
            
            
            //Player Name INPUTS
            playerNameInputRow.setPreferredSize(new Dimension(700, 160));   //INPUT HEIGHT: 80 (PER INP.)
            playerNameInputRow.setBackground(veryLightGray);

            //p1 LABEL
            p1NameLabel.setFont(nunitoLight.deriveFont(45f));
            gridBContsPlr.insets = new Insets(5, -20, 0, 0);
            gridBContsPlr.gridx = 0;
            gridBContsPlr.gridy = 0;
            playerNameInputRow.add(p1NameLabel, gridBContsPlr);

            //p1 INPUT
            p1Name.setFont(nunitoLight.deriveFont(25f));
            p1Name.setPreferredSize(new Dimension(200, 50));
            p1Name.setHorizontalAlignment(JTextField.CENTER);
            gridBContsPlr.insets = new Insets(5, 0, 0, 0);
            gridBContsPlr.gridx = 1;
            gridBContsPlr.gridy = 0;
            playerNameInputRow.add(p1Name, gridBContsPlr);

            //p2 LABEL
            p2NameLabel.setFont(nunitoLight.deriveFont(45f));
            gridBContsPlr.insets = new Insets(10, -20, 0, 0);
            gridBContsPlr.gridx = 0;
            gridBContsPlr.gridy = 1;
            playerNameInputRow.add(p2NameLabel, gridBContsPlr);

            //p2 INPUT
            p2Name.setFont(nunitoLight.deriveFont(25f));
            p2Name.setPreferredSize(new Dimension(200, 50));
            p2Name.setHorizontalAlignment(JTextField.CENTER);
            gridBContsPlr.insets = new Insets(10, 0, 0, 0);
            gridBContsPlr.gridx = 1;
            gridBContsPlr.gridy = 1;
            playerNameInputRow.add(p2Name, gridBContsPlr);

            gridBConts.insets = new Insets(0, -100, 30, 100);
            gridBConts.gridx = 0;
            gridBConts.gridy = 2;
            this.add(playerNameInputRow, gridBConts);


            //Map LABEL
            pickMap.setFont(nunitoLight.deriveFont(70f));
            pickMapLabelRow.add(pickMap);
            pickMapLabelRow.setPreferredSize(new Dimension(700, 100));
            pickMapLabelRow.setBorder(new EmptyBorder(0, -300, 0, 0));
            pickMapLabelRow.setBackground(Color.LIGHT_GRAY);

            gridBConts.insets = new Insets(0, -100, 0, 100);
            gridBConts.gridx = 0;
            gridBConts.gridy = 3;
            this.add(pickMapLabelRow, gridBConts);


            ///Map selection RADIO
            pickMapRadioRow.setPreferredSize(new Dimension(700, 240));
            pickMapRadioRow.setBackground(veryLightGray);

            ButtonGroup group = new ButtonGroup();
            map1.setSelected(true);
            mapVal = "src/res/mnl_map.png";

            group.add(map1);
            group.add(map2);
            group.add(map3);

            //map1
            map1.setFont(nunitoLight.deriveFont(45f));
            map1.setFocusPainted(false);
            map1.setBackground(veryLightGray);

            map1.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    getMapPickVal(map1.isSelected(), map2.isSelected(), map3.isSelected());
                    repaint();
                }
            });

            gridBContsMap.insets = new Insets(0, -50, 0, 0);
            gridBContsMap.gridx = 0;
            gridBContsMap.gridy = 0;
            pickMapRadioRow.add(map1, gridBContsMap);

            //map2
            map2.setFont(nunitoLight.deriveFont(45f));
            map2.setFocusPainted(false);
            map2.setBackground(veryLightGray);

            map2.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    getMapPickVal(map1.isSelected(), map2.isSelected(), map3.isSelected());
                    repaint();
                }
            });

            gridBContsMap.insets = new Insets(10, -60, 0, 0);
            gridBContsMap.gridx = 0;
            gridBContsMap.gridy = 1;
            pickMapRadioRow.add(map2, gridBContsMap);

            //map3
            map3.setFont(nunitoLight.deriveFont(45f));
            map3.setFocusPainted(false);
            map3.setBackground(veryLightGray);

            map3.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    getMapPickVal(map1.isSelected(), map2.isSelected(), map3.isSelected());
                    repaint();
                }
            });

            gridBContsMap.insets = new Insets(10, -153, 0, 0);  //10, -137, 0, 0
            gridBContsMap.gridx = 0;
            gridBContsMap.gridy = 2;
            pickMapRadioRow.add(map3, gridBContsMap);

            gridBConts.insets = new Insets(0, -100, 30, 100);
            gridBConts.gridy = 4;
            this.add(pickMapRadioRow, gridBConts);


            //Load game LABEL
            loadGameLabel.setFont(nunitoLight.deriveFont(70f));

            loadGameLabelRow.setPreferredSize(new Dimension(700, 100));
            loadGameLabelRow.setBorder(new EmptyBorder(-10, -115, 0, 0));
            loadGameLabelRow.setBackground(Color.LIGHT_GRAY);
            loadGameLabelRow.add(loadGameLabel);

            gridBConts.insets = new Insets(0, -100, 0, 100);
            gridBConts.gridx = 0;
            gridBConts.gridy = 5;
            this.add(loadGameLabelRow, gridBConts);


            //Load game BUTTON
            Border loadBorder = BorderFactory.createLineBorder(Color.DARK_GRAY, 3);
            loadButton.setBorder(loadBorder);
            loadButton.setFont(nunitoLight.deriveFont(30f));
            loadButton.setPreferredSize(new Dimension(400, 55));
            loadButton.setVerticalAlignment(SwingConstants.CENTER);
            loadButton.setFocusPainted(false);

            loadGameButtonRow.setPreferredSize(new Dimension(700, 80)); //INPUT HEIGHT: 80 (PER INP.)
            loadGameButtonRow.setBackground(veryLightGray);
            loadGameButtonRow.setBorder(new EmptyBorder(8, 0, 0, 0));
            loadGameButtonRow.add(loadButton);

            gridBConts.insets = new Insets(0, -100, 0, 100);
            gridBConts.gridx = 0;
            gridBConts.gridy = 6;
            this.add(loadGameButtonRow, gridBConts);


            //StEx LABEL
            startGameLabel.setFont(nunitoLight.deriveFont(66f));
            stexLabelRow.setPreferredSize(new Dimension(700, 100));
            stexLabelRow.setBorder(new EmptyBorder(-8, 0, 0, 0));
            stexLabelRow.setBackground(Color.LIGHT_GRAY);
            stexLabelRow.add(startGameLabel);

            gridBConts.insets = new Insets(0, 100, 0, -100);
            gridBConts.gridx = 1;
            gridBConts.gridy = 5;
            this.add(stexLabelRow, gridBConts);


            //StEx BUTTON
            stexButtonRow.setPreferredSize(new Dimension(700, 80));   //INPUT HEIGHT: 80 (PER INP.)
            stexButtonRow.setBorder(new EmptyBorder(8, 0, 0, 0));
            stexButtonRow.setBackground(veryLightGray);

            //Start BUTTON
            Border startBorder = BorderFactory.createLineBorder(algaeGreen, 3);
            startButton.setBorder(startBorder);
            startButton.setFont(nunitoLight.deriveFont(30f));
            startButton.setPreferredSize(new Dimension(300, 55));
            startButton.setVerticalAlignment(SwingConstants.CENTER);
            startButton.setFocusPainted(false);

            gridBContsStex.insets = new Insets(0, 0, 0, 0);
            gridBContsStex.gridx = 0;
            gridBContsStex.gridy = 0;
            stexButtonRow.add(startButton, gridBContsStex);

            //Exit BUTTON
            Border exitBorder = BorderFactory.createLineBorder(cinnabar, 3);
            exitButton.setBorder(exitBorder);
            exitButton.setFont(nunitoLight.deriveFont(30f));
            exitButton.setPreferredSize(new Dimension(300, 55));
            exitButton.setVerticalAlignment(SwingConstants.CENTER);
            exitButton.setFocusPainted(false);

            exitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });

            gridBContsStex.insets = new Insets(0, 0, 0, 0);
            gridBContsStex.gridx = 0;
            gridBContsStex.gridy = 1;
            stexButtonRow.add(exitButton, gridBContsStex);

            gridBConts.insets = new Insets(0, 100, 0, -100);
            gridBConts.gridx = 1;
            gridBConts.gridy = 6;
            this.add(stexButtonRow, gridBConts);
            
        } catch(IOException | FontFormatException e) {
            //System.out.println("Exception thrown :" + e);
        }
    }

    /**
     * nevek helyesek-e
     * @param
     * @return f
     */
    public boolean isCorrect() {
        Color pastelRed = new Color(255, 105, 97);
        boolean f = true;
        
        if (p1Name.getText().equals("")) {
            p1Name.setBackground(pastelRed);
            Border border = BorderFactory.createLineBorder(Color.RED, 3);
            p1Name.setBorder(border);
            p1Name.setText("");
            f = false;
        }

        if (p2Name.getText().equals("")) {
            p2Name.setBackground(pastelRed);
            Border border = BorderFactory.createLineBorder(Color.RED, 3);
            p2Name.setBorder(border);
            p2Name.setText("");
            f = false;
        }
        
        if (p1Name.getText().equals(p2Name.getText())) {
            p1Name.setBackground(pastelRed);
            Border p1Border = BorderFactory.createLineBorder(Color.RED, 3);
            p1Name.setBorder(p1Border);
            p2Name.setBackground(pastelRed);
            Border p2Border = BorderFactory.createLineBorder(Color.RED, 3);
            p2Name.setBorder(p2Border);
            return false;
        }
        
        return f;
    }

    /**
     * Rádiógombok beállítása
     * @return 
     */
    public int getSelectedRadioButton() {
        if (map1.isSelected()) {
            return 1;
        } else if (map2.isSelected()) {
            return 2;
        }
        return 3;
    }

    /**
     * Getterek, setterek
     * @return 
     */
    public JButton getStartButton() {
        return startButton;
    }

    public JButton getLoadButton() {
        return loadButton;
    }

    public JTextField getP1Name() {
        return p1Name;
    }

    public JTextField getP2Name() {
        return p2Name;
    }
}
