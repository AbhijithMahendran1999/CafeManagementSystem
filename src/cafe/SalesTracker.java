package cafe;

import java.math.*;
import java.util.*;

// To maintain global sales statistics like quantity and total revenue
public class SalesTracker {
	
    private final Map<ProductType, Integer> qty = new HashMap<>();
    private final Map<ProductType, BigDecimal> revenue = new HashMap<>();

    public SalesTracker() {
        for (ProductType p : ProductType.values()) {
            qty.put(p, 0);
            revenue.put(p, BigDecimal.ZERO);
        }
    }

    // Update the total quantity and revenue
    public void record(Order order, PriceList prices) {
        for (Map.Entry<ProductType, Integer> e : order.orders().entrySet()) {
            ProductType p = e.getKey();
            int count = e.getValue();
            BigDecimal rev = prices.priceOf(p).multiply(new BigDecimal(count));
            qty.put(p, qty.get(p) + count);
            revenue.put(p, revenue.get(p).add(rev));
        }
    }

    public int totalQty() {
        int sum = 0;
        for (int x : qty.values()) sum += x;
        return sum;
    }

    public BigDecimal totalRevenue() {
        BigDecimal sum = BigDecimal.ZERO;
        for (BigDecimal r : revenue.values()) sum = sum.add(r);
        return sum;
    }

    public int qtyOf(ProductType p) { return qty.get(p); }
    public BigDecimal revenueOf(ProductType p) { return revenue.get(p); }
}