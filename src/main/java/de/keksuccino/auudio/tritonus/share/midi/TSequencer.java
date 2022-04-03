package de.keksuccino.auudio.tritonus.share.midi;

import java.io.IOException;
import java.io.InputStream;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import javax.sound.midi.ControllerEventListener;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;

import de.keksuccino.auudio.tritonus.share.ArraySet;

public abstract class TSequencer extends TMidiDevice implements Sequencer {

   private static final float MPQ_BPM_FACTOR = 6.0E7F;
   private static final SyncMode[] EMPTY_SYNCMODE_ARRAY = new SyncMode[0];
   private boolean m_bRunning = false;
   private Sequence m_sequence = null;
   private Set m_metaListeners = new ArraySet();
   private Set[] m_aControllerListeners = (Set[])(new Set[128]);
   private float m_fNominalTempoInMPQ;
   private float m_fTempoFactor;
   private Collection m_masterSyncModes;
   private Collection m_slaveSyncModes;
   private SyncMode m_masterSyncMode;
   private SyncMode m_slaveSyncMode;
   private BitSet m_muteBitSet;
   private BitSet m_soloBitSet;
   private BitSet m_enabledBitSet;
   private long m_lLoopStartPoint;
   private long m_lLoopEndPoint;
   private int m_nLoopCount;


   protected TSequencer(de.keksuccino.auudio.tritonus.share.midi.TMidiDevice.Info info, Collection masterSyncModes, Collection slaveSyncModes) {
      super(info);
      this.setTempoFactor(1.0F);
      this.setTempoInMPQ(500000.0F);
      this.m_masterSyncModes = masterSyncModes;
      this.m_slaveSyncModes = slaveSyncModes;
      if(this.getMasterSyncModes().length > 0) {
         this.m_masterSyncMode = this.getMasterSyncModes()[0];
      }

      if(this.getSlaveSyncModes().length > 0) {
         this.m_slaveSyncMode = this.getSlaveSyncModes()[0];
      }

      this.m_muteBitSet = new BitSet();
      this.m_soloBitSet = new BitSet();
      this.m_enabledBitSet = new BitSet();
      this.updateEnabled();
      this.setLoopStartPoint(0L);
      this.setLoopEndPoint(-1L);
      this.setLoopCount(0);
   }

   public void setSequence(Sequence sequence) throws InvalidMidiDataException {
      if(this.getSequence() != sequence) {
         this.m_sequence = sequence;
         this.setSequenceImpl();
         this.setTempoFactor(1.0F);
      }

   }

   protected void setSequenceImpl() {}

   public void setSequence(InputStream inputStream) throws InvalidMidiDataException, IOException {
      Sequence sequence = MidiSystem.getSequence(inputStream);
      this.setSequence(sequence);
   }

   public Sequence getSequence() {
      return this.m_sequence;
   }

   public void setLoopStartPoint(long lTick) {
      this.m_lLoopStartPoint = lTick;
   }

   public long getLoopStartPoint() {
      return this.m_lLoopStartPoint;
   }

   public void setLoopEndPoint(long lTick) {
      this.m_lLoopEndPoint = lTick;
   }

   public long getLoopEndPoint() {
      return this.m_lLoopEndPoint;
   }

   public void setLoopCount(int nLoopCount) {
      this.m_nLoopCount = nLoopCount;
   }

   public int getLoopCount() {
      return this.m_nLoopCount;
   }

   public synchronized void start() {
      this.checkOpen();
      if(!this.isRunning()) {
         this.m_bRunning = true;
         this.startImpl();
      }

   }

   protected void startImpl() {}

   public synchronized void stop() {
      this.checkOpen();
      if(this.isRunning()) {
         this.stopImpl();
         this.m_bRunning = false;
      }

   }

   protected void stopImpl() {}

   public synchronized boolean isRunning() {
      return this.m_bRunning;
   }

   protected void checkOpen() {
      if(!this.isOpen()) {
         throw new IllegalStateException("Sequencer is not open");
      }
   }

   protected int getResolution() {
      Sequence sequence = this.getSequence();
      int nResolution;
      if(sequence != null) {
         nResolution = sequence.getResolution();
      } else {
         nResolution = 1;
      }

      return nResolution;
   }

   protected void setRealTempo() {
      float fTempoFactor = this.getTempoFactor();
      if(fTempoFactor == 0.0F) {
         fTempoFactor = 0.01F;
      }

      float fRealTempo = this.getTempoInMPQ() / fTempoFactor;

      this.setTempoImpl(fRealTempo);
   }

   public float getTempoInBPM() {
      float fBPM = 6.0E7F / this.getTempoInMPQ();
      return fBPM;
   }

