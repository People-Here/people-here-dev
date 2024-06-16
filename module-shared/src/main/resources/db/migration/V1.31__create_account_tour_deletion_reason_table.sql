-- account 정보 삭제 사유 테이블 생성
CREATE TABLE account_deactivation_reason
(
    id         BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    account_id BIGINT    NOT NULL,
    reason     VARCHAR   NOT NULL,
    created_at TIMESTAMP NOT NULL default NOW(),
    updated_at TIMESTAMP NOT NULL default NOW() -- 수정일
);

-- tour 정보 삭제 사유 테이블 생성
CREATE TABLE tour_deletion_reason
(
    id         BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY,
    tour_id    BIGINT    NOT NULL,
    reason     VARCHAR   NOT NULL,
    created_at TIMESTAMP NOT NULL default NOW(),
    updated_at TIMESTAMP NOT NULL default NOW() -- 수정일
);

CREATE index if not exists idx_account_deactivation_reason_account_id ON account_deactivation_reason (account_id);
CREATE index if not exists idx_tour_deletion_reason_tour_id ON tour_deletion_reason (tour_id);
