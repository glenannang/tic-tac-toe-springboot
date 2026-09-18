# Tic-Tac-Toe Spring Boot Web Service

A RESTful multiplayer Tic-Tac-Toe backend built with **Java 25**,
**Spring Boot 4.1.1**, and **Apache Cassandra**. The application manages
players, rooms, games, moves, rematches, player statistics, game
history, and leaderboard rankings while keeping REST, business, domain,
and persistence responsibilities separated.

The project migrates the Tic-Tac-Toe web service to Spring Boot and
applies layered architecture, constructor injection, DTO-based API
boundaries, Jakarta Validation, centralized exception handling, Spring
Data Cassandra repositories, enums for domain states, and SLF4J logging.

## Features

-   Create players with generated UUIDs
-   Create and join rooms using 6--10 character alphanumeric room codes
-   Assign the room host as **X** and the guest as **O**
-   Create games only when a room is ready
-   Enforce player membership, turn order, and board-position
    availability
-   Detect wins and draws on the backend
-   Retrieve game status and the current board
-   Abandon/remove an active game
-   Leave and close a room
-   Request and start rematches when both players accept
-   Track wins, losses, draws, games played, and incomplete games
-   Retrieve game IDs played by a player
-   Rank players by wins using competition ranking
-   Return structured validation and domain errors
-   Persist application state in Cassandra

------------------------------------------------------------------------

## Technology Stack and Dependencies

  ---------------------------------------------------------------------
  Technology / Dependency            Purpose
  ---------------------------------- ----------------------------------
  Java 25                            Application language and runtime

  Spring Boot 4.1.1                  Application bootstrap,
                                     configuration, and dependency
                                     management

  Spring Web MVC                     REST controllers, routing, JSON
                                     request/response handling

  Spring Data Cassandra              Repository abstraction and
                                     Cassandra object mapping

  Spring Cassandra                   Cassandra integration and
                                     configuration

  Jakarta Validation                 Declarative validation of request
                                     DTOs

  Spring Boot Actuator               Application monitoring/management
                                     support

  SLF4J                              Logging meaningful business state
                                     changes

  Apache Cassandra                   Persistent data store

  Maven                              Build and dependency management

  Postman                            Endpoint and validation testing
  ---------------------------------------------------------------------

------------------------------------------------------------------------

## Architecture

The main request flow is:

``` text
Client / Postman
       |
       v
   Controller
       |
       v
Service Interface
       |
       v
Service Implementation
       |
       +------> GameEngine / Utilities
       |
       v
   Repository
       |
       v
Apache Cassandra
```

### Layer Responsibilities

**Controller**\
Exposes `/api/v1` REST endpoints, validates request DTOs with `@Valid`,
delegates work to services, and returns HTTP responses.

**Service Interface**\
Defines the operations available to controllers without exposing
implementation details.

**Service Implementation**\
Coordinates business workflows such as room lifecycle, move validation,
game completion, rematches, statistics updates, and persistence
operations.

**Repository**\
Uses Spring Data `CassandraRepository` interfaces to isolate database
access.

**DTOs**\
Request DTOs define accepted API input. Response DTOs define API output
so Cassandra entities are not directly exposed.

**Mappers**\
Convert entity/domain information into response DTOs.

**Entities**\
Represent records stored in Cassandra.

**GameEngine**\
Contains the core Tic-Tac-Toe rules: next-turn calculation, win
detection, and draw detection.

**Enums**\
Represent controlled game, room, result, and player-symbol states.

**GlobalExceptionHandler**\
Uses centralized exception handling to translate validation and domain
exceptions into consistent HTTP responses.

**Constants**\
Centralize reusable success and error messages.

**BoardUtil**\
Reconstructs the nine-position board from persisted move records.

------------------------------------------------------------------------

## Project Structure

``` text
src/main/java/com/svi/tictactoe/
├── constant/
│   ├── ErrorMessages.java
│   └── SuccessMessages.java
├── controller/
│   ├── GameController.java
│   ├── LeaderboardController.java
│   ├── PlayerController.java
│   └── RoomController.java
├── dto/
│   ├── request/
│   │   ├── game/
│   │   └── room/
│   └── response/
│       ├── game/
│       ├── leaderboard/
│       ├── player/
│       └── room/
├── engine/
│   └── GameEngine.java
├── entity/
├── enums/
├── exception/
│   ├── game/
│   ├── player/
│   └── room/
├── mapper/
├── repository/
├── service/
│   └── impl/
├── util/
│   └── BoardUtil.java
└── TictactoeApplication.java

src/main/resources/
├── application.properties
└── schema.cql
```

