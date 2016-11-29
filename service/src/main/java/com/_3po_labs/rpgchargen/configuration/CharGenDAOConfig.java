package com._3po_labs.rpgchargen.configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com._3po_labs.derpwizard.persistence.configuration.AccountLinkingDAOConfig;
import com._3po_labs.derpwizard.persistence.configuration.UserPreferencesDAOConfig;

public class CharGenDAOConfig {

    @Valid
    @NotNull
    private UserPreferencesDAOConfig userPreferencesDaoConfig;
    @Valid
    @NotNull
    private AccountLinkingDAOConfig accountLinkingDaoConfig;

    public UserPreferencesDAOConfig getUserPreferencesDaoConfig() {
        return userPreferencesDaoConfig;
    }

    public void setUserPreferencesDaoConfig(UserPreferencesDAOConfig userPreferencesDaoConfig) {
        this.userPreferencesDaoConfig = userPreferencesDaoConfig;
    }

    public AccountLinkingDAOConfig getAccountLinkingDaoConfig() {
        return accountLinkingDaoConfig;
    }

    public void setAccountLinkingDaoConfig(AccountLinkingDAOConfig accountLinkingDaoConfig) {
        this.accountLinkingDaoConfig = accountLinkingDaoConfig;
    }
}
