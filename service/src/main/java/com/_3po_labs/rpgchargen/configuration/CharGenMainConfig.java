package com._3po_labs.rpgchargen.configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com._3po_labs.derpwizard.core.configuration.MainConfig;


public class CharGenMainConfig extends MainConfig{

    @Valid
    @NotNull
    protected CharGenConfig charGenConfig;
    @Valid
    @NotNull
    protected CharGenDAOConfig daoConfig;
    private boolean certLogging = false;

    public CharGenConfig getCharGenConfig() {
        return charGenConfig;
    }

    public void setCharGenConfig(CharGenConfig charGenConfig) {
        this.charGenConfig = charGenConfig;
    }

    public CharGenDAOConfig getDaoConfig() {
        return daoConfig;
    }

    public void setDaoConfig(CharGenDAOConfig daoConfig) {
        this.daoConfig = daoConfig;
    }

    public boolean isCertLogging() {
        return certLogging;
    }

    public void setCertLogging(boolean certLogging) {
        this.certLogging = certLogging;
    }
}
