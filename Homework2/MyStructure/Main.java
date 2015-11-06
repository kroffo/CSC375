import java.util.Random;

public class Main {
    public static void main(String[] args) {
        final int numberOfInitialSongs = 100;
        Song[] songs = new Song[numberOfInitialSongs];
        Random r = new Random();
        for (int i = 0; i < songs.length; i++) {
            songs[i] = new Song("Song" + i, r.nextInt(10) + 3);
        }

        MusicBase base = new MusicBase(songs);
        
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                    Random randy = new Random();
                    while(true) {
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
                while(true) {
                    if (randy.nextInt(100000000) == 22) {
                        base.addSong(new Song("Song" + nameCounter++, randy.nextInt(10) + 3));
                    }
                }
        },"Adder").start();

        // The thread which removes songs!
        new Thread(() -> {
                Random randy = new Random();
                while(true) {
                    if (randy.nextInt(100000000) == 22) {
                        base.removeSong("Song" + randy.nextInt(base.getSize()));
                    }
                }
        },"Deleter").start();
    }
}
