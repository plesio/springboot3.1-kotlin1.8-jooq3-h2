CREATE TABLE author
(
    author_id      VARCHAR(36)  NOT NULL PRIMARY KEY,
    first_name     VARCHAR(50)  ,
    last_name      VARCHAR(50)  NOT NULL,
    birth_year     INTEGER      ,
    remarks        text
);

CREATE TABLE book
(
    book_id        VARCHAR(36)  NOT NULL PRIMARY KEY,
    title          text         NOT NULL,
    isbn_code      VARCHAR(20)  NOT NULL,
    published_date DATE         NOT NULL,
    remarks        text
);

CREATE TABLE author_book
(
    author_id      VARCHAR(36) NOT NULL,
    book_id        VARCHAR(36) NOT NULL,
    remarks        text         ,

    PRIMARY KEY (author_id, book_id),
    CONSTRAINT fk_ab_author FOREIGN KEY (author_id) REFERENCES author (author_id)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT fk_ab_book FOREIGN KEY (book_id) REFERENCES book (book_id)
);

-- MEMO: UUID がうまくいかなかったら VARCHAR(36)  にする