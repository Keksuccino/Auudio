package de.keksuccino.auudio.tritonus.share;

public class TSettings {

   public static boolean SHOW_ACCESS_CONTROL_EXCEPTIONS = false;
   private static final String PROPERTY_PREFIX = "tritonus.";
   public static boolean AlsaUsePlughw = getBooleanProperty("AlsaUsePlughw");


   private static boolean getBooleanProperty(String strName) {
      String strPropertyName = "tritonus." + strName;
      String strValue = "false";

      try {
         strValue = System.getProperty(strPropertyName, "false");
      } catch (Exception var4) {
         if(SHOW_ACCESS_CONTROL_EXCEPTIONS) {
         }
      }

      boolean bValue = strValue.toLowerCase().equals("true");
      return bValue;
   }

}
