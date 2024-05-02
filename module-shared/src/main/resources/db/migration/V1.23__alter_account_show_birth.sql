-- 유저 테이블에 show_birth 컬럼 추가
ALTER TABLE account
    ADD COLUMN show_birth BOOLEAN default true;
