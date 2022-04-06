package de.keksuccino.auudio.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Overlay;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class AudioHandler {

    private static final Logger LOGGER = LogManager.getLogger("auudio/AudioHandler");

    private static List<AudioClip> clips = new ArrayList<>();

    //TODO übernehmen
    private static List<AudioClip> wasPlayingLastTick = new ArrayList<>();

    protected static Overlay lastOverlay = null;

    public static void init() {

        MinecraftForge.EVENT_BUS.register(new AudioHandler());

        LOGGER.info("Initialized!");

    }

    public static void registerAudioClip(AudioClip clip) {
        if (!clips.contains(clip)) {
            clips.add(clip);
        }
    }

    public static void unregisterAudioClip(AudioClip clip) {
        if (clips.contains(clip)) {
            clip.destroy();
        }
    }

    public static void updateVolumes() {
        Minecraft.getInstance().getSoundManager().updateSourceVolume(SoundSource.MASTER, Minecraft.getInstance().options.getSoundSourceVolume(SoundSource.MASTER));
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent e) {

        //TODO übernehmen
        for (AudioClip c : clips) {

            //Handle looping
            if (c.isLooping() && wasPlayingLastTick.contains(c)) {
                if (!c.playing() && (c.channel != null) && !c.paused()) {
                    c.stop();
                    c.play();
                }
            }

            if (!c.playing()) {
                wasPlayingLastTick.remove(c);
            } else if (!wasPlayingLastTick.contains(c)) {
                wasPlayingLastTick.add(c);
            }

        }

        if ((lastOverlay != null) && (Minecraft.getInstance().getOverlay() == null)) {
            LOGGER.info("Reloading sounds!");
            for (AudioClip c : clips) {
                c.prepare();
            }
        }
        lastOverlay = Minecraft.getInstance().getOverlay();

    }

}
