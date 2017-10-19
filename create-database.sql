-- Schema: public

-- DROP SCHEMA public;

CREATE SCHEMA public
  AUTHORIZATION hackpucdet;

CREATE TABLE event (
	id SERIAL PRIMARY KEY,    
	name varchar(255)
);

CREATE TABLE client (
	id SERIAL PRIMARY KEY,    
	name varchar(255)
);

CREATE TABLE event_client (
	id SERIAL PRIMARY KEY,
	id_client integer not null,
	id_event integer not null,		
	created_at date,
	foreign key (id_client) references client (id),
	foreign key (id_event) references event (id)	
);

CREATE TABLE payment (
	id SERIAL PRIMARY KEY,
	id_event_client integer not null,
	value decimal,
	created_at date,
	foreign key (id_event_client) references event_client (id)
);


CREATE TABLE checkout (
	id SERIAL PRIMARY KEY,
	id_event_client integer not null,
	value decimal,
	created_at date,
	foreign key (id_event_client) references event_client (id)
);

ALTER TABLE payment ALTER COLUMN created_at TYPE TIMESTAMP WITH TIME ZONE USING created_at AT TIME ZONE 'BRT'
ALTER TABLE checkout ALTER COLUMN created_at TYPE TIMESTAMP WITH TIME ZONE USING created_at AT TIME ZONE 'BRT'
ALTER TABLE event_client ALTER COLUMN created_at TYPE TIMESTAMP WITH TIME ZONE USING created_at AT TIME ZONE 'BRT'

GRANT ALL ON SCHEMA public TO hackpucdet;
GRANT ALL ON SCHEMA public TO public;
COMMENT ON SCHEMA public
  IS 'standard public schema';
