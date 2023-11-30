CREATE TABLE IF NOT EXISTS character (
    character_id TEXT PRIMARY KEY,
    character_name TEXT NOT NULL,
    nicknames TEXT[],
    origin_type TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS role (
    character_id TEXT PRIMARY KEY REFERENCES character(character_id) ON DELETE CASCADE,
    role_type TEXT NOT NULL,
    ship_name TEXT NOT NULL
);
