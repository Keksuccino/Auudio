package de.keksuccino.auudio.tritonus.share.sampled;

import javax.sound.sampled.AudioFileFormat.Type;
import de.keksuccino.auudio.tritonus.share.StringHashedSet;

public class AudioFileTypes extends Type {

   private static StringHashedSet types = new StringHashedSet();


   AudioFileTypes(String name, String ext) {
      super(name, ext);
   }

   public static Type getType(String name) {
      return getType(name, (String)null);
   }

   public static Type getType(String name, String extension) {
      Object res = (Type)types.get(name);
      if(res == null) {
         if(extension == null) {
            return null;
         }

         res = new AudioFileTypes(name, extension);
         types.add(res);
      }

      return (Type)res;
   }

   public static boolean equals(Type t1, Type t2) {
      return t2.toString().equals(t1.toString());
   }

   static {
      types.add(Type.AIFF);
      types.add(Type.AIFC);
      types.add(Type.AU);
      types.add(Type.SND);
      types.add(Type.WAVE);
   }
}
