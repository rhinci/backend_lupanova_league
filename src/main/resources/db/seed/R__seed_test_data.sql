-- 500 команд в пяти городах
INSERT INTO teams (name, city, max_players)
SELECT 'Команда ' || n,
       (ARRAY['Москва', 'Казань', 'Самара', 'Хабаровск', 'Сочи'])[1 + n % 5],
       10
FROM generate_series(1, 500) AS n;

-- по 10 игроков в каждой из них
INSERT INTO players (team_id, full_name, number, birth_date, horse_name)
SELECT t.id,
       'Игрок ' || t.id || '-' || p,
       p,
       DATE '2000-01-01' + ((t.id * 37 + p * 101) % 3000)::int,
       'Лошадь ' || p
FROM teams t
CROSS JOIN generate_series(1, 10) AS p
WHERE t.name LIKE 'Команда %';