CREATE TABLE rooms(
    id         BIGSERIAL PRIMARY KEY,
    name       VARCHAR(100) NOT NULL UNIQUE,
    capacity   INT NOT NULL CHECK (capacity > 0),
    location   VARCHAR(100),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);