------------------------------------------------------------------------

# Game Flow and Behavior

## Overall Game Flow

This is the recommended order for understanding and testing one complete
game. The Postman collection is grouped by resource (Player, Room, Game,
Leaderboard), so its folder order does not need to match this flow.

``` text
Create Player X
      |
Create Player O
      |
Player X creates room
      |
Room = WAITING
      |
Player O joins
      |
Room = READY
      |
Create game
      |
Room = IN_GAME
Game = IN_PROGRESS
      |
X moves first
      |
O moves
      |
Players alternate
      |
      +----------------+
      |                |
     WIN              DRAW
      |                |
      +-------+--------+
              |
Game = FINISHED
Room = REMATCH
Update game records and player statistics
              |
       What happens next?
        /             \
Both accept rematch   Player leaves
       |                   |
Create new game        Room = CLOSED
Room = IN_GAME
```

The important idea is that the **room** manages the two-player session,
while the **game** manages one Tic-Tac-Toe match inside that room. A
rematch therefore creates a new game instead of reusing the finished
game.

## 1. Create Players

Players are created before joining a game session.

``` text
POST /api/v1/players
```

The backend generates a UUID and initializes:

``` text
wins            = 0
losses          = 0
draws           = 0
gamesPlayed     = 0
incompleteGames = 0
```

No request body is required.

------------------------------------------------------------------------

## 2. Create a Room

The first player creates a room:

``` text
POST /api/v1/rooms
```

Example:

``` json
{
  "playerId": "550e8400-e29b-41d4-a716-446655440000",
  "roomCode": "ROOM01"
}
```

Before creating the room, the service:

1.  Verifies that the player exists.
2.  Verifies that the room code has not already been used.
3.  Creates the room with the player as the host.
4.  Assigns the host the **X** symbol.
5.  Sets the room to `WAITING`.

``` text
Player X creates room
        |
        v
     WAITING
```

Room codes must contain only letters and numbers and must be between 6
and 10 characters.

------------------------------------------------------------------------

## 3. Join a Room

The second player joins:

``` text
POST /api/v1/rooms/{roomCode}/join
```

The service verifies that:

-   the joining player exists;
-   the room exists;
-   the room is not closed;
-   the player is not already in the room; and
-   the room is still waiting for another player.

The joining player becomes the guest and receives the **O** symbol.

``` text
WAITING
   |
   | Player O joins
   v
 READY
```

------------------------------------------------------------------------

## 4. Create a Game

A game can be created when the room has both players:

``` text
POST /api/v1/games
```

``` json
{
  "roomCode": "ROOM01"
}
```

Game creation is rejected if the room is:

-   `WAITING` for another player;
-   already `IN_GAME`;
-   waiting in `REMATCH`; or
-   `CLOSED`.

When successful:

1.  A new game UUID is generated.
2.  The host becomes `playerXId`.
3.  The guest becomes `playerOId`.
4.  The game status becomes `IN_PROGRESS`.
5.  A `games_by_player` record is created for each player.
6.  The room stores the new game ID and changes to `IN_GAME`.

``` text
Room READY
    |
    | Create Game
    v
Room IN_GAME
Game IN_PROGRESS
```

------------------------------------------------------------------------

## 5. Make a Move

Moves are submitted to:

``` text
POST /api/v1/games/{gameId}/moves
```

Example:

``` json
{
  "playerId": "550e8400-e29b-41d4-a716-446655440000",
  "position": 0
}
```

Positions use zero-based board indexing:

``` text
0 | 1 | 2
---------
3 | 4 | 5
---------
6 | 7 | 8
```

Before saving a move, the service performs these checks:

``` text
Move Request
    |
    v
Player exists?
    |
    v
Game exists?
    |
    v
Player belongs to game?
    |
    v
Game still IN_PROGRESS?
    |
    v
Correct player's turn?
    |
    v
Position available?
    |
    v
Save move
```

### Turn Rules

-   **X always moves first.**
-   After X moves, O is expected.
-   After O moves, X is expected.
-   The service uses the persisted move history to determine whose turn
    is next.

### Position Rules

A move position must be between `0` and `8`. A position already occupied
by a previous move cannot be selected again.

After a valid move is persisted, the backend checks for a win and then a
draw.

