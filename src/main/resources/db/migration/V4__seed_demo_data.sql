-- ===========================
-- V3__seed_demo_data.sql
-- Demo data for users, cars, auctions, and bids
-- ===========================

-- 1) USERS (10 total: 3 sellers, 6 buyers, 1 admin)
INSERT INTO users (email, password_hash, first_name, last_name, status, created_at, updated_at)
VALUES
  -- Sellers
  ('seller1@cars.com', '$2a$10$CPfYVsBq1Rf4y0.hx7nxAOIHYtt8xNy10OJTs7OwXg3vdL0ERm1AO', 'Sam', 'Seller', 'ACTIVE', now(), now()),
  ('seller2@cars.com', '$2a$10$CPfYVsBq1Rf4y0.hx7nxAOIHYtt8xNy10OJTs7OwXg3vdL0ERm1AO', 'Jane', 'Dealer', 'ACTIVE', now(), now()),
  ('seller3@cars.com', '$2a$10$CPfYVsBq1Rf4y0.hx7nxAOIHYtt8xNy10OJTs7OwXg3vdL0ERm1AO', 'Mike', 'Garage', 'ACTIVE', now(), now()),

  -- Buyers
  ('buyer1@cars.com', '$2a$10$5oSpm9DfErYcpyikEnEB1OK3wgSiPHhLgXEs75PKCmZNCjJUZTRQq', 'Becky', 'Bidder', 'ACTIVE', now(), now()),
  ('buyer2@cars.com', '$2a$10$5oSpm9DfErYcpyikEnEB1OK3wgSiPHhLgXEs75PKCmZNCjJUZTRQq', 'Chris', 'Hunter', 'ACTIVE', now(), now()),
  ('buyer3@cars.com', '$2a$10$5oSpm9DfErYcpyikEnEB1OK3wgSiPHhLgXEs75PKCmZNCjJUZTRQq', 'Alex', 'Rider', 'ACTIVE', now(), now()),
  ('buyer4@cars.com', '$2a$10$5oSpm9DfErYcpyikEnEB1OK3wgSiPHhLgXEs75PKCmZNCjJUZTRQq', 'Sophia', 'Driver', 'ACTIVE', now(), now()),
  ('buyer5@cars.com', '$2a$10$5oSpm9DfErYcpyikEnEB1OK3wgSiPHhLgXEs75PKCmZNCjJUZTRQq', 'David', 'Collector', 'ACTIVE', now(), now()),
  ('buyer6@cars.com', '$2a$10$5oSpm9DfErYcpyikEnEB1OK3wgSiPHhLgXEs75PKCmZNCjJUZTRQq', 'Ella', 'Chaser', 'ACTIVE', now(), now()),

  -- Admin
  ('admin@cars.com', '$2a$10$09V1TuD4hQtf3/NwvPLUqOnvLcmE/EKmpjJzZLZ6Eo8fX5UgFqD1C', 'Adam', 'Admin', 'ACTIVE', now(), now())
ON CONFLICT (email) DO NOTHING;

-- 2) CARS (10 total, assigned to sellers)
INSERT INTO cars (make, model, year, description, owner_id)
SELECT 'Porsche', '911 GT3 RS', 2019, 'Track-ready, 520hp', u.id FROM users u WHERE u.email='seller1@cars.com'
UNION ALL
SELECT 'Ford', 'Mustang Fastback', 1967, 'Classic muscle', u.id FROM users u WHERE u.email='seller1@cars.com'
UNION ALL
SELECT 'Tesla', 'Model S Plaid', 2022, 'Electric beast', u.id FROM users u WHERE u.email='seller2@cars.com'
UNION ALL
SELECT 'BMW', 'M3 Competition', 2021, 'Luxury sports sedan', u.id FROM users u WHERE u.email='seller2@cars.com'
UNION ALL
SELECT 'Toyota', 'Supra', 2020, 'Japanese legend', u.id FROM users u WHERE u.email='seller2@cars.com'
UNION ALL
SELECT 'Lamborghini', 'Huracan Evo', 2023, 'V10 supercar', u.id FROM users u WHERE u.email='seller3@cars.com'
UNION ALL
SELECT 'Ferrari', '488 GTB', 2018, 'Italian masterpiece', u.id FROM users u WHERE u.email='seller3@cars.com'
UNION ALL
SELECT 'Mercedes', 'AMG GT', 2019, 'Track-focused AMG', u.id FROM users u WHERE u.email='seller3@cars.com'
UNION ALL
SELECT 'Audi', 'R8 V10', 2020, 'Quattro performance', u.id FROM users u WHERE u.email='seller3@cars.com'
UNION ALL
SELECT 'Nissan', 'GTR R35', 2017, 'Godzilla', u.id FROM users u WHERE u.email='seller1@cars.com'
ON CONFLICT DO NOTHING;

