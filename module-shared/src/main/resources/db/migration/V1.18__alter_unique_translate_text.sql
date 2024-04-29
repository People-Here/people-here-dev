-- 번역 결과 테이블에 src, langCode unique 추가
ALTER TABLE translate_text
    ADD CONSTRAINT unique_src_target_lang_code UNIQUE (src, target_lang_code);
