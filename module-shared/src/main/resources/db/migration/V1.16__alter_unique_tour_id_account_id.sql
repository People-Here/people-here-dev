-- tour 테이블에 id, account_id unique 추가
ALTER TABLE tour
    ADD CONSTRAINT unique_tour_id_account_id UNIQUE (id, account_id);
