package de.keksuccino.auudio.audio;

import de.keksuccino.auudio.audio.exceptions.AudioNotReadyException;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AudioClip {

    protected final String audioPathOrUrl;
    protected final AudioSource source;

    protected volatile Clip clip = null;
    protected boolean playing = false;
    protected volatile boolean playWhenReady = false;
    protected boolean looping = false;
    protected int volume = 100;

    protected AudioChannel channel = AudioChannel.MASTER;
    protected int baseVolume = 100;

    public AudioClip(String audioFilePathOrUrl, AudioSource audioSource) {
        this.audioPathOrUrl = audioFilePathOrUrl;
        this.source = audioSource;
        if ((audioSource != null) && (audioFilePathOrUrl != null)) {
            new Thread(() -> {
                clip = createNewClipOf(audioFilePathOrUrl, audioSource);
            }).start();
        }
        AudioHandler.registerAudioClip(this);
    }

    public void play() throws AudioNotReadyException {
        if (!isPlaying()) {
            if (isAudioReady()) {
                this.playing = true;
                this.setLooping(this.looping);
                this.setBaseVolume(this.baseVolume);
                this.clip.start();
            } else {
                throw new AudioNotReadyException("[Auudio] Unable to play audio! Not ready yet: " + this.audioPathOrUrl);
            }
        }
    }

    public boolean hasFinishedPlaying() {
        if (isAudioReady()) {
            return (this.clip.getMicrosecondPosition() >= this.clip.getMicrosecondLength());
        }
        return false;
    }

    /**
     * Will try to play the audio once it's ready.<br><br>
     *
     * <b>Will fail if the audio needs too much time to get ready!</b>
     */
    public void tryPlayWhenReady() {
        if (!this.playWhenReady) {
            this.playWhenReady = true;
            new Thread(() -> {
                long start = System.currentTimeMillis();
                boolean failed = false;
                while (!isAudioReady()) {
                    long now = System.currentTimeMillis();
                    if ((start + 10000) < now) {
                        failed = true;
                        break;
                    }
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (!failed) {
                    try {
                        play();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                playWhenReady = false;
            }).start();
        }
    }

    public void pause() {
        if (isPlaying()) {
            if (isAudioReady()) {
                this.clip.stop();
                this.playing = false;
            }
        }
    }

    /**
     * Will reset the clip, so it starts from the beginning.<br>
     * <b>This will NOT play the clip, it just resets its progress!</b>
     */
    public void restart() {
        if (isAudioReady()) {
            this.clip.setMicrosecondPosition(0);
        }
    }

    public void stop() {
        pause();
        restart();
    }

    public boolean isPlaying() {
        return this.playing;
    }

    public void setLooping(boolean b) {
        this.looping = b;
        if (isAudioReady()) {
            if (b) {
                this.clip.setLoopPoints(0, -1);
                this.clip.loop(-1);
            } else {
                this.clip.loop(0);
            }
        }
    }

//    /**
//     * <b>FOR INTERNAL USE ONLY!</b><br>
//     * Use {@link AudioClip#setBaseVolume(int)} to set the volume of the clip!<br><br>
//     *
//     * @param volume Value between 0 and 100 percent
//     */
//    public void setVolume(int volume) {
//        if (volume < 0) {
//            volume = 0;
//        }
//        if (volume > 100) {
//            volume = 100;
//        }
//        float floatVolume = ((float) volume) / 100.0F;
//        if (isAudioReady()) {
//            FloatControl f = ((FloatControl)this.clip.getControl(FloatControl.Type.MASTER_GAIN));
//            float gain = 20F * (float) Math.log10(floatVolume);
//            f.setValue(gain);
//        }
//        this.volume = volume;
//    }

    /**
     * <b>FOR INTERNAL USE ONLY!</b><br>
     * Use {@link AudioClip#setBaseVolume(int)} to set the volume of the clip!<br><br>
     *
     * @param percentage Value between 0 and 100 percent
     */
    public void setVolume(int percentage) {
        if (percentage < 0) {
            percentage = 0;
        }
        if (percentage > 100) {
            percentage = 100;
        }
        this.volume = percentage;
        if ((this.clip != null) && this.clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            FloatControl c = (FloatControl) this.clip.getControl(FloatControl.Type.MASTER_GAIN);
            float onePercent = (c.getMaximum() - c.getMinimum()) / 100;
            c.setValue(c.getMinimum() + (onePercent * percentage));
        }
    }

    /**
     * <b>FOR INTERNAL USE ONLY!</b><br>
     * Use {@link AudioClip#getBaseVolume()} to get the volume of the clip!<br><br>
     */
    public int getVolume() {
        return this.volume;
    }

    public void setAudioChannel(AudioChannel channel) {
        this.channel = channel;
    }

    public AudioChannel getAudioChannel() {
        return this.channel;
    }

    /**
     * Value between 0 and 100 percent.
     */
    public void setBaseVolume(int volume) {
        if (volume < 0) {
            volume = 0;
        }
        if (volume > 100) {
            volume = 100;
        }
        this.baseVolume = volume;
        AudioHandler.updateAudioClipVolume(this);
    }

    public int getBaseVolume() {
        return this.baseVolume;
    }

    public boolean isAudioReady() {
        return (this.clip != null);
    }

    public void destroy() {
        stop();
        this.clip = null;
        AudioHandler.unregisterAudioClip(this);
    }

    protected static Clip createNewClipOf(String audioPathOrUrl, AudioSource source) {

        try {

            AudioInputStream in = null;

            if (source == AudioSource.WEB) {

                //TODO fix web source
                URL u = new URL(audioPathOrUrl);
                HttpURLConnection http = (HttpURLConnection) u.openConnection();
                http.addRequestProperty("User-Agent", "Mozilla/4.0");
                InputStream s = http.getInputStream();
                if (s != null) {
                    in = AudioSystem.getAudioInputStream(s);
                }

            } else if (source == AudioSource.LOCAL) {

                BufferedInputStream s = new BufferedInputStream(new FileInputStream(audioPathOrUrl));
                in = AudioSystem.getAudioInputStream(s);

            }

            if (in != null) {
                Clip c = AudioSystem.getClip();
                AudioInputStream din;
                AudioFormat baseFormat = in.getFormat();
                AudioFormat decodedFormat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        baseFormat.getSampleRate(),
                        16,
                        baseFormat.getChannels(),
                        baseFormat.getChannels() * 2,
                        baseFormat.getSampleRate(),
                        false);
                din = AudioSystem.getAudioInputStream(decodedFormat, in);
                c.open(din);
//                c.open(decodedFormat, din.readAllBytes(), 0, 1024);
                return c;
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;

    }

    public enum AudioSource {
        WEB,
        LOCAL;
    }

}
