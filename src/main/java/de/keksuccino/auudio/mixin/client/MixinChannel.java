package de.keksuccino.auudio.mixin.client;

import com.mojang.blaze3d.audio.Channel;
import de.keksuccino.auudio.Auudio;
import de.keksuccino.auudio.audio.VanillaSoundUtils;
import de.keksuccino.auudio.audio.external.ExternalSimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundEngineExecutor;
import net.minecraft.util.thread.BlockableEventLoop;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

//TODO remove debug
@Mixin(Channel.class)
public class MixinChannel {

    @Shadow @Nullable private AudioStream stream;

//    @Inject(at = @At("HEAD"), method = "destroy")
//    private void onDestroy(CallbackInfo info) {
//        if (this.stream != null) {
//            Auudio.LOGGER.info("########## Channel: destroy(): " + this.stream);
//            for (StackTraceElement e : new Throwable().getStackTrace()) {
//                Auudio.LOGGER.info(e.toString());
//            }
//        }
//    }

//    @Inject(at = @At("HEAD"), method = "play")
//    private void onPlay(CallbackInfo info) {
//        SoundInstance si = VanillaSoundUtils.getInstanceOfChannel((Channel)((Object)this));
//        if ((si != null) && (si instanceof ExternalSimpleSoundInstance)) {
//            Auudio.LOGGER.info("########## Channel: play(): " + ((ExternalSimpleSoundInstance)si).getParent().getSoundPath());
//        }
//    }
//
//    @Inject(at = @At("HEAD"), method = "getState")
//    private void onGetState(CallbackInfoReturnable<Integer> cir) {
//        SoundInstance si = VanillaSoundUtils.getInstanceOfChannel((Channel)((Object)this));
//        if ((si != null) && (si instanceof ExternalSimpleSoundInstance)) {
//            Auudio.LOGGER.info("########## Channel: getState(): " + ((ExternalSimpleSoundInstance)si).getParent().getSoundPath());
//        }
//    }

//    @Inject(at = @At("HEAD"), method = "pause")
//    private void onPause(CallbackInfo info) {
//        Auudio.LOGGER.info("########## Channel: pause(): " + this.stream);
//    }

//    @Inject(at = @At("HEAD"), method = "stop")
//    private void onStop(CallbackInfo info) {
//        if (this.stream != null) {
//            Auudio.LOGGER.info("########## Channel: stop(): " + this.stream);
//            for (StackTraceElement e : new Throwable().getStackTrace()) {
//                Auudio.LOGGER.info(e.toString());
//            }
//        }
//    }

}
