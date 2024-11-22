CREATE TABLE cointransaction (
	transaction_id int8 DEFAULT nextval('transaction_seq'::regclass) NOT NULL,
	t_created_at timestamp NOT NULL,
	t_type varchar(255) NOT NULL,
	t_coin_amount float8 NOT NULL,
	t_pay_id varchar(50) NULL,
	t_pay_method varchar(30) NULL,
	t_exc_bank varchar(30) NULL,
	t_exc_account varchar(50) NULL,
	user_id int8 NOT NULL,
	CONSTRAINT cointransaction_pkey PRIMARY KEY (transaction_id),
	CONSTRAINT cointransaction_t_type_check CHECK (((t_type)::text = ANY (ARRAY[('PAYMENT'::character varying)::text, ('EXCHANGE'::character varying)::text])))
);