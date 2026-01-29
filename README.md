# I-Wish: Collaborative Wishlist Application

![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-0078D4?style=for-the-badge&logo=java&logoColor=white)
![MySQL](https://img.shields.io/badge/mysql-%234479A5.svg?style=for-the-badge&logo=mysql&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)

**I-Wish** is a full-featured, **real-time collaborative wishlist platform** designed to make gift-giving and group contributions fun, organized, and transparent. Users can create personal wishlists, add items with details (name, price, link, description), share them with friends, and allow others to contribute toward gifts—while seeing live updates and notifications when contributions happen.

---

## ✨ Key Features

- **User Authentication & Profiles** — Secure registration, login, and friend management.
- **Personal & Shared Wishlists** — Create wishlists that auto-generate on signup; add/edit items with rich details.
- **Real-Time Contributions** — Friends can contribute money toward items; contributions are tracked and reflected instantly.
- **Advanced Notification System v2.0** — Soft-delete architecture (notifications preserved for history), smart sidebar sync, fade-out animations, "Clear All" with confirmation, relative timestamps ("5 min ago"), and color-coded types.
- **Dashboard UI** — Modern, intuitive interface with smooth scene transitions.
- **Balance & Transaction Tracking** — Users maintain a balance for contributions.

---

## 🏗️ Architecture

This project is built as a **desktop client-server application**. It demonstrates a clean separation of concerns between layers and modules.

### Back-End: N-Tier (3-Tier) Architecture

The server is structured into three distinct, decoupled layers:

```
┌───────────────────────────────────────────────────────────────┐
│                      1. Request Layer                         │
│  (Server.java, ClientHandler.java - like a web server/nginx)  │
│  Accepts TCP connections on port 5005, handles raw I/O.       │
└──────────────────────────────┬────────────────────────────────┘
                               │
                               ▼
┌───────────────────────────────────────────────────────────────┐
│                        2. API Layer                           │
│ (RequestRouter.java, *Apis.java - e.g., UserApis, FriendsApis)│
│  Routes requests and contains all business logic.             │
└──────────────────────────────┬────────────────────────────────┘
                               │
                               ▼
┌───────────────────────────────────────────────────────────────┐
│                        3. DB Layer                            │
│  (*Handler.java - e.g., UsersHandler, WishListHandler)        │
│  Data Access Objects (DAOs) for all MySQL interactions.       │
└───────────────────────────────────────────────────────────────┘
```

### Front-End: JavaFX Client

The client is a standalone JavaFX application that communicates with the server over TCP sockets.

---

## 🎨 Design Patterns

This project utilizes several key design patterns to ensure maintainability and scalability.

### Back-End Patterns

| Pattern | Implementation |
|---|---|
| **Observer** | The client listens for pushed notifications from the server on a separate thread (`ClientConnection.startListening()`). When a `NotificationDto` is received, the UI is updated asynchronously via `Platform.runLater()`. |
| **Singleton** | Core handlers and services (e.g., `DBHandler` for the database connection pool) are instantiated once and reused throughout the server's lifecycle. |

### Front-End Patterns

| Pattern | Implementation |
|---|---|
| **MVC (Model-View-Controller)** | JavaFX naturally enforces MVC. **Models** are in `iwish-common`. **Views** are FXML files. **Controllers** handle UI logic and user events. |
| **Facade** | `IWishManager` and `ClientConnection` act as facades. `IWishManager` simplifies scene management and session state. `ClientConnection` hides the complexity of socket communication behind a simple `sendAndWait()` method. |

---

## 🛠️ Tech Stack

| Category | Technology |
|---|---|
| **Frontend** | JavaFX (UI), CSS (styling), Maven (build & dependency management) |
| **Backend** | Java (server logic), Raw TCP Sockets (real-time client-server communication) |
| **Database** | MySQL (JDBC for data access) |
| **Architecture** | Client-server model with DTO-based request/response protocol, multi-threaded server, soft-delete notifications |

---

## 📁 Project Structure

```
I-Wish/
├── iwish/                        # Maven project root
│   ├── iwish-common/             # Shared DTOs and Models
│   ├── iwish-server/             # Server-side logic (N-Tier)
│   └── iwish-client/             # JavaFX Client (MVC)
├── DataBaseInitializationScript  # SQL script to create tables and sample data
├── README.md                     # This file
└── DOCUMENTATION.md              # Detailed technical documentation
```

---

## 🚀 Getting Started

### Prerequisites
- Java Development Kit (JDK) 11 or higher.
- Maven 3.8.0 or higher.
- MySQL Server.

### Database Setup
1. Open your MySQL client (Workbench, Command Line, etc.).
2. Run the `DataBaseInitializationScript` script to create the `iwish` database and tables.
3. Create a `db.properties` file in `iwish-server/src/main/resources/` with your credentials:
   ```properties
   db.url=jdbc:mysql://localhost:3306/iwish
   db.user=YOUR_USER
   db.password=YOUR_PASSWORD
   ```

### Building and Running

1.  Navigate to the `iwish` directory:
    ```bash
    cd iwish
    ```

2.  Build the project:
    ```bash
    mvn clean install
    ```

3.  Run the Server (in one terminal):
    ```bash
    # Or use the provided run_server.ps1 script
    cd iwish-server
    mvn javafx:run
    ```

4.  Run the Client (in another terminal):
    ```bash
    # Or use the provided run_client.ps1 script
    cd iwish-client
    mvn javafx:run
    ```

---

## 📜 Recent Updates

- **Notification System v2.0**: Soft Delete Architecture, Smart Right Sidebar Synchronization, fade-out animations, "Clear All" with confirmation.
- **Database Fixes**: Resolved foreign key constraint naming conflicts.
- **Frontend Overhaul**: Implemented a modern Dashboard UI.

---

## 👥 Authors

Built as part of a learning journey in full-stack Java development at ITI.
