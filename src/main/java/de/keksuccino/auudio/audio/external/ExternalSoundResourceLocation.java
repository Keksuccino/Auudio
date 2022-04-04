package de.keksuccino.auudio.audio.external;

import de.keksuccino.auudio.audio.AudioClip;
import net.minecraft.resources.ResourceLocation;

public class ExternalSoundResourceLocation extends ResourceLocation {

    protected String soundPath;
    protected AudioClip.AudioType audioType;

    public ExternalSoundResourceLocation(String soundPath, AudioClip.AudioType audioType) {
        super("", soundPath);
        this.soundPath = soundPath;
        this.audioType = audioType;
    }

    @Override
    public String getPath() {
        return this.soundPath;
    }

    public AudioClip.AudioType getAudioType() {
        return audioType;
    }

}
