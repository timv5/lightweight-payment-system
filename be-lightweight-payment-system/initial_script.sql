insert into products (product_id, product_name, product_price)
values (1, 'book', 50);
insert into products (product_id, product_name, product_price)
values (2, 'bike', 1000);
insert into products (product_id, product_name, product_price)
values (3, 'phone', 500);


insert into products_stock (product_stock_id, product_id, quantity)
values (1, 1, 10);
insert into products_stock (product_stock_id, product_id, quantity)
values (2, 2, 10);
insert into products_stock (product_stock_id, product_id, quantity)
values (3, 3, 10);

insert into users (user_id, user_email)
values (1, 'test@test.com');

insert into user_balances (user_balance_id, balance, user_id)
values (1, 5000, 1);