CREATE KEYSPACE IF NOT EXISTS tictactoe
    WITH replication = {
        'class': 'SimpleStrategy',
        'replication_factor': 1
        };

USE tictactoe;

CREATE TABLE IF NOT EXISTS rooms (
                                     room_code text PRIMARY KEY,
                                     host_player_id uuid,
                                     guest_player_id uuid,
                                     player_count int,
                                     latest_game_id uuid,
                                     status text,
                                     created_at timestamp,
                                     updated_at timestamp
);

CREATE TABLE IF NOT EXISTS games (
                                     game_id uuid PRIMARY KEY,
                                     room_code text,
                                     player_x_id uuid,
                                     player_o_id uuid,
                                     board text,
                                     status text,
                                     next_turn text,
                                     winner_id uuid,
                                     result text,
                                     move_count int,
                                     created_at timestamp,
                                     ended_at timestamp
);

CREATE TABLE IF NOT EXISTS moves_by_game (
                                             game_id uuid,
                                             move_number int,
                                             player_id uuid,
                                             mark text,
                                             position int,
                                             created_at timestamp,
                                             PRIMARY KEY ((game_id), move_number)
) WITH CLUSTERING ORDER BY (move_number ASC);

CREATE TABLE IF NOT EXISTS players (
                                       player_id uuid PRIMARY KEY,
                                       username text,
                                       wins int,
                                       losses int,
                                       draws int,
                                       games_played int
);