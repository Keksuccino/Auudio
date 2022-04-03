package de.keksuccino.auudio.tritonus.share.sampled.file;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFileFormat.Type;

public class TAudioFileFormat extends AudioFileFormat {

   private Map m_properties;
   private Map m_unmodifiableProperties;


   public TAudioFileFormat(Type type, AudioFormat audioFormat, int nLengthInFrames, int nLengthInBytes) {
      super(type, nLengthInBytes, audioFormat, nLengthInFrames);
   }

   public TAudioFileFormat(Type type, AudioFormat audioFormat, int nLengthInFrames, int nLengthInBytes, Map properties) {
      super(type, nLengthInBytes, audioFormat, nLengthInFrames);
      this.initMaps(properties);
   }

   private void initMaps(Map properties) {
      this.m_properties = new HashMap();
      this.m_properties.putAll(properties);
      this.m_unmodifiableProperties = Collections.unmodifiableMap(this.m_properties);
   }

   public Map properties() {
      return this.m_unmodifiableProperties;
   }

   protected void setProperty(String key, Object value) {
      this.m_properties.put(key, value);
   }
}
