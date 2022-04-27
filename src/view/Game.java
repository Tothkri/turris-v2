package view;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Game {

    private GameWindow gw;
    private StartScreen ss;

    private final JFrame frame;
    private final JPanel container;

    private final int width;
    private final int height;

    public Game() {
        width = 1920;
        height = 1080;

        frame = new JFrame("Tower Defense Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(true);
        frame.setVisible(true);
        container = new JPanel();
        CardLayout cardLayout = new CardLayout();

        container.setLayout(cardLayout);

        ss = new StartScreen(width, height);

        container.setLayout(cardLayout);
        container.add(ss, "1");

        /**
         * megnézi, hogy helyesek-e a felhasználók által beírt nevek a textboxok
         * nem maradhatnak üresen
         */
        ss.getStartButton().addActionListener((ActionEvent ae) -> {
            if (!ss.isCorrect()) {
                return;
            }
            gw = new GameWindow(width, height, ss.getP1Name().getText(), ss.getP2Name().getText(), ss.getSelectedRadioButton());
            container.add(gw, "2");
            cardLayout.show(container, "2");
            gw.getBackToMenuButton().addActionListener(action ->{
                ss.getP1Name().setText("");
                ss.getP2Name().setText("");
                container.add(ss, "1");
                gw.saveGame();
                cardLayout.show(container,"1");
            });
        });

        /**
         * mentett játék betöltésére használt gomb
         */
        ss.getLoadButton().addActionListener(ae -> {

            JFileChooser fc = new JFileChooser(System.getProperty("user.dir"));
            FileNameExtensionFilter filter = new FileNameExtensionFilter(".txt", "txt", "text");
            fc.setFileFilter(filter);
            int returnVal = fc.showOpenDialog(frame);
            String fileName = "";
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                fileName = fc.getSelectedFile().getName();
            }

            gw = new GameWindow();
            try {
                ss.loadGame(gw, fileName);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "No saved games found!", "Warning", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            gw.constructor(width, height);
            container.add(gw, "2");
            cardLayout.show(container, "2");
            gw.playerDataUpdate();
            gw.getBackToMenuButton().addActionListener(action ->{
                ss.getP1Name().setText("");
                ss.getP2Name().setText("");
                container.add(ss, "1");
                gw.saveGame();
                cardLayout.show(container,"1");
            });
        });

        frame.setResizable(false);
        frame.getContentPane().add(container);
        frame.pack();
        frame.setVisible(true);
    }

    public GameWindow getGw() {
        return gw;
    }

    public StartScreen getSs() {
        return ss;
    }
}
