CREATE TABLE client (
    id UUID PRIMARY KEY,
    fio VARCHAR(30),
    telephone_number VARCHAR(20),
    email VARCHAR(30),
    passport_number VARCHAR(30)
);

CREATE TABLE credit (
    id UUID PRIMARY KEY,
    credit_limit NUMERIC(19,2),
    interest_rate NUMERIC(5,2)
);

CREATE TABLE credit_offer (
    id UUID PRIMARY KEY,
    credit_sum NUMERIC(19,2),
    client_id UUID NOT NULL,
    credit_id UUID NOT NULL,
    CONSTRAINT offer_client_fk FOREIGN KEY (client_id) REFERENCES "PUBLIC"."CLIENT" (id),
    CONSTRAINT offer_credit_fk FOREIGN KEY (credit_id) REFERENCES "PUBLIC"."CREDIT" (id)
);

CREATE TABLE payment_schedule (
    id UUID PRIMARY KEY,
    payment_date DATE,
    payment_sum NUMERIC(19,2),
    credit_repayment NUMERIC(19,2),
    interest_repayment NUMERIC(19,2),
    credit_offer_id UUID NOT NULL,
    CONSTRAINT schedule_offer_fk FOREIGN KEY (credit_offer_id) REFERENCES "PUBLIC"."CREDIT_OFFER" (id)
    ON DELETE CASCADE ON UPDATE CASCADE
);
