create table user (user_id int auto_increment primary key, name varchar(250));
create table airline (airline_id int auto_increment primary key, name varchar(250));
create table flight (id int auto_increment primary key, seat_id int , user_id int);
CREATE INDEX idx_user_id ON flight(user_id);
show create table flight;
CREATE INDEX user_id_idx ON flight (user_id);
CREATE UNIQUE INDEX seat_id_idx ON flight (seat_id);
