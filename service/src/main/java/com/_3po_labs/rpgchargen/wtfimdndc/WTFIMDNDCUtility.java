package com._3po_labs.rpgchargen.wtfimdndc;

public class WTFIMDNDCUtility {

  private static WTFIMDNDCUtility instance;
  private WTFIMDNDCData data = null;
  
  private WTFIMDNDCUtility(){}
  
  public static WTFIMDNDCUtility getInstance(){
    if(instance == null){
      instance = new WTFIMDNDCUtility();
    }
    
    return instance;
  }
  
  public void setData(WTFIMDNDCData data){
    synchronized(this){
      this.data = data;
    }
  }
  
  public String generateCharacter(){
    if(data == null){
      return null;
    }
    String template = "%s %s %s, from %s, who %s";
    return String.format(template, data.getRandomAdjective(),data.getRandomRace(),data.getRandomDClass(),data.getRandomLocation(),data.getRandomBackstory());
  }
  
  public String generateHeading(){
    return data.getRandomHeading();
  }
  
  public String generateResponse(){
      return data.getRandomResponse();
  }
}
