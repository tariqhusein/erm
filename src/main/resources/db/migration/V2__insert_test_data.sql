-- Insert test users
INSERT INTO tb_user (id, email, password, name) VALUES 
    (1, 'tarek@example.com', '$2a$10$3NJ3CND44WSjeIE18GZCRO.pdf50f5JZK/bZlqsyD9dE9o72.fIL2', 'tarek');

-- Adjust the sequence to start after our manually inserted IDs
SELECT setval('tb_user_id_seq', (SELECT MAX(id) FROM tb_user));
