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
    
    private final int WIDTH = 1024;
    private final int HEIGHT = 768;
    
    public Game()
    {
        frame = new JFrame("Tower Defense Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        container = new JPanel();
        CardLayout cardLayout = new CardLayout();
        
        container.setLayout(cardLayout);
        
        ss = new StartScreen(WIDTH,HEIGHT);
        gw = new GameWindow(WIDTH,HEIGHT);
        
        container.setLayout(cardLayout);
        container.add(ss,"1");
        container.add(gw,"2");
        
        ss.getTeszt().addActionListener((ActionEvent ae) -> {
            cardLayout.show(container, "2");
        });
        
        gw.getTeszt().addActionListener((ActionEvent ae) -> {
            cardLayout.show(container, "1");
        });
        
        
        
        
        frame.setResizable(false);
        frame.getContentPane().add(container);
        frame.pack();
        frame.setVisible(true);
    }
}
