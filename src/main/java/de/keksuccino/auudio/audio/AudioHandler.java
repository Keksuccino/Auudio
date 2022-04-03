package de.keksuccino.auudio.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AudioHandler {

    private static List<AudioClip> clips = new ArrayList<>();

    private static Map<SoundSource, Float> cachedChannelVolumes = new HashMap<>();

    public static void init() {

        MinecraftForge.EVENT_BUS.register(new AudioHandler());

    }

    public static void registerAudioClip(AudioClip clip) {
        if (!clips.contains(clip)) {
            clips.add(clip);
        }
    }

    public static void unregisterAudioClip(AudioClip clip) {
        if (clips.contains(clip)) {
            clips.remove(clip);
        }
    }

    public static void updateAudioClipVolume(AudioClip clip) {

        int newVol;
        int baseVol = clip.getBaseVolume(); //100% for MC sound category
        SoundSource source = convertToMinecraftSoundCategory(clip.channel);
        if (source == null) {
            source = SoundSource.MASTER;
        }
        int mcVol = (int) (Minecraft.getInstance().options.getSoundSourceVolume(source) * 100);
        double baseVolPercent = ((double)baseVol) / 100.0D;
        newVol = (int) (baseVolPercent * mcVol);

        if (source != SoundSource.MASTER) {
            int masterVol = (int) (Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MASTER) * 100);
            double mainSourcePercent = ((double)newVol) / 100.0D;
            newVol = (int) (mainSourcePercent * masterVol);
        }

        clip.setVolume(newVol);

    }

    protected static SoundSource convertToMinecraftSoundCategory(AudioChannel channel) {
        try {
            for (SoundSource s : SoundSource.values()) {
                if (s.getName().equalsIgnoreCase(channel.getName())) {
                    return s;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    protected static AudioChannel convertToAudioChannel(SoundSource source) {
        AudioChannel channel = null;
        try {
            channel = AudioChannel.getForName(source.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return channel;
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e) {

        for (AudioClip c : clips) {
            if (c.hasFinishedPlaying()) {
                c.stop();
                c.restart();
            }
        }

        //Check for volume changes
        if (!cachedChannelVolumes.isEmpty()) {
            for (SoundSource s : SoundSource.values()) {
                AudioChannel c = convertToAudioChannel(s);
                if (c != null) {
                    float currentVol = Minecraft.getInstance().options.getSoundSourceVolume(s);
                    float lastVol = cachedChannelVolumes.get(s);
                    if (currentVol != lastVol) {
                        for (AudioClip clip : clips) {
                            if ((clip.getAudioChannel() == c) || (c == AudioChannel.MASTER)) {
                                updateAudioClipVolume(clip);
                            }
                        }
                    }
                }
            }
        }

        //Update cached values for next tick
        for (SoundSource s : SoundSource.values()) {
            cachedChannelVolumes.put(s, Minecraft.getInstance().options.getSoundSourceVolume(s));
        }

    }

}
