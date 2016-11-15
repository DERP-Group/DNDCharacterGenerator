/**
 * Copyright (C) 2015 David Phillips
 * Copyright (C) 2015 Eric Olson
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

package com._3po_labs.dndchargen.manager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._3po_labs.dndchargen.CharGenMetadata;
import com._3po_labs.dndchargen.QuestionTopic;
import com._3po_labs.dndchargen.configuration.CharGenMainConfig;
import com._3po_labs.dndchargen.model.preferences.CharGenPreferences;
import com._3po_labs.dndchargen.wtfimdndc.WTFIMDNDCUtility;
import com.derpgroup.derpwizard.dao.UserPreferencesDAO;
import com.derpgroup.derpwizard.dao.impl.UserPreferencesDAOFactory;
import com.derpgroup.derpwizard.voice.exception.DerpwizardException;
import com.derpgroup.derpwizard.voice.model.ConversationHistoryEntry;
import com.derpgroup.derpwizard.voice.model.ServiceInput;
import com.derpgroup.derpwizard.voice.model.ServiceOutput;
import com.derpgroup.derpwizard.voice.util.ConversationHistoryUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import io.dropwizard.setup.Environment;

/**
 * Manager class for Character Generation.
 *
 * @author Eric
 * @since 0.0.1
 */
public class CharGenManager {

    private static final Logger LOG = LoggerFactory.getLogger(CharGenManager.class);

    private static WTFIMDNDCUtility charGenUtility = WTFIMDNDCUtility.getInstance();
    
    private static final String[] META_SUBJECT_VALUES = new String[] { "REPEAT", "YES", "NO" };
    private static final Set<String> META_SUBJECTS = new HashSet<String>(Arrays.asList(META_SUBJECT_VALUES));

    private static String[] repeatHeadings = {
        "Listen the fuck up this time, it's a",
        "I said, it's a",
        "Pay attention bro, it's a gotdamn",
        "That's right, it's a",
        "You heard me just fine, it's a fucking",
        "I'll tell you again, but only because I love talking about my fucking",
        "Ya snooze ya lose. Shit, fine. It's a",
        "How did you already forget that shit? It's a fucking",
        "Repeat that? It's a gotdamn"
        };
    
    private UserPreferencesDAO userPreferencesDao;
    
    public CharGenManager(CharGenMainConfig config, Environment env){
	userPreferencesDao = UserPreferencesDAOFactory.build(config.getDaoConfig().getUserPreferencesDaoConfig());
    }

    /**
     * Primary entry point for dispatching requests
     * 
     * @param serviceInput
     * @param serviceOutput
     * @throws DerpwizardException 
     */
    public void handleRequest(ServiceInput serviceInput, ServiceOutput serviceOutput) throws DerpwizardException {
	
	String subject = serviceInput.getSubject();
	
	String userId = serviceInput.getUserId();
	if(userId == null){
	    LOG.error("Unknown user, could not retrieve user preferences.");
	    throw new DerpwizardException("Sorry, but I can't for the life of me seem to figure out who you are or how you got here.");
	}
	
    	LOG.info("Request subject: " + subject);
	switch (subject) {
	case "GENERATE_CHARACTER":
	    retrieveUserPreferences(userId);
	    doGenerateCharacterRequest(serviceInput, serviceOutput, retrieveUserPreferences(userId));
	    break;
	case "HELP":
	    doHelpRequest(serviceInput, serviceOutput);
	    break;
	case "ENABLE_PROFANITY":
	    toggleProfanity(serviceInput, serviceOutput, true);
	    break;
	case "DISABLE_PROFANITY":
	    toggleProfanity(serviceInput, serviceOutput, false);
	    break;
	case "START_OF_CONVERSATION":
	    retrieveUserPreferences(userId);
	    doGenerateCharacterRequest(serviceInput, serviceOutput, retrieveUserPreferences(userId));
	    break;
	case "END_OF_CONVERSATION":
	    doGoodbyeRequest(serviceInput, serviceOutput);
	    break;
	case "CANCEL":
	    doStopRequest(serviceInput, serviceOutput);
	    break;
	case "STOP":
	    doStopRequest(serviceInput, serviceOutput);
	    break;
	case "REPEAT":
	    retrieveUserPreferences(userId);
	    doRepeatRequest(serviceInput, serviceOutput, retrieveUserPreferences(userId));
	    break;
	case "YES":
	    doYesOrNoRequest(serviceInput, serviceOutput, true);
	    break;
	case "NO":
	    doYesOrNoRequest(serviceInput, serviceOutput, false);
	    break;
	default:
	    break;
	}
    }

