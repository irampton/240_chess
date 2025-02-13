# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```

##  Chess Server Design

[Editable](https://sequencediagram.org/index.html#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIei0azF5vDgHYsgwr5kvDrco67F8H5LCBALnAWspqig5QIAePKwvuh6ouisTYgmhgumGbpkhSBq0uWo4mkS4YWhyMDcryBqCsKMCvmIrrSkml6weUtHaI6zoErhLIerqmSxv604hsx5qRhy0YwMJ8ZythyZQZcKE8tmuaYP+IIwSUVxTIxC6Ts8+ivOB46tpM35XlphTZD2MD9oOvR6cRowGaBxmfm5MBLpwq7eH4gReCg6B7gevjMMe6SZJgtkXkU1DXtIACiu5JfUSXNC0D6qE+3TTrO6Dtr+ZxAiW+WNoVmlslh8Fhb6sLCWhGKYfJGp8aSMAgKkKAgA2wkBkGYntRGhSWvA3W9TJQahmRbqdjVMDTmpCBYFhbWzfxnXcHg-WidopFMnNknlJEwwQDQU1+jNh0sdp4IwIqyrcYmimlWmKHhcteZKdZ8U6Q9Soqj+naxWAfYDkOPkrp4-kbpCtq7tCMAAOKjqykWnjF57MNp17I2lmX2KOeVBgVaBFWymnlOVc4aT91XyfB0Ko6MqgNUGTUYc92G8RtHVdT1fVBgNcb7ddZojey5RwBNDaLdN4kRnd8ry36X3c+tN0dcgsQs2osIHRLFHlMjFIwDrzAQAAZjAROs+L5GsX99164jsQa07VMo6ObvfW981WbpfR22ofQVN0MCR6WwejgAktI4wwAAjL2ADMAAsTwnpkBoVmZ1ZTDoCCgA2ufAfnhkhwAcqOkcTn0ewwBHUcwC4bfdI0lkJb9oPg45PRTCHqhh83kf9CH8eJynGdZ1F+ouWO9dPEXJdl65FdfNXtcb43o+R23Lgd1DfnroE2A+FA2DcPAgmGHrKRz1jOQ42x-03g0hPE8EpMVc+vRb6MLuxxXqpmpj-OcQ4AEoEgv7ZWcFOq3z1rCOAt9OZYg9rzLW7pOqy12oNMWitjbjUFpdVIDsjpwLAWrNQ6k1o4T5tgz0eokGG0dqNSiZAQiGDQCgZItta5aHkOQ26r97qPSBq1T2P1paoJoStOmsDcZpgHjHUYk9yjT3Tt5CmTte72Qhk5VRKB1HJzTlo4+MNT4BEsCgZUEA+EACkIA8m9qMQIK9epP3MAzbu5RqiUjvC0EOJM6y-yHFfYANioBwAgAhKAswJ7SCAR2EBwIqEzjCb0CJUSYlxISXHJJCjUw+KdCrAAVs4tAsInGqRQGiZqGCZDDXKALXqeDRbyFYUddh0tZakOERJShqtUjqzoZgiW+EwB60ZAbAZksxqm2YCHIw2ghRhHjnMgO7F+GjDkrBTWEzFo2iqMXKJ9YWGbOOjJG0MBskdkgDsww9ENl0NSSWGpaB1ZVSdj+IOdzomxOgCsAA6iwWO6UWgACFdwKDgAAaU3gUqeZjtHA10djPuQ45inMoLkoFExQXgqSlCmF8LEVqITholFFi1wBQCF4SJXYvSwGANgK+hB4iJAfpjUGJTEopTShlVoxgdF-mkQDJ63zREqw4LYikKBZljKaQw0osruBCWVLMwhx1pBysyDAJKJRLB+h5PYS5QzxEezeWmS13ylEKkBjo04eiHKQzML5IAA)

[Present](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIei0azF5vDgHYsgwr5kvDrco67F8H5LCBALnAWspqig5QIAePKwvuh6ouisTYgmhgumGbpkhSBq0uWo4mkS4YWhyMDcryBqCsKMCvmIrrSkml6weUtHaI6zoErhLIerqmSxv604hsx5qRhy0YwMJ8ZythyZQZcKE8tmuaYP+IIwSUVxTIxC6Ts8+ivOB46tpM35XlphTZD2MD9oOvR6cRowGaBxmfm5MBLpwq7eH4gReCg6B7gevjMMe6SZJgtkXkU1DXtIACiu5JfUSXNC0D6qE+3TTrO6Dtr+ZxAiW+WNoVmlslh8Fhb6sLCWhGKYfJGp8aSMAgKkKAgA2wkBkGYntRGhSWvA3W9TJQahmRbqdjVMDTmpCBYFhbWzfxnXcHg-WidopFMnNknlJEwwQDQU1+jNh0sdp4IwIqyrcYmimlWmKHhcteZKdZ8U6Q9Soqj+naxWAfYDkOPkrp4-kbpCtq7tCMAAOKjqykWnjF57MNp17I2lmX2KOeVBgVaBFWymnlOVc4aT91XyfB0Ko6MqgNUGTUYc92G8RtHVdT1fVBgNcb7ddZojey5RwBNDaLdN4kRnd8ry36X3c+tN0dcgsQs2osIHRLFHlMjFIwDrzAQAAZjAROs+L5GsX99164jsQa07VMo6ObvfW981WbpfR22ofQVN0MCR6WwejgAktI4wwAAjL2ADMAAsTwnpkBoVmZ1ZTDoCCgA2ufAfnhkhwAcqOkcTn0ewwBHUcwC4bfdI0lkJb9oPg45PRTCHqhh83kf9CH8eJynGdZ1F+ouWO9dPEXJdl65FdfNXtcb43o+R23Lgd1DfnroE2A+FA2DcPAgmGHrKRz1jOQ42x-03g0hPE8EpMVc+vRb6MLuxxXqpmpj-OcQ4AEoEgv7ZWcFOq3z1rCOAt9OZYg9rzLW7pOqy12oNMWitjbjUFpdVIDsjpwLAWrNQ6k1o4T5tgz0eokGG0dqNSiZAQiGDQCgZItta5aHkOQ26r97qPSBq1T2P1paoJoStOmsDcZpgHjHUYk9yjT3Tt5CmTte72Qhk5VRKB1HJzTlo4+MNT4BEsCgZUEA+EACkIA8m9qMQIK9epP3MAzbu5RqiUjvC0EOJM6y-yHFfYANioBwAgAhKAswJ7SCAR2EBwIqEzjCb0CJUSYlxISXHJJCjUw+KdCrAAVs4tAsInGqRQGiZqGCZDDXKALXqeDRbyFYUddh0tZakOERJShqtUjqzoZgiW+EwB60ZAbAZksxqm2YCHIw2ghRhHjnMgO7F+GjDkrBTWEzFo2iqMXKJ9YWGbOOjJG0MBskdkgDsww9ENl0NSSWGpaB1ZVSdj+IOdzomxOgCsAA6iwWO6UWgACFdwKDgAAaU3gUqeZjtHA10djPuQ45inMoLkoFExQXgqSlCmF8LEVqITholFFi1wBQCF4SJXYvSwGANgK+hB4iJAfpjUGJTEopTShlVoxgdF-mkQDJ63zREqw4LYikKBZljKaQw0osruBCWVLMwhx1pBysyDAJKJRLB+h5PYS5QzxEezeWmS13ylEKkBjo04eiHKQzML5IAA)