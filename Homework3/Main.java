import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.awt.*;
import java.awt.event.*;
import javax.swing.SwingUtilities;
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

public class Main {

    public static double[] metals = new double[3];
    public static long[][] previousTemperatures1;
    public static long[][] previousTemperatures2;
    public static boolean converged = false;
    public static boolean swapGrid = false;
    private static int height;
    private static int maxIter = 1000000000;

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        boolean validInput = false;
        for (int i = 0; i < metals.length; i++) {
            do {
                System.out.println("Enter a thermal constant for metal " + (i + 1) + ":");
                if (sc.hasNextDouble()) {
                    metals[i] = sc.nextDouble();
                    System.out.println(metals[i]);
                    validInput = true;
                } else {
                    sc.nextLine();
                    System.out.println("Please enter a number.");
                }
            } while (!validInput);
            validInput = false;
        }
        do {
            System.out.println("Enter the height of the grid:");
            if (sc.hasNextInt()) {
                height = sc.nextInt();
                validInput = true;
            } else {
                sc.nextLine();
                System.out.println("Please enter an integer.");
            }
        } while (!validInput);
        validInput = false;

        final Region[][] grid = new Region[2*height][height];
        previousTemperatures1 = new long[2*height][height];
        previousTemperatures2 = new long[2*height][height];

        for (int i = 0; i < 2*height; i++) {
            for (int j = 0; j < height; j++) {
                double p1 = ThreadLocalRandom.current().nextDouble(1.0);
                double p2 = ThreadLocalRandom.current().nextDouble(1.0 - p1);
                double p3 = 1.0 - p1 - p2;
                if ((i > 0 || j > 0) && ((i < 2*height - 1) || j < height-1))
                    grid[i][j] = new Region(p1, p2, p3, i, j, false);
                else
                    grid[i][j] = new Region(p1, p2, p3, i, j, true);
            }
        }
        
        for (int i = 0; i < 2*height; i++) {
            for (int j = 0; j < height; j++) {
                Region[] neighbors;
                int h = height;
                if (i > 0) { // Not a left edge
                    if (j > 0) { // Not a top edge
                        if (i < 2*h - 1) { // Not a right edge
                            if (j < h-1) { // Not a bottom edge. MUST BE INNER PIECES!!!
                                neighbors = new Region[4];
                                neighbors[0] = grid[i][j-1];
                                neighbors[1] = grid[i][j+1];
                                neighbors[2] = grid[i-1][j];
                                neighbors[3] = grid[i+1][j];
                            } else { // bottom edges
                                neighbors = new Region[3];
                                neighbors[0] = grid[i][j-1];
                                neighbors[1] = grid[i+1][j];
                                neighbors[2] = grid[i-1][j];
                            }
                        } else if (j < h-1) { // right edges
                            neighbors = new Region[3];
                            neighbors[0] = grid[i][j+1];
                            neighbors[1] = grid[i][j-1];
                            neighbors[2] = grid[i-1][j];
                        } else { // The bottom right corner
                            neighbors = new Region[2];
                            neighbors[0] = grid[i][j-1];
                            neighbors[1] = grid[i-1][j];
                        }
                    } else if (i < 2*h - 1) { // Must be up side edges
                        neighbors = new Region[3];
                        neighbors[0] = grid[i+1][j];
                        neighbors[1] = grid[i-1][j];
                        neighbors[2] = grid[i][j+1];
                    } else { // Must be the upper right corner
                        neighbors = new Region[2];
                        neighbors[0] = grid[i][j+1];
                        neighbors[1] = grid[i-1][j];
                    }
                } else if (j > 0) { // Not the upper left corner
                    if (j < h-1) { // Must be a left edge
                        neighbors = new Region[3];
                        neighbors[0] = grid[i][j+1];
                        neighbors[1] = grid[i][j-1];
                        neighbors[2] = grid[i+1][j];
                    } else { // This only reached by bottom left corner
                        neighbors = new Region[2];
                        neighbors[0] = grid[i][j-1];
                        neighbors[1] = grid[i+1][j];
                    }
                } else { //This will only be reached by top left corner
                    neighbors = new Region[2];
                    neighbors[0] = grid[i][j+1];
                    neighbors[1] = grid[i+1][j];
                }
                grid[i][j].setNeighbors(neighbors);
            }
        }

        do {
            System.out.println("Enter the temperature of the region at (0,0):");
            if (sc.hasNextLong()) {
                grid[0][0].setTemp(sc.nextLong());
                validInput = true;
            } else {
                sc.nextLine();
                System.out.println("Please enter a number.");
            }
        } while (!validInput);
        validInput = false;
        do {
            System.out.println("Enter the temperature of the region at (" + (2*height-1) + "," + (height-1) + "):");
            if (sc.hasNextLong()) {
                grid[2*height-1][height-1].setTemp(sc.nextLong());
                validInput = true;
            } else {
                sc.nextLine();
                System.out.println("Please enter a number.");
            }
        } while (!validInput);
        validInput = false;

        Grid displayGrid = new Grid(grid);
        JFrame window = new JFrame();
        window.setSize(1200, 700);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.add(displayGrid);
        window.setVisible(true);

	setUpRecords(grid);
	displayGrid.repaintStart(grid);

                ForkJoinPool pool = new ForkJoinPool();
		UpdateTemp tempUpdate = new UpdateTemp(grid, 0, grid.length, 0, grid[0].length);
                for (int i = 0; i < maxIter; i++) {
		    if (converged) {
			System.out.println(i);
			System.out.println("Converged.");
			break;
		    }
		    converged = true;
		    tempUpdate.reinitialize();
                    pool.invoke(tempUpdate);
		    if (swapGrid) 
			swapGrid = false;
		    else
			swapGrid = true;
                }
                System.out.println("Done");
    }

    public static void setUpRecords(Region[][] grid) {
	for (int i = 0; i < 2*height; i++) {
            for (int j = 0; j < height; j++) {
		previousTemperatures1[i][j] = grid[i][j].getTemp();
		previousTemperatures2[i][j] = grid[i][j].getTemp();
	    }
	}
    }
}