package de.keksuccino.auudio.audio.external;

import de.keksuccino.auudio.audio.AudioClipSound;

public class ExternalSound extends AudioClipSound {

    public ExternalSound(ExternalSoundResourceLocation location, float volume, float pitch, int weight, Type type, boolean preload, int attenuationDistance) {
        //It's important to set 'stream' to 'true', otherwise the system can't handle external sounds
        super(location, volume, pitch, weight, type, true, preload, attenuationDistance);
    }

    @Override
    public String toString() {
        return "Sound[" + this.location.getPath() + "]";
    }

}
