CREATE KEYSPACE IF NOT EXISTS tictactoe
    WITH replication = {
        'class': 'SimpleStrategy',
        'replication_factor': 1
        };

USE tictactoe;

CREATE TABLE IF NOT EXISTS players (
                                       player_id uuid PRIMARY KEY,
                                       wins int,
                                       losses int,
                                       draws int,
                                       games_played int,
                                       incomplete_games int
);

CREATE TABLE IF NOT EXISTS rooms_by_code (
                                             room_code text,
                                             created_at timestamp,

                                             host_player_id uuid,
                                             guest_player_id uuid,
                                             game_id uuid,

                                             status text,

                                             host_rematch boolean,
                                             guest_rematch boolean,

                                             updated_at timestamp,

                                             PRIMARY KEY ((room_code), created_at)
) WITH CLUSTERING ORDER BY (created_at DESC);



CREATE TABLE IF NOT EXISTS games (
                                     game_id uuid PRIMARY KEY,

                                     room_code text,
                                     player_x_id uuid,
                                     player_o_id uuid,

                                     status text,
                                     result text,
                                     winner_id uuid,

                                     created_at timestamp,
                                     ended_at timestamp
);



CREATE TABLE IF NOT EXISTS moves_by_game (
                                             game_id uuid,
                                             created_at timestamp,

                                             player_id uuid,
                                             symbol text,
                                             position int,

                                             PRIMARY KEY ((game_id), created_at)
) WITH CLUSTERING ORDER BY (created_at ASC);



CREATE TABLE IF NOT EXISTS games_by_player (
                                               player_id uuid,
                                               game_id uuid,
                                               result text,

                                               PRIMARY KEY ((player_id), game_id)
);