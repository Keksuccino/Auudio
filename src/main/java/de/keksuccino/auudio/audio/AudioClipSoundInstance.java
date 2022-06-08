package de.keksuccino.auudio.audio;

import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;

public class AudioClipSoundInstance extends SimpleSoundInstance {

    protected AudioClip parent;

    public AudioClipSoundInstance(AudioClip parent, ResourceLocation location, SoundSource source, float volume, float pitch, boolean b1, int i1, Attenuation attenuation, double d1, double d2, double d3, boolean b2) {
        //TODO EXPERIMENTAL (RandomSource.create)
        super(location, source, volume, pitch, RandomSource.create(), b1, i1, attenuation, d1, d2, d3, b2);
        this.parent = parent;
    }

    public AudioClipSoundInstance(AudioClip parent, ResourceLocation location, SoundSource source, float volume, float pitch) {
        this(parent, location, source, volume, pitch, false, 0, Attenuation.NONE, 0.0D, 0.0D, 0.0D, true);
    }

    public AudioClip getParent() {
        return this.parent;
    }

}
