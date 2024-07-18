CREATE TABLE foods (
       id BIGINT NOT NULL AUTO_INCREMENT,
       name VARCHAR(60) NOT NULL,
       category VARCHAR(25) NOT NULL,
       type VARCHAR(25) NOT NULL,
       added_by VARCHAR(255),
       macronutrients_id BIGINT NOT NULL,
       PRIMARY KEY (id),
       FOREIGN KEY (macronutrients_id) REFERENCES macronutrients(id)
);