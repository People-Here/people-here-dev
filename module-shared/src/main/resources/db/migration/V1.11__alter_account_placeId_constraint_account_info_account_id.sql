-- account_info 테이블에 account_id 컬럼 unique 제거(언어별로 생성위해)
ALTER TABLE account_info
DROP
CONSTRAINT if exists account_info_account_id_key;

-- account_info 테이블에 account_id 컬럼 index 추가
CREATE index if not exists account_info_account_id_fkey ON account_info (account_id);

-- account & tour 테이블의 place_id 컬럼 varchar로 변경
ALTER TABLE account
ALTER
COLUMN place_id TYPE VARCHAR USING place_id::VARCHAR;
ALTER TABLE tour
ALTER
COLUMN place_id TYPE VARCHAR USING place_id::VARCHAR;
