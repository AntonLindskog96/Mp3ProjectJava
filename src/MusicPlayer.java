import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class MusicPlayer extends PlaybackListener {
    private Song currentSong;

    private AdvancedPlayer advancedPlayer;

    private boolean isPaused;

    // stores where i pause the music to keep playing from where i stopped.
    private int currentFrame;

    public MusicPlayer() {

    }

    public void loadSong(Song song) {
        currentSong = song;

        if(currentSong != null) {
            playCurrentSong();
        }
    }

    public void pauseSong() {
    if(advancedPlayer != null) {
        isPaused = true;

        stopSong();

    }
    }
    public void stopSong(){
        if(advancedPlayer != null) {
            advancedPlayer.stop();
            advancedPlayer.close();
            advancedPlayer = null;
        }
    }
    public void playCurrentSong() {
        if(currentSong != null) return;
        try {
            FileInputStream fileInputStream = new FileInputStream(currentSong.getFilePath());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            advancedPlayer = new AdvancedPlayer(bufferedInputStream);
            advancedPlayer.setPlayBackListener(this);

            startMusicThread();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startMusicThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(isPaused) {
                        //resume music
                        advancedPlayer.play(currentFrame, Integer.MAX_VALUE);
                    } else {
                        advancedPlayer.play();
                    }
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public void playbackStarted(PlaybackEvent evt) {
        System.out.println("playback started");
    }

    @Override
    public void playbackFinished(PlaybackEvent evt) {
        System.out.println("playback finished");


        if(isPaused) {
            currentFrame += (int) ((double) evt.getFrame() * currentSong.getFrameRatePerMilliseconds());
            System.out.println("stopped at" + currentFrame);
        }
    }
}