------------------------------------------------------------------------

## 6. Win Detection

`GameEngine` contains all eight possible winning combinations:

``` text
Rows:       0-1-2, 3-4-5, 6-7-8
Columns:    0-3-6, 1-4-7, 2-5-8
Diagonals:  0-4-8, 2-4-6
```

If the player who just moved occupies one of these combinations:

``` text
Game status  -> FINISHED
Game result  -> WIN
winnerId     -> winning player's UUID
Room status  -> REMATCH
```

The corresponding `games_by_player` records are updated to:

``` text
Winner -> WIN
Loser  -> LOSS
```

Player statistics are also updated:

``` text
Winner: wins + 1, gamesPlayed + 1
Loser:  losses + 1, gamesPlayed + 1
```

------------------------------------------------------------------------

## 7. Draw Detection

If nine moves have been made and no winning combination exists:

``` text
Game status -> FINISHED
Game result -> DRAW
winnerId    -> null
Room status -> REMATCH
```

Both `games_by_player` records become `DRAW`.

Both players receive:

``` text
draws       + 1
gamesPlayed + 1
```

------------------------------------------------------------------------

## 8. Game Status and Board Status

### Game Status

``` text
GET /api/v1/games/{gameId}
```

Returns the current game information, including:

-   game ID;
-   game status;
-   nine-position board;
-   next turn while the game is in progress;
-   move count;
-   winner ID when applicable; and
-   game result when applicable.

### Board Status

``` text
GET /api/v1/games/{gameId}/board
```

Returns the reconstructed nine-position board.

The board is rebuilt from the persisted `moves_by_game` records instead
of being stored as a separate board object.

------------------------------------------------------------------------

## 9. Remove / Abandon an Active Game

``` text
POST /api/v1/games/{gameId}/remove
```

Only a game that is still `IN_PROGRESS` can be removed.

When removed:

``` text
Game status -> ABANDONED
Game result -> INCOMPLETE
Room status -> READY
Room gameId -> null
```

Both player-game records become `INCOMPLETE`, and both players receive:

``` text
incompleteGames + 1
gamesPlayed     + 1
```

The room can then create another game.

------------------------------------------------------------------------

## 10. Leave a Room

``` text
POST /api/v1/rooms/{roomCode}/leave
```

Only the host or guest can leave the room.

If a player leaves while the room is `IN_GAME`, the active game is first
abandoned and recorded as incomplete. The room is then changed to:

``` text
CLOSED
```

If the room is already closed, the service returns the corresponding
already-closed response instead of changing the state again.

------------------------------------------------------------------------

## 11. Rematch Flow

After a normal **WIN** or **DRAW**, the room enters:

``` text
REMATCH
```

Each player submits:

``` text
POST /api/v1/rooms/{roomCode}/rematch
```

The room independently tracks whether the host and guest have accepted.

``` text
Game FINISHED
     |
     v
Room REMATCH
     |
     +---- Player 1 accepts ----> wait for Player 2
     |
     +---- Player 2 accepts ----> both accepted
                                  |
                                  v
                            Old room row FINISHED
                                  |
                                  v
                            Create new Game
                                  |
                                  v
                          New room row IN_GAME
```

When only one player has accepted, the service returns a waiting
response.

When both players accept:

1.  The current room record becomes `FINISHED`.
2.  A new game is created for the same two players.
3.  A new room record is created using the same room code.
4.  The new room record points to the new game.
5.  The new room status becomes `IN_GAME`.
6.  Both rematch flags are reset.

------------------------------------------------------------------------

# Domain States

## RoomStatus

  ---------------------------------------------------------------------
  State                              Meaning
  ---------------------------------- ----------------------------------
  `WAITING`                          Host created the room; waiting for
                                     the second player

  `READY`                            Both players are present and a
                                     game can be created

  `IN_GAME`                          Room currently has an active game

  `REMATCH`                          A completed game is waiting for
                                     rematch decisions

  `FINISHED`                         Previous room record has completed
                                     its rematch lifecycle

  `CLOSED`                           Room session has been closed
  ---------------------------------------------------------------------

## GameStatus

  State           Meaning
  --------------- -----------------------------------------------------
  `IN_PROGRESS`   Game accepts moves
  `FINISHED`      Game ended normally through a win or draw
  `ABANDONED`     Active game was terminated before normal completion

## GameResult

``` text
WIN
DRAW
INCOMPLETE
```

