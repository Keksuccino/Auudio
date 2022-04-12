package de.keksuccino.auudio.mixin.client;

import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(SoundEngine.class)
public interface IMixinSoundEngine {

    @Accessor public Map<SoundInstance, ChannelAccess.ChannelHandle> getInstanceToChannel();

}
