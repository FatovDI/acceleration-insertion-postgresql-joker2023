SET SCHEMA 'test_insertion';

INSERT INTO currency (code, name) VALUES ('RUB', 'Российский рубль');
INSERT INTO currency (code, name) VALUES ('USD', 'Доллар США');
INSERT INTO currency (code, name) VALUES ('CHY', 'Юань');
INSERT INTO currency (code, name) VALUES ('EUR', 'Евро');

INSERT INTO account (cur, name, number) VALUES ('RUB', 'ГПБ, руб.', '00000000000000000001');
INSERT INTO account (cur, name, number) VALUES ('USD', 'ГПБ, usd.', '00000000000000000002');
INSERT INTO account (cur, name, number) VALUES ('EUR', 'ГПБ, eur.', '00000000000000000003');
INSERT INTO account (cur, name, number) VALUES ('RUB', 'СБЕР, руб.', '00000000000000000004');
INSERT INTO account (cur, name, number) VALUES ('CHY', 'СБЕР, юань.', '00000000000000000005');