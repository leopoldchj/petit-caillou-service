CREATE TABLE users
(
  username         VARCHAR(30)  NOT NULL,
  email         VARCHAR(320) NOT NULL,
  password_hash VARCHAR(100) NOT NULL,
  role          VARCHAR(20)  NOT NULL,
  CONSTRAINT pk_users PRIMARY KEY (username),
  CONSTRAINT uq_users_email UNIQUE (email)
);
