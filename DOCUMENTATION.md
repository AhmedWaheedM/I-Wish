# I-Wish Application Documentation

## Overview

I-Wish is a Java-based wishlist management application with a client-server architecture. It allows users to create and manage wishlists, add items, manage friendships, and track contributions from friends towards wishlist items.

## Architecture

### Client-Server Model

The application uses a socket-based client-server architecture:

- **Client**: JavaFX-based GUI application that connects to the server
- **Server**: Multi-threaded server that handles multiple client connections simultaneously
- **Communication**: Serialized Request/Response objects over TCP sockets (port 5005)

### Client Side

**Main Entry Points:**
- [ClientApp.java](iwish/src/main/java/clientSide/ClientApp.java): JavaFX Application entry point
- [ClientConnection.java](iwish/src/main/java/clientSide/ClientConnection.java): Manages socket connection to server
- [IWishManager.java](iwish/src/main/java/clientSide/appManger/IWishManager.java): Application manager for scene switching and state management

**Controllers:**
- [LoginController.java](iwish/src/main/java/clientSide/controllers/LoginController.java): Handles user login logic
- [DashboardController.java](iwish/src/main/java/clientSide/controllers/DashboardController.java): Manages main dashboard UI
- [IFiledsChecker.java](iwish/src/main/java/clientSide/controllers/IFiledsChecker.java): Interface for input validation

### Server Side

**Main Entry Points:**
- [ServerApp.java](iwish/src/main/java/serverSide/ServerApp.java): Server application entry point
- [Server.java](iwish/src/main/java/serverSide/Server.java): Listens for client connections on port 5005
- [ClientHandler.java](iwish/src/main/java/serverSide/ClientHandler.java): Handles individual client connections

**Database Layer:**
- [DBHandler.java](iwish/src/main/java/serverSide/dbLayer/DBHandler.java): Core database connection management
- [UsersHandler.java](iwish/src/main/java/serverSide/dbLayer/UsersHandler.java): User-related database operations
- [WishListHandler.java](iwish/src/main/java/serverSide/dbLayer/WishListHandler.java): Wishlist operations
- [WishListItemHandler.java](iwish/src/main/java/serverSide/dbLayer/WishListItemHandler.java): Wishlist item operations
- [FriendsHandler.java](iwish/src/main/java/serverSide/dbLayer/FriendsHandler.java): Friend relationship management
- [ContributionHandler.java](iwish/src/main/java/serverSide/dbLayer/ContributionHandler.java): Contribution tracking
- [ItemHandler.java](iwish/src/main/java/serverSide/dbLayer/ItemHandler.java): Item catalog management

## Data Models

The application uses the following domain models (in [models/](iwish/src/main/java/models/)):

- **User**: Represents a user account with balance
- **WishList**: Represents a user's wishlist
- **WishListItem**: Represents an item in a wishlist
- **Item**: Represents an item in the catalog
- **Friend**: Represents a friendship relationship between users
- **Contribution**: Represents a contribution towards a wishlist item

## Request-Response System

The application uses a request-response pattern for client-server communication.

**Request DTOs** (in [dtos/requestDtos/](iwish/src/main/java/dtos/requestDtos/)):

- **User Handler**: LoginRequest, HasEnoughBalanceRequest, UpdateBalanceRequest
- **Friends Handler**: AddFriendRequest, GetFriendsRequest, GetPendingFriendsRequest, RejectFriendRequest
- **Item Handler**: AddItemRequest, DeleteItemRequest, GetItemPriceRequest
- **Contribution Handler**: AddContributionRequest, RemoveContributionRequest
- **WishList Handler**: DeleteWishListRequest, GetFriendsWishListsRequest, GetWishListByUserIdRequest
- **WishList Item Handler**: Various item management requests

**RequestRouter** handles incoming requests on the server side and routes them to appropriate handlers.

## Database Schema

The database `iwish` consists of the following tables:

### 1. User
Stores user account information.
- `user_id`: Primary Key
- `username`: Unique username
- `password`: User password (hashed recommended)
- `balance`: User's current balance

### 2. WishList
Stores the wishlist for each user.
- `wishlist_id`: Primary Key
- `user_id`: Foreign Key referencing User

### 3. Item
Catalog of available items.
- `item_id`: Primary Key
- `name`: Item name
- `price`: Item price
- `description`: Item description

### 4. WishList_Item (Junction Table)
Links items to specific wishlists.
- `rec_id`: Primary Key
- `wishlist_id`: Foreign Key referencing WishList
- `item_id`: Foreign Key referencing Item

### 5. Friends
Manages friendships between users.
- `user1_id`: Foreign Key referencing User
- `user2_id`: Foreign Key referencing User
- `status`: Friendship status ('accepted', 'pending', 'rejected')
- Primary Key: (user1_id, user2_id)

### 6. Contribution
Tracks contributions towards a wishlist item.
- `contribution_id`: Primary Key
- `wishlist_item_id`: Foreign Key referencing WishList_Item
- `contributor_id`: Foreign Key referencing User
- `amount`: Contribution amount

## User Interface

**FXML Views** (in [resources/views/](iwish/src/main/resources/views/)):

- **Login View** ([login.fxml](iwish/src/main/resources/views/login/login.fxml)): User authentication screen
- **Dashboard View** ([dashboard.fxml](iwish/src/main/resources/views/dashboard/dashboard.fxml)): Main application interface

**Styling:**
- [login.css](iwish/src/main/resources/views/login/login.css): Login page styling
- [dashboard.css](iwish/src/main/resources/views/dashboard/dashboard.css): Dashboard styling

## Running the Application

### Prerequisites
- Java Development Kit (JDK) 11 or higher
- Maven 3.8.0 or higher
- MySQL Server

### Build
```bash
cd iwish
mvn clean install
```

### Run Client
```bash
mvn javafx:run -pl . -Djavafx.mainClass=clientSide.ClientApp
```

### Run Server
```bash
mvn exec:java -Dexec.mainClass="serverSide.ServerApp"
```

## Troubleshooting

### "Duplicate foreign key constraint name"
If you encounter this error when running the database script:
**Solution:** The script includes `DROP TABLE IF EXISTS` statements. Ensure the script is run completely.

### "Failed to open referenced table"
This occurs if tables are not dropped properly or there's a case sensitivity mismatch.
**Solution:** Check that table names use consistent casing (WishList_Item, User, Item, etc.).

### Connection refused on client startup
**Solution:** Ensure the server is running on port 5005 before starting the client application.

## important
don't forget to create db.properties in resources