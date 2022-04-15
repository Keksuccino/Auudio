package de.keksuccino.auudio.mixin.client;

import com.mojang.blaze3d.audio.Channel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Channel.class)
public interface IMixinChannel {

    @Accessor("source") public int getSourceAuudio();

    @Invoker("getState") public abstract int getChannelStateInvokerAuudio();

}
