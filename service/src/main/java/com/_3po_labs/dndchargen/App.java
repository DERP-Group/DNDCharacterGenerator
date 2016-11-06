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

package com._3po_labs.dndchargen;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.io.IOException;

import com._3po_labs.dndchargen.configuration.MainConfig;
import com._3po_labs.dndchargen.resource.CharGenAlexaResource;
import com._3po_labs.dndchargen.wtfimdndc.WTFIMDNDCData;
import com._3po_labs.dndchargen.wtfimdndc.WTFIMDNDCUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Main method for spinning up the HTTP server.
 *
 * @author Rusty Gerard
 * @since 0.0.1
 */
public class App extends Application<MainConfig> {

  public static void main(String[] args) throws Exception {
    new App().run(args);
  }

  @Override
  public void initialize(Bootstrap<MainConfig> bootstrap) {}

  @Override
  public void run(MainConfig config, Environment environment) throws IOException {
    if (config.isPrettyPrint()) {
      ObjectMapper mapper = environment.getObjectMapper();
      mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    // Resources
    environment.jersey().register(new CharGenAlexaResource(config, environment));
    
    WTFIMDNDCUtility charGen = WTFIMDNDCUtility.getInstance();
    charGen.setData(new WTFIMDNDCData());
  }
}
