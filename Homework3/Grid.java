
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.awt.Graphics2D;

public class Grid extends JPanel {

    private float maxTemp;
    private int boxWidth = 2;
    private int height;
    private int width;
    private Region[][] grid;
    private Point[][] fillCells;

    public Grid(Region[][] grid) {
        this.width = grid.length;
        this.height = grid[0].length;
        this.grid = grid;
        if (grid[0][0].getTemp() < grid[width-1][height-1].getTemp())
            maxTemp = grid[width-1][height-1].getTemp();
        else
            maxTemp = grid[0][0].getTemp();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        super.paintComponent(g);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int cellX = boxWidth + (i * boxWidth);
                int cellY = boxWidth + (j * boxWidth);
		float redness = (float)grid[i][j].getTemp()/maxTemp;
		if (redness > 1)
		    redness = 1;
                g.setColor(new Color(redness,(float)0,(float)0));
                g.fillRect(cellX, cellY, boxWidth, boxWidth);
            }
        }
    }

    public void repaintStart(Region[][] grid) {
	new Thread(() -> {
	    while(true) {
		try {
		    this.grid = grid;
		    repaint();
		    Thread.currentThread().sleep(100);
		} catch (InterruptedException e) {

		}
	    }
	}).start();
    }
}
