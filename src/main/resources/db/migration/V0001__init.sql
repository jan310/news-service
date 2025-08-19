CREATE TABLE IF NOT EXISTS users (
    id                      TEXT,
    notification_enabled    BOOLEAN     NOT NULL,
    notification_email      TEXT        NOT NULL,
    notification_time       TIME        NOT NULL,
    time_zone               TEXT        NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT users_notification_email_unique UNIQUE (notification_email)
);

CREATE TABLE IF NOT EXISTS stocks (
    ticker                  TEXT,
    company_name            TEXT        NOT NULL,
    latest_news_link        TEXT        NOT NULL,
    CONSTRAINT stocks_pkey PRIMARY KEY (ticker)
);

CREATE TABLE IF NOT EXISTS user_stock_junction (
    user_id                 TEXT        NOT NULL,
    stock_ticker            TEXT        NOT NULL,
    CONSTRAINT user_stock_junction_pkey PRIMARY KEY (user_id, stock_ticker),
    CONSTRAINT user_stock_junction_user_fkey FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT user_stock_junction_stock_fkey FOREIGN KEY (stock_ticker) REFERENCES stocks(ticker) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS user_stock_junction_user_id_idx ON user_stock_junction(user_id);
CREATE INDEX IF NOT EXISTS user_stock_junction_stock_ticker_idx ON user_stock_junction(stock_ticker);

CREATE TABLE IF NOT EXISTS news_articles (
    link                    TEXT,
    stock_ticker            TEXT        NOT NULL,
    summary                 TEXT        NOT NULL,
    sentiment               TEXT        NOT NULL,
    created_at              TIMESTAMP   NOT NULL,
    CONSTRAINT news_articles_pkey PRIMARY KEY (link),
    CONSTRAINT news_articles_stock_fkey FOREIGN KEY (stock_ticker) REFERENCES stocks(ticker) ON DELETE CASCADE
);
CREATE INDEX IF NOT EXISTS news_articles_stock_ticker_idx ON news_articles(stock_ticker);