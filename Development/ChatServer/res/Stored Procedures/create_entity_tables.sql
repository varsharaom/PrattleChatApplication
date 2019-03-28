CREATE DEFINER=`root`@`%` PROCEDURE `new_procedure`()
BEGIN

create table users (
	id INTEGER PRIMARY KEY NOT NULL auto_increment,
	name VARCHAR(50) NOT NULL,
    password VARCHAR(50),
    nickname VARCHAR(50),
    last_seen DATETIME,
    invisible INTEGER DEFAULT 0
);

create table circles (
	id INTEGER PRIMARY KEY NOT NULL auto_increment,
    user_one INTEGER REFERENCES users(id),
    user_two INTEGER REFERENCES users(id)
);

create table groups (
	id INTEGER PRIMARY KEY NOT NULL auto_increment,
    name VARCHAR(40),
    isPrivate INTEGER DEFAULT 0
);

create table group_info (
	id INTEGER PRIMARY KEY auto_increment,
	group_id INTEGER REFERENCES groups(id),
    uid INTEGER REFERENCES users(id),
    role INTEGER
);

create table message (
	id INTEGER PRIMARY KEY NOT NULL auto_increment,
    sender_id INTEGER REFERENCES user(id),
    receiver_id INTEGER REFERENCES user(id),
    type VARCHAR(10),
    body VARCHAR(500),
    time_sent DATETIME,
    isDeleted INTEGER DEFAULT 0
);

END