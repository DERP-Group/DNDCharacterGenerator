package com._3po_labs.dndchargen;

import com.derpgroup.derpwizard.voice.model.CommonMetadata;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type", defaultImpl = CharGenMetadata.class)
public class CharGenMetadata extends CommonMetadata {

    private String character;
    private String heading;
    private QuestionTopic questionTopic;

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

    public QuestionTopic getQuestionTopic() {
        return questionTopic;
    }

    public void setQuestionTopic(QuestionTopic questionTopic) {
        this.questionTopic = questionTopic;
    }
}
