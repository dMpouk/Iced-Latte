CREATE TABLE IF NOT EXISTS orders
(
    id                          UUID        PRIMARY KEY,
    user_id                     UUID        NOT NULL,
    created_at                  TIMESTAMPTZ DEFAULT current_timestamp,
    status                      VARCHAR(55) NOT NULL,
    items_quantity              INT         NOT NULL CHECK (items_quantity >= 0),
    line                        VARCHAR(55) NOT NULL,
    city                        VARCHAR(55) NOT NULL,
    country                     VARCHAR(55) NOT NULL,
    postcode                    VARCHAR(55) NOT NULL,
    delivery_cost               DECIMAL     NOT NULL CHECK (delivery_cost > 0),
    tax_cost                    DECIMAL     NOT NULL CHECK (tax_cost > -1)
)
