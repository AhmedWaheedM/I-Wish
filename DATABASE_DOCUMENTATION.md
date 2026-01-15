# Database Documentation

This document references the `DataBaseInitializationScript` used for the I-Wish application.

## Schema Overview

The database `iwish` consists of the following tables:

### 1. User
Stores user account information.
- `user_id`: Primary Key
- `username`: Unique username
- `password`: User password
- `balance`: User's current balance

### 2. Wishlist
Stores the wishlist for a user.
- `wishlist_id`: Primary Key
- `user_id`: Foreign Key referencing `User(user_id)`

### 3. Item
Catalog of available items.
- `item_id`: Primary Key
- `name`: Item name
- `price`: Item price
- `description`: Item description

### 4. Wishlist_Item (Junction Table)
Links items to specific wishlists.
- `rec_id`: Primary Key
- `wishlist_id`: Foreign Key referencing `Wishlist(wishlist_id)`
- `item_id`: Foreign Key referencing `Item(item_id)`

### 5. Friends
Manages friendships between users.
- `user1_id`: Foreign Key referencing `User(user_id)`
- `user2_id`: Foreign Key referencing `User(user_id)`
- `status`: Friendship status (e.g., 'accepted', 'pending')

### 6. Contribution
Tracks contributions towards a wishlist item.
- `wishlist_item_id`: Foreign Key referencing `Wishlist_Item(rec_id)`
- `contributor_id`: Foreign Key referencing `User(user_id)`
- `amount`: ID of the user contributing

## Troubleshooting

### "Duplicate foreign key constraint name"
If you encounter this error, it is likely due to lingering constraints from previous table versions (e.g., `WishListItem` without the underscore).
**Solution:** The script has been updated to explicitly `DROP TABLE IF EXISTS WishListItem` and `Wishlist_Item` to clear old references. Run the updated script.

### "Failed to open referenced table"
This usually occurs if existing tables are not dropped or if there is a case sensitivity mismatch in table names.
**Solution:** The script now uses consistent casing (`Wishlist_Item`, `User`, `Item`) for all tables and foreign key references.
