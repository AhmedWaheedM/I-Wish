SET SQL_SAFE_UPDATES = 0;
UPDATE iwish.wishlist c
SET c.current_amount = 0 , c.total_items_amount = 0;
SET SQL_SAFE_UPDATES = 1;