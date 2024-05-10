-- consent 테이블에 account_id unique 제약 추가
ALTER TABLE consent
    ADD CONSTRAINT unique_consent_account_id UNIQUE (account_id);
