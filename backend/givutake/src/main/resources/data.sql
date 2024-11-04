-- Check if data has already been initialized
CREATE TABLE IF NOT EXISTS initialization_flag (initialized BOOLEAN);

-- Insert initial category data
INSERT INTO categories (category_name, category_type, created_date, modified_date)
SELECT * FROM (
                  SELECT '지역상품권' AS category_name, 0 AS category_type, NOW() AS created_date, NOW() AS modified_date
                  UNION ALL SELECT '농축산물', 0, NOW(), NOW()
                  UNION ALL SELECT '수산물', 0, NOW(), NOW()
                  UNION ALL SELECT '가공식품', 0, NOW(), NOW()
                  UNION ALL SELECT '공예품', 0, NOW(), NOW()
                  UNION ALL SELECT '재난재해', 1, NOW(), NOW()
                  UNION ALL SELECT '지역기부', 1, NOW(), NOW()
              ) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM initialization_flag WHERE initialized = TRUE);

-- Mark data as initialized if not already done
INSERT INTO initialization_flag (initialized)
SELECT TRUE
    WHERE NOT EXISTS (SELECT 1 FROM initialization_flag WHERE initialized = TRUE);