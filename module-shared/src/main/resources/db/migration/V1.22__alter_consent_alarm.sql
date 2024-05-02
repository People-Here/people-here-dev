-- 알람 동의 여부 컬럼 쪽지 알람 동의 여부 컬럼으로 변경
ALTER TABLE consent
    RENAME COLUMN alarm_consent TO message_alarm_consent;

-- 약속 알람 동의 여부 컬럼 추가
ALTER TABLE consent
    ADD COLUMN meeting_alarm_consent BOOLEAN;
