CREATE TABLE job_applications
(
  id               VARCHAR(36) NOT NULL,
  owner_username   VARCHAR(30) NOT NULL,
  offer_id         VARCHAR(36) NOT NULL,
  application_date DATE,
  response_status  VARCHAR(20) NOT NULL,
  notes            VARCHAR(2000),
  CONSTRAINT pk_job_applications PRIMARY KEY (id),
  CONSTRAINT fk_job_applications_owner FOREIGN KEY (owner_username) REFERENCES users (username),
  CONSTRAINT fk_job_applications_offer FOREIGN KEY (offer_id) REFERENCES offers (id),
  CONSTRAINT uq_job_applications_owner_offer UNIQUE (owner_username, offer_id)
);
CREATE INDEX ix_job_applications_owner ON job_applications (owner_username);
CREATE INDEX ix_job_applications_offer ON job_applications (offer_id);
