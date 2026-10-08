CREATE TABLE url_mapping (
                             id BIGINT NOT NULL AUTO_INCREMENT,
                             short_code VARCHAR(20),
                             original_url VARCHAR(2048) NOT NULL,
                             created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                             PRIMARY KEY (id),
                             UNIQUE KEY uk_url_mapping_short_code (short_code)
);