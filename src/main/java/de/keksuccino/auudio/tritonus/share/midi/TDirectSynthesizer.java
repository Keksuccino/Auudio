package de.keksuccino.auudio.tritonus.share.midi;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.Synthesizer;

public abstract class TDirectSynthesizer extends TMidiDevice implements Synthesizer {

   public TDirectSynthesizer(TMidiDevice.Info info) {
      super(info, false, true);
   }

   private MidiChannel getChannel(int nChannel) {
      return this.getChannels()[nChannel];
   }

   protected void receive(MidiMessage message, long lTimeStamp) {
      if(message instanceof ShortMessage) {
         ShortMessage shortMsg = (ShortMessage)message;
         int nChannel = shortMsg.getChannel();
         int nCommand = shortMsg.getCommand();
         int nData1 = shortMsg.getData1();
         int nData2 = shortMsg.getData2();
         switch(nCommand) {
         case 128:
            this.getChannel(nChannel).noteOff(nData1, nData2);
            break;
         case 144:
            this.getChannel(nChannel).noteOn(nData1, nData2);
            break;
         case 160:
            this.getChannel(nChannel).setPolyPressure(nData1, nData2);
            break;
         case 176:
            this.getChannel(nChannel).controlChange(nData1, nData2);
            break;
         case 192:
            this.getChannel(nChannel).programChange(nData1);
            break;
         case 208:
            this.getChannel(nChannel).setChannelPressure(nData1);
            break;
         case 224:
            this.getChannel(nChannel).setPitchBend(nData1 | nData2 << 7);
         }
      }

   }
}
