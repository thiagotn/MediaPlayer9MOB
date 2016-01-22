package cc.thiago.mediaplayer9mob.adapter;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import cc.thiago.mediaplayer9mob.R;
import cc.thiago.mediaplayer9mob.model.Music;

/**
 * Created by thiagotn on 20/01/16.
 */
public class MusicAdapter extends BaseAdapter {

    private ArrayList<Music> musics;
    private LayoutInflater musicInf;

    public MusicAdapter(Context context, ArrayList<Music> musics) {
        this.musics = musics;
        this.musicInf = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return musics.size();
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout songLay = (LinearLayout) musicInf.inflate (R.layout.music, parent, false);
        TextView songView = (TextView)songLay.findViewById(R.id.music_title);
        TextView artistView = (TextView)songLay.findViewById(R.id.music_artist);
        Music currMusic = musics.get(position);
        songView.setText(currMusic.getTitle());
        artistView.setText(currMusic.getArtist());
        songLay.setTag(position);
        return songLay;
    }
}