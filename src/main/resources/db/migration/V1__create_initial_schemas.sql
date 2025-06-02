

CREATE TABLE IF NOT EXISTS tb_user (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(200) NOT NULL,
    password VARCHAR(129) NOT NULL,
    name VARCHAR(120)
);
CREATE TABLE IF NOT EXISTS tb_user_external_project (
    id VARCHAR(200) NOT NULL,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    PRIMARY KEY (id, user_id)
); 