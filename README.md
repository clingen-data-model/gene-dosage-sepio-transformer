# gene-dosage-sepio-transformer

Transforms raw gene dosage curations from JIRA API format into SEPIO. Supports Kafka Streams

## Installation

Clone the Git repository; may be run as either a JAR file (compile using lein uberjar) or using Docker.

## Usage

For non-docker usage:

    $ java -jar gene-dosage-sepio-transformer.jar

## Options

The following environment variables are expected to be set, specifying the kafka host, user, and password respectively:

KAFKA_HOST
KAFKA_USER
KAFKA_PASSWORD

## License

Copyright © 2019 

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
