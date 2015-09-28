import java.awt.*;
import java.awt.event.*;
import javax.swing.SwingUtilities;
import java.util.Random;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class TrainStation extends Frame implements WindowListener,ActionListener {
    private final long startTime = System.currentTimeMillis()/1000;
    private int totalPassengers = 0;
    private TextField waiting = new TextField("Passengers Waiting: 0", 20);
    private TextField boarded = new TextField("Passengers Boarded: 0", 20);
    private TextField status = new TextField("Train Waiting...", 10);
    private TextField throughput = new TextField("Average Throughput (Passengers per second) : 0");
    private Button startTrain;
    private ArrayList<Passenger> passengers = new ArrayList<Passenger>();
    private AtomicInteger numBoarded = new AtomicInteger();
    private AtomicInteger numWaiting = new AtomicInteger();
    private AtomicInteger attemptingToBoard = new AtomicInteger();
    private volatile boolean doorsClosed = false;
    private Train train1 = new Train(200);
    public static void main(String[] args) {
        TrainStation myWindow = new TrainStation("Ye Olde Train Station");
        myWindow.setSize(350,100);
        myWindow.setVisible(true);
    }

    private void updateNumbers() {
        waiting.setText("Passengers Waiting: " + numWaiting.get());
        boarded.setText("Passengers Boarded: " + numBoarded.get());
        long time = (System.currentTimeMillis()/1000) - startTime;
        if (time > 0) {
            long rate = totalPassengers / time;
            throughput.setText("Average Throughput (Passengers per second) : " + rate);
        }
    }
    
    private void addPassenger(String type) {
        if (type.equals("waiting")) {
            numWaiting.incrementAndGet();
        } else { // must be boarded
            numBoarded.incrementAndGet();
        }
    }

    private void removePassenger(String type) {
        if (type.equals("waiting")) {
            numWaiting.decrementAndGet();
        } else { // must be boarded
            numBoarded.decrementAndGet();
        }
    }

    public TrainStation(String title) {
        super(title);
        setLayout(new FlowLayout());
        addWindowListener(this);
        startTrain = new Button("Start Train");
        status.setEditable(false);
        waiting.setEditable(false);
        boarded.setEditable(false);
        throughput.setEditable(false);
        add(waiting);
        add(boarded);
        add(startTrain);
        add(status);
        add(throughput);
        startTrain.addActionListener(this);
        new Thread(() -> {
                Random r = new Random();
                while(true) {
                    if (r.nextInt(10000000) == 22) {
                        Passenger p = new Passenger();
                        passengers.add(p);
                        addPassenger("waiting");
                        updateNumbers();
                        new Thread(() -> {
                                while(doorsClosed) {
                                    try {
                                        Thread.currentThread().sleep(100);
                                    } catch (InterruptedException ex) {
                                        Thread.currentThread().interrupt();
                                    }
                                }
                                attemptingToBoard.incrementAndGet();
                                while(!train1.addPassenger(p)) {
                                    if (doorsClosed) {
                                        attemptingToBoard.decrementAndGet();
                                        while(doorsClosed) {
                                            try {
                                                Thread.currentThread().sleep(100);
                                            } catch (InterruptedException ex) {
                                                Thread.currentThread().interrupt();
                                            }
                                        }
                                        attemptingToBoard.incrementAndGet();
                                    }
                                }
                                attemptingToBoard.decrementAndGet();
                                removePassenger("waiting");
                                addPassenger("boarded");
                                updateNumbers();
                                if (train1.isFull()) {
                                    status.setText("Train Running...");
                                    startTrain.setEnabled(false);
                                    SwingUtilities.invokeLater(() -> {sendTrain(train1);});
                                }
                        }).start();
                    }
                }
        }).start();
    }
    
    public void closeDoors() { //Stop passengers from trying to board.
        status.setText("Doors closing...");
        doorsClosed = true;
        System.out.println(attemptingToBoard.get());
        while(attemptingToBoard.get() > 0) {System.out.println(attemptingToBoard.get());}
        return;
    }

    public void trainFinished(Train train) {
         train1.clearPassengers();
         totalPassengers += numBoarded.get();
         numBoarded.set(0);
         train.stop();
         status.setText("Train Waiting...");
         doorsClosed = false;
         startTrain.setEnabled(true);
    }
    
    public void sendTrain(Train train) {
        new Thread(() -> {
                closeDoors();
                status.setText("Train Running...");
                train.run();
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
                trainFinished(train);
        }).start();
    }
    
    public void actionPerformed(ActionEvent e) {
            startTrain.setEnabled(false);
            SwingUtilities.invokeLater(() -> {sendTrain(train1);});
    }

    public void windowClosing(WindowEvent e) {
        dispose();
        System.exit(0);
    }

    public void windowOpened(WindowEvent e) {}
    public void windowActivated(WindowEvent e) {}
    public void windowIconified(WindowEvent e) {}
    public void windowDeiconified(WindowEvent e) {}
    public void windowDeactivated(WindowEvent e) {}
    public void windowClosed(WindowEvent e) {}
}
