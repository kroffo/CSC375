package org.sample;

import java.util.concurrent.ConcurrentHashMap;

public class MusicBase {
    private ConcurrentHashMap<String, Song> songBase;

    public MusicBase(Song[] initialSongs) {
        songBase = new ConcurrentHashMap<String, Song>(initialSongs.length);
        for (int i = 0; i < initialSongs.length; ++i) {
            addSong(initialSongs[i]);
        }
    }

    // Adds the song s if a song of the same name is not already stored
    // returns true if the song was added
    public boolean addSong(Song s) {
        songBase.putIfAbsent(s.getName(), s);
        if (s == songBase.get(s.getName())) {
            System.out.println(Thread.currentThread().getName() + " added " + s.getName());
            return true;
        }
        return false;
    }

    // Removes the song named name or does nothing if no such song is stored
    public void removeSong(String name) {
        if (songBase.get(name) != null) {
            songBase.remove(name);
            System.out.println(Thread.currentThread().getName() + " removed " + name);
        }
    }

    // returns the song associated with name, or null if none exists
    public Song getSong(String name) {
        return songBase.get(name);
    }

    // returns the number of songs stored
    public int getSize() {
        return songBase.size();
    }
}
