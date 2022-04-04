package de.keksuccino.auudio;

import de.keksuccino.auudio.audio.AudioClip;
import de.keksuccino.konkrete.gui.content.AdvancedButton;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EventHandler {

    AudioClip clip = null;

    protected AdvancedButton button = new AdvancedButton(20, 20, 100, 20, "play sound", true, (press) -> {

        if (this.clip == null) {
            this.clip = new AudioClip("test_short.ogg", AudioClip.AudioType.LOCAL, SoundSource.MUSIC);
            this.clip.setVolume(30);
            this.clip.setLooping(true);
        }
        clip.play();

    });

    protected AdvancedButton button2 = new AdvancedButton(20, 45, 100, 20, "vol up", true, (press) -> {
        if (this.clip != null) {
            this.clip.setVolume(this.clip.getVolume() + 5);
        }
    });

    protected AdvancedButton button3 = new AdvancedButton(20, 70, 100, 20, "vol down", true, (press) -> {
        if (this.clip != null) {
            this.clip.setVolume(this.clip.getVolume() - 5);
        }
    });

    protected AdvancedButton button4 = new AdvancedButton(20, 95, 100, 20, "pause", true, (press) -> {
        if (this.clip != null) {
            this.clip.pause();
        }
    });

    protected AdvancedButton button5 = new AdvancedButton(20, 120, 100, 20, "un-pause", true, (press) -> {
        if (this.clip != null) {
            this.clip.unpause();
        }
    });

    protected AdvancedButton button6 = new AdvancedButton(20, 145, 100, 20, "stop", true, (press) -> {
        if (this.clip != null) {
            this.clip.stop();
        }
    });

    @SubscribeEvent
    public void onDrawScreen(ScreenEvent.DrawScreenEvent.Post e) {

        if (this.clip != null) {
            Minecraft.getInstance().font.drawShadow(e.getPoseStack(), "" + this.clip.getVolume(), 20, 10, -1);
        }

        button.render(e.getPoseStack(), e.getMouseX(), e.getMouseY(), e.getPartialTicks());
        button2.render(e.getPoseStack(), e.getMouseX(), e.getMouseY(), e.getPartialTicks());
        button3.render(e.getPoseStack(), e.getMouseX(), e.getMouseY(), e.getPartialTicks());
        button4.render(e.getPoseStack(), e.getMouseX(), e.getMouseY(), e.getPartialTicks());
        button5.render(e.getPoseStack(), e.getMouseX(), e.getMouseY(), e.getPartialTicks());
        button6.render(e.getPoseStack(), e.getMouseX(), e.getMouseY(), e.getPartialTicks());

    }

}
