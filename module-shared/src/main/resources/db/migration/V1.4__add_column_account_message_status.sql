-- account 테이블 direct message status 컬럼 추가
ALTER TABLE account
    ADD COLUMN direct_message_status BOOLEAN default true;
