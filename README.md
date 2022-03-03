# Salesforce API decryption example

## Instructions

In `src/main/resources` update `auth.properties` and change the `username` and `password` fields

Execute `mvn compile exec:java -Dexec.mainClass=nyy.client.sf.OrderFlow`

This will produce the following output (among the maven output)



```
23:58:20.125 [nyy.client.sf.OrderFlow.main()] DEBUG org.springframework.web.client.RestTemplate - HTTP GET https://sf.sit.jfl.nowyoyo.net/R2/api/mandate/braintree/new-vault/client-authorisation-id
23:58:20.138 [nyy.client.sf.OrderFlow.main()] DEBUG org.springframework.web.client.RestTemplate - Accept=[application/json, application/x-jackson-smile, application/*+json]
23:58:24.548 [nyy.client.sf.OrderFlow.main()] DEBUG org.springframework.web.client.RestTemplate - Response 200 OK
23:58:24.552 [nyy.client.sf.OrderFlow.main()] DEBUG org.springframework.web.client.RestTemplate - Reading to [com.fasterxml.jackson.databind.node.ObjectNode]
* Billing Profile ID : cd387ad8-2b39-4db6-8652-3d7496c027a1
* Client Token : eyJ2ZXJzaW9uIjoyLCJhdXRob3JpemF0aW9uRmluZ2VycHJpbnQiOiJleUowZVhBaU9pSktWMV.......
```