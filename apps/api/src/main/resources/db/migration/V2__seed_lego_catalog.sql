-- Seed 12 LEGO sets across Ninjago, Architecture Skylines, Technic, and Star Wars

INSERT INTO lego_set (
    id, set_number, name, theme, piece_count, release_year,
    retail_price_cad, estimated_resale_value_cad, retirement_status, description
) VALUES
(
    nextval('lego_set_seq'), '71799', 'NINJAGO City Markets', 'Ninjago',
    6163, 2023, 479.99, 520.00, 'ACTIVE',
    'Four-story modular NINJAGO City waterfront market with working cable car, bakery, karaoke club, and 21 minifigures.'
),
(
    nextval('lego_set_seq'), '71719', 'Zane''s Mino Creature', 'Ninjago',
    616, 2020, 69.99, 95.00, 'RETIRED',
    'Posable Mino creature with disc shooter and dungeons board game elements from the Shintaro mountain arc.'
),
(
    nextval('lego_set_seq'), '71741', 'NINJAGO City Gardens', 'Ninjago',
    5686, 2021, 399.99, 510.00, 'RETIRED',
    'Five-tier 10th-anniversary modular NINJAGO City expansion featuring a museum, noodle house, and ninja zone.'
),
(
    nextval('lego_set_seq'), '21057', 'Singapore', 'Architecture',
    827, 2022, 79.99, 85.00, 'ACTIVE',
    'Skyline build featuring Marina Bay Sands, OCBC Centre, One Raffles Place, Lau Pa Sat, and Supertree Grove.'
),
(
    nextval('lego_set_seq'), '21044', 'Paris', 'Architecture',
    649, 2019, 69.99, 75.00, 'ACTIVE',
    'Parisian skyline uniting the Eiffel Tower, Arc de Triomphe, Champs-Elysees, Tour Montparnasse, and the Louvre.'
),
(
    nextval('lego_set_seq'), '21028', 'New York City', 'Architecture',
    598, 2016, 69.99, 72.00, 'ACTIVE',
    'Manhattan skyline model including the Flatiron Building, Chrysler Building, Empire State Building, and Statue of Liberty.'
),
(
    nextval('lego_set_seq'), '42143', 'Ferrari Daytona SP3', 'Technic',
    3778, 2022, 559.99, 580.00, 'ACTIVE',
    '1:8 scale Technic supercar with 8-speed sequential gearbox, moving V12 pistons, and butterfly doors.'
),
(
    nextval('lego_set_seq'), '42154', '2022 Ford GT', 'Technic',
    1466, 2023, 149.99, 155.00, 'ACTIVE',
    '1:12 scale Technic Ford GT featuring rear-wheel drive with differential, working V6 engine, and adjustable spoiler.'
),
(
    nextval('lego_set_seq'), '42115', 'Lamborghini Sian FKP 37', 'Technic',
    3696, 2020, 499.99, 540.00, 'ACTIVE',
    '1:8 scale lime-green Technic supercar with V12 engine, movable rear spoiler, and scissor doors.'
),
(
    nextval('lego_set_seq'), '75192', 'Millennium Falcon', 'Star Wars',
    7541, 2017, 1049.99, 1120.00, 'ACTIVE',
    'Ultimate Collector Series Corellian freighter with interchangeable sensor dishes and detailed interior bays.'
),
(
    nextval('lego_set_seq'), '75355', 'X-Wing Starfighter', 'Star Wars',
    1949, 2023, 309.99, 325.00, 'ACTIVE',
    'UCS Rebel Alliance T-65 X-Wing with adjustable S-foils, display stand, and Luke Skywalker pilot minifigure.'
),
(
    nextval('lego_set_seq'), '75367', 'Venator-Class Republic Attack Cruiser', 'Star Wars',
    5374, 2023, 849.99, 890.00, 'ACTIVE',
    'Clone Wars UCS flagship measuring over 109 cm long with command bridge, hangar bay, and Captain Rex minifigure.'
);
