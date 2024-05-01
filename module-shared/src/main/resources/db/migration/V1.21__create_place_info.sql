-- place_info 테이블 생성
CREATE TABLE place_info
(
    id             BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    place_id       VARCHAR   NOT NULL,
    language       VARCHAR,
    name           VARCHAR   NOT NULL,
    address        VARCHAR,
    country        VARCHAR,
    city           VARCHAR,
    district       VARCHAR,
    street_address VARCHAR,
    created_at     TIMESTAMP NOT NULL default NOW(),
    updated_at     TIMESTAMP NOT NULL default NOW() -- 수정일
);

-- place 테이블의 동일 컬럼 정보를 place_info 테이블로 이동
INSERT INTO place_info (place_id, language, name, address, country, city, district, street_address)
SELECT place_id, 'KOREAN' AS language, name, address, country, city, district, street_address
FROM place;

-- place 테이블의 동일 컬럼 삭제
ALTER TABLE place
DROP
COLUMN name,
DROP
COLUMN address,
DROP
COLUMN country,
DROP
COLUMN city,
DROP
COLUMN district,
DROP
COLUMN street_address;

-- 장소 테이블 이름, 전체 주소 gin index 생성 및 제약 조건 설정
CREATE
EXTENSION IF NOT EXISTS pg_trgm;
CREATE index if not exists gin_place_info_name_idx ON place_info USING gin (name gin_trgm_ops);
CREATE index if not exists gin_place_info_address_idx ON place_info USING gin (address gin_trgm_ops);

ALTER TABLE place_info
    ADD CONSTRAINT unique_place_info_place_id_language UNIQUE (place_id, language);

CREATE index if not exists idx_place_info_place_id ON place_info (place_id);
CREATE index if not exists idx_place_info_language ON place_info (language);

