-- account 테이블에 있던 info 컬럼들을 account_info로 옮긴다
ALTER TABLE account
DROP
COLUMN favorite,
    DROP
COLUMN hobby,
    DROP
COLUMN pet,
    DROP
COLUMN school;

ALTER TABLE account_info
    ADD COLUMN favorite VARCHAR,
    ADD COLUMN hobby VARCHAR,
    ADD COLUMN pet VARCHAR,
    ADD COLUMN school VARCHAR;
