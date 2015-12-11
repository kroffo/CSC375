import java.util.concurrent.RecursiveAction;

public class UpdateTemp extends RecursiveAction {
    final Region[][] grid;
    final int vLow;
    final int vHigh;
    final int xLow;
    final int xHigh;
    
    public UpdateTemp(Region[][] g, int xl, int xh, int vl, int vh) {
        grid = g;
        xLow = xl;
        xHigh = xh;
        vLow = vl;
        vHigh = vh;
    }

    protected void compute() {
	//	System.out.print("*");
        if (xHigh - xLow <= 2) {
            if (vHigh - vLow <= 2) { // small enough!
                for (int i = xLow; i < xHigh; i++) {
                    for (int j = vLow; j < vHigh; j++) {
                        grid[i][j].nextTemp();
                    }
                }
            } else { // Too large vertically
                int mid = ((vHigh + vLow) / 2);
                invokeAll(new UpdateTemp(grid, xLow, xHigh, vLow, mid),
                          new UpdateTemp(grid, xLow, xHigh, mid, vHigh));
            }
        } else { // Too large horizontally
            int mid = ((xHigh + xLow) / 2);
            invokeAll(new UpdateTemp(grid, xLow, mid, vLow, vHigh),
                      new UpdateTemp(grid, mid, xHigh, vLow, vHigh));
        }
    }
}
