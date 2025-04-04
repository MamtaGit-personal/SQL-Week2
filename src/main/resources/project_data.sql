
-- Insert into the category table
INSERT INTO category (category_name) VALUES ('Repairs');
INSERT INTO category (category_name) VALUES ('Cooking');
INSERT INTO category (category_name) VALUES ('Backyard');
INSERT INTO category (category_name) VALUES ('Buyiing Material');

-- Project 1
INSERT INTO project (project_name, estimated_hours, actual_hours, difficulty, notes) VALUES ('Home Repair', 2.27, 03.30, 2, 'Buy and hang a door');

INSERT INTO material (project_id, material_name, num_required, cost) VALUES (1, 'door frame', 1, 175);
INSERT INTO material (project_id, material_name, num_required, cost) VALUES (1, 'nails', 20, 10.25);
INSERT INTO material (project_id, material_name, num_required, cost) VALUES (1, 'paint', 1, 12.5);

INSERT INTO step (project_id, step_text, step_order) VALUES (1, 'Align hangers', 1);
INSERT INTO step (project_id, step_text, step_order) VALUES (1, 'Nail it on the frame', 2);
INSERT INTO step (project_id, step_text, step_order) VALUES (1, 'Paint', 3);

INSERT INTO project_category (project_id, category_id) VALUES (1, 1);
INSERT INTO project_category (project_id, category_id) VALUES (1, 4);

-- Project 2
INSERT INTO project (project_name, estimated_hours, actual_hours, difficulty, notes) VALUES ('Baking', 2.0, 3.3, 2, 'Bake a nice cake.');

INSERT INTO material (project_id, material_name, num_required, cost) VALUES (2, 'chocolate cake mix', 1, 16.25);
INSERT INTO material (project_id, material_name, num_required, cost) VALUES (2, 'Eggs and butter', 2, 16.25);

INSERT INTO step (project_id, step_text, step_order) VALUES (2, 'Prepare cake', 1);
INSERT INTO step (project_id, step_text, step_order) VALUES (2, 'Bake cake', 2);
INSERT INTO step (project_id, step_text, step_order) VALUES (2, 'Remove Cake.', 3);

INSERT INTO project_category (project_id, category_id) VALUES (2, 2);
INSERT INTO project_category (project_id, category_id) VALUES (2, 4);

-- Project 3
INSERT INTO project (project_name, estimated_hours, actual_hours, difficulty, notes) VALUES ('Backyard Work', 20.2, 15.5, 4, 'Install grass and plant flowers.');

INSERT INTO material (project_id, material_name, num_required, cost) VALUES (3, 'Grass', 10, 30.00);
INSERT INTO material (project_id, material_name, num_required, cost) VALUES (3, 'Flowers', 15, 20.25);

INSERT INTO step (project_id, step_text, step_order) VALUES (3, 'Install grass', 1);
INSERT INTO step (project_id, step_text, step_order) VALUES (3, 'Plant flowers', 2);

INSERT INTO project_category (project_id, category_id) VALUES (3, 4);

