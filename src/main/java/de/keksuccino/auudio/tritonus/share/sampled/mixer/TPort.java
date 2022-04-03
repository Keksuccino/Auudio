package de.keksuccino.auudio.tritonus.share.sampled.mixer;

import java.util.Collection;
import javax.sound.sampled.Line;
import javax.sound.sampled.Port;

public class TPort extends TLine implements Port {

   public TPort(TMixer mixer, Line.Info info) {
      super(mixer, info);
   }

   public TPort(TMixer mixer, Line.Info info, Collection controls) {
      super(mixer, info, controls);
   }
}
