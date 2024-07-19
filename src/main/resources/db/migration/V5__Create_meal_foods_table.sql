CREATE TABLE meal_foods (
        id BIGINT NOT NULL AUTO_INCREMENT,
        meal_id BIGINT NOT NULL,
        food_id BIGINT NOT NULL,
        PRIMARY KEY (id),
        FOREIGN KEY (meal_id) REFERENCES meals(id) ON DELETE CASCADE,
        FOREIGN KEY (food_id) REFERENCES foods(id) ON DELETE CASCADE
);