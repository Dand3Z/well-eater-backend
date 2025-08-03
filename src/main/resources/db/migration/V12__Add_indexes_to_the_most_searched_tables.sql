CREATE INDEX idx_to_delete_name__foods ON foods (to_delete, name);
CREATE INDEX idx_username_diet_date__diet_days ON diet_days (username, diet_date);
CREATE INDEX idx_meal_id_food_id_meal__foods ON meal_foods (meal_id, food_id);
