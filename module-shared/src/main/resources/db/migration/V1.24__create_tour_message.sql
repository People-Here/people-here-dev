-- 투어 테이블에 쪽지 가능 여부 컬럼 추가
ALTER TABLE tour
    ADD COLUMN direct_message_status BOOLEAN default true;

-- 투어 쪽지 테이블 생성
CREATE TABLE tour_message
(
    id          BIGINT PRIMARY KEY,
    tour_id     BIGINT    NOT NULL,
    sender_id   BIGINT    NOT NULL,
    receiver_id BIGINT    NOT NULL,
    message     VARCHAR   NOT NULL,
    created_at  TIMESTAMP NOT NULL default NOW(),
    updated_at  TIMESTAMP NOT NULL default NOW() -- 수정일
);

-- 투어 쪽지 인덱스 및 제약사항 추가
CREATE index if not exists tour_message_sender_id_idx ON tour_message (sender_id);
CREATE index if not exists tour_message_receiver_id_idx ON tour_message (receiver_id);
CREATE index if not exists tour_message_tour_id_idx ON tour_message (tour_id);

