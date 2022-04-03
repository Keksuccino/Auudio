package de.keksuccino.auudio.tritonus.share.sampled;

import java.util.Collection;
import java.util.Iterator;
import javax.sound.sampled.AudioFormat;
import de.keksuccino.auudio.tritonus.share.ArraySet;

public class AudioFormatSet extends ArraySet {

   private static final long serialVersionUID = 1L;
   protected static final AudioFormat[] EMPTY_FORMAT_ARRAY = new AudioFormat[0];


   public AudioFormatSet() {}

   public AudioFormatSet(Collection c) {
      super(c);
   }

   public boolean add(AudioFormat elem) {
      return elem == null?false:super.add(elem);
   }

   public boolean contains(AudioFormat elem) {
      if(elem == null) {
         return false;
      } else {
         AudioFormat comp = elem;
         Iterator it = this.iterator();

         do {
            if(!it.hasNext()) {
               return false;
            }
         } while(!AudioFormats.equals(comp, (AudioFormat)it.next()));

         return true;
      }
   }

   public AudioFormat get(AudioFormat elem) {
      if(elem == null) {
         return null;
      } else {
         AudioFormat comp = elem;
         Iterator it = this.iterator();

         AudioFormat thisElem;
         do {
            if(!it.hasNext()) {
               return null;
            }

            thisElem = (AudioFormat)it.next();
         } while(!AudioFormats.equals(comp, thisElem));

         return thisElem;
      }
   }

   public AudioFormat getAudioFormat(AudioFormat elem) {
      return this.get(elem);
   }

   public AudioFormat matches(AudioFormat elem) {
      if(elem == null) {
         return null;
      } else {
         Iterator it = this.iterator();

         AudioFormat thisElem;
         do {
            if(!it.hasNext()) {
               return null;
            }

            thisElem = (AudioFormat)it.next();
         } while(!AudioFormats.matches(elem, thisElem));

         return thisElem;
      }
   }

   public AudioFormat[] toAudioFormatArray() {
      return (AudioFormat[])this.toArray(EMPTY_FORMAT_ARRAY);
   }

   public void add(int index, AudioFormat element) {
      throw new UnsupportedOperationException("unsupported");
   }

   public AudioFormat set(int index, AudioFormat element) {
      throw new UnsupportedOperationException("unsupported");
   }

}