    private void filterServiceOutput(ServiceOutput serviceOutput, CharGenPreferences userPreferences) {
	boolean allowProfanity = userPreferences == null ? false : userPreferences.isAllowProfanity();
	String filteredText = serviceOutput.getVoiceOutput().getSsmltext();
	if(allowProfanity){
	    filteredText = profanityToSsml(filteredText);
	}else{
	    filteredText = profanityToNofanity(filteredText);
	    String filteredTitle = profanityToNofanity(serviceOutput.getVisualOutput().getTitle());
	    serviceOutput.getVisualOutput().setTitle(filteredTitle);
	}
	serviceOutput.getVoiceOutput().setSsmltext(filteredText);
    }
    
    private CharGenPreferences retrieveUserPreferences(String userId){
    	try {
    	    return userPreferencesDao.getPreferencesForDefaultNamespace(userId,new TypeReference<CharGenPreferences>(){});
    	} catch (Throwable t) {
    	    LOG.error("Could not retrieve preferences for user '" + userId + "' due to exception. Continuing anonymously.", t);
    	}
    	return null;
    }

    protected void doGenerateCharacterRequest(ServiceInput serviceInput, ServiceOutput serviceOutput, CharGenPreferences userPreferences) throws DerpwizardException {
	if (userPreferences == null || userPreferences.isAllowProfanity() == null) { //This is essentially lazy initialization
    	    initializePreferences(serviceInput, serviceOutput);
    	    return;
    	}
	String heading = charGenUtility.generateHeading();
	String character = charGenUtility.generateCharacter();
	serviceOutput.getVoiceOutput().setSsmltext(heading + " " + character);
	serviceOutput.getVisualOutput().setTitle(heading);
	serviceOutput.getVisualOutput().setText(character);
	
	CharGenMetadata outputMetadata = (CharGenMetadata)serviceOutput.getMetadata();
	outputMetadata.setCharacter(character);
	outputMetadata.setHeading(heading);

	ConversationHistoryEntry entry = ConversationHistoryUtils.getLastNonMetaRequestBySubject(serviceOutput.getMetadata().getConversationHistory(), META_SUBJECTS);
	CharGenMetadata inputMetadata = (CharGenMetadata) entry.getMetadata();
	inputMetadata.setCharacter(character);
	inputMetadata.setHeading(heading);
	
	serviceOutput.setConversationEnded(false);
	filterServiceOutput(serviceOutput, userPreferences);
    }

    protected void doHelpRequest(ServiceInput serviceInput, ServiceOutput serviceOutput) {
	serviceOutput.getVoiceOutput()
		.setSsmltext("I'd love to help, but I don't have any help topics programmed yet.");
	serviceOutput.getVoiceOutput()
		.setPlaintext("I'd love to help, but I don't have any help topics programmed yet.");
	serviceOutput.setConversationEnded(true);
    }

    protected void doGoodbyeRequest(ServiceInput serviceInput, ServiceOutput serviceOutput) {
	serviceOutput.getVoiceOutput().setSsmltext("Goodbye!");
	serviceOutput.getVoiceOutput().setPlaintext("Goodbye!");
	serviceOutput.setConversationEnded(true);
    }

    protected void doStopRequest(ServiceInput serviceInput, ServiceOutput serviceOutput) {
	serviceOutput.getVoiceOutput().setSsmltext("You bet your ass.");
	serviceOutput.getVoiceOutput().setPlaintext("You bet your ass.");
	serviceOutput.setConversationEnded(true);
    }

    private void doYesOrNoRequest(ServiceInput serviceInput, ServiceOutput serviceOutput, boolean input) throws DerpwizardException {
	CharGenMetadata inputMetadata = (CharGenMetadata) serviceInput.getMetadata();
	if(inputMetadata == null || inputMetadata.getConversationHistory() == null || inputMetadata.getConversationHistory().isEmpty()){
	    throw new DerpwizardException("Sorry, I heard what sounded like an answer to a question, but I don't think we had an ongoing conversation.");
	}
	ConversationHistoryEntry entry = ConversationHistoryUtils.getLastNonMetaRequestBySubject(serviceInput.getMetadata().getConversationHistory(), META_SUBJECTS);
	CharGenMetadata requestMetadata = (CharGenMetadata) entry.getMetadata();
	QuestionTopic questionTopic = requestMetadata.getQuestionTopic(); 
	if(questionTopic == null){
	    throw new DerpwizardException("Sorry, I heard what sounded like an answer to a question, but I don't recall asking any questions.");
	}
	
	switch(questionTopic){
	case ALLOW_PROFANITY:
	    setProfanityAllowableState(serviceInput.getUserId(), input);
	    break;
	    default: 
		throw new DerpwizardException("Sorry, I know I asked you a question, but I seem to have forgotten what I was doing.");
	}
	serviceInput.setSubject(entry.getMessageSubject());
	handleRequest(serviceInput, serviceOutput);
    }

