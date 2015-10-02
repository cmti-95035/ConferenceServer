drop user 'conference'@'localhost';
create user 'conference'@'localhost' identified by 'some_pass';
grant all privileges on *.* to 'conference'@'localhost' with grant option;
set password for 'conference'@'localhost' = password('conference');
create database conference_schema;
