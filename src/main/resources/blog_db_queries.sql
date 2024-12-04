create table user(user_id int primary key, user_name varchar(255), user_country varchar (100));
create table blog (blog_id int primary key, author_id int not null, title varchar(255) not null, content text, created_at timestamp default current_timestamp);
insert into blog (blog_id, author_id, title, content) values (1, 1, 'first blog', 'first blog content');
insert into user (user_id, user_name, user_country) values (1, 'yukig', 'IND');