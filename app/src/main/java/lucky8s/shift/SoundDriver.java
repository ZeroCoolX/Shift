package lucky8s.shift;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

/**
 * Created by Christian on 9/19/2015.
 */
public class SoundDriver {

    SoundPool soundPool;
    Context context;
    AudioManager audioManager;

    int swipe;
    int backPress;
    int fadeIn;
    int fadeOut;
    int hint;
    int hitBreakable;
    int hitMolten;
    int portalIn;
    int portalOut;
    int hitPoppable;
    int buttonPress;
    int star;
    int uiBackground;
    int playBackground;
    int currentlyPlaying;

    float actVolume;
    float maxVolume;
    float volume;


    boolean uiBackgroundLoaded;
    boolean playBackgroundLoaded;
    boolean starLoaded;
    boolean uiBackgroundPlaying;
    boolean playBackgroundPlaying;
    boolean playSounds;
    boolean playMusic;


    public SoundDriver(Context context) {
        super();

        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        actVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actVolume / maxVolume;

        playSounds = context.getSharedPreferences("Music", Context.MODE_PRIVATE).getBoolean("Sounds", true);
        playMusic = context.getSharedPreferences("Music", Context.MODE_PRIVATE).getBoolean("Music", true);

        soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(listener);
        final Context contextFinal = context;
        new Thread() {
            @Override
            public void run() {
                swipe = soundPool.load(contextFinal, R.raw.slide, 1);
                backPress = soundPool.load(contextFinal, R.raw.backpress, 1);
                fadeIn = soundPool.load(contextFinal, R.raw.blue_block_fadein, 1);
                fadeOut = soundPool.load(contextFinal, R.raw.blue_block_fadeout, 1);
                hint = soundPool.load(contextFinal, R.raw.hint, 1);
                hitBreakable = soundPool.load(contextFinal, R.raw.hitting_breakable, 1);
                hitMolten = soundPool.load(contextFinal, R.raw.hitting_molten, 1);
                portalIn = soundPool.load(contextFinal, R.raw.portal_in, 1);
                portalOut = soundPool.load(contextFinal, R.raw.portal_out, 1);
                hitPoppable = soundPool.load(contextFinal, R.raw.hitting_poppable, 1);
                buttonPress = soundPool.load(contextFinal, R.raw.settings_button, 1);
                star = soundPool.load(contextFinal, R.raw.star, 1);
                uiBackground = soundPool.load(contextFinal, R.raw.jungle_loop1, 1);
                playBackground = soundPool.load(contextFinal, R.raw.jungle_loop2, 1);
                initiate();
            }
        }.start();
        this.context = context;
    }

    SoundPool.OnLoadCompleteListener listener = new SoundPool.OnLoadCompleteListener() {
        @Override
        public void onLoadComplete(SoundPool soundPool, int i, int i1) {
            switch (i) {
                case 12:
                    starLoaded = true;
                    break;
                case 13:
                    uiBackgroundLoaded = true;
                    break;
                case 14:
                    playBackgroundLoaded = true;
                    break;
            }
        }
    };

    public void swipe() {
        if (playSounds) {
            currentlyPlaying = soundPool.play(swipe, volume, volume, 1, 0, 1f);
        }
    }

    public void backPress() {
        if (playSounds) {
            currentlyPlaying = soundPool.play(backPress, volume, volume, 1, 0, 1f);
        }
    }

    public void fadeIn() {
        if (playSounds) {
            currentlyPlaying = soundPool.play(fadeIn, volume, volume, 1, 0, 1f);
        }
    }

    public void fadeOut() {
        if (playSounds) {
            currentlyPlaying = soundPool.play(fadeOut, volume, volume, 1, 0, 1f);
        }
    }

    public void hint() {
        if (playSounds) {
            currentlyPlaying = soundPool.play(hint, volume, volume, 1, 0, 1f);
        }
    }

    public void hitBreakable() {
        if (playSounds) {
            currentlyPlaying = soundPool.play(hitBreakable, volume, volume, 1, 0, 1f);
        }
    }

    public void buttonPress() {
        if (playSounds) {
            currentlyPlaying = soundPool.play(buttonPress, volume, volume, 1, 0, 1f);
        }
    }

    public void hitMolten() {
        if (playSounds) {
            currentlyPlaying = soundPool.play(hitMolten, volume, volume, 1, 0, 1f);
        }
    }

    public void hitPoppable() {
        if (playSounds) {
            currentlyPlaying = soundPool.play(hitPoppable, volume, volume, 1, 0, 1f);
        }
    }

    public void portalIn() {
        if (playSounds) {
            currentlyPlaying = soundPool.play(portalIn, volume, volume, 1, 0, 1f);
        }
    }

    public void portalOut() {
        if (playSounds) {
            currentlyPlaying = soundPool.play(portalOut, volume, volume, 1, 0, 1f);
        }
    }

    public void star() {
        if (playSounds) {
            currentlyPlaying = soundPool.play(star, volume, volume, 1, 0, 1f);
        }
    }

    public void homeBackground() {
        if (playMusic) {
            if (uiBackgroundLoaded) {
                currentlyPlaying = soundPool.play(uiBackground, volume / 2, volume / 2, 1, -1, 1f);
                uiBackgroundPlaying = true;
            } else {
                new Thread() {
                    public void run() {
                        while (!uiBackgroundPlaying) {
                            try {
                                sleep(50);
                            } catch (InterruptedException ie) {
                            }
                            if (uiBackgroundLoaded) {
                                currentlyPlaying = soundPool.play(uiBackground, volume / 2, volume / 2, 1, -1, 1f);
                                uiBackgroundPlaying = true;
                            }
                        }
                    }
                }.start();
            }
        }
    }

    public void gameBackground() {
        if (playMusic) {
            if (playBackgroundLoaded) {
                currentlyPlaying = soundPool.play(playBackground, volume / 4, volume / 4, 1, -1, 1f);
                playBackgroundPlaying = true;
            } else {
                new Thread() {
                    @Override
                    public void run() {
                        while (!playBackgroundLoaded) {
                            try {
                                sleep(50);
                            } catch (InterruptedException ie) {
                            }
                            if (playBackgroundLoaded) {
                                currentlyPlaying = soundPool.play(playBackground, volume / 5, volume / 5, 1, -1, .75f);
                                playBackgroundPlaying = true;
                            }
                        }
                    }
                }.start();
            }
        }
    }

    public void initiate() {
        currentlyPlaying = soundPool.play(uiBackground, 0, 0, 1, 0, 1f);
        soundPool.stop(currentlyPlaying);
        currentlyPlaying = soundPool.play(playBackground, 0, 0, 1, 0, 1f);
        soundPool.stop(currentlyPlaying);
    }

    public void stop() {
        if (soundPool != null) {
            soundPool.autoPause();
            uiBackgroundPlaying = false;
            playBackgroundPlaying = false;
        }
    }

    public boolean isLoaded(String arg) {
        switch (arg) {
            case "star":
                return starLoaded;
            default:
                return false;
        }
    }

    public boolean isPlaying(String sound) {
        switch (sound) {
            case "gameBackground":
                return playBackgroundPlaying;
            case "homeBackground":
                return uiBackgroundPlaying;
            default:
                return false;
        }
    }

    public void resetSettings() {

        playSounds=context.getSharedPreferences("Music",Context.MODE_PRIVATE).getBoolean("Sounds",true);

        playMusic=context.getSharedPreferences("Music",Context.MODE_PRIVATE).getBoolean("Music",true);
    }
}
