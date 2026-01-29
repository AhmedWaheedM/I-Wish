# I-Wish Project

I-Wish is a Wishlist application built with JavaFX and Maven. It allows users to create wishlists, add items, and manage contributions from friends.

## Project Structure

- `iwish/`: Source code directory (Maven project).
- `DataBaseInitializationScript`: SQL script to initialize the MySQL database.

## Prerequisites

- Java Development Kit (JDK) 11 or higher.
- Maven 3.8.0 or higher.
- MySQL Server.

## Database Setup

Before running the application, you must initialize the database using the provided script.

1.  Open your MySQL client (Workbench, Command Line, etc.).
2.  Run the `DataBaseInitializationScript` script.
    - This will create the `iwish` database.
    - It will create necessary tables: `User`, `Wishlist`, `Item`, `Wishlist_Item`, `Friends`, `Contribution`.
    - It inserts sample data for testing.

**Note:** The script includes `DROP TABLE IF EXISTS` statements to ensure a clean setup if correcting errors.

## Building and Running

1.  Navigate to the `iwish` directory:
    ```bash
    cd iwish
    ```

2.  Build the project:
    ```bash
    mvn clean install
    ```

3.  Run the application using the JavaFX Maven plugin:
    ```bash
    mvn javafx:run
    ```

## Recent Updates

- **Database Fixes**: Resolved foreign key constraint naming conflicts in the `Wishlist_Item` and `Contribution` tables.
- **Table Naming**: Standardized table naming to `Wishlist_Item` (CamelCase with underscore) for consistency.
- **Frontend Overhaul**: Implemented a modern **Dashboard UI** (see `dashboard.fxml`).
    - Database initialization is currently disabled in `IWishManager.java`.

- **Notification System v2.0**:
    - **Soft Delete Architecture**: Notifications are now preserved in the database (`cleared` flag) instead of permanent deletion, enabling history tracking while keeping the UI clean.
    - **Smart Synchronization**: The Right Sidebar now intelligently syncs with the main Notifications tab. Dismissing a notification (with the new 'X' button) marks it as read and clears it from the database simultaneously.
    - **Interactive UI**: Added fade-out animations for dismissals, a "Clear All" button with confirmation dialog, and precise relative timestamps (e.g., "5 min ago", "Yesterday").
    - **Visual Consistency**: Unified color coding and iconography for notification types (e.g., Red for "Friend Removed", Purple for "Friend Accepted") across all views.
