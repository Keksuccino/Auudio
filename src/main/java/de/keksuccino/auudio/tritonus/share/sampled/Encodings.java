package de.keksuccino.auudio.tritonus.share.sampled;

import java.util.Iterator;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat.Encoding;
import de.keksuccino.auudio.tritonus.share.StringHashedSet;

public class Encodings extends Encoding {

   private static StringHashedSet encodings = new StringHashedSet();


   Encodings(String name) {
      super(name);
   }

   public static Encoding getEncoding(String name) {
      Object res = (Encoding)encodings.get(name);
      if(res == null) {
         res = new Encodings(name);
         encodings.add(res);
      }

      return (Encoding)res;
   }

   public static boolean equals(Encoding e1, Encoding e2) {
      return e2.toString().equals(e1.toString());
   }

   public static Encoding[] getEncodings() {
      StringHashedSet iteratedSources = new StringHashedSet();
      StringHashedSet retrievedTargets = new StringHashedSet();
      Iterator sourceFormats = encodings.iterator();

      while(sourceFormats.hasNext()) {
         Encoding source = (Encoding)sourceFormats.next();
         iterateEncodings(source, iteratedSources, retrievedTargets);
      }

      return (Encoding[])retrievedTargets.toArray(new Encoding[retrievedTargets.size()]);
   }

   private static void iterateEncodings(Encoding source, StringHashedSet iteratedSources, StringHashedSet retrievedTargets) {
      if(!iteratedSources.contains(source)) {
         iteratedSources.add(source);
         Encoding[] targets = AudioSystem.getTargetEncodings(source);

         for(int i = 0; i < targets.length; ++i) {
            Encoding target = targets[i];
            if(retrievedTargets.add(target)) {
               iterateEncodings(target, iteratedSources, retrievedTargets);
            }
         }
      }

   }

   static {
      encodings.add(Encoding.PCM_SIGNED);
      encodings.add(Encoding.PCM_UNSIGNED);
      encodings.add(Encoding.ULAW);
      encodings.add(Encoding.ALAW);
   }
}
