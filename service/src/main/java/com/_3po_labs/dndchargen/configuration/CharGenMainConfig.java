package com._3po_labs.dndchargen.configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.derpgroup.derpwizard.configuration.MainConfig;

public class CharGenMainConfig extends MainConfig{

    @Valid
    @NotNull
    protected CharGenConfig charGenConfig;
    @Valid
    @NotNull
    protected CharGenDAOConfig daoConfig;

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
}
