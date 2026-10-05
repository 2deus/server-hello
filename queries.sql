-- Q1. Rooms for 6 or more people, largest first.
SELECT id, name, capacity
    FROM demo WHERE capacity >= 6
        ORDER BY capacity DESC;

-- Q2. Every reservation made by Mina.
SELECT *
    FROM reservation
    WHERE reserved_by = 'Mina';

-- Q3. Every reservation with the name of its room.
SELECT r.id, d.name AS room_name, r.reserved_by, r.start_time, r.end_time
    FROM reservation r
        JOIN demo d ON d.id = r.demo_id;

-- Q4. Reservations for Seminar A on 6 October 2026.
SELECT r.id, d.name AS room_name, r.reserved_by, r.start_time, r.end_time
    FROM reservation r
        JOIN demo d ON d.id = r.demo_id
    WHERE d.name = 'Seminar A'
        AND r.start_time >= '2026-10-06 00:00:00'
        AND r.start_time <  '2026-10-07 00:00:00';

-- Q5. Number of reservations per room (JOIN).
SELECT d.id, d.name, COUNT(r.id) AS reservation_count
    FROM demo d
        JOIN reservation r ON r.demo_id = d.id
    GROUP BY d.id, d.name;

-- Q6. Same as Q5, but show 0 for rooms with no reservations.
SELECT d.id, d.name, COUNT(r.id) AS reservation_count
    FROM demo d
        LEFT JOIN reservation r ON r.demo_id = d.id
    GROUP BY d.id, d.name;

-- Q7. Rooms that have never been reserved.
SELECT d.id, d.name, d.capacity
    FROM demo d
        LEFT JOIN reservation r ON r.demo_id = d.id
    WHERE r.id IS NULL;

-- Q8. Rooms with more than two reservations.
SELECT d.id, d.name, COUNT(r.id) AS reservation_count
    FROM demo d
        JOIN reservation r ON r.demo_id = d.id
    GROUP BY d.id, d.name
        HAVING COUNT(r.id) > 2;

-- Challenge. Which reservations in room 1 overlap 10:30-11:30 on 6 October 2026?
SELECT id, demo_id, reserved_by, start_time, end_time
    FROM reservation
    WHERE demo_id = 1
        AND start_time < '2026-10-06 11:30:00'
        AND end_time   > '2026-10-06 10:30:00';