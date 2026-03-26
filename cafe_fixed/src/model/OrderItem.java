package model;

public class OrderItem {
    private final MenuItem menuItem;
    private int quantity;

    public OrderItem(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    public MenuItem getMenuItem() { return menuItem; }
    public int getQuantity() { return quantity; }

    public void increaseQuantity(int qty) { this.quantity += qty; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getSubtotal() {
        return menuItem.getPrice() * quantity;
    }
}