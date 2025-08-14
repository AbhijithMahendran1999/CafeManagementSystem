package cafe;

import java.math.*;
import java.util.*;

public class Cafe {
    // --- Domain ---
    enum Item { MUFFIN, SHAKE, COFFEE }

    // --- Defaults (Part A) ---
    private static final BigDecimal price_muffin = new BigDecimal("2.00");
    private static final BigDecimal price_shake  = new BigDecimal("3.00");
    private static final BigDecimal price_coffee = new BigDecimal("2.50");

    // --- State ---
    private final Map<Item, BigDecimal> prices = new HashMap<>();
    private final Map<Item, Integer>    salesQty = new HashMap<>();
    private final Map<Item, BigDecimal> salesRevenue = new HashMap<>();
    private int muffinsInStock = 25; // Start-of-day stock rule

    private final Scanner scanner = new Scanner(System.in);

    public Cafe() {
        initPrices();
        initSales();
    }

    // --- App loop ---
    public void run() {
        while (true) {
            printMenu();
            int choice = readInt("Choose an option: ");
            switch (choice) {
                case 1:
                    handleOrder();
                    break;
                case 2:
                    handleBake();
                    break;
                case 3:
                    handleReport();
                    break;
                case 4:
                    handleUpdatePrices();
                    break;
                case 5:
                    System.out.println("Goodbye!");
                    return; // exit the program
                default:
                    System.out.println("Invalid option. Please choose 1-5.\n");
            }
        }
    }

    // --- Init helpers ---
    private void initPrices() {
        prices.put(Item.MUFFIN, price_muffin);
        prices.put(Item.SHAKE,  price_shake);
        prices.put(Item.COFFEE, price_coffee);
    }

    private void initSales() {
        for (Item i : Item.values()) {
            salesQty.put(i, 0);
            salesRevenue.put(i, BigDecimal.ZERO);
        }
    }

    private void printMenu() {
        System.out.println("=== Cafe for Geeks ===");
        System.out.println("1) Order");
        System.out.println("2) Bake Muffins (+25)");
        System.out.println("3) Sales Report");
        System.out.println("4) Update Prices");
        System.out.println("5) Exit");
    }

    // --- Handlers ---
    private void handleOrder() {
        Map<Item, Integer> order = new HashMap<>();
        while (true) {
            System.out.println("Select an item: 1) Muffin 2) Shake 3) Coffee 4) Cancel order 5) No more");
            int choice = readInt("Your choice: ");
            if (choice == 4) {
                System.out.println("Order cancelled.\n");
                return;
            }
            if (choice == 5) break;

            Item selected = null;
            switch (choice) {
                case 1: selected = Item.MUFFIN; break;
                case 2: selected = Item.SHAKE;  break;
                case 3: selected = Item.COFFEE; break;
                default:
                    System.out.println("Invalid selection.");
                    continue;
            }

            int qty = readInt("Enter quantity: ");
            if (qty <= 0) {
                System.out.println("Quantity must be positive.");
                continue;
            }

            // Early muffin stock check (cumulative within this order)
            if (selected == Item.MUFFIN) {
                int currentMuffinsInOrder = order.getOrDefault(Item.MUFFIN, 0);
                if (currentMuffinsInOrder + qty > muffinsInStock) {
                    System.out.println("Not enough muffins in stock. Order cancelled.\n");
                    return;
                }
                order.put(Item.MUFFIN, currentMuffinsInOrder + qty);
            } else {
                // Non-muffin items have no stock limit
                order.put(selected, order.getOrDefault(selected, 0) + qty);
            }
        }

        if (order.isEmpty()) {
            System.out.println("No items ordered.\n");
            return;
        }

        // (No need for a second muffin stock check here since we already validated during item entry.)

        // Calculate total
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Item, Integer> entry : order.entrySet()) {
            total = total.add(prices.get(entry.getKey()).multiply(new BigDecimal(entry.getValue())));
        }

        System.out.println("Total: " + money(total));
        BigDecimal payment = readMoney("Enter payment: ");
        if (payment.compareTo(total) < 0) {
            System.out.println("Insufficient payment. Order cancelled.\n");
            return;
        }

        // Process order
        int muffinsRequested = order.getOrDefault(Item.MUFFIN, 0);
        muffinsInStock -= muffinsRequested;
        for (Map.Entry<Item, Integer> entry : order.entrySet()) {
            salesQty.put(entry.getKey(), salesQty.get(entry.getKey()) + entry.getValue());
            BigDecimal revenue = prices.get(entry.getKey()).multiply(new BigDecimal(entry.getValue()));
            salesRevenue.put(entry.getKey(), salesRevenue.get(entry.getKey()).add(revenue));
        }

        BigDecimal change = payment.subtract(total);
        System.out.println("Change: " + money(change) + "\n");
    }


    private void handleBake() {
        muffinsInStock += 25;
        System.out.println("Baked 25 muffins. Current stock: " + muffinsInStock + "\n");
    }

    private void handleReport() {
        System.out.println("=== Sales Report ===");
        System.out.printf("%-10s %-10s %-10s%n", "Item", "Quantity", "Revenue");
        for (Item item : Item.values()) {
            System.out.printf("%-10s %-10d %-10s%n",
                    item.name(),
                    salesQty.get(item),
                    money(salesRevenue.get(item)));
        }
        System.out.printf("%nUnsold muffins in stock: %d%n", muffinsInStock);

        int totalQty = 0; // simpler sum (no streams)
        for (Integer qty : salesQty.values()) {
            totalQty += qty;
        }

        BigDecimal totalRevenue = BigDecimal.ZERO;
        for (BigDecimal rev : salesRevenue.values()) {
            totalRevenue = totalRevenue.add(rev);
        }
        System.out.printf("Total items sold: %d%n", totalQty);
        System.out.printf("Total revenue: %s%n%n", money(totalRevenue));
    }

    private void handleUpdatePrices() {
        System.out.println("Update which price? 1) Muffin  2) Shake  3) Coffee");
        int choice = readInt("Your choice: ");

        Item item = null;
        switch (choice) {
            case 1: item = Item.MUFFIN; break;
            case 2: item = Item.SHAKE;  break;
            case 3: item = Item.COFFEE; break;
            default:
                System.out.println("Invalid selection. Returning to menu.\n");
                return;
        }

        BigDecimal current = prices.get(item);
        System.out.println("Current price: " + money(current));
        BigDecimal newPrice = readMoney("Enter new price: ");
        if (newPrice.compareTo(BigDecimal.ZERO) < 0) {
            System.out.println("Price cannot be negative.\n");
            return;
        }

        prices.put(item, newPrice);
        System.out.println(item.name() + " price updated to " + money(newPrice) + ".\n");
    }

    // --- Input & money helpers ---
    private int readInt(String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
//            System.out.println("Please enter a whole number.");
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
        return "$" + amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