## PlayerGameResult

``` text
WIN
LOSS
DRAW
INCOMPLETE
```

## PlayerSymbol

``` text
X
O
```

------------------------------------------------------------------------

# API Endpoints

All endpoints use the `/api/v1` prefix.

## Player

  -----------------------------------------------------------------------
  Method               Endpoint                      Purpose
  -------------------- ----------------------------- --------------------
  `POST`               `/players`                    Create a player

  `GET`                `/players/{playerId}/games`   Return game IDs
                                                     associated with a
                                                     player
  -----------------------------------------------------------------------

`GET /players/{playerId}/games` returns only game UUIDs:

``` json
[
  "11111111-1111-1111-1111-111111111111",
  "22222222-2222-2222-2222-222222222222"
]
```

An existing player with no games receives an empty list.

## Room

  Method   Endpoint                      Purpose
  -------- ----------------------------- -------------------------
  `POST`   `/rooms`                      Create a room
  `POST`   `/rooms/{roomCode}/join`      Join a room
  `GET`    `/rooms/{roomCode}`           Get current room status
  `POST`   `/rooms/{roomCode}/leave`     Leave/close a room
  `POST`   `/rooms/{roomCode}/rematch`   Accept a rematch

## Game

  Method   Endpoint                   Purpose
  -------- -------------------------- --------------------------------
  `POST`   `/games`                   Create a game for a ready room
  `POST`   `/games/{gameId}/moves`    Save a validated move
  `GET`    `/games/{gameId}`          Get complete game status
  `GET`    `/games/{gameId}/board`    Get the current board
  `POST`   `/games/{gameId}/remove`   Abandon/remove an active game

## Leaderboard

  -----------------------------------------------------------------------
  Method                Endpoint                    Purpose
  --------------------- --------------------------- ---------------------
  `GET`                 `/leaderboard`              Get all ranked
                                                    players

  `GET`                 `/leaderboard/{playerId}`   Get one player's
                                                    statistics and rank
  -----------------------------------------------------------------------

------------------------------------------------------------------------

# Endpoint Responses and Common Scenarios

This section is a quick guide to what the API does in normal and error
situations. Exact validation messages come from the request DTOs and
centralized error messages in the application.

## Player Scenarios

  -----------------------------------------------------------------------
  Endpoint / Scenario     Expected HTTP Result    What Happens
  ----------------------- ----------------------- -----------------------
  Create player           `201 Created`           A new player UUID and
                                                  zeroed statistics are
                                                  created

  Get games for an        `200 OK`                Returns the player's
  existing player                                 game UUIDs; may be an
                                                  empty list

  Get games for a player  `404 Not Found`         Returns a
  that does not exist                             player-not-found error

  Malformed UUID in the   `400 Bad Request`       Request is rejected as
  path                                            an invalid UUID
  -----------------------------------------------------------------------

## Room Scenarios

  -----------------------------------------------------------------------
  Scenario                HTTP Status             Result
  ----------------------- ----------------------- -----------------------
  Valid room creation     Success                 Host is assigned X and
                                                  room becomes `WAITING`

  Missing or invalid room `400 Bad Request`       DTO validation rejects
  code                                            the request

  Player does not exist   `404 Not Found`         Room is not created

  Room code already       `409 Conflict`          Duplicate room code is
  exists                                          rejected

  Valid second player     Success                 Guest is assigned O and
  joins                                           room becomes `READY`

  Host tries to join own  `409 Conflict`          Same player cannot
  room                                            occupy both room slots

  Third player tries to   `409 Conflict`          Full or unavailable
  join                                            room rejects the player

  Player not in room      `403 Forbidden`         Leave request is
  tries to leave                                  rejected

  Valid player leaves     Success                 Room is closed; an
                                                  active game is
                                                  abandoned first when
                                                  necessary
  -----------------------------------------------------------------------

## Game Creation Scenarios

  -----------------------------------------------------------------------
  Scenario                Expected HTTP Result    What Happens
  ----------------------- ----------------------- -----------------------
  Room is `READY`         Success                 New game is created and
                                                  becomes `IN_PROGRESS`;
                                                  room becomes `IN_GAME`

  Room does not exist     `404 Not Found`         Game is not created

  Room is still `WAITING` `409 Conflict`          Game cannot start
                                                  before both players are
                                                  present

  Room already has an     `409 Conflict`          Another game cannot be
  active game                                     created for the active
                                                  room

  Room is in another      `409 Conflict`          Current room state
  unavailable state                               prevents game creation
  -----------------------------------------------------------------------

