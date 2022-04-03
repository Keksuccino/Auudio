package de.keksuccino.auudio.javazoom.spi.mpeg.sampled.file;

import de.keksuccino.auudio.javazoom.spi.mpeg.sampled.file.tag.MP3Tag;
import de.keksuccino.auudio.javazoom.spi.mpeg.sampled.file.tag.TagParseEvent;
import de.keksuccino.auudio.javazoom.spi.mpeg.sampled.file.tag.TagParseListener;

public class IcyListener implements TagParseListener {

   private static IcyListener instance = null;
   private MP3Tag lastTag = null;
   private String streamTitle = null;
   private String streamUrl = null;


   public static synchronized IcyListener getInstance() {
      if(instance == null) {
         instance = new IcyListener();
      }

      return instance;
   }

   public void tagParsed(TagParseEvent tpe) {
      this.lastTag = tpe.getTag();
      String name = this.lastTag.getName();
      if(name != null && name.equalsIgnoreCase("streamtitle")) {
         this.streamTitle = (String)this.lastTag.getValue();
      } else if(name != null && name.equalsIgnoreCase("streamurl")) {
         this.streamUrl = (String)this.lastTag.getValue();
      }

   }

   public MP3Tag getLastTag() {
      return this.lastTag;
   }

   public void setLastTag(MP3Tag tag) {
      this.lastTag = tag;
   }

   public String getStreamTitle() {
      return this.streamTitle;
   }

   public String getStreamUrl() {
      return this.streamUrl;
   }

   public void setStreamTitle(String string) {
      this.streamTitle = string;
   }

   public void setStreamUrl(String string) {
      this.streamUrl = string;
   }

   public void reset() {
      this.lastTag = null;
      this.streamTitle = null;
      this.streamUrl = null;
   }

}
