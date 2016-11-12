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

package com._3po_labs.dndchargen.manager;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com._3po_labs.dndchargen.CharGenMetadata;
import com._3po_labs.dndchargen.wtfimdndc.WTFIMDNDCUtility;
import com.derpgroup.derpwizard.voice.model.ConversationHistoryEntry;
import com.derpgroup.derpwizard.voice.model.ServiceInput;
import com.derpgroup.derpwizard.voice.model.ServiceOutput;
import com.derpgroup.derpwizard.voice.util.ConversationHistoryUtils;

/**
 * Manager class for dispatching input messages.
 *
 * @author David
 * @author Eric
 * @author Rusty
 * @author Paul
 * @since 0.0.1
 */
public class CharGenManager {

    private static final Logger LOG = LoggerFactory.getLogger(CharGenManager.class);

    private static WTFIMDNDCUtility charGenUtility = WTFIMDNDCUtility.getInstance();
    
    private static final String[] META_SUBJECT_VALUES = new String[] { "REPEAT" };
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

    protected void doHelpRequest(ServiceInput serviceInput, ServiceOutput serviceOutput) {
	serviceOutput.getVoiceOutput()
		.setSsmltext("I'd love to help, but I don't have any help topics programmed yet.");
	serviceOutput.getVoiceOutput()
		.setPlaintext("I'd love to help, but I don't have any help topics programmed yet.");
	serviceOutput.setConversationEnded(true);
    }

    protected void doGenerateCharacterRequest(ServiceInput serviceInput, ServiceOutput serviceOutput) {
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

    protected void doRepeatRequest(ServiceInput serviceInput, ServiceOutput serviceOutput) {
	ConversationHistoryEntry entry = ConversationHistoryUtils.getLastNonMetaRequestBySubject(serviceInput.getMetadata().getConversationHistory(), META_SUBJECTS);
	CharGenMetadata inputMetadata = (CharGenMetadata) entry.getMetadata();

	String heading = generateRandomRepeatHeading();
	String character = inputMetadata.getCharacter();
	
	serviceOutput.getVoiceOutput().setSsmltext(heading + " " + character);
	serviceOutput.getVisualOutput().setTitle(heading);
	serviceOutput.getVisualOutput().setText(character);
	
	serviceOutput.setConversationEnded(false);
    }

    /**
     * An example primary entry point into the service. At this point the
     * Resource classes should have mapped any device-specific requests into
     * standard ServiceInput/ServiceOutput POJOs. As well as mapped any
     * device-specific requests into service understandable subjects.
     * 
     * @param serviceInput
     * @param serviceOutput
     */
    public void handleRequest(ServiceInput serviceInput, ServiceOutput serviceOutput) {
	String subject = serviceInput.getSubject();
	LOG.info("Request subject: " + subject);
	switch (serviceInput.getSubject()) {
	case "GENERATE_CHARACTER":
	    doGenerateCharacterRequest(serviceInput, serviceOutput);
	    break;
	case "HELP":
	    doHelpRequest(serviceInput, serviceOutput);
	    break;
	case "START_OF_CONVERSATION":
	    doGenerateCharacterRequest(serviceInput, serviceOutput);
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
	    doRepeatRequest(serviceInput, serviceOutput);
	    break;
	default:
	    break;
	}
	
	serviceOutput.getVoiceOutput().setSsmltext(profanityToSsml(serviceOutput.getVoiceOutput().getSsmltext()));
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
    
    public static String generateRandomRepeatHeading(){
	return repeatHeadings[RandomUtils.nextInt(0, repeatHeadings.length)];
    }
}
