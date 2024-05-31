-- tour_message 테이블에 메시지 읽음 여부 컬럼 추가
ALTER TABLE tour_message
    ADD COLUMN read_flag BOOLEAN NOT NULL default false;
