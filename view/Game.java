package view;

import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;

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
         * megnézi, hogy helyesek-e a felhasználók által beírt nevek 
         * a textboxok nem maradhatnak üresen
         */
        ss.getStartButton().addActionListener((ActionEvent ae) -> {
            if (!ss.isCorrect()) {
                return;
            }
            gw = new GameWindow(width, height, ss.getP1Name().getText(), ss.getP2Name().getText(), ss.getSelectedRadioButton());
            container.add(gw, "2");
            cardLayout.show(container, "2");
        });

        /**
         * mentett játék betöltésére használt gomb
         */
        ss.getLoadButton().addActionListener(ae ->{
            gw = new GameWindow();
            ss.loadGame(gw);
            gw.constructor(width,height);
            container.add(gw,"2");
            cardLayout.show(container, "2");
            gw.playerDataUpdate();
            gw.getRounds().setText("Round: " + gw.getBoard().getModel().getRound());
        });

        frame.setResizable(false);
        frame.getContentPane().add(container);
        frame.pack();
        frame.setVisible(true);
    }
}
