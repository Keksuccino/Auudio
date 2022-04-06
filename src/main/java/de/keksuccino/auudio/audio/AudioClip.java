package de.keksuccino.auudio.audio;

import de.keksuccino.auudio.audio.exceptions.InvalidAudioException;
import de.keksuccino.auudio.audio.external.ExternalSound;
import de.keksuccino.auudio.audio.external.ExternalSoundResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.lwjgl.openal.AL10;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class AudioClip {

    protected final SoundType soundType;
    protected ResourceLocation soundLocation;
    protected SoundEventAccessor soundEvents;
    protected AudioClipSound sound;
    protected AudioClipSoundInstance soundInstance;
    protected SoundSource channel = null;

    protected boolean looping = false;
    protected int volume = 100;
    protected SoundCategory soundSource;

    public AudioClip(@Nonnull ResourceLocation soundLocation, @Nonnull SoundType soundType) throws NullPointerException, InvalidAudioException {
        this(soundLocation, soundType, null);
    }

    public AudioClip(@Nonnull ResourceLocation soundLocation, @Nonnull SoundType soundType, @Nullable SoundCategory soundSource) throws NullPointerException, InvalidAudioException {
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
            this.soundSource = SoundCategory.MASTER;
        }
        this.prepare();
        AudioHandler.registerAudioClip(this);
    }

    public static AudioClip buildExternalClip(String soundPath, SoundType soundType, @Nullable SoundCategory soundSource) throws InvalidAudioException, NullPointerException {
        return new AudioClip(new ExternalSoundResourceLocation(soundPath, soundType), soundType, soundSource);
    }

    public static AudioClip buildExternalClip(String soundPath, SoundType soundType) throws InvalidAudioException, NullPointerException {
        return buildExternalClip(soundPath, soundType, null);
    }

    /**
     * Don't use {@link ExternalSoundResourceLocation}s here! Only normal {@link ResourceLocation}s are supported!
     */
    public static AudioClip buildInternalClip(ResourceLocation soundLocation, @Nullable SoundCategory soundSource) throws InvalidAudioException, NullPointerException {
        return new AudioClip(soundLocation, SoundType.INTERNAL_ASSET, soundSource);
    }

    /**
     * Don't use {@link ExternalSoundResourceLocation}s here! Only normal {@link ResourceLocation}s are supported!
     */
    public static AudioClip buildInternalClip(ResourceLocation soundLocation) throws InvalidAudioException, NullPointerException {
        return buildInternalClip(soundLocation, SoundCategory.MASTER);
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
                this.soundEvents = new SoundEventAccessor(this.soundLocation, null);
                this.sound = new ExternalSound((ExternalSoundResourceLocation)this.soundLocation, 1.0F, 1.0F, 1, Sound.Type.FILE, false, 0);
                this.soundEvents.addSound(this.sound);
                VanillaSoundUtils.getSoundManagerRegistry().put(soundLocation, soundEvents);
                this.soundEvents.enqueuePreload(VanillaSoundUtils.getSoundEngine());

                return true;

            } else if (this.soundType == SoundType.INTERNAL_ASSET) {

                this.soundInstance = new AudioClipSoundInstance(this, this.soundLocation, this.soundSource, 1.0F, 1.0F);
                this.soundEvents = new SoundEventAccessor(this.soundLocation, null);
                this.sound = new AudioClipSound(this.soundLocation, 1.0F, 1.0F, 1, Sound.Type.FILE, false, false, 0);
                this.soundEvents.addSound(this.sound);
                VanillaSoundUtils.getSoundManagerRegistry().put(soundLocation, soundEvents);
                this.soundEvents.enqueuePreload(VanillaSoundUtils.getSoundEngine());

                return true;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void play() {
        if (!this.playing()) {
            Minecraft.getInstance().getSoundHandler().play(this.soundInstance);
            this.channel = VanillaSoundUtils.getChannelOfInstance(this.soundInstance);
            AudioHandler.updateVolumes();
        }
    }

    public boolean stopped() {
        if (this.channel != null) {
            return this.channel.isStopped();
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

    public boolean paused() {
        try {
            if (this.channel != null) {
                return (AL10.alGetSourcei(getChannelSource(), AL10.AL_SOURCE_STATE) == AL10.AL_PAUSED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void unpause() {
        if (this.channel != null) {
            this.channel.resume();
        }
    }

    public void stop() {
        if (this.soundInstance != null) {
            Minecraft.getInstance().getSoundHandler().stop(this.soundInstance);
        }
        this.channel = null;
    }

    public boolean playing() {
        if (this.channel != null) {
            return (this.getChannelState() == 4114);
        }
        return false;
    }

    private int getChannelState() {
        try {
            Method m = ObfuscationReflectionHelper.findMethod(SoundSource.class, "func_216428_j"); //getState()
            return (int) m.invoke(this.channel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void setLooping(boolean b) {
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

    public SoundCategory getSoundSource() {
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

    public SoundEventAccessor getSoundEvents() {
        return this.soundEvents;
    }

    public int getChannelSource() {
        try {
            Field f = ObfuscationReflectionHelper.findField(SoundSource.class, "field_216441_b"); //id
            return (int) f.get(this.channel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public enum SoundType {
        EXTERNAL_WEB,
        EXTERNAL_LOCAL,
        INTERNAL_ASSET;
    }

}