    protected void doRepeatRequest(ServiceInput serviceInput, ServiceOutput serviceOutput, CharGenPreferences userPreferences) {
	//TODO: Implement this for non-CharGen methods (maybe just "HELP"?)
	ConversationHistoryEntry entry = ConversationHistoryUtils.getLastNonMetaRequestBySubject(serviceInput.getMetadata().getConversationHistory(), META_SUBJECTS);
	CharGenMetadata inputMetadata = (CharGenMetadata) entry.getMetadata();

	String heading = generateRandomRepeatHeading();
	String character = inputMetadata.getCharacter();
	
	serviceOutput.getVoiceOutput().setSsmltext(heading + " " + character);
	serviceOutput.getVisualOutput().setTitle(heading);
	serviceOutput.getVisualOutput().setText(character);
	
	serviceOutput.setConversationEnded(false);
	filterServiceOutput(serviceOutput, userPreferences);
    }

    private void initializePreferences(ServiceInput serviceInput, ServiceOutput serviceOutput) throws DerpwizardException {
	try{
	    CharGenMetadata inputMetadata = (CharGenMetadata) ConversationHistoryUtils.getLastNonMetaRequestBySubject(serviceOutput.getMetadata().getConversationHistory(), META_SUBJECTS).getMetadata();
	    inputMetadata.setQuestionTopic(QuestionTopic.ALLOW_PROFANITY);
	    CharGenMetadata outputMetadata = (CharGenMetadata) serviceOutput.getMetadata();
	    outputMetadata.setQuestionTopic(QuestionTopic.ALLOW_PROFANITY);
	    
	}catch(Throwable t){
	    throw new DerpwizardException("Could not operate on conversation history metadata due to exception.");
	}
	LOG.info("Initializing preferences for user '" + serviceInput.getUserId() + "'.");
	serviceOutput.getVoiceOutput()
		.setSsmltext("Hi! It looks like it's your first time here. Before we start, I should tell you that I sometimes swear when I get excited. Is that okay?");
	serviceOutput.getDelayedVoiceOutput().setSsmltext("Say 'yes' if you're cool with profanity, or 'no' if you want me to keep it P.G.");
	serviceOutput.getVisualOutput().setTitle("Hi. How do you feel about profanity?");
	serviceOutput.getVisualOutput().setText("Hi! It looks like this is the first time I've seen you here. Are you okay with me using profanity?\n\n Say 'yes' if that's cool with you, or 'no' if you want me to watch my mouth.");
	serviceOutput.setConversationEnded(false);
    }

    private void toggleProfanity(ServiceInput serviceInput, ServiceOutput serviceOutput, boolean input) throws DerpwizardException {
	try{
	    setProfanityAllowableState(serviceInput.getUserId(), input);
	}catch(Throwable t){
	    LOG.error("Couldn't update allowable profanity state due to exception.",t);
	    throw new DerpwizardException("Sorry, something went wrong and I couldn't change the level of my profanity filter.");
	}
	String output = "You bet your %s. What else can I do for you?";
	if(input){
	    output = String.format(output, "ass");
	}else{
	    output = String.format(output, "bottom");
	}
	serviceOutput.getVoiceOutput().setSsmltext(output);
	serviceOutput.getVisualOutput().setText(output);
	serviceOutput.getVisualOutput().setTitle("Updated!");
	serviceOutput.setConversationEnded(false);
    }

    private void setProfanityAllowableState(String userId, boolean allowed) {
	//If we could do preference-level toggling, we wouldn't need this retrieval step first.
	CharGenPreferences preferences = userPreferencesDao.getPreferencesForDefaultNamespace(userId, new TypeReference<CharGenPreferences>(){});
	if(preferences == null){
	    preferences = new CharGenPreferences();
	}
	preferences.setAllowProfanity(allowed);
	userPreferencesDao.setPreferencesForDefaultNamespace(userId, preferences);
    }

    public static String profanityToSsml(String input){
	if(input == null){
	    return null;
	}
	String output = input.toLowerCase();
	output = output.replaceAll("fucking", "<phoneme ph=\"fʌkIn\" />");
	output = output.replaceAll("shit", "<phoneme ph=\"ʃIt\" />");
	output = output.replaceAll("fuck", "<phoneme ph=\"fʌk\" />");
	
	return output;
    }
    
    private String profanityToNofanity(String input) {
	if(input == null){
	    return null;
	}
	String output = input.toLowerCase();
	output = output.replaceAll("fucking", "friggin");
	output = output.replaceAll("shit", "crap");
	output = output.replaceAll("fuck", "f.");
	output = output.replaceAll("gotdamn", "gotdang.");
	
	return output;
    }
    
    public static String generateRandomRepeatHeading(){
	return repeatHeadings[RandomUtils.nextInt(0, repeatHeadings.length)];
    }
}
