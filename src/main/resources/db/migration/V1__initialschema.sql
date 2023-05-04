CREATE TYPE symbol_status_type AS ENUM
    ('PRE_TRADING',
    'TRADING',
    'POST_TRADING',
    'END_OF_DAY',
    'HALT',
    'AUCTION_MATCH',
    'BREAK');

CREATE TABLE symbols (
    symbol_id SERIAL PRIMARY KEY,
    symbol_name VARCHAR(64) NOT NULL UNIQUE,
    symbol_status symbol_status_type NOT NULL,
    base_asset VARCHAR(32) NOT NULL,
    base_asset_precision INT NOT NULL,
    quote_asset VARCHAR(32) NOT NULL,
    quote_precision INT NOT NULL,
    quote_asset_precision INT NOT NULL
);