-- V2__seed_demo_data.sql
-- 1) demo users: seller & bidder
INSERT INTO users (email, password_hash, first_name, last_name, status, created_at, updated_at)
VALUES
  (
    'seller@bidcars.com',
    -- replace with your own BCrypt‑hash of "seller123":
    '$2a$10$CPfYVsBq1Rf4y0.hx7nxAOIHYtt8xNy10OJTs7OwXg3vdL0ERm1AO',
    'Sam',
    'Seller',
    'ACTIVE',
    now(),
    now()
  ),
  (
    'bidder@bidcars.com',
    -- replace with your own BCrypt‑hash of "bidder123":
    '$2a$10$5oSpm9DfErYcpyikEnEB1OK3wgSiPHhLgXEs75PKCmZNCjJUZTRQq',
    'Becky',
    'Bidder',
    'ACTIVE',
    now(),
    now()
  )
ON CONFLICT (email) DO NOTHING;

-- 2) demo cars (we’ll look up the seller’s id by email)
INSERT INTO cars (make, model, year, description, owner_id)
SELECT
  'Porsche', '911 GT3 RS', 2019, 'Track‑ready, 520hp', u.id
FROM users u
WHERE u.email = 'seller@bidcars.com'
UNION ALL
SELECT
  'Ford', 'Mustang Fastback', 1967, 'Classic muscle', u.id
FROM users u
WHERE u.email = 'seller@bidcars.com'
ON CONFLICT DO NOTHING;

-- 3) auctions (open + closed), referencing cars by a sub‑select
INSERT INTO auctions (car_id, start_time, end_time, starting_price, is_closed, winner_id)
VALUES
  (
    -- open auction on the Porsche
    (SELECT c.id FROM cars c WHERE c.make='Porsche' AND c.model='911 GT3 RS' LIMIT 1),
    now() - interval '1 hour',
    now() + interval '2 day',
    180000.0,
    false,
    NULL
  ),
  (
    -- closed auction on the Mustang, winner = our bidder
    (SELECT c.id FROM cars c WHERE c.make='Ford' AND c.model='Mustang Fastback' LIMIT 1),
    now() - interval '5 day',
    now() - interval '1 day',
    70000.0,
    true,
    (SELECT u.id FROM users u WHERE u.email='bidder@bidcars.com' LIMIT 1)
  )
ON CONFLICT DO NOTHING;

-- 4) bids (new ID, auto‑assigned)
INSERT INTO bids (auction_id, bidder_id, amount, timestamp)
VALUES
  (
    (SELECT a.id FROM auctions a WHERE a.car_id = (SELECT c.id FROM cars c WHERE c.make='Porsche' AND c.model='911 GT3 RS') LIMIT 1),
    (SELECT u.id FROM users u WHERE u.email='bidder@bidcars.com' LIMIT 1),
    185000.0,
    now() - interval '30 minute'
  ),
  (
    (SELECT a.id FROM auctions a WHERE a.car_id = (SELECT c.id FROM cars c WHERE c.make='Ford' AND c.model='Mustang Fastback') LIMIT 1),
    (SELECT u.id FROM users u WHERE u.email='bidder@bidcars.com' LIMIT 1),
    75000.0,
    now() - interval '4 day'
  )
ON CONFLICT DO NOTHING;
