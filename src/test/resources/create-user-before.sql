delete from _user_authority;
delete from _user;
delete from _item;

insert into _user(id, first_name, last_name, email, password) values (1, 'User', 'Userov', 'user@mail.com', '1234');
insert into _user(id, first_name, last_name, email, password) values (2, 'Admin', 'Adminov', 'admin@mail.com', '2222');
insert into _user(id, first_name, last_name, email, password) values (3, 'Super', 'Adminov', 'super@mail.com', '5555');

insert into _user_authority(user_id, authorities) values (1, 'BROWS');
insert into _user_authority(user_id, authorities) values (2, 'BROWS');
insert into _user_authority(user_id, authorities) values (2, 'CREATE');
insert into _user_authority(user_id, authorities) values (3, 'BROWS');
insert into _user_authority(user_id, authorities) values (3, 'CREATE');
insert into _user_authority(user_id, authorities) values (3, 'UPDATE');
insert into _user_authority(user_id, authorities) values (3, 'DELETE');
insert into _user_authority(user_id, authorities) values (3, 'SUPER_ADMIN');

insert into _item(id, name, article) values (1, 'item1', 22222);
alter sequence _item_seq restart with 5;