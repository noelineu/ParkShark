-- PostalCode becomes a value object (code + label) stored on each table: the zipcode table disappears.
-- member also gets street_name / street_number so ME1 can reuse Address as is.

-- contact_person
ALTER TABLE contact_person ADD COLUMN postal_code VARCHAR(20);
ALTER TABLE contact_person ADD COLUMN postal_label VARCHAR(255);
UPDATE contact_person
SET postal_code  = zip,
    postal_label = (SELECT z.label FROM zipcode z WHERE z.zip = contact_person.zip);
ALTER TABLE contact_person DROP COLUMN zip;

-- parking_lot
ALTER TABLE parking_lot ADD COLUMN postal_code VARCHAR(20);
ALTER TABLE parking_lot ADD COLUMN postal_label VARCHAR(255);
UPDATE parking_lot
SET postal_code  = zip,
    postal_label = (SELECT z.label FROM zipcode z WHERE z.zip = parking_lot.zip);
ALTER TABLE parking_lot DROP COLUMN zip;

-- member: split address like V3 did, and the same postal code columns
ALTER TABLE member ADD COLUMN street_name VARCHAR(255);
ALTER TABLE member ADD COLUMN street_number VARCHAR(20);
UPDATE member
SET street_name   = REGEXP_REPLACE(address, ' [^ ]+$', ''),
    street_number = REGEXP_REPLACE(address, '^.* ', '')
WHERE address IS NOT NULL;
ALTER TABLE member DROP COLUMN address;

ALTER TABLE member ADD COLUMN postal_code VARCHAR(20);
ALTER TABLE member ADD COLUMN postal_label VARCHAR(255);
UPDATE member
SET postal_code  = zip,
    postal_label = (SELECT z.label FROM zipcode z WHERE z.zip = member.zip);
ALTER TABLE member DROP COLUMN zip;

DROP TABLE zipcode;