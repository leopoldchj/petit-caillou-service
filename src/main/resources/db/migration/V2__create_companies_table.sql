CREATE TABLE companies
(
  id              VARCHAR(36)  NOT NULL,
  name            VARCHAR(255) NOT NULL,
  normalized_name VARCHAR(255) NOT NULL,
  website         VARCHAR(2048),
  CONSTRAINT pk_companies PRIMARY KEY (id),
  CONSTRAINT uq_companies_normalized_name UNIQUE (normalized_name)
);
