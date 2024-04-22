-- account_info 테이블에 account_id 컬럼과 langcode unique 추가
ALTER TABLE account_info
    ADD CONSTRAINT unique_account_id_language UNIQUE (account_id, language);

