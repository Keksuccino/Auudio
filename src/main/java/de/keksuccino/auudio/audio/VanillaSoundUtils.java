package de.keksuccino.auudio.audio;

import com.mojang.blaze3d.audio.Channel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.Map;

public class VanillaSoundUtils {

    @Nullable
    public static Channel getChannelOfInstance(SoundInstance instance) {
        Map<SoundInstance, ChannelAccess.ChannelHandle> instances = getSoundEngineInstanceChannels();
        if (instances != null) {
            ChannelAccess.ChannelHandle handle = instances.get(instance);
            if (handle != null) {
                return getChannelOfHandle(handle);
            }
        }
        return null;
    }

    @Nullable
    public static SoundInstance getInstanceOfChannel(Channel channel) {
        Map<SoundInstance, ChannelAccess.ChannelHandle> instances = getSoundEngineInstanceChannels();
        if (instances != null) {
            for (Map.Entry<SoundInstance, ChannelAccess.ChannelHandle> m : instances.entrySet()) {
                Channel c = getChannelOfHandle(m.getValue());
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
    public static Channel getChannelOfHandle(ChannelAccess.ChannelHandle handle) {
        try {
            Field f = ObfuscationReflectionHelper.findField(ChannelAccess.ChannelHandle.class, "f_120146_"); //channel
            return (Channel) f.get(handle);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static Map<SoundInstance, ChannelAccess.ChannelHandle> getSoundEngineInstanceChannels() {
        try {
            Field f = ObfuscationReflectionHelper.findField(SoundEngine.class, "f_120226_"); //instanceToChannel
            return (Map<SoundInstance, ChannelAccess.ChannelHandle>) f.get(getSoundEngine());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static SoundEngine getSoundEngine() {
        try {
            Field f = ObfuscationReflectionHelper.findField(SoundManager.class, "f_120349_"); //soundEngine
            return (SoundEngine) f.get(Minecraft.getInstance().getSoundManager());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    public static Map<ResourceLocation, WeighedSoundEvents> getSoundManagerRegistry() {
        try {
            Field f = ObfuscationReflectionHelper.findField(SoundManager.class, "f_120348_"); //registry
            return (Map<ResourceLocation, WeighedSoundEvents>) f.get(Minecraft.getInstance().getSoundManager());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
