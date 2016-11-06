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

import com._3po_labs.dndchargen.wtfimdndc.WTFIMDNDCUtility;
import com.derpgroup.derpwizard.voice.model.ServiceInput;
import com.derpgroup.derpwizard.voice.model.ServiceOutput;

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
  
  private static WTFIMDNDCUtility charGenUtility = WTFIMDNDCUtility.getInstance();

  protected void doHelpRequest(ServiceInput voiceInput, ServiceOutput serviceOutput) {
    serviceOutput.getVoiceOutput().setSsmltext("I'd love to help, but I don't have any help topics programmed yet.");
    serviceOutput.getVoiceOutput().setPlaintext("I'd love to help, but I don't have any help topics programmed yet.");
    serviceOutput.setConversationEnded(true);
  }

  protected void doHelloRequest(ServiceInput voiceInput, ServiceOutput serviceOutput) {
    String output = charGenUtility.generateHeading();
    serviceOutput.getVoiceOutput().setSsmltext(output);
    serviceOutput.getVoiceOutput().setPlaintext(output);
    serviceOutput.setConversationEnded(true);
  }

  protected void doGoodbyeRequest(ServiceInput voiceInput, ServiceOutput serviceOutput) {
    serviceOutput.getVoiceOutput().setSsmltext("Goodbye!");
    serviceOutput.getVoiceOutput().setPlaintext("Goodbye!");
    serviceOutput.setConversationEnded(true);
  }

  protected void doCancelRequest(ServiceInput voiceInput, ServiceOutput serviceOutput) {
    serviceOutput.getVoiceOutput().setSsmltext("Cancelling");
    serviceOutput.getVoiceOutput().setPlaintext("Cancelling");
    serviceOutput.setConversationEnded(true);
  }

  protected void doStopRequest(ServiceInput voiceInput, ServiceOutput serviceOutput) {
    serviceOutput.getVoiceOutput().setSsmltext("Stopping");
    serviceOutput.getVoiceOutput().setPlaintext("Stopping");
    serviceOutput.setConversationEnded(true);
  }

  protected void doRepeatRequest(ServiceInput voiceInput, ServiceOutput serviceOutput) {
    serviceOutput.getVoiceOutput().setSsmltext("Repeating");
    serviceOutput.getVoiceOutput().setPlaintext("Repeating");
    serviceOutput.setConversationEnded(true);
  }

  protected void doYesRequest(ServiceInput voiceInput, ServiceOutput serviceOutput) {
    serviceOutput.getVoiceOutput().setSsmltext("Yes");
    serviceOutput.getVoiceOutput().setPlaintext("Yes");
    serviceOutput.setConversationEnded(true);
  }

  protected void doNoRequest(ServiceInput voiceInput, ServiceOutput serviceOutput) {
    serviceOutput.getVoiceOutput().setSsmltext("No");
    serviceOutput.getVoiceOutput().setPlaintext("No");
    serviceOutput.setConversationEnded(true);
  }

  /**
   * An example primary entry point into the service.
   * At this point the Resource classes should have mapped any device-specific requests
   * into standard ServiceInput/ServiceOutput POJOs. As well as mapped any device-specific
   * requests into service understandable subjects.
   * @param serviceInput
   * @param serviceOutput
   */
  public void handleRequest(ServiceInput serviceInput, ServiceOutput serviceOutput){
    switch(serviceInput.getSubject()){
    case "HELP":
      doHelpRequest(serviceInput, serviceOutput);
      break;
    case "START_OF_CONVERSATION":
      doHelloRequest(serviceInput, serviceOutput);
      break;
    case "END_OF_CONVERSATION":
      doGoodbyeRequest(serviceInput, serviceOutput);
      break;
    case "CANCEL":
      doCancelRequest(serviceInput, serviceOutput);
      break;
    case "STOP":
      doStopRequest(serviceInput, serviceOutput);
      break;
    case "REPEAT":
      doRepeatRequest(serviceInput, serviceOutput);
      break;
    case "YES":
      doYesRequest(serviceInput, serviceOutput);
      break;
    case "NO":
      doNoRequest(serviceInput, serviceOutput);
      break;
    default:
      break;
    }
  }
}
