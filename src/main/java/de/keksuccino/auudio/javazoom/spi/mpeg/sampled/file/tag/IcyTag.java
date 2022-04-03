package de.keksuccino.auudio.javazoom.spi.mpeg.sampled.file.tag;

public class IcyTag extends MP3Tag implements StringableTag {

   public IcyTag(String name, String stringValue) {
      super(name, stringValue);
   }

   public String getValueAsString() {
      return (String)this.getValue();
   }
}
