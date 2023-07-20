Create TABLE if not exists Node
(
--     id      BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    id      serial PRIMARY KEY,
    version INT,
    parent_id  INT,
    node_value    VARCHAR(255) NOT NULL
);
