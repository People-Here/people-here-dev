-- 투어-유저 좋아요 릴레이션
CREATE TABLE tour_like
(
    id         BIGINT NOT NULL GENERATED ALWAYS AS IDENTITY,
    tour_id    BIGINT NOT NULL,
    account_id BIGINT  NOT NULL,
    is_like    BOOLEAN NOT NULL
);

-- 투어-유저 좋아요 릴레이션 tour_id, account_id unique 제약 추가 및 인덱스 생성
ALTER TABLE tour_like
    ADD CONSTRAINT unique_tour_like_tour_id_account_id UNIQUE (tour_id, account_id);
CREATE index if not exists idx_tour_like_tour_id ON tour_like (tour_id);
CREATE index if not exists idx_tour_like_account_id ON tour_like (account_id);
