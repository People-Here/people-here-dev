-- tour_room 테이블에 tour_id, owner_id, guest_id 컬럼 unique 추가
ALTER TABLE tour_room
    ADD CONSTRAINT unique_tour_room_tour_id_owner_id_guest_id UNIQUE (tour_id, owner_id, guest_id);

