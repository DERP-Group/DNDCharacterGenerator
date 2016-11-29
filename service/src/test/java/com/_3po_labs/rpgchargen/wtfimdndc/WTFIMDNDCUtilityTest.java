package com._3po_labs.rpgchargen.wtfimdndc;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com._3po_labs.rpgchargen.wtfimdndc.WTFIMDNDCData;
import com._3po_labs.rpgchargen.wtfimdndc.WTFIMDNDCUtility;

public class WTFIMDNDCUtilityTest {

    protected WTFIMDNDCUtility utility = WTFIMDNDCUtility.getInstance();
    
    @Before
    public void setup(){
	utility.setData(new WTFIMDNDCData());
    }

    @Test
    public void testGenerateHeading() {
	WTFIMDNDCData data = new WTFIMDNDCData();
	data.setHeadings(new String[]{"TestHeading"});
	utility.setData(data);
	String heading = utility.generateHeading();
	assertEquals(heading, "TestHeading");
    }

    @Test
    public void testGenerateResponse() {
	WTFIMDNDCData data = new WTFIMDNDCData();
	data.setResponses(new String[]{"TestResponse"});
	utility.setData(data);
	String response = utility.generateResponse();
	assertEquals(response, "TestResponse");
    }

    @Test
    public void testGenerateCharacter() {
	WTFIMDNDCData data = new WTFIMDNDCData();
	data.setAdjectives(new String[]{"TestAdjective"});
	data.setRaces(new String[]{"TestRace"});
	data.setdClasses(new String[]{"TestClass"});
	data.setLocations(new String[]{"TestLocation"});
	data.setBackstories(new String[]{"TestBackstory"});
	utility.setData(data);
	String character = utility.generateCharacter();
	assertEquals(character, "TestAdjective TestRace TestClass, from TestLocation, who TestBackstory");
    }
}
