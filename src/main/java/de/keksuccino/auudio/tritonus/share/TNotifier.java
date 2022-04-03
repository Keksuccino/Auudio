package de.keksuccino.auudio.tritonus.share;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;

public class TNotifier extends Thread {

   public static TNotifier notifier = null;
   private List m_entries = new ArrayList();


   public TNotifier() {
      super("Tritonus Notifier");
   }

   public void addEntry(EventObject event, Collection listeners) {
      List var3 = this.m_entries;
      synchronized(this.m_entries) {
         this.m_entries.add(new TNotifier.NotifyEntry(event, listeners));
         this.m_entries.notifyAll();
      }
   }

   public void run() {
      while(true) {
         TNotifier.NotifyEntry entry = null;
         List var2 = this.m_entries;
         synchronized(this.m_entries) {
            while(this.m_entries.size() == 0) {
               try {
                  this.m_entries.wait();
               } catch (InterruptedException var5) {
               }
            }

            entry = (TNotifier.NotifyEntry)this.m_entries.remove(0);
         }

         entry.deliver();
      }
   }

   static {
      notifier = new TNotifier();
      notifier.setDaemon(true);
      notifier.start();
   }

   public static class NotifyEntry {

      private EventObject m_event;
      private List m_listeners;


      public NotifyEntry(EventObject event, Collection listeners) {
         this.m_event = event;
         this.m_listeners = new ArrayList(listeners);
      }

      public void deliver() {
         Iterator iterator = this.m_listeners.iterator();

         while(iterator.hasNext()) {
            LineListener listener = (LineListener)iterator.next();
            listener.update((LineEvent)this.m_event);
         }

      }
   }
}
