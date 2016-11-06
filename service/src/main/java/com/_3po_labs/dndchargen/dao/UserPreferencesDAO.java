package com._3po_labs.dndchargen.dao;

import com._3po_labs.dndchargen.model.preferences.UserPreferences;
import com.fasterxml.jackson.core.type.TypeReference;

public interface UserPreferencesDAO {

  public void setPreferences(UserPreferences userPreferences);
  
  public UserPreferences getPreferences(String userId);
  
  public <T> T getPreferencesBySkillName(String userId, String skillName, TypeReference<T> type);
}