## Move Scenarios

  -----------------------------------------------------------------------
  Scenario                Expected HTTP Result    What Happens
  ----------------------- ----------------------- -----------------------
  Valid move              `200 OK`                Move is saved and the
                                                  updated move/game
                                                  information is returned

  O attempts the first    `409 Conflict`          Move is rejected
  move                                            because X moves first

  Same player attempts    `409 Conflict`          Move is rejected
  consecutive turns                               because it is the other
                                                  player's turn

  Position is already     `409 Conflict`          Existing move is kept;
  occupied                                        new move is not saved

  Player is not part of   `403 Forbidden`         Move is rejected
  the game                                        

  Player does not exist   `404 Not Found`         Move is rejected

  Game does not exist     `404 Not Found`         Move is rejected

  Position is missing or  `400 Bad Request`       DTO validation rejects
  outside `0–8`                                   the request

  Malformed player/game   `400 Bad Request`       Request is rejected as
  UUID                                            an invalid UUID

  Move after a WIN or     `409 Conflict`          Finished games no
  DRAW                                            longer accept moves

  **Move after the game   **`409 Conflict`**      Removed game is
  was removed**                                   `ABANDONED`, so it is
                                                  no longer in progress
                                                  and cannot accept moves
  -----------------------------------------------------------------------

The last two cases are intentionally handled by the same game-state
rule:

``` text
Can a move be added?
        |
Game status == IN_PROGRESS?
      /        \
    Yes         No
     |           |
Validate move   Reject request
                 |
             409 Conflict
```

## Game Status / Board Scenarios

  -----------------------------------------------------------------------
  Scenario                Expected HTTP Result    What Happens
  ----------------------- ----------------------- -----------------------
  Existing game           `200 OK`                Current game/board
                                                  information is returned

  Game does not exist     `404 Not Found`         Returns a
                                                  game-not-found error

  Finished game           `200 OK`                Final state remains
                                                  available for viewing

  Abandoned game          `200 OK`                Stored abandoned game
                                                  state remains available
                                                  for viewing
  -----------------------------------------------------------------------

## Remove Game Scenarios

  -----------------------------------------------------------------------
  Scenario                Expected HTTP Result    What Happens
  ----------------------- ----------------------- -----------------------
  Remove an active game   Success                 Game becomes
                                                  `ABANDONED` with result
                                                  `INCOMPLETE`; room
                                                  returns to `READY`

  Remove a game already   `409 Conflict`          A non-active game
  finished/abandoned                              cannot be removed again

  Game does not exist     `404 Not Found`         Nothing is changed
  -----------------------------------------------------------------------

## Rematch Scenarios

  -----------------------------------------------------------------------
  Scenario                Expected HTTP Result    What Happens
  ----------------------- ----------------------- -----------------------
  First player accepts    Success                 Acceptance is saved and
                                                  the service waits for
                                                  the other player

  Second player accepts   Success                 Both accepted; a new
                                                  game and new active
                                                  room record are created

  Same player sends       Conflict response       Duplicate rematch
  another acceptance                              acceptance is rejected

  Player is not part of   `403 Forbidden`         Rematch request is
  room                                            rejected

  Room is not in the      `409 Conflict`          Rematch cannot start
  correct rematch state                           from the current room
                                                  state
  -----------------------------------------------------------------------

## Leaderboard Scenarios

  -----------------------------------------------------------------------
  Scenario                Expected HTTP Result    What Happens
  ----------------------- ----------------------- -----------------------
  Get leaderboard         `200 OK`                Players are returned
                                                  ranked by wins

  Get existing player's   `200 OK`                Player statistics and
  rank/stats                                      calculated rank are
                                                  returned

  Player does not exist   `404 Not Found`         Returns a
                                                  player-not-found error
  -----------------------------------------------------------------------

> **Note:** `409 Conflict` is used when the request itself can be
> understood, but the current state of the room/game does not allow the
> action. For example, a valid move request cannot be applied to an
> already abandoned game.

# Leaderboard and Player Statistics

Each player stores:

``` text
wins
losses
draws
gamesPlayed
incompleteGames
```

`gamesPlayed` is incremented when a game reaches a win, draw, or
incomplete/abandoned result.

The leaderboard sorts players by **wins in descending order**.

