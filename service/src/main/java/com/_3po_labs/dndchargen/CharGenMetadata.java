package com._3po_labs.dndchargen;

import com.derpgroup.derpwizard.voice.model.CommonMetadata;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", defaultImpl = CharGenMetadata.class)
public class CharGenMetadata extends CommonMetadata {

    private String character;
    private String heading;

    public String getCharacter() {
	return character;
    }

    public void setCharacter(String character) {
	this.character = character;
    }

    public String getHeading() {
	return heading;
    }

    public void setHeading(String heading) {
	this.heading = heading;
    }
}
