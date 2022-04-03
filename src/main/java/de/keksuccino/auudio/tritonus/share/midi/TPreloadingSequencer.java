package de.keksuccino.auudio.tritonus.share.midi;

import java.util.Collection;
import javax.sound.midi.MidiMessage;

public abstract class TPreloadingSequencer extends TSequencer {

   private static final int DEFAULT_LATENCY = 100;
   private int m_nLatency;
   private Thread m_loaderThread;


   protected TPreloadingSequencer(de.keksuccino.auudio.tritonus.share.midi.TMidiDevice.Info info, Collection masterSyncModes, Collection slaveSyncModes) {
      super(info, masterSyncModes, slaveSyncModes);

      this.m_nLatency = 100;

   }

   public void setLatency(int nLatency) {
      this.m_nLatency = nLatency;
   }

   public int getLatency() {
      return this.m_nLatency;
   }

   protected void openImpl() {

   }

   public abstract void sendMessageTick(MidiMessage var1, long var2);
}
