-- 투어 테이블 생성
CREATE TABLE tour
(
    id               BIGINT PRIMARY KEY,
    account_id       BIGINT    NOT NULL,
    place_id         BIGINT    NOT NULL,
    title            VARCHAR,
    is_default_image BOOLEAN,
    created_at       TIMESTAMP NOT NULL default NOW(),
    updated_at       TIMESTAMP NOT NULL default NOW() -- 수정일
);

-- 카테고리 테이블 생성
CREATE TABLE category
(
    id   BIGINT  NOT NULL GENERATED ALWAYS AS IDENTITY,
    name VARCHAR NOT NULL
);

-- 투어-카테고리 릴레이션 생성
CREATE TABLE tour_category
(
    id          BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    tour_id     BIGINT    NOT NULL,
    category_id BIGINT    NOT NULL,
    created_at  TIMESTAMP NOT NULL default NOW(),
    updated_at  TIMESTAMP NOT NULL default NOW() -- 수정일
);

-- 투어-이미지 테이블 생성
CREATE TABLE tour_image
(
    id                      BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    tour_id                 BIGINT    NOT NULL,
    thumbnail_url           VARCHAR,
    optimized_thumbnail_url VARCHAR,
    created_at              TIMESTAMP NOT NULL default NOW(),
    updated_at              TIMESTAMP NOT NULL default NOW() -- 수정일
);

-- 장소 테이블 생성 TODO: 추후 변경 예정(위,경도 및 주소 정보)
CREATE TABLE place
(
    id                    BIGINT         NOT NULL GENERATED ALWAYS AS IDENTITY,
    place_id              VARCHAR UNIQUE NOT NULL,
    name                  VARCHAR        NOT NULL,
    default_thumbnail_url VARCHAR,
    latitude              DOUBLE PRECISION,
    longitude             DOUBLE PRECISION,
    address               VARCHAR,
    country               VARCHAR,
    city                  VARCHAR,
    district              VARCHAR,
    street_address        VARCHAR,
    created_at            TIMESTAMP      NOT NULL default NOW(),
    updated_at            TIMESTAMP      NOT NULL default NOW() -- 수정일
);

-- 장소 테이블 이름, 전체 주소 gin index 생성
CREATE
EXTENSION IF NOT EXISTS pg_trgm;
CREATE index if not exists gin_place_name_idx ON place USING gin (name gin_trgm_ops);
CREATE index if not exists gin_place_address_idx ON place USING gin (address gin_trgm_ops);
