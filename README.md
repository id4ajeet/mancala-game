# Mancala Game

A web based Mancala or Kalaha game.

## Rules

### Game Play

The player who begins with the first move picks up all the stones in any of his own six pits, and sows the stones on to
the right, one in each of the following pits, including his own big pit. No stones are put in the opponents' big pit. If
the player's last stone lands in his own big pit, he gets another turn. This can be repeated several times before it's
the other player's turn.

### Capturing Stones

During the game the pits are emptied on both sides. Always when the last stone lands in an own empty pit, the player
captures his own stone and all stones in the opposite pit (the other player’s pit) and puts them in his own (big or
little?) pit.

### The Game Ends

The game is over as soon as one of the sides runs out of stones. The player who still has stones in his pits keeps them
and puts them in his big pit. The winner of the game is the player who has the most stones in his big pit.

### Limitations

- In the start of game, by default Player1 will get the turn to play
- In memory repository used to store the game data, once application restart, it will lose all saved games.
- Fixed number of Pits (6 for each player)

## Software Specification

Project built using

- java 11.0.10 (adoptopenjdk-11.jdk)
- maven 3.8.1
- spring boot 2.5.2
- thymeleaf 3.0.12.RELEASE
- lombok 1.18.20
- swagger springfox 3.0.0

## Building Application Locally

### Build

To build the application go to the project directory and run maven install. This will execute Unit tests as well

```shell
cd mancala-game 
mvn clean install
```

To only run unit test

```shell
cd mancala-game 
mvn test
```

### Run the application

There can be different ways to run the application

- Run using maven command

```shell
cd mancala-game 
mvn spring-boot:run
```

check the logs for port number. for example

```shell
2021-07-08 20:35:42,297 INFO  [main] org.springframework.boot.web.embedded.tomcat.TomcatWebServer: Tomcat started on port(s): 8888 (http) with context path ''
```

open browser and access http://localhost:8888/

- Run using java command

```shell
cd mancala-game
java -jar target/mancala-game-0.0.1-SNAPSHOT.jar
```

once application starts, open http://localhost:8888/ in browser


## Game UI details

### Start page 

there are two options
- start new game by giving player1 and player2 name
- load old game using gameId

### Game page 

- there are radio button against each pit, Player have to select the radio button and click `Go`
- In start Player1 will have the turn to play
- when all pits became empty for any player then game ends, Result will be displayed in same page
- there is option to reset the game to play again from start
- there is option to exit game and go back to start page


## Project Configurations

Application properties can be changed in file `application.properties`

- Initial stones per pit can be changed using application property. Default value is 6

```shell
mancala.pit.stones.initial.value=6
```

- Application configured to run on port 8888, It can be changed using below property

```shell
server.port=8888
```

## Swagger API documentation

After server startup

- Swagger api-docs in json can be accessed at http://localhost:8888/v2/api-docs
- Swagger UI can be accessed at http://localhost:8888/swagger-ui/#/

## Screenshots
Application runtime screenshot is available in `snapshots` directory under project root directory.

## Further Improvements

- Security implementation
- Persistence storage
- Spring boot actuator for metrics
- Game statistics
- Game history
- Component testing (maybe using cucumber)
- Containerization
