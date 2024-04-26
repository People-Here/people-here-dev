-- tour 테이블에 language 컬럼 추가
ALTER TABLE tour
    ADD COLUMN language VARCHAR;

ALTER TABLE tour
    ADD CONSTRAINT unique_tour_id_language UNIQUE (id, language);
