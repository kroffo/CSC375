package benchmarks;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class MusicBase {
    private volatile HashMap<String, Song> songBase;
    private AtomicInteger seq = new AtomicInteger(); // This is my seqlock
    private AtomicInteger numberOfSongs = new AtomicInteger();
    
    public MusicBase(int numberOfInitialSongs) {
        songBase = new HashMap<String, Song>(numberOfInitialSongs);
        for (int i = 0; i < numberOfInitialSongs; i++) {
            addSong(new Song("Song" + i, 1));
        }
    }

    // returns true if song did not already exist and was added, false otherwise
    public boolean addSong(Song s) {
        for(;;) {             
            if (songBase.get(s.getName()) == null) {
                int start = seq.get();
                if (start % 2 == 0) {
                    if (seq.compareAndSet(start,start+1)) {
                        songBase.putIfAbsent(s.getName(),s);
                        if (s == songBase.get(s.getName())) {
                            numberOfSongs.incrementAndGet();
                            seq.incrementAndGet();
                            return true;
                        }
                        seq.incrementAndGet();
                        return false;
                    }
                }
            } else {
                return false;
            }
        }
    }
        

    // removes the song, or does nothing if the song does not exist
    public void removeSong(String name) {
        for(;;) {
            if (songBase.get(name) != null) {
                int start = seq.get();
                if (start % 2 == 0) {
                    if (seq.compareAndSet(start,start+1)) {
                        songBase.remove(name);
                        numberOfSongs.decrementAndGet();
                        seq.incrementAndGet();
                        return;
                    }
                }
            } else {
                return;
            }
        }
    }

    // returns the song associated with name, or null if none exists
    public Song getSong(String name) {
        for (;;) {
            int start = seq.get();
            if (start % 2 == 0) {
                Song s = songBase.get(name);
                if (start - seq.get() == 0) {
                    return s;
                }
            }
        }
    }

    // returns the number of songs stored (or at least the number that were stored at a recent point in time)
    public int getSize() {
        return numberOfSongs.get();
    }
}
