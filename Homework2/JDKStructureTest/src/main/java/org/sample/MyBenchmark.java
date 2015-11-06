package org.sample;

import java.util.Random;
import org.openjdk.jmh.annotations.Benchmark;

public class MyBenchmark {
    @Benchmark
    public void testMethod() {
        final int numberOfInitialSongs = 100;
        Song[] songs = new Song[numberOfInitialSongs];
        Random r = new Random();
        for (int i = 0; i < songs.length; i++) {
            songs[i] = new Song("Song" + i, r.nextInt(10) + 3);
        }

        MusicBase base = new MusicBase(songs);
        
        for (int i = 0; i < 1; i++) {
            new Thread(() -> {
                    Random randy = new Random();
                    for (int k = 0; k < 2; k++) {
                        String songName = "Song" + randy.nextInt(base.getSize());
                        Song song = base.getSong(songName);
                        if (song != null) {
                            song.listen();
                        }
                    }
            },"Listener" + i).start();
        }
        

        // The thread which adds songs!
        new Thread(() -> {
                Random randy = new Random();
                int nameCounter = numberOfInitialSongs;
                for(int k = 0; k < 100; k++) {
                        base.addSong(new Song("Song" + nameCounter, randy.nextInt(10) + 3));
                }
        },"Adder").start();

        // The thread which removes songs!
        new Thread(() -> {
                for(int k = 0; k < 100; k++) {
                        base.removeSong("Song" + k);
                }
        },"Deleter").start();
    }
}
