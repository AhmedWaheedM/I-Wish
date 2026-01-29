# I-Wish: Collaborative Wishlist Application

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-0078D4?style=for-the-badge&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/mysql-%234479A5.svg?style=for-the-badge&logo=mysql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)

**I-Wish** is a full-featured, **real-time collaborative wishlist platform** designed to make gift-giving and group contributions fun, organized, and transparent. Users can create personal wishlists, add items with details (name, price, link, description), share them with friends, and allow others to contribute toward gifts—while seeing live updates and notifications when contributions happen.

Built as a **desktop client-server application**, it uses **JavaFX** for a modern, responsive UI, **raw sockets** for real-time communication, and **MySQL** for persistent storage. It's a complete end-to-end project demonstrating client-server architecture, database integration, multi-user interactions, and real-time features.

### Key Features
- **User Authentication & Profiles** — Secure registration, login, and friend management.
- **Personal & Shared Wishlists** — Create wishlists that auto-generate on signup; add/edit items with rich details.
- **Real-Time Contributions** — Friends can contribute money toward items; contributions are tracked and reflected instantly.
- **Advanced Notification System v2.0** — Soft-delete architecture (notifications preserved for history), smart sidebar sync, fade-out animations, "Clear All" with confirmation, relative timestamps ("5 min ago"), and color-coded types (e.g., purple for friend accepted).
- **Dashboard UI** — Modern, intuitive interface with smooth scene transitions.
- **Balance & Transaction Tracking** — Users maintain a balance for contributions.

### Tech Stack
- **Frontend** — JavaFX (UI), CSS (styling), Maven (build & dependency management)
- **Backend** — Java (server logic), Raw TCP Sockets (real-time client-server communication)
- **Database** — MySQL (JDBC for data access), with comprehensive initialization script
- **Architecture** — Client-server model with DTO-based request/response protocol, multi-threaded server, soft-delete notifications

### Why I Built This
This project was created as part of my learning journey in full-stack Java development, focusing on:
- Client-server communication without frameworks (raw sockets → understanding low-level networking)
- Database design & operations (transactions, constraints, sample data)
- Real-time features (threaded listeners, async notifications on JavaFX thread)
- UI/UX best practices (animations, consistent theming, responsive design)

It's a proof-of-concept that could evolve into a web/mobile app (e.g., with Spring Boot + React).

## Project Structure
- `iwish/` — Source code directory (Maven project root).
- `DataBaseInitializationScript` — SQL script to initialize the MySQL database (creates tables, constraints, and inserts sample data).

## Prerequisites
- Java Development Kit (JDK) 11 or higher.
- Maven 3.8.0 or higher.
- MySQL Server.

## Database Setup
Before running the application, you must initialize the database using the provided script.

1. Open your MySQL client (Workbench, Command Line, etc.).
2. Run the `DataBaseInitializationScript` script.
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
