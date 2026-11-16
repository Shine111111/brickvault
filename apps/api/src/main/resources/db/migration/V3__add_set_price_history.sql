CREATE SEQUENCE IF NOT EXISTS set_price_history_seq START WITH 1 INCREMENT BY 50;

CREATE TABLE IF NOT EXISTS set_price_history (
    id                          INTEGER PRIMARY KEY,
    lego_set_id                 INTEGER        NOT NULL REFERENCES lego_set(id) ON DELETE CASCADE,
    recorded_at                 DATE           NOT NULL,
    retail_price_cad            NUMERIC(10, 2),
    estimated_market_value_cad  NUMERIC(10, 2) NOT NULL
);

INSERT INTO set_price_history (id, lego_set_id, recorded_at, retail_price_cad, estimated_market_value_cad)
SELECT nextval('set_price_history_seq'), id, DATE '2024-07-01', retail_price_cad, COALESCE(retail_price_cad, estimated_resale_value_cad, 0.00)
FROM lego_set;

INSERT INTO set_price_history (id, lego_set_id, recorded_at, retail_price_cad, estimated_market_value_cad)
SELECT nextval('set_price_history_seq'), id, DATE '2025-01-01', retail_price_cad, COALESCE(estimated_resale_value_cad, retail_price_cad, 0.00)
FROM lego_set;
