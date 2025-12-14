USE boomerGame;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    display_name VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE rooms (
    id INT AUTO_INCREMENT PRIMARY KEY,
    room_code VARCHAR(36) NOT NULL UNIQUE,
    owner_id INT NOT NULL,                 -- CHỦ PHÒNG
    status ENUM('WAITING', 'PLAYING', 'FINISHED') DEFAULT 'WAITING',
    max_players INT DEFAULT 4,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (owner_id) 
        REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE TABLE room_players (
    room_id INT NOT NULL,
    user_id INT NOT NULL,
    role ENUM('HOST', 'PLAYER') DEFAULT 'PLAYER',
    joined_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    PRIMARY KEY (room_id, user_id),
    
    FOREIGN KEY (room_id) 
        REFERENCES rooms(id)
        ON DELETE CASCADE,
        
    FOREIGN KEY (user_id) 
        REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE TABLE matches (
    id INT AUTO_INCREMENT PRIMARY KEY,
    room_id INT NOT NULL,
    started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ended_at TIMESTAMP NULL,
    
    FOREIGN KEY (room_id)
        REFERENCES rooms(id)
        ON DELETE CASCADE
);

CREATE TABLE match_results (
    match_id INT NOT NULL,
    user_id INT NOT NULL,
    kills INT DEFAULT 0,
    is_winner BOOLEAN DEFAULT FALSE,
    
    PRIMARY KEY (match_id, user_id),
    
    FOREIGN KEY (match_id)
        REFERENCES matches(id)
        ON DELETE CASCADE,
        
    FOREIGN KEY (user_id)
        REFERENCES users(id)
        ON DELETE CASCADE
);

----------------------------------------------------
#simple data
INSERT INTO users (username, password, display_name)
VALUES 
('hien', '123456', 'Hiển'),
('player2', '123456', 'Player 2'),
('player3', '123456', 'Player 3');

select * from users;

INSERT INTO rooms (room_code, owner_id)
VALUES 
(uuid(), '2');

INSERT INTO room_players (room_id, user_id, role)
VALUES 
(1, 2, 'HOST');

select * from rooms;
select * from room_players;

delete from rooms where id between 30 AND 41;
delete from room_players where room_id between 30 AND 41;

------------------------------------------
#SQL lấy list room + số người, chỉ WAITING
SELECT 
    r.id,
    r.room_code,
    r.owner_id,
    u.username,
    r.status,
    r.max_players,
    r.created_at,
    COUNT(rp.user_id) AS player_count
FROM rooms r
LEFT JOIN room_players rp 
    ON r.id = rp.room_id
LEFT JOIN users u
    ON r.owner_id = u.id 
WHERE r.status = 'WAITING'
GROUP BY 
    r.id, r.room_code, r.owner_id, 
    r.status, r.max_players, r.created_at
ORDER BY r.created_at DESC;


------------------------------------------
#SQL lấy WAITING + chưa full (chuẩn cho nút JOIN)
SELECT 
    r.id,
    r.room_code,
    r.status,
    r.max_players,
    COUNT(rp.user_id) AS player_count
FROM rooms r
LEFT JOIN room_players rp 
    ON r.id = rp.room_id
WHERE r.status = 'WAITING'
GROUP BY r.id, r.room_code, r.status, r.max_players
HAVING COUNT(rp.user_id) < r.max_players;