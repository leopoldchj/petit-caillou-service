CREATE TABLE offers
(
  id               VARCHAR(36)  NOT NULL,
  company_id       VARCHAR(36)  NOT NULL,
  title            VARCHAR(255) NOT NULL,
  location         VARCHAR(255),
  publication_date DATE         NOT NULL,
  link             VARCHAR(2048),
  description      VARCHAR(4000),
  created_by       VARCHAR(30)  NOT NULL,
  verified         BOOLEAN      NOT NULL,
  content_hash     VARCHAR(64)  NOT NULL,
  CONSTRAINT pk_offers PRIMARY KEY (id),
  CONSTRAINT fk_offers_company FOREIGN KEY (company_id) REFERENCES companies (id),
  CONSTRAINT fk_offers_created_by FOREIGN KEY (created_by) REFERENCES users (username),
  CONSTRAINT uq_offers_hash_scope UNIQUE (content_hash, created_by)
);
CREATE INDEX ix_offers_company ON offers (company_id);
CREATE INDEX ix_offers_created_by ON offers (created_by);
CREATE INDEX ix_offers_publication_date ON offers (publication_date);
