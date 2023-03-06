CREATE TABLE users (
                       id SERIAL PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255) NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(20) NOT NULL
);

CREATE TABLE courier_info (
                              id SERIAL PRIMARY KEY,
                              user_id INTEGER NOT NULL REFERENCES users(id),
                              latitude DECIMAL(9,6) NOT NULL,
                              longitude DECIMAL(9,6) NOT NULL,
                              available BOOLEAN NOT NULL
);

CREATE TABLE courier_location (
                                  id SERIAL PRIMARY KEY,
                                  courier_id INTEGER NOT NULL REFERENCES courier_info(id),
                                  latitude DECIMAL(9,6) NOT NULL,
                                  longitude DECIMAL(9,6) NOT NULL,
                                  timestamp TIMESTAMP NOT NULL,
);

CREATE TABLE parcels (
                         id SERIAL PRIMARY KEY,
                         sender_id INTEGER NOT NULL REFERENCES users(id),
                         receiver_name VARCHAR(255) NOT NULL,
                         receiver_phone VARCHAR(20) NOT NULL,
                         receiver_address VARCHAR(255) NOT NULL,
                         weight DECIMAL(5,2) NOT NULL,
                         status VARCHAR(20) NOT NULL
);

CREATE TABLE deliveries (
                            id SERIAL PRIMARY KEY,
                            parcel_id INTEGER NOT NULL REFERENCES parcels(id),
                            courier_id INTEGER REFERENCES courier_info(id),
                            pickup_location POINT NOT NULL,
                            delivery_location POINT NOT NULL,
                            pickup_time TIMESTAMP NOT NULL,
                            delivery_time TIMESTAMP,
                            status VARCHAR(20) NOT NULL
);
