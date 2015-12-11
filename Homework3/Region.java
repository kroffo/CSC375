import java.util.concurrent.ThreadLocalRandom;

public class Region {
    private double metal1percentage;
    private double metal2percentage;
    private double metal3percentage;
    private long temperature;
    private Region[] neighbors;
    private int xIndex;
    private int yIndex;
    private boolean source;
    
    public Region(double p1, double p2, double p3, int x, int y, boolean s) {
        metal1percentage = p1;
        metal2percentage = p2;
        metal3percentage = p3;
        source = s;
        xIndex = x;
        yIndex = y;
    }

    public void setNeighbors(Region[] neighbs) {
        neighbors = neighbs;
    }

    public long nextTemp() {
        if (source) {
            return temperature;
        }
	long[][] temps;
	if (Main.swapGrid)
	    temps = Main.previousTemperatures1;
	else
	    temps = Main.previousTemperatures2;
        long outerSum = 0;
        for (int metal = 0; metal < 3; metal++) {
            double thermalConstant = Main.metals[metal];
            long innerSum = 0;
            for (int neighb = 0; neighb < neighbors.length; neighb++) {
                innerSum += temps[neighbors[neighb].getX()][neighbors[neighb].getY()] * neighbors[neighb].getPercentage(metal);
            }
            outerSum += thermalConstant * innerSum;
        }
        temperature = outerSum / neighbors.length;
	if (temps[xIndex][yIndex] != temperature)
	    Main.converged = false;
	if (Main.swapGrid)
	    Main.previousTemperatures2[xIndex][yIndex] = temperature;
	else
	    Main.previousTemperatures1[xIndex][yIndex] = temperature;
        return temperature;
    }

    public long getTemp() {
        return temperature;
    }

    public int getX() {
        return xIndex;
    }

    public int getY() {
        return yIndex;
    }

    public void setTemp(long temp) {
        temperature = temp;
    }

    public double getPercentage(int metal) {
        switch(metal) {
        case 0: return metal1percentage;
        case 1: return metal2percentage;
        case 2: return metal3percentage;
        default: return 0;
        } 
    }



}
