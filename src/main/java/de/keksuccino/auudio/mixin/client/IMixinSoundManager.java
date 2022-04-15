package de.keksuccino.auudio.mixin.client;

import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(SoundManager.class)
public interface IMixinSoundManager {

    @Accessor("soundEngine") public SoundEngine getSoundEngineAuudio();

    @Accessor("registry") public Map<ResourceLocation, WeighedSoundEvents> getRegistryAuudio();

}
