package view;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import model.*;

public class Board extends JPanel {

    private Model model;
    private final int width;
    private final int height;

    /**
    * board kapott paraméterek szerinti beállítása
    * @param selectedMap
    * @param p1Name
    * @param p2Name
    * @param width
    * @param height
    */
    public Board(int selectedMap, String p1Name, String p2Name, int width, int height) {
        model = new Model(selectedMap, p1Name, p2Name, height - 150);
        this.setPreferredSize(new Dimension(height - 150, height - 150));
        this.width = width;
        this.height = height;
    }

    /**
    * board méretének beállítása
    * @param width
    * @param height
    */
    public Board(int width, int height){
        this.width = width;
        this.height = height;
        this.setPreferredSize(new Dimension(height - 150, height - 150));
    }

    /**
    * Getterek, setterek
    */
    public Model getModel() {
        return model;
    }

    public void setModel(Model m) {
        model = m;
    }

    /**
    * Sprite-ok kirajzolása a játékteren
    */
    @Override
    protected void paintComponent(Graphics grphcs) {
        //painting the whole board
        super.paintComponent(grphcs);
        Graphics2D g2 = (Graphics2D) grphcs;
        grphcs.drawImage(new ImageIcon("src/res/Background.png").getImage(), 0, 0, height - 150, height - 150, null);
        ArrayList<Sprite> terrain = model.getTerrain();

        ArrayList<Unit> p1units = model.getPlayers()[0].getUnits();
        ArrayList<Unit> p2units = model.getPlayers()[1].getUnits();
        ArrayList<Sprite> selectables = model.getSelectables();
        for (Sprite s : terrain) {
            s.draw(g2);
        }

        if (!selectables.isEmpty()) {
            for (Sprite s : selectables) {
                s.draw(g2);
            }
        }

        if (!p1units.isEmpty()) {
            for (Unit u : p1units) {
                u.draw(g2);
            }
        }

        if (!p2units.isEmpty()) {
            for (Unit u : p2units) {
                u.draw(g2);
            }
        }
    }
}
