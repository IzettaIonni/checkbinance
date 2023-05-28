CREATE TABLE subscriptions (
    symbol_subscription_price_id SERIAL PRIMARY KEY,
    symbol_id INTEGER REFERENCES symbols (symbol_id) NOT NULL UNIQUE,
    subscription_status BOOLEAN NOT NULL
);