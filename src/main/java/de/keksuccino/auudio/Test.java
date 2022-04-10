package de.keksuccino.auudio;

//import de.keksuccino.auudio.audio.AudioClip;
//import de.keksuccino.konkrete.gui.content.AdvancedButton;
//import net.minecraft.client.Minecraft;
//import net.minecraft.util.SoundCategory;
//import net.minecraftforge.client.event.GuiScreenEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Test {

//    AudioClip clip = null;
//
//    protected AdvancedButton button = new AdvancedButton(20, 20, 100, 20, "play sound", true, (press) -> {
//
//        if (this.clip == null) {
//            try {
////                this.clip = AudioClip.buildExternalClip("test.ogg", AudioClip.SoundType.EXTERNAL_LOCAL, SoundCategory.MASTER);
//                this.clip = AudioClip.buildExternalClip("https://file-examples.com/storage/fe1fca3bab62533d59c03ef/2017/11/file_example_OOG_2MG.ogg", AudioClip.SoundType.EXTERNAL_WEB, SoundCategory.MASTER);
////                this.clip = AudioClip.buildInternalClip(new ResourceLocation("auudio", "test_file.ogg"), SoundCategory.MUSIC);
//                this.clip.setVolume(30);
//                this.clip.setLooping(true);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        clip.play();
//
//    });
//    protected AdvancedButton button2 = new AdvancedButton(20, 45, 100, 20, "vol up", true, (press) -> {
//        if (this.clip != null) {
//            this.clip.setVolume(this.clip.getVolume() + 5);
//        }
//    });
//
//    protected AdvancedButton button3 = new AdvancedButton(20, 70, 100, 20, "vol down", true, (press) -> {
//        if (this.clip != null) {
//            this.clip.setVolume(this.clip.getVolume() - 5);
//        }
//    });
//
//    protected AdvancedButton button4 = new AdvancedButton(20, 95, 100, 20, "pause", true, (press) -> {
//        if (this.clip != null) {
//            this.clip.pause();
//        }
//    });
//
//    protected AdvancedButton button5 = new AdvancedButton(20, 120, 100, 20, "un-pause", true, (press) -> {
//        if (this.clip != null) {
//            this.clip.unpause();
//        }
//    });
//
//    protected AdvancedButton button6 = new AdvancedButton(20, 145, 100, 20, "stop", true, (press) -> {
//        if (this.clip != null) {
//            this.clip.stop();
//        }
//    });
//
//    @SubscribeEvent
//    public void onDrawScreen(GuiScreenEvent.DrawScreenEvent.Post e) {
//
//        if (this.clip != null) {
//            Minecraft.getInstance().fontRenderer.drawStringWithShadow(e.getMatrixStack(), "" + this.clip.getVolume(), 20, 10, -1);
//        }
//
//        button.render(e.getMatrixStack(), e.getMouseX(), e.getMouseY(), e.getRenderPartialTicks());
//        button2.render(e.getMatrixStack(), e.getMouseX(), e.getMouseY(), e.getRenderPartialTicks());
//        button3.render(e.getMatrixStack(), e.getMouseX(), e.getMouseY(), e.getRenderPartialTicks());
//        button4.render(e.getMatrixStack(), e.getMouseX(), e.getMouseY(), e.getRenderPartialTicks());
//        button5.render(e.getMatrixStack(), e.getMouseX(), e.getMouseY(), e.getRenderPartialTicks());
//        button6.render(e.getMatrixStack(), e.getMouseX(), e.getMouseY(), e.getRenderPartialTicks());
//
//    }

}
