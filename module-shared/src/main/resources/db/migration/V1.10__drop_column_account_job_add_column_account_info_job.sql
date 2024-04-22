-- account 테이블에 직업 컬럼 삭제
ALTER TABLE account
DROP
COLUMN job;

-- account_info 테이블에 직업 컬럼 추가
ALTER TABLE account_info
    ADD COLUMN job VARCHAR;
