ALTER TABLE meals ADD COLUMN diet_day_id BIGINT;
ALTER TABLE meals ADD CONSTRAINT fk_diet_day_id FOREIGN KEY (diet_day_id) REFERENCES diet_days(id);