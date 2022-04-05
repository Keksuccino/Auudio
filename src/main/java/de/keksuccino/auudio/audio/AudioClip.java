package de.keksuccino.auudio.audio;

import com.mojang.blaze3d.audio.Channel;
import de.keksuccino.auudio.audio.exceptions.InvalidAudioException;
import de.keksuccino.auudio.audio.external.ExternalSound;
import de.keksuccino.auudio.audio.external.ExternalSoundResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AudioClip {

    protected final SoundType soundType;
    protected ResourceLocation soundLocation;
    protected WeighedSoundEvents soundEvents;
    protected AudioClipSound sound;
    protected AudioClipSoundInstance soundInstance;
    protected Channel channel = null;

    protected boolean looping = false;
    protected int volume = 100;
    protected SoundSource soundSource;

    public AudioClip(@Nonnull ResourceLocation soundLocation, @Nonnull SoundType soundType) throws NullPointerException, InvalidAudioException {
        this(soundLocation, soundType, null);
    }

    public AudioClip(@Nonnull ResourceLocation soundLocation, @Nonnull SoundType soundType, @Nullable SoundSource soundSource) throws NullPointerException, InvalidAudioException {
        if (soundLocation == null) {
            throw new NullPointerException("Sound location is NULL!");
        }
        if (soundType == null) {
            throw new NullPointerException("Sound type is NULL!");
        }
        if ((soundLocation instanceof ExternalSoundResourceLocation) && (soundType == SoundType.INTERNAL_ASSET)) {
            throw new InvalidAudioException("Trying to load an ExternalSoundResourceLocation as internal asset!");
        }
        if (!(soundLocation instanceof ExternalSoundResourceLocation) && (soundType != SoundType.INTERNAL_ASSET)) {
            throw new InvalidAudioException("Trying to load an internal asset ResourceLocation as external sound!");
        }
        if (soundLocation instanceof ExternalSoundResourceLocation) {
            if (((ExternalSoundResourceLocation)soundLocation).getSoundType() != soundType) {
                throw new InvalidAudioException("Sound type of external sound doesn't match clip sound type!");
            }
        }
        this.soundLocation = soundLocation;
        this.soundType = soundType;
        this.soundSource = soundSource;
        if (this.soundSource == null) {
            this.soundSource = SoundSource.MASTER;
        }
        this.prepare();
        AudioHandler.registerAudioClip(this);
    }

    public static AudioClip buildExternalClip(String soundPath, SoundType soundType, @Nullable SoundSource soundSource) throws InvalidAudioException, NullPointerException {
        return new AudioClip(new ExternalSoundResourceLocation(soundPath, soundType), soundType, soundSource);
    }

    public static AudioClip buildExternalClip(String soundPath, SoundType soundType) throws InvalidAudioException, NullPointerException {
        return buildExternalClip(soundPath, soundType, null);
    }

    /**
     * Don't use {@link ExternalSoundResourceLocation}s here! Only normal {@link ResourceLocation}s are supported!
     */
    public static AudioClip buildInternalClip(ResourceLocation soundLocation, @Nullable SoundSource soundSource) throws InvalidAudioException, NullPointerException {
        return new AudioClip(soundLocation, SoundType.INTERNAL_ASSET, soundSource);
    }

    /**
     * Don't use {@link ExternalSoundResourceLocation}s here! Only normal {@link ResourceLocation}s are supported!
     */
    public static AudioClip buildInternalClip(ResourceLocation soundLocation) throws InvalidAudioException, NullPointerException {
        return buildInternalClip(soundLocation, SoundSource.MASTER);
    }

    /**
     * Gets called after reloading resources and in the constructor of new {@link AudioClip} instances.
     */
    public boolean prepare() {
        try {

            this.stop();
            this.channel = null;

            if ((this.soundType == SoundType.EXTERNAL_LOCAL) || (this.soundType == SoundType.EXTERNAL_WEB)) {

                this.soundInstance = new AudioClipSoundInstance(this, this.soundLocation, this.soundSource, 1.0F, 1.0F);
                this.soundEvents = new WeighedSoundEvents(this.soundLocation, null);
                this.sound = new ExternalSound((ExternalSoundResourceLocation)this.soundLocation, 1.0F, 1.0F, 1, Sound.Type.FILE, false, 0);
                this.soundEvents.addSound(this.sound);
                VanillaSoundUtils.getSoundManagerRegistry().put(soundLocation, soundEvents);
                this.soundEvents.preloadIfRequired(VanillaSoundUtils.getSoundEngine());

                return true;

            } else if (this.soundType == SoundType.INTERNAL_ASSET) {

                this.soundInstance = new AudioClipSoundInstance(this, this.soundLocation, this.soundSource, 1.0F, 1.0F);
                this.soundEvents = new WeighedSoundEvents(this.soundLocation, null);
                this.sound = new AudioClipSound(this.soundLocation, 1.0F, 1.0F, 1, Sound.Type.FILE, false, false, 0);
                this.soundEvents.addSound(this.sound);
                VanillaSoundUtils.getSoundManagerRegistry().put(soundLocation, soundEvents);
                this.soundEvents.preloadIfRequired(VanillaSoundUtils.getSoundEngine());

                return true;

            }

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
        return this.soundLocation.getPath();
    }

    public SoundType getSoundType() {
        return this.soundType;
    }

    public ResourceLocation getSoundLocation() {
        return this.soundLocation;
    }

    public AudioClipSoundInstance getSoundInstance() {
        return this.soundInstance;
    }

    public AudioClipSound getSound() {
        return this.sound;
    }

    public WeighedSoundEvents getSoundEvents() {
        return this.soundEvents;
    }

    public enum SoundType {
        EXTERNAL_WEB,
        EXTERNAL_LOCAL,
        INTERNAL_ASSET;
    }

}
