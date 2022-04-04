package de.keksuccino.auudio.audio;

import com.mojang.blaze3d.audio.Channel;
import de.keksuccino.auudio.audio.external.ExternalSimpleSoundInstance;
import de.keksuccino.auudio.audio.external.ExternalSound;
import de.keksuccino.auudio.audio.external.ExternalSoundResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.sounds.SoundSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AudioClip {

    protected final String soundPathOrUrl;
    protected final AudioType audioType;

    protected ExternalSoundResourceLocation soundLocation;
    protected WeighedSoundEvents soundEvents;
    protected ExternalSound sound;
    protected ExternalSimpleSoundInstance soundInstance;
    protected Channel channel = null;

    protected boolean looping = false;
    protected int volume = 100;
    protected SoundSource soundSource;

    public AudioClip(@Nonnull String audioPathOrUrl, @Nonnull AudioType audioType) throws NullPointerException {
        this(audioPathOrUrl, audioType, null);
    }

    public AudioClip(@Nonnull String audioPathOrUrl, @Nonnull AudioType audioType, @Nullable SoundSource soundSource) throws NullPointerException {
        this.soundPathOrUrl = audioPathOrUrl;
        this.audioType = audioType;
        this.soundSource = soundSource;
        if (this.soundSource == null) {
            this.soundSource = SoundSource.MASTER;
        }
        if ((audioType != null) && (audioPathOrUrl != null)) {
            this.init();
            AudioHandler.registerAudioClip(this);
        } else {
            throw new NullPointerException("Audio type and/or audio path is NULL!");
        }
    }

    /**
     * ONLY FOR INTERNAL USAGE!<br><br>
     *
     * Gets called after reloading resources and in the constructor of new {@link AudioClip} instances.
     */
    public boolean init() {
        try {

            this.stop();
            this.channel = null;

            if (this.audioType == AudioType.LOCAL) {

                this.soundLocation = new ExternalSoundResourceLocation(this.soundPathOrUrl, AudioType.LOCAL);
                this.soundInstance = new ExternalSimpleSoundInstance(this, this.soundLocation, this.soundSource, 1.0F, 1.0F);
                this.soundEvents = new WeighedSoundEvents(this.soundLocation, null);
                //This is important, otherwise the sound wouldn't be registered correctly
                boolean stream = true;
                this.sound = new ExternalSound(this.soundLocation, 1.0F, 1.0F, 1, Sound.Type.FILE, stream, false, 0);
                this.soundEvents.addSound(this.sound);
                VanillaSoundUtils.getSoundManagerRegistry().put(soundLocation, soundEvents);
                this.soundEvents.preloadIfRequired(VanillaSoundUtils.getSoundEngine());

                return true;

            }

            //TODO add web support

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void play() {
        if (!this.playing()) {
            Minecraft.getInstance().getSoundManager().play(this.soundInstance);
            this.channel = VanillaSoundUtils.getChannelOfInstance(this.soundInstance);
            this.setLooping(this.looping);
            AudioHandler.updateVolumes();
        }
    }

    public boolean stopped() {
        if (this.channel != null) {
            return this.channel.stopped();
        }
        return true;
    }

    public void pause() {
        if (playing()) {
            if (this.channel != null) {
                this.channel.pause();
            }
        }
    }

    public void unpause() {
        if (this.channel != null) {
            this.channel.unpause();
        }
    }

    public void stop() {
        if (this.soundInstance != null) {
            Minecraft.getInstance().getSoundManager().stop(this.soundInstance);
        }
        this.channel = null;
    }

    public boolean playing() {
        if (this.channel != null) {
            return this.channel.playing();
        }
        return false;
    }

    public void setLooping(boolean b) {
        if (this.channel != null) {
            this.channel.setLooping(b);
        }
        this.looping = b;
    }

    public boolean isLooping() {
        return this.looping;
    }

    /**
     * Set the volume of the sound.
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
        AudioHandler.updateVolumes();
    }

    /**
     * Get the volume of the sound.
     */
    public int getVolume() {
        return this.volume;
    }

    public void destroy() {
        stop();
        this.sound = null;
        this.soundInstance = null;
        this.soundEvents = null;
        this.soundLocation = null;
        //TODO unregister from sound manager + engine
        AudioHandler.unregisterAudioClip(this);
    }

    public SoundSource getSoundSource() {
        return this.soundSource;
    }

    public String getSoundPath() {
        return this.soundPathOrUrl;
    }

    public AudioType getAudioType() {
        return this.audioType;
    }

    public ExternalSoundResourceLocation getSoundLocation() {
        return this.soundLocation;
    }

    public ExternalSimpleSoundInstance getSoundInstance() {
        return this.soundInstance;
    }

    public ExternalSound getSound() {
        return this.sound;
    }

    public WeighedSoundEvents getSoundEvents() {
        return this.soundEvents;
    }

    public enum AudioType {
        WEB,
        LOCAL;
    }

}
