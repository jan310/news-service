CREATE TABLE IF NOT EXISTS users (
    id                  TEXT    PRIMARY KEY,
    email               TEXT    UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS stocks (
    ticker              TEXT    PRIMARY KEY,
    company_name        TEXT    NOT NULL,
    latest_news_link    TEXT    NOT NULL
);

CREATE TABLE IF NOT EXISTS user_stock_junction (
    user_id             TEXT    NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    stock_ticker        TEXT    NOT NULL REFERENCES stocks(ticker) ON DELETE CASCADE,
    PRIMARY KEY (user_id, stock_ticker)
);
CREATE INDEX IF NOT EXISTS idx_user_stock_junction_user_id ON user_stock_junction(user_id);
CREATE INDEX IF NOT EXISTS idx_user_stock_junction_stock_ticker ON user_stock_junction(stock_ticker);

CREATE TABLE IF NOT EXISTS news_articles (
    link                TEXT    PRIMARY KEY,
    stock_ticker        TEXT    NOT NULL REFERENCES stocks(ticker) ON DELETE CASCADE,
    summary             TEXT    NOT NULL,
    sentiment           TEXT    NOT NULL
);
CREATE INDEX IF NOT EXISTS idx_news_articles_stock_ticker ON news_articles(stock_ticker);