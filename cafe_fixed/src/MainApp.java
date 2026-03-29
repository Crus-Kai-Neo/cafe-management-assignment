import controller.AuthController;
import controller.OrderController;
import dao.*;
import db.DatabaseConfig;
import db.DatabaseInitializer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.AnalyticsService;

public class MainApp extends Application {
    private Stage primaryStage;
    private DatabaseInitializer dbInitializer;
    private AuthController authController;
    private OrderController orderController;
    private AnalyticsService analyticsService;

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;
        this.primaryStage.setTitle("Café Order Management System");
        
        // Set initial window size and constraints
        this.primaryStage.setWidth(1200);
        this.primaryStage.setHeight(800);
        this.primaryStage.setMinWidth(900);
        this.primaryStage.setMinHeight(600);
        
        // Center on screen
        this.primaryStage.centerOnScreen();

        try {
            dbInitializer = DatabaseInitializer.getInstance();
            dbInitializer.seedDatabaseIfNeeded();

            UserDAO userDAO         = dbInitializer.getUserDAO();
            MenuItemDAO menuItemDAO = dbInitializer.getMenuItemDAO();
            OrderDAO orderDAO       = dbInitializer.getOrderDAO();
            OrderItemDAO orderItemDAO = dbInitializer.getOrderItemDAO();
            PaymentDAO paymentDAO   = dbInitializer.getPaymentDAO();

            authController    = new AuthController(userDAO);
            orderController   = new OrderController(orderDAO, orderItemDAO, menuItemDAO);
            analyticsService  = new AnalyticsService(orderDAO, orderItemDAO);

            showLoginView();
            this.primaryStage.show();

        } catch (Exception e) {
            System.err.println("Error initialising application: " + e.getMessage());
            e.printStackTrace();
            primaryStage.close();
        }
    }

    public void showLoginView() {
        view.LoginView loginView = new view.LoginView(authController, user -> {
            switch (user.getRole()) {
                case ADMIN    -> showAdminDashboard();
                case CASHIER  -> showCashierDashboard(user);
                case CUSTOMER -> showCustomerDashboard(user);
            }
        }, this::showRegistrationView);
        primaryStage.setScene(new Scene(loginView.getRoot(), 600, 700));
    }

    public void showRegistrationView() {
        view.RegistrationView regView = new view.RegistrationView(
            dbInitializer.getUserDAO(),
            this::showLoginView,
            success -> showLoginView()
        );
        // Fixed: was 900px tall which was too tall for most screens
        primaryStage.setScene(new Scene(regView.getRoot(), 600, 780));
    }

    public void showAdminDashboard() {
        view.AdminDashboardView view = new view.AdminDashboardView(
            dbInitializer.getMenuItemDAO(),
            dbInitializer.getOrderDAO(),
            dbInitializer.getOrderItemDAO(),
            analyticsService,
            this::showLoginView
        );
        primaryStage.setScene(new Scene(view.getRoot(), 1200, 720));
    }

    public void showCashierDashboard(model.User user) {
        view.CashierDashboardView view = new view.CashierDashboardView(
            dbInitializer.getMenuItemDAO(),
            orderController,
            dbInitializer.getOrderDAO(),
            dbInitializer.getOrderItemDAO(),
            dbInitializer.getPaymentDAO(),
            user.getUsername(),
            user.getUserId(),
            this::showLoginView
        );
        primaryStage.setScene(new Scene(view.getRoot(), 1200, 720));
    }

    public void showCustomerDashboard(model.User user) {
        view.CustomerDashboardView view = new view.CustomerDashboardView(
            dbInitializer.getMenuItemDAO(),
            orderController,
            dbInitializer.getOrderDAO(),
            dbInitializer.getOrderItemDAO(),
            dbInitializer.getPaymentDAO(),
            user.getUsername(),
            user.getUserId(),
            this::showLoginView
        );
        primaryStage.setScene(new Scene(view.getRoot(), 1200, 720));
    }

    @Override
    public void stop() {
        DatabaseConfig.closePool();
        System.out.println("Application closed.");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
