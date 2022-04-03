package de.keksuccino.auudio.tritonus.share.midi;

import javax.sound.midi.MidiChannel;

public abstract class TMidiChannel implements MidiChannel {

   private int m_nChannel;


   protected TMidiChannel(int nChannel) {
      this.m_nChannel = nChannel;
   }

   protected int getChannel() {
      return this.m_nChannel;
   }

   public void noteOff(int nNoteNumber) {
      this.noteOff(nNoteNumber, 0);
   }

   public void programChange(int nBank, int nProgram) {
      int nBankMSB = nBank >> 7;
      int nBankLSB = nBank & 127;
      this.controlChange(0, nBankMSB);
      this.controlChange(32, nBankLSB);
      this.programChange(nProgram);
   }

   public void resetAllControllers() {
      this.controlChange(121, 0);
   }

   public void allNotesOff() {
      this.controlChange(123, 0);
   }

   public void allSoundOff() {
      this.controlChange(120, 0);
   }

   public boolean localControl(boolean bOn) {
      this.controlChange(122, bOn?127:0);
      return this.getController(122) >= 64;
   }

   public void setMono(boolean bMono) {
      this.controlChange(bMono?126:127, 0);
   }

   public boolean getMono() {
      return this.getController(126) == 0;
   }

   public void setOmni(boolean bOmni) {
      this.controlChange(bOmni?125:124, 0);
   }

   public boolean getOmni() {
      return this.getController(125) == 0;
   }
}
