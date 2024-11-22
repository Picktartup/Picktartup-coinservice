INSERT INTO cointransaction (t_created_at, t_type, t_coin_amount, t_pay_id, t_pay_method, user_id) VALUES
('2024-11-01 14:45:00', 'PAYMENT', 100, 'payment-1aeiuhl10sdjk', 'CARD', 1),
('2024-11-02 10:45:00', 'PAYMENT', 50, 'payment-634la8ff3klp', 'CARD', 1);

INSERT INTO cointransaction (t_created_at, t_type, t_coin_amount, t_exc_bank, t_exc_account, user_id) VALUES
('2024-11-03 14:45:00', 'EXCHANGE', 1000, 'IBK', '1289237817237', 1),
('2024-11-04 10:45:00', 'EXCHANGE', 5000, 'KB', '389147198719', 1);