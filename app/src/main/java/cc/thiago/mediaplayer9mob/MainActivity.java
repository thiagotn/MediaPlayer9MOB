package cc.thiago.mediaplayer9mob;

import android.os.IBinder;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.view.MenuItem;
import android.view.View;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.ListView;
import android.widget.MediaController;

import java.util.ArrayList;

import cc.thiago.mediaplayer9mob.adapter.MusicAdapter;
import cc.thiago.mediaplayer9mob.controller.MusicController;
import cc.thiago.mediaplayer9mob.model.Music;
import cc.thiago.mediaplayer9mob.service.MusicService;

public class MainActivity extends AppCompatActivity implements MediaController.MediaPlayerControl {

    private ArrayList<Music> musicList;

    private ListView musicView;

    private MusicService musicService;

    private Intent playIntent;

    private boolean musicBound = false;

    private MusicController controller;

    private boolean paused = false;

    private boolean playbackPaused = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        musicList = new ArrayList<Music>();
        musicView = (ListView)findViewById(R.id.song_list);

        loadMedia();
        setController();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(playIntent==null){
            playIntent = new Intent(this, MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    //connect to the service
    private ServiceConnection musicConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MusicService.MusicBinder binder = (MusicService.MusicBinder)service;
            musicService = binder.getService();
            musicService.setList(musicList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    private void loadMedia() {

        ContentResolver contentResolver = getContentResolver();
        Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String[] projection = null;
        String sortOrder = android.provider.MediaStore.Audio.Media.TITLE;
        String selectionMimeType = MediaStore.Files.FileColumns.MIME_TYPE + "=?";

        // TODO: Melhorar... mais extensoes suportadas, se possivel
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension("mp3");
        String[] selectionArgsMp3 = new String[]{ mimeType };

        Cursor cursor = contentResolver.query(uri, projection, selectionMimeType, selectionArgsMp3, sortOrder);
        if (cursor == null) {
            // query failed, handle error.
            Log.i("loadMedia", "Falha ao buscar mp3 do SdCard");
        } else if (!cursor.moveToFirst()) {
            // no media on the device
            Log.i("loadMedia", "Nao há mp3 no SdCard");
        } else {
            int titleColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = cursor.getColumnIndex(android.provider.MediaStore.Audio.Media._ID);
            int albumColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int artistColumn = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int durationColumn = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            do {
                long thisId = cursor.getLong(idColumn);
                String thisTitle = cursor.getString(titleColumn);
                String thisAlbum = cursor.getString(albumColumn);
                String thisArtist = cursor.getString(artistColumn);
                Long thisDuration = cursor.getLong(durationColumn);

                Log.i("Musica", thisId + " - " + thisTitle + " - " + thisArtist);

                Music music = new Music(thisId, thisTitle, thisArtist);
                musicList.add(music);

            } while (cursor.moveToNext());
        }

        MusicAdapter songAdt = new MusicAdapter(this, musicList);
        musicView.setAdapter(songAdt);
    }

    public void songPicked(View view){
        musicService.setMusic(Integer.parseInt(view.getTag().toString()));
        musicService.playSong();
        if (playbackPaused) {
            setController();
            playbackPaused=false;
        }
        controller.show(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_shuffle:
                musicService.setShuffle();
                break;
            case R.id.action_end:
                stopService(playIntent);
                musicService =null;
                System.exit(0);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicService =null;
        super.onDestroy();
    }

    private void setController() {
        controller = new MusicController(this);
        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });

        controller.setMediaPlayer(this);
        controller.setAnchorView(findViewById(R.id.song_list));
        controller.setEnabled(true);
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (musicService !=null && musicBound && musicService.isPlaying()){
            return musicService.getPosition();
        } else {
            return 0;
        }
    }

    @Override
    public int getDuration() {
        if (musicService !=null && musicBound && musicService.isPlaying()) {
            return musicService.getDuration();
        } else {
            return 0;
        }
    }

    @Override
    public boolean isPlaying() {
        if (musicService !=null && musicBound) {
            return musicService.isPlaying();
        } else {
            return false;
        }
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public void pause() {
        playbackPaused = true;
        musicService.pausePlayer();
    }

    @Override
    public void seekTo(int pos) {
        musicService.seek(pos);
    }

    @Override
    public void start() {
        musicService.go();
    }

    @Override
    protected void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (paused) {
            setController();
            paused = false;
        }
    }

    @Override
    protected void onStop() {
        controller.hide();
        super.onStop();
    }

    private void playNext() {
        musicService.playNext();
        if (playbackPaused) {
            setController();
            playbackPaused=false;
        }
        controller.show(0);
    }

    private void playPrev() {
        musicService.playPrev();
        if (playbackPaused) {
            setController();
            playbackPaused=false;
        }
        controller.show(0);
    }
}
