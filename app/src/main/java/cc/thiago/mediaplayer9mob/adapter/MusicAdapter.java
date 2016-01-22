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
    private LayoutInflater songInf;

    public MusicAdapter(Context c, ArrayList<Music> theMusics){
        musics = theMusics;
        songInf=LayoutInflater.from(c);
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
        //map to song layout
        LinearLayout songLay = (LinearLayout)songInf.inflate
                (R.layout.song, parent, false);
        //get title and artist views
        TextView songView = (TextView)songLay.findViewById(R.id.song_title);
        TextView artistView = (TextView)songLay.findViewById(R.id.song_artist);
        //get song using position
        Music currMusic = musics.get(position);
        //get title and artist strings
        songView.setText(currMusic.getTitle());
        artistView.setText(currMusic.getArtist());
        //set position as tag
        songLay.setTag(position);
        return songLay;
    }

}