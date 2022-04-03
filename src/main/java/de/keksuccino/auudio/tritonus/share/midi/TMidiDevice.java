package de.keksuccino.auudio.tritonus.share.midi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

public abstract class TMidiDevice implements MidiDevice {

   private javax.sound.midi.MidiDevice.Info m_info;
   private boolean m_bDeviceOpen;
   private boolean m_bUseTransmitter;
   private boolean m_bUseReceiver;
   private List m_receivers;
   private List m_transmitters;


   public TMidiDevice(javax.sound.midi.MidiDevice.Info info) {
      this(info, true, true);
   }

   public TMidiDevice(javax.sound.midi.MidiDevice.Info info, boolean bUseTransmitter, boolean bUseReceiver) {
      this.m_info = info;
      this.m_bUseTransmitter = bUseTransmitter;
      this.m_bUseReceiver = bUseReceiver;
      this.m_bDeviceOpen = false;
      this.m_receivers = new ArrayList();
      this.m_transmitters = new ArrayList();
   }

   public javax.sound.midi.MidiDevice.Info getDeviceInfo() {
      return this.m_info;
   }

   public synchronized void open() throws MidiUnavailableException {

      if(!this.isOpen()) {
         this.openImpl();
         this.m_bDeviceOpen = true;
      }

   }

   protected void openImpl() throws MidiUnavailableException {

   }

   public synchronized void close() {

      if(this.isOpen()) {
         this.closeImpl();
         this.m_bDeviceOpen = false;
      }

   }

   protected void closeImpl() {

   }

   public boolean isOpen() {
      return this.m_bDeviceOpen;
   }

   protected boolean getUseTransmitter() {
      return this.m_bUseTransmitter;
   }

   protected boolean getUseReceiver() {
      return this.m_bUseReceiver;
   }

   public long getMicrosecondPosition() {
      return -1L;
   }

   public int getMaxReceivers() {
      byte nMaxReceivers = 0;
      if(this.getUseReceiver()) {
         nMaxReceivers = -1;
      }

      return nMaxReceivers;
   }

   public int getMaxTransmitters() {
      byte nMaxTransmitters = 0;
      if(this.getUseTransmitter()) {
         nMaxTransmitters = -1;
      }

      return nMaxTransmitters;
   }

   public Receiver getReceiver() throws MidiUnavailableException {
      if(!this.getUseReceiver()) {
         throw new MidiUnavailableException("Receivers are not supported by this device");
      } else {
         return new TMidiDevice.TReceiver();
      }
   }

   public Transmitter getTransmitter() throws MidiUnavailableException {
      if(!this.getUseTransmitter()) {
         throw new MidiUnavailableException("Transmitters are not supported by this device");
      } else {
         return new TMidiDevice.TTransmitter();
      }
   }

   public List getReceivers() {
      return Collections.unmodifiableList(this.m_receivers);
   }

   public List getTransmitters() {
      return Collections.unmodifiableList(this.m_transmitters);
   }

   protected void receive(MidiMessage message, long lTimeStamp) {

   }

   protected void addReceiver(Receiver receiver) {
      List var2 = this.m_receivers;
      synchronized(this.m_receivers) {
         this.m_receivers.add(receiver);
      }
   }

   protected void removeReceiver(Receiver receiver) {
      List var2 = this.m_receivers;
      synchronized(this.m_receivers) {
         this.m_receivers.remove(receiver);
      }
   }

   protected void addTransmitter(Transmitter transmitter) {
      List var2 = this.m_transmitters;
      synchronized(this.m_transmitters) {
         this.m_transmitters.add(transmitter);
      }
   }

   protected void removeTransmitter(Transmitter transmitter) {
      List var2 = this.m_transmitters;
      synchronized(this.m_transmitters) {
         this.m_transmitters.remove(transmitter);
      }
   }

   protected void sendImpl(MidiMessage message, long lTimeStamp) {

      TMidiDevice.TTransmitter transmitter;
      Object copiedMessage;
      for(Iterator transmitters = this.m_transmitters.iterator(); transmitters.hasNext(); transmitter.send((MidiMessage)copiedMessage, lTimeStamp)) {
         transmitter = (TMidiDevice.TTransmitter)transmitters.next();
         copiedMessage = null;
         if(message instanceof MetaMessage) {
            MetaMessage origMessage = (MetaMessage)message;
            MetaMessage metaMessage = new MetaMessage();

            try {
               metaMessage.setMessage(origMessage.getType(), origMessage.getData(), origMessage.getData().length);
            } catch (InvalidMidiDataException var10) {
            }

            copiedMessage = metaMessage;
         } else {
            copiedMessage = (MidiMessage)message.clone();
         }

         if(message instanceof MetaMessage) {

         }
      }


   }

   public class TReceiver implements Receiver {

      private boolean m_bOpen;


      public TReceiver() {
         TMidiDevice.this.addReceiver(this);
         this.m_bOpen = true;
      }

      protected boolean isOpen() {
         return this.m_bOpen;
      }

      public void send(MidiMessage message, long lTimeStamp) {

         if(this.m_bOpen) {
            TMidiDevice.this.receive(message, lTimeStamp);
         } else {
            throw new IllegalStateException("receiver is not open");
         }
      }

      public void close() {
         TMidiDevice.this.removeReceiver(this);
         this.m_bOpen = false;
      }
   }

   public static class Info extends javax.sound.midi.MidiDevice.Info {

      public Info(String a, String b, String c, String d) {
         super(a, b, c, d);
      }
   }

   public class TTransmitter implements Transmitter {

      private boolean m_bOpen = true;
      private Receiver m_receiver;


      public TTransmitter() {
         TMidiDevice.this.addTransmitter(this);
      }

      public void setReceiver(Receiver receiver) {
         synchronized(this) {
            this.m_receiver = receiver;
         }
      }

      public Receiver getReceiver() {
         return this.m_receiver;
      }

      public void send(MidiMessage message, long lTimeStamp) {
         if(this.getReceiver() != null && this.m_bOpen) {
            this.getReceiver().send(message, lTimeStamp);
         }

      }

      public void close() {
         TMidiDevice.this.removeTransmitter(this);
         this.m_bOpen = false;
      }
   }
}
