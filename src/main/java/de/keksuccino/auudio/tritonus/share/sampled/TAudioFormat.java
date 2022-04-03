package de.keksuccino.auudio.tritonus.share.sampled;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioFormat.Encoding;

public class TAudioFormat extends AudioFormat {

   private Map m_properties;
   private Map m_unmodifiableProperties;


   public TAudioFormat(Encoding encoding, float sampleRate, int sampleSizeInBits, int channels, int frameSize, float frameRate, boolean bigEndian, Map properties) {
      super(encoding, sampleRate, sampleSizeInBits, channels, frameSize, frameRate, bigEndian);
      this.initMaps(properties);
   }

   public TAudioFormat(AudioFormat format) {
      this(format.getEncoding(), format.getSampleRate(), format.getSampleSizeInBits(), format.getChannels(), format.getFrameSize(), format.getFrameRate(), format.isBigEndian(), format.properties());
   }

   public TAudioFormat(AudioFormat format, Map properties) {
      this(format);
      this.m_properties.putAll(properties);
   }

   public TAudioFormat(float sampleRate, int sampleSizeInBits, int channels, boolean signed, boolean bigEndian, Map properties) {
      super(sampleRate, sampleSizeInBits, channels, signed, bigEndian);
      this.initMaps(properties);
   }

   private void initMaps(Map properties) {
      this.m_properties = new HashMap();
      if(properties != null) {
         this.m_properties.putAll(properties);
      }

      this.m_unmodifiableProperties = Collections.unmodifiableMap(this.m_properties);
   }

   public Map properties() {
      if(this.m_properties == null) {
         this.initMaps((Map)null);
      }

      return this.m_unmodifiableProperties;
   }

   public Object getProperty(String key) {
      return this.m_properties == null?null:this.m_properties.get(key);
   }

   protected void setProperty(String key, Object value) {
      if(this.m_properties == null) {
         this.initMaps((Map)null);
      }

      this.m_properties.put(key, value);
   }
}
