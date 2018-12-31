create table users(
	id int(11) primary key auto_increment,
	username varchar(50) unique,
	email varchar(200) unique,
	password varchar(64) not null
);