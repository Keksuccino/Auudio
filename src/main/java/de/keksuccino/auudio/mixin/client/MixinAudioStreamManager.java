package de.keksuccino.auudio.mixin.client;

import de.keksuccino.auudio.audio.AudioClipInputStream;
import de.keksuccino.auudio.audio.AudioClip;
import de.keksuccino.auudio.audio.exceptions.InvalidAudioException;
import de.keksuccino.auudio.audio.external.ExternalSoundResourceLocation;
import net.minecraft.client.audio.AudioStreamManager;
import net.minecraft.client.audio.IAudioStream;
import net.minecraft.client.audio.OggAudioStream;
import net.minecraft.client.audio.OggAudioStreamWrapper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Mixin(AudioStreamManager.class)
public class MixinAudioStreamManager {

    private static final Logger MIXIN_LOGGER = LogManager.getLogger("auudio/mixin/SoundBufferLibrary");

    @Inject(at = @At("HEAD"), method = "createStreamingResource", cancellable = true)
    private void onGetStream(ResourceLocation location, boolean loop, CallbackInfoReturnable<CompletableFuture<IAudioStream>> info) {
        if (location instanceof ExternalSoundResourceLocation) {

            AudioClip.SoundType locationSoundType = ((ExternalSoundResourceLocation)location).getSoundType();

            info.setReturnValue(
                    CompletableFuture.supplyAsync(() -> {
                        try {

                            InputStream inputstream = null;

                            if (locationSoundType == AudioClip.SoundType.EXTERNAL_WEB) {

                                URL u = new URL(location.getPath());
                                HttpURLConnection http = (HttpURLConnection) u.openConnection();
                                http.addRequestProperty("User-Agent", "Mozilla/4.0");
                                inputstream = new AudioClipInputStream(http.getInputStream(), location.getPath(), AudioClip.SoundType.EXTERNAL_WEB);

                            } else if (locationSoundType == AudioClip.SoundType.EXTERNAL_LOCAL) {

                                File f = new File(location.getPath());
                                if (f.isFile() && f.getPath().toLowerCase().endsWith(".ogg")) {
                                    inputstream = new FileInputStream(f);
                                } else {
                                    throw new InvalidAudioException("File not found or not a valid OGG file!");
                                }

                            }

                            if (inputstream != null) {
                                return (loop ? new OggAudioStreamWrapper(OggAudioStream::new, inputstream) : new OggAudioStream(inputstream));
                            } else {
                                throw new NullPointerException("Unable to get input stream for sound! Input stream is NULL!");
                            }

                        } catch (Exception ex) {
                            MIXIN_LOGGER.error("Error while trying to get input stream for external sound! (" + locationSoundType.name() + ")");
//                            ex.printStackTrace();
                            throw new CompletionException(ex);
                        }
                    }, Util.getServerExecutor())
            );

        }
    }

}
