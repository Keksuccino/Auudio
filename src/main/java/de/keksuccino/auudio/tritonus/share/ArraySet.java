package de.keksuccino.auudio.tritonus.share;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class ArraySet extends ArrayList implements Set {

   private static final long serialVersionUID = 1L;


   public ArraySet() {}

   public ArraySet(Collection c) {
      this();
      this.addAll(c);
   }

   public boolean add(Object element) {
      if(!this.contains(element)) {
         super.add(element);
         return true;
      } else {
         return false;
      }
   }

   public void add(int index, Object element) {
      throw new UnsupportedOperationException("ArraySet.add(int index, Object element) unsupported");
   }

   public Object set(int index, Object element) {
      throw new UnsupportedOperationException("ArraySet.set(int index, Object element) unsupported");
   }
}
