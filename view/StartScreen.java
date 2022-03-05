package view;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class StartScreen extends JPanel {
    
    JButton teszt; 
    
    public StartScreen(int width, int height)
    {
        super();
        this.setPreferredSize(new Dimension(width,height));
        this.setLayout(new GridBagLayout());
        
        teszt = new JButton("Starscreen button");
        this.add(teszt);
    }
    public void start(int selectedMap, String p1Name,String p2Name)
    {
        
    }
    public void loadGame()
    {
        
    }
    public JButton getTeszt()
    {
        return teszt;
    }
}
