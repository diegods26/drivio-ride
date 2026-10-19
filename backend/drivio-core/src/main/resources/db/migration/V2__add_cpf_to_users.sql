ALTER TABLE users
    ADD COLUMN cpf VARCHAR(11) NOT NULL;

ALTER TABLE users
    ADD CONSTRAINT ck_users_cpf_format
        CHECK (cpf ~ '^[0-9]{11}$');

ALTER TABLE users
    ADD CONSTRAINT uk_users_cpf UNIQUE (cpf);
