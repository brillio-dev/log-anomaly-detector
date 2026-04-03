# Log Anomaly Detector
## Problem Statement
- Applications produce thousands of log events across multiple services. 
- When these logs are stored in server application log files, Operations and Engineering teams have the challenge of having to go through multiple log files and connect the dots to determine the root cause of production incidents. 
- This is often a lengthy and tedious process, leading to unusable prod environments for unacceptable periods of time. 
- Tools like Splunk help in analyzing logs and searching for relevant information, but they don't have an automated way of anomaly detection and presentation.

## Solution
![Architecture Diagram](/architecture.png "Architecture Diagram")
- This project implements a real-time log anomaly detection pipeline by using an event-driven microservices architecture.
- Application logs emitted by the source are sent to a topic in Apache Kafka.
- The consumer implemented in Spring uses Kafka Streams API to subscribe to the topic and listen to the log events.
- When it accumulates log events reaching a predefined batch size, it sends the log to the Agent Service.
- The agent service is another microservice implemented in Python.
- It uses a tool-calling LangChain agent to analyze the batch of logs, classify them based on the severity, and provide remediation guidance.

## Reusability
- Although inspired by the challenge in MEP, this solution can be reused for different problems, as this is a common challenge applicable across different domains and projects.
- The agent service uses two LangChain tools, one that provides a catalogue of known errors and remediation patterns, and another to classify severity based on the log level and message content. These tools can be customized or supplemented with additional information to make them flexible to handle additional scenarios.
- The architecture is modular and so it's possible to replace or add microservices for additional functionality.

# How to run
1. Create a .env file at the project root and add the Anthropic key in the format `ANTHROPIC_API_KEY=<key>`
2. Ensure that Python and Java environments are present, and Docker is installed and the demon is up.
3. Execute `docker compose up --build`


## Verify everything works
curl http://localhost:8081/alerts | jq
curl http://localhost:8081/alerts/severity/HIGH
curl http://localhost:8081/alerts/service/order-service
curl "http://localhost:8081/alerts/filter?severity=HIGH&service=order-service"

## Database tables
- List all tables
`\dt`

- View all alerts
`SELECT * FROM alerts;`

- View high severity alerts
`SELECT * FROM alerts WHERE severity = 'HIGH';`

- View alerts by service
`SELECT * FROM alerts WHERE affected_service = 'order-service';`

- Count alerts by severity
`SELECT severity, COUNT(*) FROM alerts GROUP BY severity;`

- Most recent 10 alerts
`SELECT * FROM alerts ORDER BY detected_at DESC LIMIT 10;`
