CREATE TABLE IF NOT EXISTS tbl_category (
    category_id SERIAL PRIMARY KEY,
    category_name VARCHAR(255) NOT NULL UNIQUE,

    category_description TEXT,

    created_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP,
    updated_by VARCHAR(255),
    is_unavailable BOOLEAN
);

CREATE TABLE IF NOT EXISTS tbl_product (
    product_id SERIAL PRIMARY KEY,
    category_id BIGINT,

    product_image TEXT,
    product_name VARCHAR(255) NOT NULL UNIQUE,
    product_description TEXT,

    product_quantity INTEGER,
    product_price DOUBLE PRECISION,
    minimum_thresh_hold_count INTEGER,
    product_special_price DOUBLE PRECISION,

    created_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP,
    updated_by VARCHAR(255),
    is_unavailable BOOLEAN
);
