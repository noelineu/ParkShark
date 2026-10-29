-- Story PL1: an address consists of a street name, a street number and a postal code.
-- Splits the single "address" column of contact_person and parking_lot into street_name + street_number.
-- Existing values like 'Bondgenotenlaan 10' are split on the last space.

ALTER TABLE contact_person ADD COLUMN street_name VARCHAR(255);
ALTER TABLE contact_person ADD COLUMN street_number VARCHAR(20);
UPDATE contact_person
SET street_name   = REGEXP_REPLACE(address, ' [^ ]+$', ''),
    street_number = REGEXP_REPLACE(address, '^.* ', '')
WHERE address IS NOT NULL;
ALTER TABLE contact_person DROP COLUMN address;

ALTER TABLE parking_lot ADD COLUMN street_name VARCHAR(255);
ALTER TABLE parking_lot ADD COLUMN street_number VARCHAR(20);
UPDATE parking_lot
SET street_name   = REGEXP_REPLACE(address, ' [^ ]+$', ''),
    street_number = REGEXP_REPLACE(address, '^.* ', '')
WHERE address IS NOT NULL;
ALTER TABLE parking_lot DROP COLUMN address;
