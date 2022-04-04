package de.keksuccino.auudio.audio.external;

import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class ExternalSound extends Sound {

    private final ResourceLocation location;

    public ExternalSound(ExternalSoundResourceLocation location, float volume, float pitch, int weight, Type type, boolean stream, boolean preload, int attenuationDistance) {
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
        return "Sound[" + this.location.getPath() + "]";
    }

}
