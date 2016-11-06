package com._3po_labs.dndchargen.wtfimdndc;

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
    String template = "@adjective @race @dclass from @location who @backstory";
    return template;
  }
  
  public String generateHeading(){
    return data.getRandomHeading();
  }
}
