package cafe;

import java.math.*;
import java.util.*;

public class Cafe {
    // --- Domain ---
    enum Item {
        MUFFIN,
        SHAKE,
        COFFEE,
        COFFEE_MUFFIN_COMBO, // new: Coffee + Muffin ($1 off)
        SHAKE_MUFFIN_COMBO   // new: Shake + Muffin ($1 off)
    }

    // --- Defaults (Part A base prices) ---
    private static final BigDecimal price_muffin = new BigDecimal("2.00");
    private static final BigDecimal price_shake  = new BigDecimal("3.00");
    private static final BigDecimal price_coffee = new BigDecimal("2.50");

    // --- Combo discount (Part B) ---
    private static final BigDecimal combo_discount = new BigDecimal("1.00"); // $1.00 off combos

    // --- State ---
    private final Map<Item, BigDecimal> prices = new HashMap<>();      // base items only
    private final Map<Item, Integer>    salesQty = new HashMap<>();    // includes combos
    private final Map<Item, BigDecimal> salesRevenue = new HashMap<>();// includes combos
    private int muffinsInStock = 25; // Start-of-day stock rule

    private final Scanner scanner = new Scanner(System.in);

    public Cafe() {
        initPrices();
        initSales(); // includes combos via Item.values()
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
        // Note: combos are dynamically priced from base items; not stored in prices map
    }

    private void initSales() {
        for (Item i : Item.values()) {
            salesQty.put(i, 0);
            salesRevenue.put(i, BigDecimal.ZERO);
        }
    }

    private void printMenu() {
        System.out.println("=== Cafe for Geeks ===");
        System.out.println("1) Order  (includes combos — $1.00 off)");
        System.out.println("2) Bake Muffins (+25)");
        System.out.println("3) Sales Report");
        System.out.println("4) Update Prices (base items)");
        System.out.println("5) Exit");
    }

    // --- Handlers ---
    private void handleOrder() {
        Map<Item, Integer> order = new HashMap<>();
        while (true) {
            // Multi-line product list with combo info
            System.out.println("Select an item:");
            System.out.println(" 1) Muffin");
            System.out.println(" 2) Shake");
            System.out.println(" 3) Coffee");
            System.out.println(" 4) Coffee + Muffin Combo ($1.00 off)");
            System.out.println(" 5) Shake + Muffin Combo ($1.00 off)");
            System.out.println(" 6) Cancel order");
            System.out.println(" 7) No more");
            int choice = readInt("Your choice: ");

            if (choice == 6) {
                System.out.println("Order cancelled.\n");
                return;
            }
            if (choice == 7) break;

            Item selected = null;
            switch (choice) {
                case 1: selected = Item.MUFFIN; break;
                case 2: selected = Item.SHAKE;  break;
                case 3: selected = Item.COFFEE; break;
                case 4: selected = Item.COFFEE_MUFFIN_COMBO; break;
                case 5: selected = Item.SHAKE_MUFFIN_COMBO;  break;
                default:
                    System.out.println("Invalid selection.");
                    continue;
            }

            int qty = readInt("Enter quantity: ");
            if (qty <= 0) {
                System.out.println("Quantity must be positive.");
                continue;
            }

            // Early muffin stock check (cumulative within this order), combos consume 1 muffin each
            int muffinsAlreadyInOrder =
                    order.getOrDefault(Item.MUFFIN, 0)
                  + order.getOrDefault(Item.COFFEE_MUFFIN_COMBO, 0)
                  + order.getOrDefault(Item.SHAKE_MUFFIN_COMBO, 0);

            if (selected == Item.MUFFIN
                || selected == Item.COFFEE_MUFFIN_COMBO
                || selected == Item.SHAKE_MUFFIN_COMBO) {
                int muffinsNeededForThisSelection = qty;
                if (muffinsAlreadyInOrder + muffinsNeededForThisSelection > muffinsInStock) {
                    System.out.println("Not enough muffins in stock. Order cancelled.\n");
                    return;
                }
            }

            order.put(selected, order.getOrDefault(selected, 0) + qty);
        }

        if (order.isEmpty()) {
            System.out.println("No items ordered.\n");
            return;
        }

        // Calculate total using dynamic combo pricing
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<Item, Integer> entry : order.entrySet()) {
            Item it = entry.getKey();
            int qty = entry.getValue();
            BigDecimal unitPrice = comboPrice(it);
            total = total.add(unitPrice.multiply(new BigDecimal(qty)));
        }

        System.out.println("Total: " + money(total));
        BigDecimal payment = readMoney("Enter payment: ");
        if (payment.compareTo(total) < 0) {
            System.out.println("Insufficient payment. Order cancelled.\n");
            return;
        }

        // Process order: reduce muffin stock for MUFFIN and both COMBOs
        int muffinsRequested =
                order.getOrDefault(Item.MUFFIN, 0)
              + order.getOrDefault(Item.COFFEE_MUFFIN_COMBO, 0)
              + order.getOrDefault(Item.SHAKE_MUFFIN_COMBO, 0);
        muffinsInStock -= muffinsRequested;

        for (Map.Entry<Item, Integer> entry : order.entrySet()) {
            Item it = entry.getKey();
            int qty = entry.getValue();
            BigDecimal unitPrice = comboPrice(it);
            salesQty.put(it, salesQty.get(it) + qty);
            salesRevenue.put(it, salesRevenue.get(it).add(unitPrice.multiply(new BigDecimal(qty))));
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
        System.out.printf("%-22s %-10s %-10s%n", "Item", "Quantity", "Revenue");
        for (Item item : Item.values()) {
            System.out.printf("%-22s %-10d %-10s%n",
                    label(item),
                    salesQty.get(item),
                    money(salesRevenue.get(item)));
        }
        System.out.println("--------------------------");
        
        int totalQty = 0; // beginner-friendly sum
        for (Integer qty : salesQty.values()) {
        	totalQty += qty;
        }
        
        BigDecimal totalRevenue = BigDecimal.ZERO;
        for (BigDecimal rev : salesRevenue.values()) {
        	totalRevenue = totalRevenue.add(rev);
        }
        
        System.out.printf("%-22s %-10s %-10s%n", "", totalQty, money(totalRevenue));
        System.out.printf("%nUnsold muffins in stock: %d%n", muffinsInStock);

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

    // --- Helpers ---
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
        return "$" + amount.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    // Friendly label for combos in report/menu rows
    private String label(Item item) {
        switch (item) {
            case COFFEE_MUFFIN_COMBO: return "Coffee + Muffin (combo)";
            case SHAKE_MUFFIN_COMBO:  return "Shake + Muffin (combo)";
            default: return item.name();
        }
    }

    // Dynamic unit price: base items from 'prices', combos = sum(base) - $1.00
    private BigDecimal comboPrice(Item item) {
        switch (item) {
            case MUFFIN:
            case SHAKE:
            case COFFEE:
                return prices.get(item);
            case COFFEE_MUFFIN_COMBO:
                return prices.get(Item.COFFEE)
                        .add(prices.get(Item.MUFFIN))
                        .subtract(combo_discount);
            case SHAKE_MUFFIN_COMBO:
                return prices.get(Item.SHAKE)
                        .add(prices.get(Item.MUFFIN))
                        .subtract(combo_discount);
            default:
                return BigDecimal.ZERO;
        }
    }
}
