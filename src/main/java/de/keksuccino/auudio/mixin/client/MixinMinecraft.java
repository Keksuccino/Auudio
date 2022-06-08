package de.keksuccino.auudio.mixin.client;

import de.keksuccino.auudio.Auudio;
import de.keksuccino.auudio.util.event.events.ClientTickEvent;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {

    @Inject(at = @At("HEAD"), method = "tick")
    private void onTick(CallbackInfo info) {
        Auudio.EVENT_HANDLER.callEventsFor(new ClientTickEvent());
    }

}