It uses **standard competition ranking**. Players with the same number
of wins share a rank, and subsequent ranks account for the tied
positions.

Example:

``` text
Wins: 8, 6, 6, 5
Rank: 1, 2, 2, 4
```

Another example:

``` text
Wins: 1, 1, 0, 0
Rank: 1, 1, 3, 3
```

The player-specific leaderboard endpoint calculates rank as:

``` text
1 + number of players with more wins
```

------------------------------------------------------------------------

# Validation and Error Handling

## Request Validation

Jakarta Validation is applied to request DTOs.

Examples:

-   `@NotNull` --- required player IDs and move positions
-   `@NotBlank` --- required room codes
-   `@Size(min = 6, max = 10)` --- room-code length
-   `@Pattern` --- alphanumeric room codes
-   `@Min(0)` / `@Max(8)` --- valid move positions

Using `Integer` for the move position allows a missing position to
remain `null` and be rejected by `@NotNull` rather than being
interpreted as position `0`.

## Domain Validation

The service layer handles rules that require application state,
including:

-   whether a player exists;
-   whether a room/game exists;
-   whether a player belongs to a room/game;
-   whether a room can be joined;
-   whether a game can be created;
-   whose turn it is;
-   whether a board position is occupied;
-   whether a game is still in progress; and
-   whether a room is available for rematch.

## Centralized Exception Handling

`GlobalExceptionHandler` uses `@RestControllerAdvice` and
`@ExceptionHandler` to return consistent JSON errors.

Example:

``` json
{
  "message": "Player does not exist."
}
```

  ---------------------------------------------------------------------
  HTTP Status                        Used For
  ---------------------------------- ----------------------------------
  `400 Bad Request`                  DTO validation, malformed UUIDs,
                                     invalid request format

  `403 Forbidden`                    Player does not belong to the
                                     requested room/game

  `404 Not Found`                    Player, room, or game does not
                                     exist

  `409 Conflict`                     Current domain state conflicts
                                     with the requested operation

  `500 Internal Server Error`        Unexpected unhandled server
                                     failure
  ---------------------------------------------------------------------

------------------------------------------------------------------------

# Cassandra Data Model

The schema is intentionally organized around the queries performed by
the application.

  ---------------------------------------------------------------------
  Table                              Purpose
  ---------------------------------- ----------------------------------
  `players`                          Player identity and accumulated
                                     statistics

  `rooms_by_code`                    Room lifecycle/history grouped by
                                     room code

  `games`                            Game participants, status, result,
                                     winner, and timestamps

  `moves_by_game`                    Ordered moves belonging to a game

  `games_by_player`                  Games associated with each player
                                     and the player's result
  ---------------------------------------------------------------------

## `rooms_by_code`

Primary key:

``` text
((room_code), created_at)
```

-   `room_code` is the partition key.
-   `created_at` is clustered in descending order.
-   The repository can retrieve the latest room record for a room code
    first.
-   Rematches can create a new room-state row while preserving previous
    room records.

## `moves_by_game`

Primary key:

``` text
((game_id), created_at)
```

Moves are clustered in ascending timestamp order, allowing the service
to reconstruct move history and the board in play order.

## `games_by_player`

Primary key:

``` text
((player_id), game_id)
```

This table supports the query:

``` text
GET /api/v1/players/{playerId}/games
```

without requiring a scan of the main `games` table.

------------------------------------------------------------------------

# Important Spring Annotations

The following annotations define how the application's classes
participate in Spring, validation, REST handling, and Cassandra
persistence.

  Annotation                       Role in This Project
  -------------------------------- ------------------------------------------------
  `@SpringBootApplication`         Bootstraps the application
  `@RestController`                Defines REST controllers
  `@RequestMapping`                Defines each controller's `/api/v1` base path
  `@GetMapping` / `@PostMapping`   Maps endpoint operations
  `@RequestBody`                   Converts JSON bodies into request DTOs
  `@PathVariable`                  Reads room codes and UUIDs from endpoint paths
  `@Valid`                         Triggers DTO validation
  `@Service`                       Registers service implementations
  `@Component`                     Registers `GameEngine` as a Spring component
  `@Repository`                    Identifies repository interfaces
  `@RestControllerAdvice`          Centralizes REST exception handling
  `@ExceptionHandler`              Maps exception types to HTTP responses
  `@Table`                         Maps an entity to a Cassandra table
  `@PrimaryKey`                    Maps an entity's Cassandra primary key
  `@PrimaryKeyClass`               Defines a composite Cassandra key class
  `@PrimaryKeyColumn`              Defines partition/clustering key columns
  `@Column`                        Maps entity fields to Cassandra columns

