package cafe;

import org.junit.*;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * Core logic tests (no menu/UI).
 * - PriceList: base & combo pricing
 * - Inventory: stock checks and updates
 * - Order: muffinsRequired & total calculation
 * - SalesTracker: qty & revenue accumulation
 * - Cafe.money: formatting & rounding
 *
 * JUnit 4 (works on Java 8+). If you're using JUnit 5, let me know and I'll convert.
 */
public class CafeTest {

    private PriceList prices;     // uses HashMap internally per your preference
    private Inventory inventory;
    private SalesTracker sales;

    @Before
    public void setUp() {
        // Base prices match your app’s defaults
        prices = new PriceList(
                new BigDecimal("2.00"), // muffin
                new BigDecimal("3.00"), // shake
                new BigDecimal("2.50")  // coffee
        );
        // (optional) ensure default $1.00 combo discount is present; change if you tweaked it
        prices.setComboDiscount(new BigDecimal("1.00"));

        inventory = new Inventory(25);
        sales = new SalesTracker();
    }

    // 1) PriceList: combo pricing = sum of parts - discount
    @Test
    public void testComboPricing() {
        BigDecimal muffin = prices.getBase(ProductType.MUFFIN);
        BigDecimal coffee = prices.getBase(ProductType.COFFEE);
        BigDecimal shake  = prices.getBase(ProductType.SHAKE);
        BigDecimal disc   = prices.getComboDiscount();

        BigDecimal expectedCoffeeCombo =
                coffee.add(muffin).subtract(disc); // 2.50 + 2.00 - 1.00 = 3.50
        BigDecimal expectedShakeCombo  =
                shake.add(muffin).subtract(disc);  // 3.00 + 2.00 - 1.00 = 4.00

        assertEquals(0, prices.priceOf(ProductType.COFFEE_MUFFIN_COMBO).compareTo(expectedCoffeeCombo));
        assertEquals(0, prices.priceOf(ProductType.SHAKE_MUFFIN_COMBO).compareTo(expectedShakeCombo));
    }

    // 2) Inventory: bake -> canFulfill -> consume flow
    @Test
    public void testInventoryFlow() {
        assertEquals(25, inventory.getMuffinsInStock());
        inventory.bakeMuffins(25);
        assertEquals(50, inventory.getMuffinsInStock());

        assertTrue(inventory.canFulfillMuffins(30));
        inventory.consumeMuffins(10);
        assertEquals(40, inventory.getMuffinsInStock());

        assertFalse(inventory.canFulfillMuffins(1000)); // clearly too many
    }

    // 3) Order: muffinsRequired counts muffins + combos
    @Test
    public void testOrderMuffinsRequired() {
        Order order = new Order();
        order.add(ProductType.MUFFIN, 2);
        order.add(ProductType.COFFEE_MUFFIN_COMBO, 3);
        order.add(ProductType.SHAKE_MUFFIN_COMBO, 1);
        // 2 + 3 + 1 = 6 muffins required
        assertEquals(6, order.muffinsRequired());
    }

    // 4) Order total uses dynamic PriceList (combos included)
    @Test
    public void testOrderTotal() {
        Order order = new Order();
        // 2 * muffin (2.00) = 4.00
        order.add(ProductType.MUFFIN, 2);
        // 1 * coffee (2.50) = 2.50
        order.add(ProductType.COFFEE, 1);
        // 1 * (shake+muffin combo) (3.00 + 2.00 - 1.00) = 4.00
        order.add(ProductType.SHAKE_MUFFIN_COMBO, 1);

        BigDecimal total = order.total(prices); // expected = 10.50
        assertEquals(0, total.compareTo(new BigDecimal("10.50")));
    }

    // 5) SalesTracker records qty & revenue per product accurately
    @Test
    public void testSalesTrackerRecord() {
        Order order = new Order();
        order.add(ProductType.MUFFIN, 3);
        order.add(ProductType.COFFEE_MUFFIN_COMBO, 2); // each is (2.50 + 2.00 - 1.00) = 3.50

        sales.record(order, prices);

        // Quantities
        assertEquals(3, sales.qtyOf(ProductType.MUFFIN));
        assertEquals(2, sales.qtyOf(ProductType.COFFEE_MUFFIN_COMBO));

        // Revenue
        BigDecimal muffinRev = new BigDecimal("2.00").multiply(new BigDecimal("3")); // 6.00
        BigDecimal comboRev  = new BigDecimal("3.50").multiply(new BigDecimal("2")); // 7.00
        assertEquals(0, sales.revenueOf(ProductType.MUFFIN).compareTo(muffinRev));
        assertEquals(0, sales.revenueOf(ProductType.COFFEE_MUFFIN_COMBO).compareTo(comboRev));

        // Totals
        BigDecimal expectedTotal = muffinRev.add(comboRev); // 13.00
        assertEquals(5, sales.totalQty());
        assertEquals(0, sales.totalRevenue().compareTo(expectedTotal));
    }

    // 6) Cafe.money formats & rounds to 2 dp with a dollar sign
    @Test
    public void testMoneyFormattingAndRounding() {
        assertEquals("$2.00", Cafe.money(new BigDecimal("2")));
        assertEquals("$2.35", Cafe.money(new BigDecimal("2.345"))); // HALF_UP
        assertEquals("$2.34", Cafe.money(new BigDecimal("2.344")));
    }
}
