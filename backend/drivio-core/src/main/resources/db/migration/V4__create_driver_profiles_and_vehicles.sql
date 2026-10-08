CREATE TABLE driver_profiles (
    driver_id UUID PRIMARY KEY,
    status VARCHAR(30) NOT NULL,
    is_online BOOLEAN NOT NULL DEFAULT FALSE,
    approved_by UUID,
    approved_at TIMESTAMP WITH TIME ZONE,
    license_status VARCHAR(30) NOT NULL,
    license_valid_until DATE,
    verification_provider VARCHAR(64),
    verification_reference VARCHAR(100),
    license_verified_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_driver_profiles_user
        FOREIGN KEY (driver_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT fk_driver_profiles_approver
        FOREIGN KEY (approved_by) REFERENCES users (id),
    CONSTRAINT ck_driver_profiles_status
        CHECK (status IN ('ONBOARDING', 'PENDING_APPROVAL', 'APPROVED', 'SUSPENDED')),
    CONSTRAINT ck_driver_profiles_license_status
        CHECK (license_status IN (
            'NOT_SUBMITTED', 'PENDING', 'VERIFIED', 'INVALID',
            'EXPIRED', 'UNAVAILABLE', 'INCONCLUSIVE'
        )),
    CONSTRAINT ck_driver_profiles_online_eligibility
        CHECK (NOT is_online OR (status = 'APPROVED' AND license_status = 'VERIFIED')),
    CONSTRAINT ck_driver_profiles_approval_audit
        CHECK (
            (status IN ('APPROVED', 'SUSPENDED')
                AND approved_by IS NOT NULL AND approved_at IS NOT NULL)
            OR (status IN ('ONBOARDING', 'PENDING_APPROVAL')
                AND approved_by IS NULL AND approved_at IS NULL)
        ),
    CONSTRAINT ck_driver_profiles_verified_metadata
        CHECK (
            license_status <> 'VERIFIED'
            OR (license_valid_until IS NOT NULL
                AND verification_provider IS NOT NULL
                AND verification_reference IS NOT NULL
                AND license_verified_at IS NOT NULL)
        )
);

CREATE TABLE driver_vehicles (
    id UUID PRIMARY KEY,
    driver_id UUID NOT NULL,
    plate VARCHAR(7) NOT NULL,
    make VARCHAR(80) NOT NULL,
    model VARCHAR(80) NOT NULL,
    model_year INTEGER NOT NULL,
    category VARCHAR(20) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT FALSE,
    approved_by UUID,
    approved_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_driver_vehicles_profile
        FOREIGN KEY (driver_id) REFERENCES driver_profiles (driver_id) ON DELETE CASCADE,
    CONSTRAINT fk_driver_vehicles_approver
        FOREIGN KEY (approved_by) REFERENCES users (id),
    CONSTRAINT uk_driver_vehicles_plate UNIQUE (plate),
    CONSTRAINT ck_driver_vehicles_plate
        CHECK (plate ~ '^[A-Z]{3}[0-9][A-Z0-9][0-9]{2}$'),
    CONSTRAINT ck_driver_vehicles_year
        CHECK (model_year BETWEEN 1980 AND 2100),
    CONSTRAINT ck_driver_vehicles_category
        CHECK (category IN ('STANDARD', 'COMFORT', 'PREMIUM')),
    CONSTRAINT ck_driver_vehicles_approval_audit
        CHECK (
            NOT is_active
            OR (approved_by IS NOT NULL AND approved_at IS NOT NULL)
        )
);

CREATE INDEX ix_driver_vehicles_owner_active
    ON driver_vehicles (driver_id, is_active);