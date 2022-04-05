package de.keksuccino.auudio.audio;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class AudioClipSound extends Sound {

    protected ResourceLocation location;

    public AudioClipSound(ResourceLocation location, float volume, float pitch, int weight, Type type, boolean stream, boolean preload, int attenuationDistance) {
        super("", volume, pitch, weight, type, stream, preload, attenuationDistance);
        this.location = location;
    }

    @Override
    public @NotNull ResourceLocation getLocation() {
        return this.location;
    }

    @Override
    public ResourceLocation getPath() {
        return this.location;
    }

    @Override
    public String toString() {
        return "Sound[" + this.location + "]";
    }

}
