# zealux-monitor
Monitor application that poll stats from Zealux pool heat pumps and store it in
a database.

It will try its best to poll stats at a regular interval, but slow down polling
if the Wi-Fi connection is unreliable or the device goes offline for a moment.

Meant to run as a service.

## Requirements
* Java 17
* Maven

## Quick Start

1. Go to the service folder: `cd service`
2. Copy the configurationTemplate.properties and adjust your version as desired.
For early testing it's best to run with the database disabled. It will log a
report instead: `cp configurationTemplate.properties myconfig.properties`
3. Build the application:
`mvn package`
4. Run the application:
`java -jar target/zealux-monitor-1.0.0.jar --config myconfig.properties`

The application will keep running until interrupted or terminated.

## Database

The application is using Liquibase to generate necessary tables and initial data
on startup. Create a user with its own schema and privileges to create tables
and insert data. Then Liquibase will handle the rest.

Only tested with PostgreSQL, however this initial version should work with most
databases. I'm also experimenting with views for reports (not included) and
these use functions specific to PostgreSQL.

## Future Work

For now it's just a service. I'm planning to extend the monitor to write
various reports to file and present them using a simple web app with graphs.

# Credits

Thanks to [Mikko Strandborg](https://github.com/strandborg) for reverse
engineering the network protocol and the AlsavoPro app, as well as creating
the [AlsavoCtrl](https://github.com/strandborg/AlsavoCtrl) application.
