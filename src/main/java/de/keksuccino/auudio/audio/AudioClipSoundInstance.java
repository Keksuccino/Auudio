package de.keksuccino.auudio.audio;

import net.minecraft.client.audio.SimpleSound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;

public class AudioClipSoundInstance extends SimpleSound {

    protected AudioClip parent;

    public AudioClipSoundInstance(AudioClip parent, ResourceLocation location, SoundCategory source, float volume, float pitch, boolean b1, int i1, AttenuationType attenuation, double d1, double d2, double d3, boolean b2) {
        super(location, source, volume, pitch, b1, i1, attenuation, d1, d2, d3, b2);
        this.parent = parent;
    }

    public AudioClipSoundInstance(AudioClip parent, ResourceLocation location, SoundCategory source, float volume, float pitch) {
        this(parent, location, source, volume, pitch, false, 0, AttenuationType.NONE, 0.0D, 0.0D, 0.0D, true);
    }

    public AudioClip getParent() {
        return this.parent;
    }

}
