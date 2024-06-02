-- tour_message 테이블의 readFlag 컬럼을 sender_read_flag, receiver_read_flag로 분리
ALTER TABLE tour_message
    ADD COLUMN sender_read_flag BOOLEAN NOT NULL default false;
ALTER TABLE tour_message
    ADD COLUMN receiver_read_flag BOOLEAN NOT NULL default false;

ALTER TABLE tour_message DROP COLUMN read_flag;
