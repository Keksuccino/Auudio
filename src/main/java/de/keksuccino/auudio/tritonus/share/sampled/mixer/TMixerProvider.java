package de.keksuccino.auudio.tritonus.share.sampled.mixer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.spi.MixerProvider;

public abstract class TMixerProvider extends MixerProvider {

   private static final Info[] EMPTY_MIXER_INFO_ARRAY = new Info[0];
   private static Map sm_mixerProviderStructs = new HashMap();
   private boolean m_bDisabled = false;


   public TMixerProvider() {

   }

   protected void staticInit() {}

   private TMixerProvider.MixerProviderStruct getMixerProviderStruct() {

      Class cls = this.getClass();

      Class var2 = TMixerProvider.class;
      synchronized(TMixerProvider.class) {
         TMixerProvider.MixerProviderStruct struct = (TMixerProvider.MixerProviderStruct)sm_mixerProviderStructs.get(cls);
         if(struct == null) {

            struct = new TMixerProvider.MixerProviderStruct();
            sm_mixerProviderStructs.put(cls, struct);
         }

         return struct;
      }
   }

   protected void disable() {

      this.m_bDisabled = true;
   }

   protected boolean isDisabled() {
      return this.m_bDisabled;
   }

   protected void addMixer(Mixer mixer) {

      TMixerProvider.MixerProviderStruct struct = this.getMixerProviderStruct();
      synchronized(struct) {
         struct.m_mixers.add(mixer);
         if(struct.m_defaultMixer == null) {
            struct.m_defaultMixer = mixer;
         }
      }

   }

   protected void removeMixer(Mixer mixer) {

      TMixerProvider.MixerProviderStruct struct = this.getMixerProviderStruct();
      synchronized(struct) {
         struct.m_mixers.remove(mixer);
         if(struct.m_defaultMixer == mixer) {
            struct.m_defaultMixer = null;
         }
      }

   }

   public boolean isMixerSupported(Info info) {

      boolean bIsSupported = false;
      Info[] infos = this.getMixerInfo();

      for(int i = 0; i < infos.length; ++i) {
         if(infos[i].equals(info)) {
            bIsSupported = true;
            break;
         }
      }

      return bIsSupported;
   }

   public Mixer getMixer(Info info) {

      TMixerProvider.MixerProviderStruct struct = this.getMixerProviderStruct();
      Mixer mixerResult = null;
      synchronized(struct) {
         if(info == null) {
            mixerResult = struct.m_defaultMixer;
         } else {
            Iterator mixers = struct.m_mixers.iterator();

            while(mixers.hasNext()) {
               Mixer mixer = (Mixer)mixers.next();
               if(mixer.getMixerInfo().equals(info)) {
                  mixerResult = mixer;
                  break;
               }
            }
         }
      }

      if(mixerResult == null) {
         throw new IllegalArgumentException("no mixer available for " + info);
      } else {

         return mixerResult;
      }
   }

   public Info[] getMixerInfo() {

      HashSet mixerInfos = new HashSet();
      TMixerProvider.MixerProviderStruct struct = this.getMixerProviderStruct();
      synchronized(struct) {
         Iterator mixers = struct.m_mixers.iterator();

         while(true) {
            if(!mixers.hasNext()) {
               break;
            }

            Mixer mixer = (Mixer)mixers.next();
            mixerInfos.add(mixer.getMixerInfo());
         }
      }

      return (Info[])mixerInfos.toArray(EMPTY_MIXER_INFO_ARRAY);
   }


   private class MixerProviderStruct {

      public List m_mixers = new ArrayList();
      public Mixer m_defaultMixer = null;


   }
}
