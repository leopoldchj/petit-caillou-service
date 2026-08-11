CREATE TABLE job_applications
(
  id               VARCHAR(36)  NOT NULL,
  owner_username   VARCHAR(30)  NOT NULL,
  company_id       VARCHAR(36)  NOT NULL,
  link             VARCHAR(2048),
  title            VARCHAR(255) NOT NULL,
  description      VARCHAR(2000),
  application_date DATE,
  response_status  VARCHAR(20)  NOT NULL,
  CONSTRAINT pk_job_applications PRIMARY KEY (id),
  CONSTRAINT fk_job_applications_owner FOREIGN KEY (owner_username) REFERENCES users (username),
  CONSTRAINT fk_job_applications_company FOREIGN KEY (company_id) REFERENCES companies (id)
);
CREATE INDEX ix_job_applications_owner ON job_applications (owner_username);
CREATE INDEX ix_job_applications_company ON job_applications (company_id);
