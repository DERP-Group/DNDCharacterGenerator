/**
 * Copyright (C) 2015 David Phillips
 * Copyright (C) 2015 Eric Olson
 * Copyright (C) 2015 Rusty Gerard
 * Copyright (C) 2015 Paul Winters
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com._3po_labs.dndchargen.resource;

import java.util.Map;

import io.dropwizard.setup.Environment;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._3po_labs.derpwizard.core.exception.DerpwizardException;
import com._3po_labs.derpwizard.core.exception.DerpwizardException.DerpwizardExceptionReasons;
import com._3po_labs.dndchargen.CharGenMetadata;
import com._3po_labs.dndchargen.MixInModule;
import com._3po_labs.dndchargen.configuration.CharGenMainConfig;
import com._3po_labs.dndchargen.manager.CharGenManager;
import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.json.SpeechletResponseEnvelope;
import com.amazon.speech.speechlet.SpeechletRequest;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazon.speech.ui.SsmlOutputSpeech;
import com.derpgroup.derpwizard.voice.alexa.AlexaUtils;
import com.derpgroup.derpwizard.voice.exception.DerpwizardExceptionAlexaWrapper;
import com.derpgroup.derpwizard.voice.model.CommonMetadata;
import com.derpgroup.derpwizard.voice.model.ServiceInput;
import com.derpgroup.derpwizard.voice.model.ServiceOutput;
import com.derpgroup.derpwizard.voice.util.ConversationHistoryUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * REST APIs for requests generating from Amazon Alexa
 * 
 * @author David
 * @author Eric
 * @author Rusty
 * @author Paul
 * @since 0.0.1
 */
