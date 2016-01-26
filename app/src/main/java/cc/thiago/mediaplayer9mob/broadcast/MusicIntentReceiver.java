package cc.thiago.mediaplayer9mob.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.util.Log;

import cc.thiago.mediaplayer9mob.service.MusicService;

/**
 * Created by thiagotn on 21/01/16.
 */
public class MusicIntentReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
            Intent i = new Intent(context, MusicService.class);

            boolean plugged = (i.getIntExtra("state", 0) == 0 ? false : true);
            Log.i("plugged", "Fone plugado?  " + plugged);

            if (plugged) {
                i.setAction(MusicService.ACTION_PAUSE);
            } else {
                i.setAction(MusicService.ACTION_PLAY);
            }

            //BUG :(
            //context.sendBroadcast(intent);
        }
    }
}
