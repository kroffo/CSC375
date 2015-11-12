package org.sample;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.*;
import java.util.concurrent.ThreadLocalRandom;

public class MyBenchmark {

    @State(Scope.Thread)
    public static class MyBase {
        int numberOfInitialSongs = 100;
        MusicBase base = new MusicBase(numberOfInitialSongs);
    }

    @Benchmark @BenchmarkMode(Mode.Throughput) @OutputTimeUnit(TimeUnit.SECONDS)
    public void testMethod(MyBase myBase) throws InterruptedException {
        Thread[] threads = new Thread[30];

        
        for (int i = 0; i < 10; i++) {
            
            threads[i] = new Thread(() -> {
                    for (int k = 0; k < 20; k++) {
                        String songName = "Song" + ThreadLocalRandom.current().nextInt(20) + 30;
                        Song song = myBase.base.getSong(songName);
                        //if (song != null) {
                        //    song.listen();
                        //}
                    }
                },"Listener" + i);
        }
        

        for (int i = 10; i < 20; i++) {
            // The thread which adds songs!
            threads[i] = new Thread(() -> {
                    int nameCounter = myBase.numberOfInitialSongs;
                    for(int k = 0; k < 20; k++) {
                        myBase.base.addSong(new Song("Song" + nameCounter++, 2)); //nameCounter can potentially lose counts but I don't care, it's close enough
                    }
                },"Adder" + i);
        }
        
        for (int i = 20; i < 30; i++) {
            // The thread which removes songs!
            threads[i] = new Thread(() -> {
                    for(int k = 0; k < 20; k++) {
                        myBase.base.removeSong("Song" + ThreadLocalRandom.current().nextInt(myBase.base.getSize()));
                    }
                },"Deleter" + i);
        }
        
        for (int i = 0; i < 30; i++) {
            threads[i].start();
        }
        for (int i = 0; i < 30; i++) {
            threads[i].join();
        }
        
    }
}
