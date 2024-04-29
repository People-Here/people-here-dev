-- 번역 결과 테이블 생성
CREATE TABLE translate_text
(
    id               BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    src              VARCHAR,
    dest             VARCHAR,
    target_lang_code VARCHAR,
    created_at       TIMESTAMP NOT NULL default NOW(),
    updated_at       TIMESTAMP NOT NULL default NOW() -- 수정일
);

-- 번역 결과 테이블 src, dest gin index 생성
CREATE
EXTENSION IF NOT EXISTS pg_trgm;
CREATE index if not exists gin_translate_src_idx ON translate_text USING gin (src gin_trgm_ops);
CREATE index if not exists gin_translate_dest_idx ON translate_text USING gin (dest gin_trgm_ops);
