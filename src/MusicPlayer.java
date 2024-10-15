import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;

public class MusicPlayer extends PlaybackListener {

    private static final Object playSignal = new Object();

    private MusicPlayerGUI musicPlayerGUI;

    private Song currentSong;
    public Song getCurrentSong() {
        return currentSong;
    }

    private ArrayList<Song> playlist;

    private int currentPlaylistIndex;

    private AdvancedPlayer advancedPlayer;

    private boolean isPaused;

    // stores where i pause the music to keep playing from where i stopped.
    private int currentFrame;
    public void setCurrentFrame(int frame) {
        currentFrame = frame;
    }

    private int currentTimeInMilli;
    public void setCurrentTimeInMilli(int timeInMilli) {
        currentTimeInMilli = timeInMilli;
    }

    // Constructor
    public MusicPlayer(MusicPlayerGUI musicPlayerGUI) {
        this.musicPlayerGUI = musicPlayerGUI;

    }

    public void loadSong(Song song) {
        currentSong = song;

        if(currentSong != null) {
            playCurrentSong();
        }
    }

    public void loadPlaylist(File playlistFile) {
        playlist = new ArrayList<>();

        try {
            FileReader fileReader = new FileReader(playlistFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String songPath;
            while((songPath = bufferedReader.readLine()) != null){
                Song song = new Song(songPath);

                playlist.add(song);
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
        if(playlist.size() > 0) {
            musicPlayerGUI.setPlaybackSliderValue(0);
            currentTimeInMilli = 0;

            currentSong = playlist.get(0);

            currentFrame = 0;

            musicPlayerGUI.enablePauseButtonDisablePlayButton();
            musicPlayerGUI.updateSongTitleAndArtist(currentSong);
            musicPlayerGUI.updatePlaybackSlider(currentSong);

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

    public void nextSong(){
        if(playlist == null) return;

        stopSong();

        if(currentPlaylistIndex +1 > playlist.size() -1) return;

        currentPlaylistIndex++;

        currentSong = playlist.get(currentPlaylistIndex);

        currentFrame = 0;

        currentTimeInMilli = 0;

        musicPlayerGUI.enablePauseButtonDisablePlayButton();
        musicPlayerGUI.updateSongTitleAndArtist(currentSong);
        musicPlayerGUI.updatePlaybackSlider(currentSong);

        playCurrentSong();
    }

    public void prevSong(){
        if(playlist == null) return;

        if(currentPlaylistIndex -1 < 0) return;

        stopSong();

        currentPlaylistIndex--;

        currentSong = playlist.get(currentPlaylistIndex);

        currentFrame = 0;

        currentTimeInMilli = 0;

        musicPlayerGUI.enablePauseButtonDisablePlayButton();
        musicPlayerGUI.updateSongTitleAndArtist(currentSong);
        musicPlayerGUI.updatePlaybackSlider(currentSong);

        playCurrentSong();
    }
    public void playCurrentSong() {
        try {
            FileInputStream fileInputStream = new FileInputStream(currentSong.getFilePath());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            advancedPlayer = new AdvancedPlayer(bufferedInputStream);
            advancedPlayer.setPlayBackListener(this);

            startMusicThread();

            startPlaybackSliderThread();

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
                        synchronized (playSignal) {
                            isPaused = false;
                            playSignal.notify();
                        }
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

    // update slider
    private void startPlaybackSliderThread () {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(isPaused) {
                    try{
                        synchronized (playSignal) {
                            playSignal.wait();
                        }
                    }catch(Exception e) {
                        e.printStackTrace();
                    }
                }

                while(!isPaused) {
                    try {
                        currentTimeInMilli++;
                        int calculatedFrame = (int) ((double) currentTimeInMilli * 2.08 * currentSong.getFrameRatePerMilliseconds());
                        musicPlayerGUI.setPlaybackSliderValue(calculatedFrame);

                        Thread.sleep(1);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
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
        }
    }
}
