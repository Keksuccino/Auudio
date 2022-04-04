package de.keksuccino.auudio.mixin.client;

import com.mojang.blaze3d.audio.OggAudioStream;
import de.keksuccino.auudio.audio.external.ExternalSoundResourceLocation;
import net.minecraft.Util;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.LoopingAudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Mixin(SoundBufferLibrary.class)
public class MixinSoundBufferLibrary {

    @Inject(at = @At("HEAD"), method = "getStream", cancellable = true)
    private void onGetStream(ResourceLocation location, boolean loop, CallbackInfoReturnable<CompletableFuture<AudioStream>> info) {
        if (location instanceof ExternalSoundResourceLocation) {
            info.setReturnValue(
                    CompletableFuture.supplyAsync(() -> {
                        try {
                            File f = new File(location.getPath());
                            if (f.isFile() && f.getPath().toLowerCase().endsWith(".ogg")) {
                                InputStream inputstream = new FileInputStream(f);
                                return (loop ? new LoopingAudioStream(OggAudioStream::new, inputstream) : new OggAudioStream(inputstream));
                            } else {
                                throw new IOException("[AUUDIO] DYNAMIC SOUND LOCATION: File not found or not a valid OGG file!");
                            }
                        } catch (IOException ioexception) {
                            throw new CompletionException(ioexception);
                        }
                    }, Util.backgroundExecutor())
            );
        }
    }

}
