INSERT INTO cars (make, model, year, description, owner_id)
SELECT
  'Lambourghini', 'Urus', 2023, 'Track‑ready, 520hp', u.id
FROM users u
WHERE u.email = 'admin@examle.com'
UNION ALL
SELECT
  'Mercedes', 'AMG', 2024, 'Classic muscle', u.id
FROM users u
WHERE u.email = 'admin@example.com'
ON CONFLICT DO NOTHING;