package indi.kwanho.pm.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import indi.kwanho.pm.R;
import indi.kwanho.pm.manager.MusicPlayerManager;
import indi.kwanho.pm.store.PlayState;

public class NotificationButtonReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case "NotificationPreviousButtonClicked":
                    MusicPlayerManager.getInstance().getMusicPlayerService().prevSong();
                    break;
                case "NotificationPlayPauseButtonClicked":
                    Log.d("NotificationButtonReceiver", String.valueOf(PlayState.getInstance().isPlaying()));
                    if (PlayState.getInstance().isPlaying()) {
                        MusicPlayerManager.getInstance().getMusicPlayerService().pauseSong();
                        PlayState.getInstance().setPlaying(false);
                    } else {
                        MusicPlayerManager.getInstance().getMusicPlayerService().resumeSong();
                        PlayState.getInstance().setPlaying(true);
                    }
                    break;
                case "NotificationNextButtonClicked":
                    MusicPlayerManager.getInstance().getMusicPlayerService().nextSong();
                    break;
            }
        }
    }
}
