package cafe;

import org.junit.*;
import java.math.BigDecimal;
import static org.junit.Assert.*;

public class CafeTest {

    private PriceList prices;
    private Inventory inventory;
    private SalesTracker sales;

    @Before
    public void setUp() {
        // Default prices and stock are initialised before running the test
        prices = new PriceList(
                new BigDecimal("2.00"), // muffin
                new BigDecimal("3.00"), // shake
                new BigDecimal("2.50")  // coffee
        );

        inventory = new Inventory(25);
        sales = new SalesTracker();
    }

    // 1) Testing price of combos. Condition: combo pricing = sum of individual item - combo discount
    @Test
    public void testComboPricing() {
        BigDecimal muffin = prices.getBase(ProductType.MUFFIN);
        BigDecimal coffee = prices.getBase(ProductType.COFFEE);
        BigDecimal shake  = prices.getBase(ProductType.SHAKE);
        BigDecimal disc   = prices.getComboDiscount();

        BigDecimal expectedCoffeeCombo = coffee.add(muffin).subtract(disc); //  which comes to 2.50 + 2.00 - 1.00 = 3.50
        BigDecimal expectedShakeCombo  = shake.add(muffin).subtract(disc);  // which comes to 3.00 + 2.00 - 1.00 = 4.00

        assertEquals(0, prices.priceOf(ProductType.COFFEE_MUFFIN_COMBO).compareTo(expectedCoffeeCombo));
        assertEquals(0, prices.priceOf(ProductType.SHAKE_MUFFIN_COMBO).compareTo(expectedShakeCombo));
    }

    // 2) Testing muffins inventory
    @Test
    public void testInventoryFlow() {
        assertEquals(25, inventory.getMuffinsInStock());
        inventory.bakeMuffins(25);
        assertEquals(50, inventory.getMuffinsInStock());

        assertTrue(inventory.canFulfillMuffins(30));
        inventory.consumeMuffins(10);
        assertEquals(40, inventory.getMuffinsInStock());

        assertFalse(inventory.canFulfillMuffins(1000));
    }

    // 3) Testing muffins required logic i.e, muffins in combo + individual muffins
    @Test
    public void testOrderMuffinsRequired() {
        Order order = new Order();
        order.add(ProductType.MUFFIN, 2);
        order.add(ProductType.COFFEE_MUFFIN_COMBO, 3);
        order.add(ProductType.SHAKE_MUFFIN_COMBO, 1);
        // 2 + 3 + 1 = 6 muffins are required here
        assertEquals(6, order.muffinsRequired());
    }

    // 4) Testing order amount total
    @Test
    public void testOrderTotal() {
        Order order = new Order();
        // 2 * muffin (2.00) = 4.00
        order.add(ProductType.MUFFIN, 2);
        // 1 * coffee (2.50) = 2.50
        order.add(ProductType.COFFEE, 1);
        // 1 * (shake + muffin combo) (3.00 + 2.00 - 1.00) = 4.00
        order.add(ProductType.SHAKE_MUFFIN_COMBO, 1);

        BigDecimal total = order.total(prices); // expected value = 10.50
        assertEquals(0, total.compareTo(new BigDecimal("10.50")));
    }

    // 5) Testing revenue and qty of individual items as well as total sales
    @Test
    public void testSalesTrackerRecord() {
        Order order = new Order();
        order.add(ProductType.MUFFIN, 3);
        order.add(ProductType.COFFEE_MUFFIN_COMBO, 2);

        sales.record(order, prices);

        // Testing for quantities
        assertEquals(3, sales.qtyOf(ProductType.MUFFIN));
        assertEquals(2, sales.qtyOf(ProductType.COFFEE_MUFFIN_COMBO));

        // Testing for revenue
        BigDecimal muffinRev = new BigDecimal("2.00").multiply(new BigDecimal("3"));
        BigDecimal comboRev  = new BigDecimal("3.50").multiply(new BigDecimal("2"));
        assertEquals(0, sales.revenueOf(ProductType.MUFFIN).compareTo(muffinRev));
        assertEquals(0, sales.revenueOf(ProductType.COFFEE_MUFFIN_COMBO).compareTo(comboRev));

        // Testing total sales statistics
        BigDecimal expectedTotal = muffinRev.add(comboRev);
        assertEquals(5, sales.totalQty());
        assertEquals(0, sales.totalRevenue().compareTo(expectedTotal));
    }

    // 6) Testing currency formatting and rounding up
    @Test
    public void testMoneyFormattingAndRounding() {
        assertEquals("$2.00", Cafe.money(new BigDecimal("2")));
        assertEquals("$2.35", Cafe.money(new BigDecimal("2.345")));
        assertEquals("$2.34", Cafe.money(new BigDecimal("2.344")));
    }
}