------------------------------------------------------------------------

# Design Decisions and OOP Principles

## Component Dependencies and Dependency Injection

A **dependency** is an object that a class requires to perform its
responsibility.

For example, `GameServiceImpl` relies on repositories for Cassandra
access, mappers for response conversion, `PlayerService` for
player-related operations, and `GameEngine` for Tic-Tac-Toe rules.
Keeping these responsibilities in separate components prevents one
service from handling unrelated work.

``` text
GameController
    |
    | uses
    v
GameService
    |
    | implemented by
    v
GameServiceImpl
    |-- GameRepository       -> game data
    |-- MoveRepository       -> move data
    |-- RoomRepository       -> room data needed by a game
    |-- PlayerGameRepository -> each player's game record
    |-- PlayerService        -> player-related operations
    |-- GameMapper           -> game response DTOs
    |-- MoveMapper           -> move response DTOs
    `-- GameEngine           -> turns, wins, and draws
```

Other service relationships follow the same idea:

``` text
RoomServiceImpl
|-- RoomRepository -> room data
|-- GameService    -> game operations needed by the room
|-- PlayerService  -> player-related operations
`-- RoomMapper     -> room response DTOs

PlayerServiceImpl
|-- PlayerRepository     -> player data and statistics
|-- PlayerGameRepository -> games associated with a player
`-- PlayerMapper         -> player response DTOs

LeaderboardServiceImpl
|-- PlayerRepository  -> player statistics
`-- LeaderboardMapper -> leaderboard response DTOs
```

### Constructor Dependency Injection

The application uses **constructor injection** for required
dependencies. Instead of creating repositories, services, mappers, or
other components inside a class using `new`, Spring supplies the
required objects through the constructor.

``` text
Spring creates the required component
            |
            v
passes it through the constructor
            |
            v
the receiving class uses the dependency
```

This makes required dependencies explicit and keeps responsibilities
separated. For example, `GameServiceImpl` delegates player-related
operations to `PlayerService` instead of duplicating player logic.

## Service Interface + Implementation

Controllers depend on service abstractions (`GameService`,
`RoomService`, `PlayerService`, and `LeaderboardService`) rather than
directly performing persistence or business logic. Implementations are
kept in `service/impl`.

This supports **abstraction**, separation of concerns, and lower
coupling between API and business layers.

## Constructor Injection

Dependencies are supplied through constructors rather than mutable field
injection. Dependencies are explicit and service/controller classes can
be constructed with the collaborators they require.

## DTO and Entity Separation

Cassandra entities are persistence models. API requests and responses
use dedicated DTOs instead of exposing those entities directly.

This keeps the public API contract separate from database
representation.

## Mappers

Mapping responsibilities are isolated into mapper classes instead of
being repeated throughout controllers and services.

## GameEngine

Tic-Tac-Toe rule calculations are isolated from persistence and HTTP
concerns. `GameEngine` is responsible for:

``` text
getNextTurn()
hasWon()
isDraw()
```

This gives the game-rule logic a focused responsibility.

## Explicit Domain Modeling

Enums represent valid states rather than passing arbitrary strings
through the application. Dedicated entities represent games, rooms,
moves, players, and player-game records.

## Centralized Messages and Exceptions

Reusable success/error strings are kept in constants, while
domain-specific exception classes communicate failure conditions.
`GlobalExceptionHandler` owns their translation into HTTP responses.

## Logging

SLF4J logs are placed around meaningful state-changing operations,
including:

-   player creation;
-   room creation/join/leave;
-   game creation;
-   successful moves;
-   wins and draws;
-   game abandonment; and
-   rematch state transitions.

Read-only operations and routine validation branches are not logged
unnecessarily.

------------------------------------------------------------------------

# Setup

## Prerequisites

-   Java 25
-   Apache Cassandra
-   Maven or the included Maven wrapper
-   Postman for API testing

Verify Java:

``` bash
java -version
```

## Cassandra

The default configuration is:

``` properties
spring.cassandra.keyspace-name=tictactoe
spring.cassandra.contact-points=127.0.0.1
spring.cassandra.port=9042
spring.cassandra.local-datacenter=datacenter1
```

The following environment variables can override the defaults:

