package de.keksuccino.auudio.audio;

import com.mojang.blaze3d.audio.Channel;
import de.keksuccino.auudio.mixin.client.IMixinChannelHandle;
import de.keksuccino.auudio.mixin.client.IMixinSoundEngine;
import de.keksuccino.auudio.mixin.client.IMixinSoundManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

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
        return ((IMixinChannelHandle)handle).getChannel();
    }

    @Nullable
    public static Map<SoundInstance, ChannelAccess.ChannelHandle> getSoundEngineInstanceChannels() {
        return ((IMixinSoundEngine)getSoundEngine()).getInstanceToChannel();
    }

    @Nullable
    public static SoundEngine getSoundEngine() {
        return ((IMixinSoundManager)Minecraft.getInstance().getSoundManager()).getSoundEngine();
    }

    @Nullable
    public static Map<ResourceLocation, WeighedSoundEvents> getSoundManagerRegistry() {
        return ((IMixinSoundManager)Minecraft.getInstance().getSoundManager()).getRegistry();
    }

}
