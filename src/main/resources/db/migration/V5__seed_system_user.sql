-- Reserved technical account that owns public (AI-ingested) offers.
INSERT INTO users (username, email, password_hash, role)
VALUES ('system', 'system@petit-caillou.internal', 'x', 'USER');
