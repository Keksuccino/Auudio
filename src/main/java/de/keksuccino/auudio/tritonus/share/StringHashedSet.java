package de.keksuccino.auudio.tritonus.share;

import java.util.Collection;
import java.util.Iterator;

public class StringHashedSet extends ArraySet {

   private static final long serialVersionUID = 1L;


   public StringHashedSet() {}

   public StringHashedSet(Collection c) {
      super(c);
   }

   public boolean add(Object elem) {
      return elem == null?false:super.add(elem);
   }

   public boolean contains(Object elem) {
      if(elem == null) {
         return false;
      } else {
         String comp = elem.toString();
         Iterator it = this.iterator();

         do {
            if(!it.hasNext()) {
               return false;
            }
         } while(!comp.equals(it.next().toString()));

         return true;
      }
   }

   public Object get(Object elem) {
      if(elem == null) {
         return null;
      } else {
         String comp = elem.toString();
         Iterator it = this.iterator();

         Object thisElem;
         do {
            if(!it.hasNext()) {
               return null;
            }

            thisElem = it.next();
         } while(!comp.equals(thisElem.toString()));

         return thisElem;
      }
   }
}
