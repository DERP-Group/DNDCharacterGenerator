package com._3po_labs.dndchargen.configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.derpgroup.derpwizard.configuration.UserPreferencesDAOConfig;

public class CharGenDAOConfig {

    @Valid
    @NotNull
    private UserPreferencesDAOConfig userPreferencesDaoConfig;

    public UserPreferencesDAOConfig getUserPreferencesDaoConfig() {
        return userPreferencesDaoConfig;
    }

    public void setUserPreferencesDaoConfig(UserPreferencesDAOConfig userPreferencesDaoConfig) {
        this.userPreferencesDaoConfig = userPreferencesDaoConfig;
    }
}
