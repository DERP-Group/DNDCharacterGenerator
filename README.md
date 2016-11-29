# RPGCharacterGenerator 

Boilerplate DropTables project designed to interface with Amazon Echo.

http://dropwizard.io/


# Compiling

`mvn clean package`


# Running

`java -jar service/target/rpgchargen-service.jar server rpgchargen.json`


# Configuration

Refer to the Dropwizard configuration module for framework-specific configuration (logging, HTTP ports, etc.): http://www.dropwizard.io/manual/configuration.html

See `service.json` for an example configuration file suitable for a development environment.

See `service_local.json` for an example configuration file suitable for local development.
