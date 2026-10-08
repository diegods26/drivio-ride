CREATE TABLE passenger_profiles (
    passenger_id UUID PRIMARY KEY,
    phone_number VARCHAR(16),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_passenger_profiles_user
        FOREIGN KEY (passenger_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE passenger_addresses (
    id UUID PRIMARY KEY,
    passenger_id UUID NOT NULL,
    label VARCHAR(50) NOT NULL,
    street VARCHAR(160) NOT NULL,
    city VARCHAR(100) NOT NULL,
    state CHAR(2) NOT NULL,
    postal_code CHAR(8) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_passenger_addresses_user
        FOREIGN KEY (passenger_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT ck_passenger_addresses_state
        CHECK (state IN (
            'AC', 'AL', 'AP', 'AM', 'BA', 'CE', 'DF', 'ES', 'GO',
            'MA', 'MT', 'MS', 'MG', 'PA', 'PB', 'PR', 'PE', 'PI',
            'RJ', 'RN', 'RS', 'RO', 'RR', 'SC', 'SP', 'SE', 'TO'
        )),
    CONSTRAINT ck_passenger_addresses_postal_code
        CHECK (postal_code ~ '^[0-9]{8}$')
);

CREATE INDEX ix_passenger_addresses_owner_created
    ON passenger_addresses (passenger_id, created_at DESC);