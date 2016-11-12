package com._3po_labs.dndchargen;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, include = JsonTypeInfo.As.PROPERTY, property="type", defaultImpl = CharGenMetadata.class)
@JsonSubTypes({
  @Type(value = CharGenMetadata.class)
})
public abstract class CommonMetadataMixIn {

}