@Path("/dnd-character-generator/alexa")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CharGenAlexaResource {

  private static final Logger LOG = LoggerFactory.getLogger(CharGenAlexaResource.class);
  private static final String ALEXA_VERSION = "1.1.1";

  private CharGenManager manager;
  private ObjectMapper mapper;

  static {
    ConversationHistoryUtils.getMapper().registerModule(new MixInModule());
  }

  public CharGenAlexaResource(CharGenMainConfig config, Environment env) {
    manager = new CharGenManager(config, env);

    mapper = new ObjectMapper().registerModule(new MixInModule());
  }

  /**
   * Generates a welcome message.
   *
   * @return The message, never null
   */
  @POST
  public SpeechletResponseEnvelope doAlexaRequest(@NotNull @Valid SpeechletRequestEnvelope request, @HeaderParam("SignatureCertChainUrl") String signatureCertChainUrl, 
      @HeaderParam("Signature") String signature, @QueryParam("testFlag") Boolean testFlag){
      
    CommonMetadata outputMetadata = null;
    try{
      if (request.getRequest() == null) {
        throw new DerpwizardException(DerpwizardExceptionReasons.MISSING_INFO.getSsml(),"Missing request body."); //TODO: create AlexaException
      }
      if(testFlag == null || testFlag == false){ 
        AlexaUtils.validateAlexaRequest(request, signatureCertChainUrl, signature);
      }
      
      //TODO convert to retrieve via account linking.
      String userId = request.getSession().getUser().getUserId();
      
      // Build the Input Metadata object here
      CommonMetadata inputMetadata = mapper.convertValue(request.getSession().getAttributes(), new TypeReference<CharGenMetadata>(){});  // this comes from the client-side session
      // Populate it with other information here, as required by your service. UserAccount info, echoId, serviceId, info from a database, etc
      
      ///////////////////////////////////
      // Build the ServiceInput object //
      ///////////////////////////////////
//      CharGenServiceInput serviceInput = new CharGenServiceInput();
      ServiceInput serviceInput = new ServiceInput();
      serviceInput.setMetadata(inputMetadata);
      Map<String, String> messageAsMap = AlexaUtils.getMessageAsMap(request.getRequest());
      serviceInput.setMessageAsMap(messageAsMap);
      
      SpeechletRequest speechletRequest = (SpeechletRequest)request.getRequest();
      String subject = AlexaUtils.getMessageSubject(speechletRequest);
      serviceInput.setSubject(subject);
      
      ////////////////////////////////////
      // Build the ServiceOutput object //
      ////////////////////////////////////
      ServiceOutput serviceOutput = new ServiceOutput();
      outputMetadata = mapper.convertValue(request.getSession().getAttributes(), new TypeReference<CommonMetadata>(){});  // this gets sent to the client-side session
      ConversationHistoryUtils.registerRequestInConversationHistory(subject, messageAsMap, outputMetadata, outputMetadata.getConversationHistory()); // build the conversation history for the outputMetadata
      serviceOutput.setMetadata(outputMetadata);
      
      serviceInput.setUserId(userId);
      // Call the service
      manager.handleRequest(serviceInput, serviceOutput);
  
      // Build the Alexa response object
      SpeechletResponseEnvelope responseEnvelope = new SpeechletResponseEnvelope();
      Map<String,Object> sessionAttributesOutput = mapper.convertValue(outputMetadata, new TypeReference<Map<String,Object>>(){});
      responseEnvelope.setSessionAttributes(sessionAttributesOutput);

      SimpleCard card;
      SsmlOutputSpeech outputSpeech;
      Reprompt reprompt = null;
      boolean shouldEndSession = false;
      
      switch(serviceInput.getSubject()){
      case "END_OF_CONVERSATION":
      case "STOP":
      case "CANCEL":
        if(serviceOutput.getVoiceOutput() == null || serviceOutput.getVoiceOutput().getSsmltext() == null){
            outputSpeech = null;
          }else{
            outputSpeech = new SsmlOutputSpeech();
            outputSpeech.setSsml("<speak>"+serviceOutput.getVoiceOutput().getSsmltext()+"</speak>");
          }
          card = null;
          shouldEndSession = true;
          break;
      default:
        if(StringUtils.isNotEmpty(serviceOutput.getVisualOutput().getTitle()) &&
            StringUtils.isNotEmpty(serviceOutput.getVisualOutput().getText()) ){
          card = new SimpleCard();
          card.setTitle(serviceOutput.getVisualOutput().getTitle());
          card.setContent(serviceOutput.getVisualOutput().getText());
        }
        else{
          card = null;
        }
        if(serviceOutput.getDelayedVoiceOutput() !=null && StringUtils.isNotEmpty(serviceOutput.getDelayedVoiceOutput().getSsmltext())){
            reprompt = new Reprompt();
            SsmlOutputSpeech repromptSpeech = new SsmlOutputSpeech();
            repromptSpeech.setSsml("<speak>"+serviceOutput.getDelayedVoiceOutput().getSsmltext()+"</speak>");
            reprompt.setOutputSpeech(repromptSpeech);
          }

          outputSpeech = new SsmlOutputSpeech();
          outputSpeech.setSsml("<speak>"+serviceOutput.getVoiceOutput().getSsmltext()+"</speak>");
          shouldEndSession = serviceOutput.isConversationEnded();
          break;
      }

      Map<String,Object> sessionAttributes = mapper.convertValue(outputMetadata, new TypeReference<Map<String,Object>>(){});
      
      return AlexaUtils.buildOutput(outputSpeech, card, reprompt, shouldEndSession, sessionAttributes);
    }catch(DerpwizardException e){
      LOG.debug(e.getMessage());
      return new DerpwizardExceptionAlexaWrapper(e, ALEXA_VERSION,mapper.convertValue(outputMetadata, new TypeReference<Map<String,Object>>(){}));
    }catch(Throwable t){
      LOG.debug(t.getMessage());
      return new DerpwizardExceptionAlexaWrapper(new DerpwizardException(t.getMessage()),ALEXA_VERSION, mapper.convertValue(outputMetadata, new TypeReference<Map<String,Object>>(){}));
    }
  }
}
