package cafe;

import java.math.*;
import java.util.*;

public class Cafe {
    private final Scanner scanner = new Scanner(System.in);

    // Core objects (OO separation of concerns)
    private final PriceList prices = new PriceList(
            new BigDecimal("2.00"),  // muffin
            new BigDecimal("3.00"),  // shake
            new BigDecimal("2.50")); // coffee
    private final Inventory inventory = new Inventory(25);
    private final SalesTracker sales = new SalesTracker();

    public void run() {
        while (true) {
            printMenu();
            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1: handleOrder(); break;
                case 2: handleBake(); break;
                case 3: handleReport(); break;
                case 4: handleUpdatePrices(); break;
                case 5: System.out.println("Goodbye!"); return;
                default: System.out.println("Invalid option. Please choose 1-5.\n");
            }
        }
    }

    private void printMenu() {
        System.out.println("==============================================");
        System.out.println("              The Geek Cafe");
        System.out.println("==============================================");
        System.out.println("1) Order  (includes combos — $1.00 off)");
        System.out.println("2) Bake Muffins (+25)");
        System.out.println("3) Sales Report");
        System.out.println("4) Update Prices (base items)");
        System.out.println("5) Exit");
    }

    // ===== Handlers =====
    private void handleOrder() {
        Order order = new Order();
        while (true) {
            System.out.println("Select an item:");
            System.out.println(" 1) Muffin");
            System.out.println(" 2) Shake");
            System.out.println(" 3) Coffee");
            System.out.println(" 4) Coffee + Muffin Combo ($1.00 off)");
            System.out.println(" 5) Shake + Muffin Combo ($1.00 off)");
            System.out.println(" 6) Cancel order");
            System.out.println(" 7) No more");

            int choice = readInt("Your choice: ");
            if (choice == 6) { System.out.println("Order cancelled.\n"); return; }
            if (choice == 7) break;

            ProductType selected = null;
            switch (choice) {
                case 1: selected = ProductType.MUFFIN; break;
                case 2: selected = ProductType.SHAKE; break;
                case 3: selected = ProductType.COFFEE; break;
                case 4: selected = ProductType.COFFEE_MUFFIN_COMBO; break;
                case 5: selected = ProductType.SHAKE_MUFFIN_COMBO; break;
                default: System.out.println("Invalid selection."); continue;
            }

            int qty = readInt("Enter quantity: ");
            if (qty <= 0) { System.out.println("Quantity must be positive."); continue; }

            // Early muffin stock check (cumulative within this order)
            int muffinsIfAdded = order.muffinsRequired();
            if (selected == ProductType.MUFFIN
             || selected == ProductType.COFFEE_MUFFIN_COMBO
             || selected == ProductType.SHAKE_MUFFIN_COMBO) {
                muffinsIfAdded += qty;
                if (!inventory.canFulfillMuffins(muffinsIfAdded)) {
                    System.out.println("Not enough muffins in stock. Order cancelled.\n");
                    return;
                }
            }

            order.add(selected, qty);
        }

        if (order.isEmpty()) {
            System.out.println("No items ordered.\n");
            return;
        }

        BigDecimal total = order.total(prices);
        System.out.println("Total: " + money(total));
        BigDecimal payment = readMoney("Enter payment: ");
        if (payment.compareTo(total) < 0) {
            System.out.println("Insufficient payment. Order cancelled.\n");
            return;
        }

        // Commit: consume muffins and record sales
        inventory.consumeMuffins(order.muffinsRequired());
        sales.record(order, prices);

        BigDecimal change = payment.subtract(total);
        System.out.println("Change: " + money(change) + "\n");
    }

    private void handleBake() {
        inventory.bakeMuffins(25);
        System.out.println("Baked 25 muffins. Current stock: " + inventory.getMuffinsInStock() + "\n");
    }

    private void handleReport() {
        // Header: Unsold muffins
        System.out.printf("\nUnsold Muffins: %d%n%n", inventory.getMuffinsInStock());

        // Section title
        System.out.println("Total Sales:\n");

        // Per-product lines:  Label:   qty    $revenue
        // (labels left; qty/revenue aligned to the right like in your image)
        for (ProductType p : ProductType.values()) {
            System.out.printf("%-30s %5d   %s%n",
                    label(p) + ":",             // e.g., "Muffin:" / "Coffee + Muffin (combo):"
                    sales.qtyOf(p),
                    money(sales.revenueOf(p)));
        }

        // Separator and grand totals (values aligned under the columns)
        System.out.println("\n----------------------------------------------");
        System.out.printf("%30s %5d   %s%n%n",       // pad left so numbers line up
                "",                                  // no label on the total line (matches your picture)
                sales.totalQty(),
                money(sales.totalRevenue()));
    }

    private void handleUpdatePrices() {
        System.out.println("Update which price? 1) Muffin  2) Shake  3) Coffee");
        int choice = readInt("Your choice: ");

        ProductType p = null;
        switch (choice) {
            case 1: p = ProductType.MUFFIN; break;
            case 2: p = ProductType.SHAKE;  break;
            case 3: p = ProductType.COFFEE; break;
            default:
                System.out.println("Invalid selection. Returning to menu.\n");
                return;
        }

        System.out.println("Current price: " + money(prices.getBase(p)));
        BigDecimal newPrice = readMoney("Enter new price: ");
        if (newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            System.out.println("Price cannot be zero or negative.\n");
            return;
        }
        prices.setBase(p, newPrice);
        System.out.println(p.name() + " price updated to " + money(newPrice) + ".\n");
    }

    // ===== Helpers =====
    private int readInt(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
        	System.out.println("Please enter a valid integer.");
            scanner.nextLine();
            System.out.print(prompt);
        }
        int val = scanner.nextInt();
        scanner.nextLine();
        return val;
    }

    private BigDecimal readMoney(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextBigDecimal()) {
            System.out.println("Please enter a valid amount.");
            scanner.nextLine();
            System.out.print(prompt);
        }
        BigDecimal val = scanner.nextBigDecimal();
        scanner.nextLine();
        return val.setScale(2, RoundingMode.HALF_UP);
    }

    static String money(BigDecimal amount) {
        if (amount == null) return "$0.00";
        return "$" + amount.setScale(2, RoundingMode.HALF_UP);
    }

    private String label(ProductType p) {
        switch (p) {
            case COFFEE_MUFFIN_COMBO: return "Coffee + Muffin (combo)";
            case SHAKE_MUFFIN_COMBO:  return "Shake + Muffin (combo)";
            default: return p.name();
        }
    }
}