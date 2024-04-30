-- 투어 테이블과 관련된 인덱스 생성
CREATE index if not exists idx_tour_image_tour_id_fkey ON tour_image (tour_id);
CREATE index if not exists idx_tour_info_tour_id_fkey ON tour_info (tour_id);
CREATE index if not exists idx_tour_info_language ON tour_info (language);
CREATE index if not exists idx_tour_place_id ON tour (place_id);

-- account_info 테이블에 language 인덱스 추가
CREATE index if not exists idx_account_info_language ON account_info (language);