-- 3) AUCTIONS (10 total, half open, half closed)
INSERT INTO auctions (car_id, start_time, end_time, starting_price, is_closed, winner_id)
VALUES
  -- Open auctions
  ((SELECT id FROM cars WHERE make='Porsche' AND model='911 GT3 RS'), now() - interval '1 hour', now() + interval '2 day', 180000.0, false, NULL),
  ((SELECT id FROM cars WHERE make='Tesla' AND model='Model S Plaid'), now() - interval '2 hour', now() + interval '3 day', 120000.0, false, NULL),
  ((SELECT id FROM cars WHERE make='Toyota' AND model='Supra'), now() - interval '1 day', now() + interval '4 day', 60000.0, false, NULL),
  ((SELECT id FROM cars WHERE make='Lamborghini' AND model='Huracan Evo'), now() - interval '3 hour', now() + interval '5 day', 250000.0, false, NULL),
  ((SELECT id FROM cars WHERE make='Audi' AND model='R8 V10'), now() - interval '6 hour', now() + interval '2 day', 200000.0, false, NULL),

  -- Closed auctions
  ((SELECT id FROM cars WHERE make='Ford' AND model='Mustang Fastback'), now() - interval '5 day', now() - interval '1 day', 70000.0, true, (SELECT id FROM users WHERE email='buyer1@cars.com')),
  ((SELECT id FROM cars WHERE make='BMW' AND model='M3 Competition'), now() - interval '6 day', now() - interval '2 day', 90000.0, true, (SELECT id FROM users WHERE email='buyer2@cars.com')),
  ((SELECT id FROM cars WHERE make='Ferrari' AND model='488 GTB'), now() - interval '4 day', now() - interval '1 day', 220000.0, true, (SELECT id FROM users WHERE email='buyer3@cars.com')),
  ((SELECT id FROM cars WHERE make='Mercedes' AND model='AMG GT'), now() - interval '7 day', now() - interval '3 day', 150000.0, true, (SELECT id FROM users WHERE email='buyer4@cars.com')),
  ((SELECT id FROM cars WHERE make='Nissan' AND model='GTR R35'), now() - interval '3 day', now() - interval '1 day', 100000.0, true, (SELECT id FROM users WHERE email='buyer5@cars.com'))
ON CONFLICT DO NOTHING;

-- 4) BIDS (~15 total, spread across buyers)
INSERT INTO bids (auction_id, bidder_id, amount, timestamp)
VALUES
  -- Porsche open auction
  ((SELECT id FROM auctions WHERE car_id=(SELECT id FROM cars WHERE make='Porsche' AND model='911 GT3 RS')), (SELECT id FROM users WHERE email='buyer1@cars.com'), 185000.0, now() - interval '30 minute'),
  ((SELECT id FROM auctions WHERE car_id=(SELECT id FROM cars WHERE make='Porsche' AND model='911 GT3 RS')), (SELECT id FROM users WHERE email='buyer2@cars.com'), 190000.0, now() - interval '20 minute'),

  -- Tesla open auction
  ((SELECT id FROM auctions WHERE car_id=(SELECT id FROM cars WHERE make='Tesla' AND model='Model S Plaid')), (SELECT id FROM users WHERE email='buyer3@cars.com'), 125000.0, now() - interval '1 hour'),

  -- Toyota open auction
  ((SELECT id FROM auctions WHERE car_id=(SELECT id FROM cars WHERE make='Toyota' AND model='Supra')), (SELECT id FROM users WHERE email='buyer4@cars.com'), 65000.0, now() - interval '2 hour'),

  -- Lambo open auction
  ((SELECT id FROM auctions WHERE car_id=(SELECT id FROM cars WHERE make='Lamborghini' AND model='Huracan Evo')), (SELECT id FROM users WHERE email='buyer5@cars.com'), 260000.0, now() - interval '3 hour'),
  ((SELECT id FROM auctions WHERE car_id=(SELECT id FROM cars WHERE make='Lamborghini' AND model='Huracan Evo')), (SELECT id FROM users WHERE email='buyer6@cars.com'), 265000.0, now() - interval '2 hour'),

  -- Closed auctions (already winners)
  ((SELECT id FROM auctions WHERE car_id=(SELECT id FROM cars WHERE make='Ford' AND model='Mustang Fastback')), (SELECT id FROM users WHERE email='buyer1@cars.com'), 75000.0, now() - interval '4 day'),
  ((SELECT id FROM auctions WHERE car_id=(SELECT id FROM cars WHERE make='BMW' AND model='M3 Competition')), (SELECT id FROM users WHERE email='buyer2@cars.com'), 95000.0, now() - interval '5 day'),
  ((SELECT id FROM auctions WHERE car_id=(SELECT id FROM cars WHERE make='Ferrari' AND model='488 GTB')), (SELECT id FROM users WHERE email='buyer3@cars.com'), 230000.0, now() - interval '3 day'),
  ((SELECT id FROM auctions WHERE car_id=(SELECT id FROM cars WHERE make='Mercedes' AND model='AMG GT')), (SELECT id FROM users WHERE email='buyer4@cars.com'), 160000.0, now() - interval '4 day'),
  ((SELECT id FROM auctions WHERE car_id=(SELECT id FROM cars WHERE make='Nissan' AND model='GTR R35')), (SELECT id FROM users WHERE email='buyer5@cars.com'), 105000.0, now() - interval '2 day')
ON CONFLICT DO NOTHING;