   public void setTempoInBPM(float fBPM) {
      float fMPQ = 6.0E7F / fBPM;
      this.setTempoInMPQ(fMPQ);
   }

   public float getTempoInMPQ() {
      return this.m_fNominalTempoInMPQ;
   }

   public void setTempoInMPQ(float fMPQ) {
      this.m_fNominalTempoInMPQ = fMPQ;
      this.setRealTempo();
   }

   public void setTempoFactor(float fFactor) {
      this.m_fTempoFactor = fFactor;
      this.setRealTempo();
   }

   public float getTempoFactor() {
      return this.m_fTempoFactor;
   }

   protected abstract void setTempoImpl(float var1);

   public long getTickLength() {
      long lLength = 0L;
      if(this.getSequence() != null) {
         lLength = this.getSequence().getTickLength();
      }

      return lLength;
   }

   public long getMicrosecondLength() {
      long lLength = 0L;
      if(this.getSequence() != null) {
         lLength = this.getSequence().getMicrosecondLength();
      }

      return lLength;
   }

   public boolean addMetaEventListener(MetaEventListener listener) {
      Set var2 = this.m_metaListeners;
      synchronized(this.m_metaListeners) {
         return this.m_metaListeners.add(listener);
      }
   }

   public void removeMetaEventListener(MetaEventListener listener) {
      Set var2 = this.m_metaListeners;
      synchronized(this.m_metaListeners) {
         this.m_metaListeners.remove(listener);
      }
   }

   protected Iterator getMetaEventListeners() {
      Set var1 = this.m_metaListeners;
      synchronized(this.m_metaListeners) {
         return this.m_metaListeners.iterator();
      }
   }

   protected void sendMetaMessage(MetaMessage message) {
      Iterator iterator = this.getMetaEventListeners();

      while(iterator.hasNext()) {
         MetaEventListener metaEventListener = (MetaEventListener)iterator.next();
         MetaMessage copiedMessage = (MetaMessage)message.clone();
         metaEventListener.meta(copiedMessage);
      }

   }

   public int[] addControllerEventListener(ControllerEventListener listener, int[] anControllers) {
      Set[] var3 = this.m_aControllerListeners;
      synchronized(this.m_aControllerListeners) {
         int i;
         if(anControllers == null) {
            for(i = 0; i < 128; ++i) {
               this.addControllerListener(i, listener);
            }
         } else {
            for(i = 0; i < anControllers.length; ++i) {
               this.addControllerListener(anControllers[i], listener);
            }
         }
      }

      return this.getListenedControllers(listener);
   }

   private void addControllerListener(int i, ControllerEventListener listener) {
      if(this.m_aControllerListeners[i] == null) {
         this.m_aControllerListeners[i] = new ArraySet();
      }

      this.m_aControllerListeners[i].add(listener);
   }

   public int[] removeControllerEventListener(ControllerEventListener listener, int[] anControllers) {
      Set[] var3 = this.m_aControllerListeners;
      synchronized(this.m_aControllerListeners) {
         int i;
         if(anControllers == null) {
            for(i = 0; i < 128; ++i) {
               this.removeControllerListener(i, listener);
            }
         } else {
            for(i = 0; i < anControllers.length; ++i) {
               this.removeControllerListener(anControllers[i], listener);
            }
         }
      }

      return this.getListenedControllers(listener);
   }

   private void removeControllerListener(int i, ControllerEventListener listener) {
      if(this.m_aControllerListeners[i] != null) {
         this.m_aControllerListeners[i].add(listener);
      }

   }

   private int[] getListenedControllers(ControllerEventListener listener) {
      int[] anControllers = new int[128];
      int nIndex = 0;

      for(int anResultControllers = 0; anResultControllers < 128; ++anResultControllers) {
         if(this.m_aControllerListeners[anResultControllers] != null && this.m_aControllerListeners[anResultControllers].contains(listener)) {
            anControllers[nIndex] = anResultControllers;
            ++nIndex;
         }
      }

      int[] var5 = new int[nIndex];
      System.arraycopy(anControllers, 0, var5, 0, nIndex);
      return var5;
   }

   protected void sendControllerEvent(ShortMessage message) {
      int nController = message.getData1();
      if(this.m_aControllerListeners[nController] != null) {
         Iterator iterator = this.m_aControllerListeners[nController].iterator();

         while(iterator.hasNext()) {
            ControllerEventListener controllerEventListener = (ControllerEventListener)iterator.next();
            ShortMessage copiedMessage = (ShortMessage)message.clone();
            controllerEventListener.controlChange(copiedMessage);
         }
      }

   }

   protected void notifyListeners(MidiMessage message) {
      if(message instanceof MetaMessage) {
         this.sendMetaMessage((MetaMessage)message);
      } else if(message instanceof ShortMessage && ((ShortMessage)message).getCommand() == 176) {
         this.sendControllerEvent((ShortMessage)message);
      }

   }

