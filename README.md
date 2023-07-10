# simple-chat
under construction...

# SQL
```sql
CREATE DATABASE simplechatdb;
\c simplechatdb;

-- This table holds info of users of chat app
CREATE TABLE users (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    mail_address VARCHAR(100) NOT NULL,
    password VARCHAR(100) NOT NULL,
    icon BYTEA NOT NULL
);

-- This table holds info sent by users
CREATE TABLE messages (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sender_id INTEGER NOT NULL,
    receiver_id INTEGER NOT NULL,
    sent_datetime TIMESTAMP NOT NULL,
    read_datetime TIMESTAMP,
    message VARCHAR(1000) NOT NULL,
    FOREIGN KEY (sender_id) REFERENCES users (id),
    FOREIGN KEY (receiver_id) REFERENCES users (id)
);
```
