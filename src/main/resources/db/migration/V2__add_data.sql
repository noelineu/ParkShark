INSERT INTO zipcode (zip, label)
VALUES ('1000', 'Bruxelles'),
       ('2000', 'Antwerpen'),
       ('3000', 'Leuven'),
       ('4000', 'Liège'),
       ('9000', 'Gent');

INSERT INTO director (first_name, last_name)
VALUES ('Tony', 'Sharkano'),
       ('Vito', 'Finlione'),
       ('Carla', 'Dentini');

INSERT INTO division (name, original_name, director_id, parent_id)
VALUES ('ParkShark North', 'Parking Vlaanderen NV', (SELECT id FROM director WHERE last_name = 'Sharkano'), NULL),
       ('ParkShark South', 'Parking Wallonie SA', (SELECT id FROM director WHERE last_name = 'Finlione'), NULL),
       ('ParkShark Capital', 'Brussels Parking Co', (SELECT id FROM director WHERE last_name = 'Dentini'), NULL);

INSERT INTO division (name, original_name, director_id, parent_id)
VALUES ('ParkShark Leuven', 'Leuven Parkeer BVBA',
        (SELECT id FROM director WHERE last_name = 'Sharkano'),
        (SELECT id FROM division WHERE name = 'ParkShark North')),
       ('ParkShark Liège', 'Liège Stationnement',
        (SELECT id FROM director WHERE last_name = 'Finlione'),
        (SELECT id FROM division WHERE name = 'ParkShark South'));

INSERT INTO contact_person (name, mobile_phone, telephone, email, address, zip)
VALUES ('Jan Peeters', '0470123456', '016123456', 'jan.peeters@parkshark.be', 'Bondgenotenlaan 10', '3000'),
       ('Marie Dubois', '0485987654', NULL, 'marie.dubois@parkshark.be', 'Rue Léopold 5', '4000'),
       ('Lucas Janssens', NULL, '022345678', 'lucas.janssens@parkshark.be', 'Rue Neuve 20', '1000');

INSERT INTO parking_lot (name, category, max_capacity, price_per_hour, contact_person_id, division_id, address, zip)
VALUES ('Ladeuze Underground', 'UNDERGROUND_BUILDING', 250, 2.50,
        (SELECT id FROM contact_person WHERE email = 'jan.peeters@parkshark.be'),
        (SELECT id FROM division WHERE name = 'ParkShark Leuven'),
        'Ladeuzeplein 1', '3000'),
       ('Guillemins Tower', 'ABOVE_GROUND_BUILDING', 400, 2.00,
        (SELECT id FROM contact_person WHERE email = 'marie.dubois@parkshark.be'),
        (SELECT id FROM division WHERE name = 'ParkShark Liège'),
        'Place des Guillemins 2', '4000'),
       ('Grand-Place Deep', 'UNDERGROUND_BUILDING', 150, 4.00,
        (SELECT id FROM contact_person WHERE email = 'lucas.janssens@parkshark.be'),
        (SELECT id FROM division WHERE name = 'ParkShark Capital'),
        'Grand-Place 3', '1000'),
       ('Tiny Shark Box', 'ABOVE_GROUND_BUILDING', 2, 3.00,
        (SELECT id FROM contact_person WHERE email = 'jan.peeters@parkshark.be'),
        (SELECT id FROM division WHERE name = 'ParkShark North'),
        'Meir 50', '2000');

INSERT INTO license_plate (plate_number, country)
VALUES ('1-ABC-123', 'Belgium'),
       ('1-DEF-456', 'Belgium'),
       ('2-GHI-789', 'Belgium'),
       ('AB-123-CD', 'France');

INSERT INTO member (first_name, last_name, telephone, email, address, zip, license_plate_number, registration_date,
                    membership_level)
VALUES ('Emma', 'Willems', '0471111111', 'emma.willems@mail.be', 'Tiensestraat 12', '3000', '1-ABC-123', '2026-01-15',
        'BRONZE'),
       ('Noah', 'Lambert', '0472222222', 'noah.lambert@mail.be', 'Rue du Pont 8', '4000', '1-DEF-456', '2026-03-02',
        'SILVER'),
       ('Olivia', 'Maes', '0473333333', 'olivia.maes@mail.be', 'Avenue Louise 100', '1000', '2-GHI-789', '2026-05-20',
        'GOLD'),
       ('Louis', 'Martin', '0033612345678', 'louis.martin@mail.fr', 'Korenmarkt 4', '9000', 'AB-123-CD', '2026-09-10',
        'BRONZE');

INSERT INTO parking_allocation (member_id, parking_lot_id, license_plate_number)
VALUES ((SELECT id FROM member WHERE email = 'emma.willems@mail.be'),
        (SELECT id FROM parking_lot WHERE name = 'Ladeuze Underground'), '1-ABC-123'),
       ((SELECT id FROM member WHERE email = 'noah.lambert@mail.be'),
        (SELECT id FROM parking_lot WHERE name = 'Guillemins Tower'), '1-DEF-456'),
       ((SELECT id FROM member WHERE email = 'olivia.maes@mail.be'),
        (SELECT id FROM parking_lot WHERE name = 'Grand-Place Deep'), '9-XYZ-999'), -- gold avec une autre plaque
       ((SELECT id FROM member WHERE email = 'emma.willems@mail.be'),
        (SELECT id FROM parking_lot WHERE name = 'Grand-Place Deep'), '1-ABC-123'),
       ((SELECT id FROM member WHERE email = 'olivia.maes@mail.be'),
        (SELECT id FROM parking_lot WHERE name = 'Ladeuze Underground'), '2-GHI-789'),
       ((SELECT id FROM member WHERE email = 'noah.lambert@mail.be'),
        (SELECT id FROM parking_lot WHERE name = 'Tiny Shark Box'), '1-DEF-456'),
       ((SELECT id FROM member WHERE email = 'louis.martin@mail.fr'),
        (SELECT id FROM parking_lot WHERE name = 'Tiny Shark Box'), 'AB-123-CD');