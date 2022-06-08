//package de.keksuccino.auudio;
//
//import de.keksuccino.auudio.audio.AudioClip;
//import de.keksuccino.auudio.util.event.SubscribeEvent;
//import de.keksuccino.auudio.util.event.events.ClientTickEvent;
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.screens.OptionsScreen;
//import net.minecraft.client.gui.screens.TitleScreen;
//import net.minecraft.sounds.SoundSource;
//
//public class Test {
//
//    AudioClip clip = null;
//
//    boolean playing = false;
//
//    @SubscribeEvent
//    public void onClientTick(ClientTickEvent e) {
//
//        try {
//
//            if (Minecraft.getInstance().screen != null) {
//
//                if (Minecraft.getInstance().screen instanceof TitleScreen) {
//                    if (this.clip == null) {
//                        this.clip = AudioClip.buildExternalClip("test.ogg", AudioClip.SoundType.EXTERNAL_LOCAL, SoundSource.MASTER);
//                    }
//                    if (!playing) {
//                        this.clip.play();
//                        playing = true;
//                    }
//                } else if (Minecraft.getInstance().screen instanceof OptionsScreen) {
//                    if ((this.clip != null) && playing) {
//                        this.clip.stop();
//                        playing = false;
//                    }
//                }
//
//            }
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//    }
//
//}
