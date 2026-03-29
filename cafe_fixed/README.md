# 2ndCafe - Café Order Management System

A complete JavaFX-based café order management system with MySQL database integration. This application provides separate interfaces for administrators, cashiers, and customers.

## Features

### Admin Dashboard
- **Menu Management**: Add, update, and delete menu items
- **System Analytics**: View total orders, daily/weekly/monthly revenue, and popular/profitable items
- **Order Monitoring**: View all completed orders with details

### Cashier Dashboard
- **Pending Orders Management**: View and manage customer orders
- **Payment Confirmation**: Confirm customer payments
- **Bill Generation**: Generate receipts for completed orders
- **Sales Summary**: Real-time sales analytics with scrollable content

### Customer Dashboard
- **Current Order Tab**: 
  - Browse menu and add items to cart
  - Manage order items
  - Process payment
- **Order History Tab**: 
  - View all completed orders
  - View order details and timestamps

## System Requirements

- **Java**: Java 21 or later
- **Maven**: Maven 3.8 or later
- **MySQL**: MySQL 8.0 or later
- **Operating System**: Windows, macOS, or Linux

## Installation & Setup

### Step 1: Install Prerequisites

Ensure you have installed:
- [Java 21+](https://www.oracle.com/java/technologies/downloads/)
- [Maven 3.8+](https://maven.apache.org/)
- [MySQL 8.0+](https://www.mysql.com/)

### Step 2: Configure MySQL

1. Open MySQL and create the database (or let the application create it):
   ```sql
   -- The application will automatically create the database if it doesn't exist
   -- Default credentials used by the app:
   -- Host: localhost
   -- Port: 3306
   -- User: root
   -- Password: Dontbe2L@te4meok
   ```

2. Update database credentials if needed (optional):
   - Edit `src/db/DatabaseConfig.java`
   - Modify the default credentials in the DatabaseConfig class
   - Or set environment variables:
     ```powershell
     $env:DB_HOST = "localhost"
     $env:DB_PORT = "3306"
     $env:DB_NAME = "cafe_order_system"
     $env:DB_USER = "root"
     $env:DB_PASSWORD = "your_password"
     ```

### Step 3: Run the Application

#### On Windows (PowerShell):
```powershell
# Navigate to the project directory
cd path\to\2ndcafe_fixed\cafe_fixed

# Run the application with one click
.\RUN.ps1

# Or manually run:
mvn clean compile
mvn javafx:run
```

#### On macOS/Linux:
```bash
cd path/to/2ndcafe_fixed/cafe_fixed
mvn clean compile
mvn javafx:run
```

## Default Login Credentials

| Role     | Username | Password   |
|----------|----------|------------|
| Admin    | admin    | Admin123   |
| Cashier  | cashier  | Cash123    |
| Customer | customer | Cust123    |

## Features Overview

### Admin Dashboard
1. **System Analytics Overview**: 
   - Total completed orders
   - Daily, weekly, and monthly revenue
   - Most popular and profitable items

2. **Menu Management**:
   - Add new menu items with name and price
   - Update existing item prices
   - Delete menu items
   - View all menu items in a table

3. **Order Tracking**:
   - View all completed orders
   - See order details, timestamps, and totals

### Cashier Dashboard
1. **Pending Orders List**:
   - Shows all pending customer orders
   - Click to view order details
   - Refresh list to see new orders

2. **Order Details**:
   - View items in the selected order
   - See quantities and prices
   - Calculate totals

3. **Payment Confirmation**:
   - Select payment method (Credit Card, Debit Card, Cash, Mobile Payment, Check)
   - Confirm payment to mark order as completed
   - Success notification shows "The order has been completed!"

4. **Bill Generation**:
   - Generate detailed receipt for confirmed orders
   - Shows order ID, date, items, and total

5. **Sales Summary** (Scrollable):
   - Total sales amount
   - Most popular item
   - Total confirmed orders
   - Average order value

### Customer Dashboard

#### Current Order Tab:
1. **Menu Selection**:
   - Browse available menu items
   - Select quantity using spinner
   - Add items to cart

2. **Order Management**:
   - View items in current order
   - Remove individual items
   - See running total

3. **Checkout & Payment**:
   - Place order button
   - Select payment method
   - Process payment
   - Success message: "Order placed! The cashier will finalize your order."

#### Order History Tab:
1. **View Past Orders**:
   - See all completed orders with dates
   - View total amount and items count
   - Check order status

2. **Order Details**:
   - Click order to view items
   - See item names, quantities, and prices

## Architecture

### Project Structure
```
cafe_fixed/
├── src/
│   ├── MainApp.java                 # Main application entry point
│   ├── controller/
│   │   ├── AuthController.java      # Authentication logic
│   │   └── OrderController.java     # Order management logic
│   ├── dao/                         # Database Access Objects
│   │   ├── BaseDAO.java
│   │   ├── UserDAO.java
│   │   ├── MenuItemDAO.java
│   │   ├── OrderDAO.java
│   │   ├── OrderItemDAO.java
│   │   └── PaymentDAO.java
│   ├── db/
│   │   ├── DatabaseConfig.java      # Database connection configuration
│   │   └── DatabaseInitializer.java # Database initialization and seeding
│   ├── model/                       # Data models
│   │   ├── User.java
│   │   ├── MenuItem.java
│   │   ├── Order.java
│   │   ├── OrderItem.java
│   │   ├── Payment.java
│   │   └── Role.java
│   ├── service/                     # Business logic services
│   │   ├── AnalyticsService.java
│   │   ├── PaymentService.java
│   │   └── UserRegistrationService.java
│   ├── view/                        # JavaFX UI components
│   │   ├── LoginView.java
│   │   ├── RegistrationView.java
│   │   ├── AdminDashboardView.java
│   │   ├── CashierDashboardView.java
│   │   ├── CustomerDashboardView.java
│   │   └── PaymentPanel.java
│   ├── store/                       # Data store utilities
│   │   └── DataStore.java
│   └── util/
│       └── StyleManager.java        # UI styling utilities
├── pom.xml                          # Maven configuration
└── RUN.ps1                          # One-click run script (Windows)
```

### Database Schema

The application uses MySQL with the following tables:
- **users**: Stores user accounts with roles (ADMIN, CASHIER, CUSTOMER)
- **menu_items**: Stores café menu items with prices
- **orders**: Stores customer orders and their status
- **order_items**: Stores items within each order
- **payments**: Stores payment transactions

All tables are created automatically on first run.

## Troubleshooting

### MySQL Connection Error
**Error**: "Failed to load driver class com.mysql.cj.jdbc.Driver"

**Solution**:
1. Ensure MySQL is running
2. Check MySQL credentials in `DatabaseConfig.java`
3. Verify MySQL is installed and accessible
4. Run: `mvn clean compile` to rebuild classpath

### Port Already in Use
**Error**: "Port 3306 is already in use"

**Solution**:
1. Check if MySQL is already running
2. Or change MySQL port in `DatabaseConfig.java`

### Application Won't Start
**Solution**:
1. Clean and rebuild: `mvn clean compile`
2. Check Java version: `java -version` (should be 21+)
3. Check Maven: `mvn --version`
4. Review error logs in console

## Performance Notes

- The application automatically creates the database on first run
- Menu items are cached in the application
- Payment processing is immediate
- Analytics are calculated in real-time

## Security Considerations

⚠️ **Important**: This is a demonstration application. For production use:
- Use encrypted password storage (bcrypt, etc.)
- Implement proper session management
- Use HTTPS/SSL for database connections
- Implement role-based access control (RBAC)
- Add audit logging for all transactions
- Use parameterized queries (already implemented)

## Future Enhancements

- User registration for customers
- Email order confirmations
- Receipt printing
- Inventory management
- Discount codes
- Multi-location support
- Reporting and analytics export

## Support

For issues or questions:
1. Check the troubleshooting section above
2. Review the console output for error messages
3. Ensure all prerequisites are installed correctly
4. Verify MySQL connection and database setup

## License

This project is provided as-is for educational and demonstration purposes.

---

**Last Updated**: March 2026
**Version**: 1.0.0
**Author**: 2ndCafe Development Team

