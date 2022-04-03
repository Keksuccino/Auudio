package de.keksuccino.auudio.javazoom.spi.mpeg.sampled.file.tag;

import java.util.EventObject;

public class TagParseEvent extends EventObject {

   protected MP3Tag tag;


   public TagParseEvent(Object source, MP3Tag tag) {
      super(source);
      this.tag = tag;
   }

   public MP3Tag getTag() {
      return this.tag;
   }
}
