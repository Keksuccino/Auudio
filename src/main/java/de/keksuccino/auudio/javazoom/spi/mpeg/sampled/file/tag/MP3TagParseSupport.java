package de.keksuccino.auudio.javazoom.spi.mpeg.sampled.file.tag;

import java.util.ArrayList;

public class MP3TagParseSupport {

   ArrayList tagParseListeners = new ArrayList();


   public void addTagParseListener(TagParseListener tpl) {
      this.tagParseListeners.add(tpl);
   }

   public void removeTagParseListener(TagParseListener tpl) {
      this.tagParseListeners.add(tpl);
   }

   public void fireTagParseEvent(TagParseEvent tpe) {
      for(int i = 0; i < this.tagParseListeners.size(); ++i) {
         TagParseListener l = (TagParseListener)this.tagParseListeners.get(i);
         l.tagParsed(tpe);
      }

   }

   public void fireTagParsed(Object source, MP3Tag tag) {
      this.fireTagParseEvent(new TagParseEvent(source, tag));
   }
}