   public SyncMode getMasterSyncMode() {
      return this.m_masterSyncMode;
   }

   public void setMasterSyncMode(SyncMode syncMode) {
      if(this.m_masterSyncModes.contains(syncMode)) {
         if(!this.getMasterSyncMode().equals(syncMode)) {
            this.m_masterSyncMode = syncMode;
            this.setMasterSyncModeImpl(syncMode);
         }

      } else {
         throw new IllegalArgumentException("sync mode not allowed: " + syncMode);
      }
   }

   protected void setMasterSyncModeImpl(SyncMode syncMode) {}

   public SyncMode[] getMasterSyncModes() {
      SyncMode[] syncModes = (SyncMode[])this.m_masterSyncModes.toArray(EMPTY_SYNCMODE_ARRAY);
      return syncModes;
   }

   public SyncMode getSlaveSyncMode() {
      return this.m_slaveSyncMode;
   }

   public void setSlaveSyncMode(SyncMode syncMode) {
      if(this.m_slaveSyncModes.contains(syncMode)) {
         if(!this.getSlaveSyncMode().equals(syncMode)) {
            this.m_slaveSyncMode = syncMode;
            this.setSlaveSyncModeImpl(syncMode);
         }

      } else {
         throw new IllegalArgumentException("sync mode not allowed: " + syncMode);
      }
   }

   protected void setSlaveSyncModeImpl(SyncMode syncMode) {}

   public SyncMode[] getSlaveSyncModes() {
      SyncMode[] syncModes = (SyncMode[])this.m_slaveSyncModes.toArray(EMPTY_SYNCMODE_ARRAY);
      return syncModes;
   }

   public boolean getTrackSolo(int nTrack) {
      boolean bSoloed = false;
      if(this.getSequence() != null && nTrack < this.getSequence().getTracks().length) {
         bSoloed = this.m_soloBitSet.get(nTrack);
      }

      return bSoloed;
   }

   public void setTrackSolo(int nTrack, boolean bSolo) {
      if(this.getSequence() != null && nTrack < this.getSequence().getTracks().length) {
         boolean bOldState = this.m_soloBitSet.get(nTrack);
         if(bSolo != bOldState) {
            if(bSolo) {
               this.m_soloBitSet.set(nTrack);
            } else {
               this.m_soloBitSet.clear(nTrack);
            }

            this.updateEnabled();
            this.setTrackSoloImpl(nTrack, bSolo);
         }
      }

   }

   protected void setTrackSoloImpl(int nTrack, boolean bSolo) {}

   public boolean getTrackMute(int nTrack) {
      boolean bMuted = false;
      if(this.getSequence() != null && nTrack < this.getSequence().getTracks().length) {
         bMuted = this.m_muteBitSet.get(nTrack);
      }

      return bMuted;
   }

   public void setTrackMute(int nTrack, boolean bMute) {
      if(this.getSequence() != null && nTrack < this.getSequence().getTracks().length) {
         boolean bOldState = this.m_muteBitSet.get(nTrack);
         if(bMute != bOldState) {
            if(bMute) {
               this.m_muteBitSet.set(nTrack);
            } else {
               this.m_muteBitSet.clear(nTrack);
            }

            this.updateEnabled();
            this.setTrackMuteImpl(nTrack, bMute);
         }
      }

   }

   protected void setTrackMuteImpl(int nTrack, boolean bMute) {}

   private void updateEnabled() {
      BitSet oldEnabledBitSet = (BitSet)this.m_enabledBitSet.clone();
      boolean bSoloExists = this.m_soloBitSet.length() > 0;
      int i;
      if(bSoloExists) {
         this.m_enabledBitSet = (BitSet)this.m_soloBitSet.clone();
      } else {
         for(i = 0; i < this.m_muteBitSet.size(); ++i) {
            if(this.m_muteBitSet.get(i)) {
               this.m_enabledBitSet.clear(i);
            } else {
               this.m_enabledBitSet.set(i);
            }
         }
      }

      oldEnabledBitSet.xor(this.m_enabledBitSet);

      for(i = 0; i < oldEnabledBitSet.size(); ++i) {
         if(oldEnabledBitSet.get(i)) {
            this.setTrackEnabledImpl(i, this.m_enabledBitSet.get(i));
         }
      }

   }

   protected void setTrackEnabledImpl(int nTrack, boolean bEnabled) {}

   protected boolean isTrackEnabled(int nTrack) {
      return this.m_enabledBitSet.get(nTrack);
   }

   public void setLatency(int nMilliseconds) {}

   public int getLatency() {
      return -1;
   }

}
