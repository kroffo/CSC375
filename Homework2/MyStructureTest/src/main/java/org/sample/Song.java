package org.sample;

public class Song {
    private String name;
    private int length;
    
    public Song(String name, int length) {
        this.name = name;
        this.length = length;
    }

    // returns the name of the song
    public String getName() {
        return this.name;
    }

    // Simulates a thread listening to a song by putting it to sleep
    public void listen() {
        try {
            System.out.println(Thread.currentThread().getName() + " is listening to " + name);
            Thread.currentThread().sleep(length*1000);
            System.out.println(Thread.currentThread().getName() + " has finished listening to " + name);
        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " was interrupted while listening to " + name);
        }
    }
}
