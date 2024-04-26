-- tour 테이블에 language 컬럼 추가
ALTER TABLE tour
DROP
COLUMN language,
    DROP
COLUMN title;

-- tour 테이블에 id, language 컬럼 unique 제거
ALTER TABLE tour
DROP
CONSTRAINT if exists unique_tour_id_language;

-- tour 테이블에 테마 컬럼 추가
ALTER TABLE tour
    ADD COLUMN theme VARCHAR;

-- tour_info 테이블 생성
CREATE TABLE tour_info
(
    id          BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    tour_id     BIGINT    NOT NULL,
    language    VARCHAR,
    title       VARCHAR,
    description VARCHAR,
    created_at  TIMESTAMP NOT NULL default NOW(),
    updated_at  TIMESTAMP NOT NULL default NOW() -- 수정일
);

ALTER TABLE tour_info
    ADD CONSTRAINT unique_tour_id_language UNIQUE (tour_id, language);
