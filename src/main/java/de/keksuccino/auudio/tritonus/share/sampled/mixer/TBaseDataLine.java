package de.keksuccino.auudio.tritonus.share.sampled.mixer;

import java.util.Collection;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;

public abstract class TBaseDataLine extends TDataLine {

   public TBaseDataLine(TMixer mixer, DataLine.Info info) {
      super(mixer, info);
   }

   public TBaseDataLine(TMixer mixer, DataLine.Info info, Collection controls) {
      super(mixer, info, controls);
   }

   public void open(AudioFormat format, int nBufferSize) throws LineUnavailableException {

      this.setBufferSize(nBufferSize);
      this.open(format);
   }

   public void open(AudioFormat format) throws LineUnavailableException {

      this.setFormat(format);
      this.open();
   }

   protected void finalize() {
      if(this.isOpen()) {
         this.close();
      }

   }
}
