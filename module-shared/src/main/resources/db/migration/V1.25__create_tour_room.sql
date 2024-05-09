-- 투어 룸 테이블 생성
CREATE TABLE tour_room
(
    id         BIGINT PRIMARY KEY,
    tour_id    BIGINT    NOT NULL,
    owner_id   BIGINT    NOT NULL,
    guest_id   BIGINT    NOT NULL,
    created_at TIMESTAMP NOT NULL default NOW(),
    updated_at TIMESTAMP NOT NULL default NOW() -- 수정일
);

-- 투어 룸 인덱스 및 제약사항 추가
CREATE index if not exists tour_room_owner_id_idx ON tour_room (owner_id);
CREATE index if not exists tour_room_owner_id_idx ON tour_room (guest_id);
CREATE index if not exists tour_room_tour_id_idx ON tour_room (tour_id);

ALTER TABLE tour_message
DROP
COLUMN tour_id;

ALTER TABLE tour_message
    ADD COLUMN tour_room_id BIGINT NOT NULL;

CREATE index if not exists tour_message_tour_room_id_idx ON tour_message (tour_room_id);
