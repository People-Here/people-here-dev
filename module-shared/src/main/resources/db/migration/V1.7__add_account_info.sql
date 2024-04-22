-- account 테이블에 프로필 정보를 위한 컬럼 추가
ALTER TABLE account
    ADD COLUMN optimized_profile_image_url VARCHAR,
    ADD COLUMN favorite VARCHAR,
    ADD COLUMN hobby VARCHAR,
    ADD COLUMN pet VARCHAR,
    ADD COLUMN school VARCHAR,
    ADD COLUMN place_id BIGINT;

CREATE index if not exists idx_place_id_fkey ON account (place_id);

-- account_info 테이블 생성
CREATE TABLE account_info
(
    id         BIGINT        NOT NULL GENERATED ALWAYS AS IDENTITY,
    account_id BIGINT UNIQUE NOT NULL,
    language   VARCHAR,
    introduce  VARCHAR,
    created_at TIMESTAMP     NOT NULL default NOW(),
    updated_at TIMESTAMP     NOT NULL default NOW() -- 수정일
);

-- tour 테이블에 account_id index 추가
CREATE index if not exists idx_account_id_fkey ON tour (account_id);
CREATE index if not exists idx_place_id_fkey ON tour (place_id);
