package de.keksuccino.auudio.tritonus.share.sampled.convert;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;

public class TAudioInputStream extends AudioInputStream {

   private Map m_properties;
   private Map m_unmodifiableProperties;


   public TAudioInputStream(InputStream inputStream, AudioFormat audioFormat, long lLengthInFrames) {
      super(inputStream, audioFormat, lLengthInFrames);
      this.initMaps(new HashMap());
   }

   public TAudioInputStream(InputStream inputStream, AudioFormat audioFormat, long lLengthInFrames, Map properties) {
      super(inputStream, audioFormat, lLengthInFrames);
      this.initMaps(properties);
   }

   private void initMaps(Map properties) {
      this.m_properties = properties;
      this.m_unmodifiableProperties = Collections.unmodifiableMap(this.m_properties);
   }

   public Map properties() {
      return this.m_unmodifiableProperties;
   }

   protected void setProperty(String key, Object value) {
      this.m_properties.put(key, value);
   }
}
