package de.keksuccino.auudio.audio;

import net.minecraft.client.audio.Sound;
import net.minecraft.util.ResourceLocation;

public class AudioClipSound extends Sound {

    protected ResourceLocation location;

    public AudioClipSound(ResourceLocation location, float volume, float pitch, int weight, Type type, boolean stream, boolean preload, int attenuationDistance) {
        super("", volume, pitch, weight, type, stream, preload, attenuationDistance);
        this.location = location;
    }

    @Override
    public ResourceLocation getSoundLocation() {
        return this.location;
    }

    @Override
    public ResourceLocation getSoundAsOggLocation() {
        return this.location;
    }

    @Override
    public String toString() {
        return "Sound[" + this.location + "]";
    }

}
