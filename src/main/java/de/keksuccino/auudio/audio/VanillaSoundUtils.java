package de.keksuccino.auudio.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Map;

public class VanillaSoundUtils {

    @Nullable
    public static SoundSource getChannelOfInstance(ISound instance) {
        Map<ISound, ChannelManager.Entry> instances = getSoundEngineInstanceChannels();
        if (instances != null) {
            ChannelManager.Entry handle = instances.get(instance);
            if (handle != null) {
                return getChannelOfHandle(handle);
            }
        }
        return null;
    }

    @Nullable
    public static ISound getInstanceOfChannel(SoundSource channel) {
        Map<ISound, ChannelManager.Entry> instances = getSoundEngineInstanceChannels();
        if (instances != null) {
            for (Map.Entry<ISound, ChannelManager.Entry> m : instances.entrySet()) {
                SoundSource c = getChannelOfHandle(m.getValue());
                if (c != null) {
                    if (c == channel) {
                        return m.getKey();
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    public static SoundSource getChannelOfHandle(ChannelManager.Entry handle) {
        try {
            Field f = ObfuscationReflectionHelper.findField(ChannelManager.Entry.class, "field_217893_b"); //source
            return (SoundSource) f.get(handle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static Map<ISound, ChannelManager.Entry> getSoundEngineInstanceChannels() {
        try {
            Field f = ObfuscationReflectionHelper.findField(SoundEngine.class, "field_217942_m"); //playingSoundsChannel
            return (Map<ISound, ChannelManager.Entry>) f.get(getSoundEngine());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static SoundEngine getSoundEngine() {
        try {
            Field f = ObfuscationReflectionHelper.findField(SoundHandler.class, "field_147694_f"); //sndManager
            return (SoundEngine) f.get(Minecraft.getInstance().getSoundHandler());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static Map<ResourceLocation, SoundEventAccessor> getSoundManagerRegistry() {
        try {
            Field f = ObfuscationReflectionHelper.findField(SoundHandler.class, "field_147697_e"); //soundRegistry
            return (Map<ResourceLocation, SoundEventAccessor>) f.get(Minecraft.getInstance().getSoundHandler());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