``` text
CASSANDRA_KEYSPACE
CASSANDRA_CONTACT_POINTS
CASSANDRA_PORT
CASSANDRA_LOCAL_DATACENTER
```

Start Cassandra, open `cqlsh`, and execute the provided schema:

``` sql
SOURCE 'src/main/resources/schema.cql';
```

The schema creates the `tictactoe` keyspace and required tables.

------------------------------------------------------------------------

# Running the Application

From the project root:

``` bash
./mvnw spring-boot:run
```

Windows:

``` bat
mvnw.cmd spring-boot:run
```

The application can also be run directly from `TictactoeApplication` in
IntelliJ IDEA.

Default server address:

``` text
http://localhost:8080
```

API base URL:

``` text
http://localhost:8080/api/v1
```

------------------------------------------------------------------------

# Testing with Postman

The project includes:

``` text
Annang_Tictactoe Spring Boot.postman_collection.json
```

The collection contains Room, Game, Player, and Leaderboard requests,
including validation scenarios.

Collection variables include:

``` text
baseURL
roomCode
invalidRoomCode
playerXId
playerOId
player3Id
invalidPlayerId
gameId
position
```

The collection stores generated IDs automatically through post-response
scripts:

``` text
CREATE PLAYER X -> playerXId
CREATE PLAYER O -> playerOId
CREATE PLAYER 3 -> player3Id
CREATE GAME     -> gameId
```

A recommended end-to-end test order is:

``` text
CREATE PLAYER X
        |
CREATE PLAYER O
        |
CREATE ROOM
        |
JOIN ROOM
        |
ROOM STATUS
        |
CREATE GAME
        |
GAME STATUS
        |
ADD MOVE X / O
        |
WIN or DRAW
        |
GAME STATUS / BOARD STATUS
        |
PLAYER GAMES
        |
LEADERBOARD
        |
PLAYER RANK AND STATS
        |
REMATCH or LEAVE ROOM
```

For a quick X-win test:

``` text
X -> 0
O -> 1
X -> 3
O -> 2
X -> 6
```

Final board:

``` text
X | O | O
---------
X |   |
---------
X |   |
```

The final move should transition the game to `FINISHED` with result
`WIN`, set Player X as the winner, update both players' statistics,
update their `games_by_player` results, and move the room to `REMATCH`.

------------------------------------------------------------------------

# Architecture and Design Rationale

The application follows a layered architecture in which each component
has a focused responsibility:

``` text
Controller
-> receives HTTP requests and returns responses

Service
-> defines what operations are available

ServiceImpl
-> contains and coordinates the business rules

Repository
-> reads/writes Cassandra data

Entity
-> represents stored database data

DTO
-> represents API input/output

Mapper
-> converts stored/domain data into response DTOs

GameEngine
-> contains the core Tic-Tac-Toe rules

GlobalExceptionHandler
-> converts errors into proper HTTP responses
```

### Service Interfaces and Implementations

Service interfaces define **what operations are available**, while
implementation classes define **how those operations are performed**.
Controllers depend on the service abstraction rather than directly on
the implementation.

### DTO and Entity Separation

Entities represent how data is stored in Cassandra, while DTOs define
what the API accepts and returns. Keeping them separate prevents the
persistence structure from becoming the public API contract.

### Game Rule Separation with `GameEngine`

Win, draw, and turn calculations are isolated in `GameEngine`. These
rules are independent of HTTP handling and database access, giving the
component one focused responsibility.

### Repository Abstraction

Repositories isolate Cassandra access from controllers and business
logic. Services can retrieve and persist data without containing
low-level database-access code.

### Domain States with Enums

Room states, game states, symbols, and results use enums because they
have a fixed set of valid values. Values such as `IN_PROGRESS`,
`FINISHED`, `WIN`, and `DRAW` are therefore represented explicitly
instead of as arbitrary strings.

### Centralized Exception Handling

Controllers and services can raise meaningful exceptions while
`GlobalExceptionHandler` consistently maps them to the appropriate HTTP
status and JSON error response.

# Summary

This project implements a persistent Tic-Tac-Toe backend with explicit
room and game lifecycles, backend-enforced game rules, validation,
rematches, player statistics, game history, and competition-based
leaderboard ranking.

Its structure separates HTTP handling, business workflows, domain rules,
data mapping, and Cassandra persistence so each layer has a focused
responsibility and the application's behavior can be tested and
maintained independently.
