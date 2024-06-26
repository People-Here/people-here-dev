-- 유저 테이블 생성
CREATE TABLE account
(
    id           BIGINT PRIMARY KEY,
    first_name   VARCHAR,
    last_name    VARCHAR,
    user_id      VARCHAR UNIQUE NOT NULL,               -- 유저 아이디
    email        VARCHAR UNIQUE NOT NULL,               -- 이메일
    phone_number VARCHAR,
    password     VARCHAR,
    region       VARCHAR,
    birth_date   DATE,
    gender       VARCHAR,
    role         VARCHAR,
    active       BOOLEAN,
    created_at   TIMESTAMP      NOT NULL default NOW(),
    updated_at   TIMESTAMP      NOT NULL default NOW(), -- 수정일
    deleted_at   TIMESTAMP NULL
);

-- 약관 테이블 생성
CREATE TABLE consent
(
    id                BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    account_id        BIGINT    NOT NULL,
    privacy_consent   BOOLEAN NULL,
    marketing_consent BOOLEAN NULL,
    alarm_consent     BOOLEAN NULL,
    created_at        TIMESTAMP NOT NULL default NOW(),
    updated_at        TIMESTAMP NOT NULL default NOW() -- 수정일
);

-- 언어 테이블 생성
CREATE TABLE language
(
    id   INT NOT NULL GENERATED ALWAYS AS IDENTITY,
    name VARCHAR
);

-- 유저-언어 릴레이션 생성
CREATE TABLE user_language
(
    id          BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    language_id INT       NOT NULL,
    account_id  BIGINT    NOT NULL,
    created_at  TIMESTAMP NOT NULL default NOW(),
    updated_at  TIMESTAMP NOT NULL default NOW() -- 수정일
);
