package cc.thiago.mediaplayer9mob.service;

import java.util.ArrayList;
import java.util.Random;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.PowerManager;
import android.util.Log;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import cc.thiago.mediaplayer9mob.MainActivity;
import cc.thiago.mediaplayer9mob.R;
import cc.thiago.mediaplayer9mob.model.Music;

/**
 * Created by thiagotn on 20/01/16.
 */
public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {

    private static final int NOTIFY_ID = 1;

    private final IBinder musicBind = new MusicBinder();

    private MediaPlayer player;

    private ArrayList<Music> musics;

    private int musicPosition;

    private String musicTitle = "";

    private boolean shuffle = false;

    private Random random;

    @Override
    public void onCreate() {
        super.onCreate();
        musicPosition =0;
        player = new MediaPlayer();
        initMusicPlayer();
        random = new Random();
    }

    public void initMusicPlayer() {
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setList(ArrayList<Music> theMusics) {
        musics = theMusics;
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    public void playSong() {
        player.reset();
        Music playMusic = musics.get(musicPosition);
        musicTitle = playMusic.getTitle();
        long currSong = playMusic.getId();
        Uri trackUri = ContentUris.withAppendedId(android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currSong);
        try {
            player.setDataSource(getApplicationContext(), trackUri);
        } catch(Exception e) {
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }
        player.prepareAsync();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        if (player.getCurrentPosition() > 0) {
            mediaPlayer.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        mediaPlayer.reset();
        return false;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.play)
                .setTicker(musicTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
        .setContentText(musicTitle);
        Notification not = builder.build();

        startForeground(NOTIFY_ID, not);
    }

    public void setMusic(int musicIndex) {
        musicPosition =musicIndex;
    }

    public int getPosition() {
        return player.getCurrentPosition();
    }

    public int getDuration() {
        return player.getDuration();
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public void pausePlayer() {
        player.pause();
    }

    public void seek(int position) {
        player.seekTo(position);
    }

    public void go() {
        player.start();
    }

    public void playPrev() {
        musicPosition--;
        if(musicPosition < 0) musicPosition = musics.size()-1;
        playSong();
    }

    public void playNext() {
        if (shuffle) {
            int newSong = musicPosition;
            while (newSong == musicPosition) {
                newSong = random.nextInt(musics.size());
            }
            musicPosition = newSong;
        } else {
            musicPosition++;
            if(musicPosition >= musics.size()) musicPosition =0;
            playSong();
        }
    }

    public void setShuffle() {
        if (shuffle) {
            shuffle = false;
        } else {
            shuffle = true;
        }
    }
}
