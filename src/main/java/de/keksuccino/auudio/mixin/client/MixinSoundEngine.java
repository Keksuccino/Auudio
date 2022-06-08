package de.keksuccino.auudio.mixin.client;

import de.keksuccino.auudio.audio.AudioClipSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.ChannelAccess;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.sounds.SoundSource;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(SoundEngine.class)
public abstract class MixinSoundEngine {

    @Shadow @Final private Map<SoundInstance, ChannelAccess.ChannelHandle> instanceToChannel;

    @Inject(at = @At("RETURN"), method = "updateCategoryVolume")
    private void onUpdateCategoryVolume(SoundSource source, float volume, CallbackInfo info) {
        //TODO maybe check for SoundEngine.loaded, before changing volumes, if important
        this.instanceToChannel.forEach((instance, handle) -> {
            if (instance instanceof AudioClipSoundInstance) {
                float f = tweakVol(instance, this.calculateVolume(instance));
                handle.execute((channel) -> {
                    channel.setVolume(f);
                });
            }
        });
    }

    /**
     * Used to calculate the new sound volume while respecting the "base volume" of the sound
     */
    private float tweakVol(SoundInstance instance, float vol) {
        if (instance instanceof AudioClipSoundInstance) {
            int audioClipVolumePercentage = ((AudioClipSoundInstance)instance).getParent().getVolume();
            float audioClipOnePercent = (float)audioClipVolumePercentage / 100.0F;
            int mcVolumePercentage = (int)(vol * 100.0F);
            int finalPercentage = (int)(audioClipOnePercent * ((float)mcVolumePercentage));
            float finalVolume = ((float)finalPercentage) / 100.0F;
            return finalVolume;
        }
        return vol;
    }

    @Shadow protected abstract float calculateVolume(SoundInstance p_120328_);

}
