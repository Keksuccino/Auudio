package de.keksuccino.auudio.audio.external;

import de.keksuccino.auudio.audio.AudioClip;
import de.keksuccino.auudio.input.CharacterFilter;
import net.minecraft.util.ResourceLocation;

public class ExternalSoundResourceLocation extends ResourceLocation {

    protected String soundPath;
    protected AudioClip.SoundType soundType;

    public ExternalSoundResourceLocation(String soundPath, AudioClip.SoundType soundType) {
        super("", filterName(soundPath));
        this.soundPath = soundPath;
        this.soundType = soundType;
    }

    @Override
    public String getPath() {
        return this.soundPath;
    }

    public AudioClip.SoundType getSoundType() {
        return soundType;
    }

    public static String filterName(String name) {
        return CharacterFilter.getBasicFilenameCharacterFilter().filterForAllowedChars(name);
    }

}
