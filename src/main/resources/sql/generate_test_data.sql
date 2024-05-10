create
definer = springbatch@`%` procedure generate_test_data()
BEGIN
    DECLARE i INT DEFAULT 1;
    DECLARE words VARCHAR(200);
    DECLARE max_rows INT DEFAULT 50000;
    DECLARE word VARCHAR(20);

    -- 사용 가능한 단어 목록
    SET @word_list = 'apple,banana,cherry,date,elderberry,fig,grape,honeydew,kiwi,lemon,mango,nectarine,orange,peach,quince,raspberry,strawberry,tangerine,watermelon,zucchini';

    -- 트랜잭션 시작
START TRANSACTION;

-- 루프를 통해 max_rows 만큼 INSERT 실행
WHILE i <= max_rows DO
        SET words = '';

        -- 3~7개의 랜덤 단어 조합
        WHILE LENGTH(REPLACE(words, ',', '')) < 7 DO
            SET word = (
                SELECT TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(@word_list, ',', rand() * (1 + LENGTH(@word_list) - LENGTH(REPLACE(@word_list, ',', '')))), ',', -1))
            );
            IF word <> '' THEN
                SET words = CONCAT(words, ',', word);
END IF;
END WHILE;

        SET words = TRIM(LEADING ',' FROM words);

        -- INSERT 쿼리 실행
INSERT INTO test_data (original_text) VALUES (words);

SET i = i + 1;
END WHILE;

    -- 트랜잭션 커밋
COMMIT;
END;

