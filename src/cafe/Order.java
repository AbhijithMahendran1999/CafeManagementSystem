package cafe;

import java.math.*;
import java.util.*;

// To handle the order logic
public class Order {
	
    private Map<ProductType, Integer> order = new HashMap<>();

    public void add(ProductType p, int qty) {
        order.put(p, order.getOrDefault(p, 0) + qty);
    }

    public Map<ProductType, Integer> orders() {
        return order;
    }

    // Count the total muffins required to complete an order( Since we can only have limited muffins in stock)
    public int muffinsRequired() {
        int muffins = 0;
        muffins += order.getOrDefault(ProductType.MUFFIN, 0);
        muffins += order.getOrDefault(ProductType.COFFEE_MUFFIN_COMBO, 0);
        muffins += order.getOrDefault(ProductType.SHAKE_MUFFIN_COMBO, 0);
        return muffins;
    }

    // To get total order amount
    public BigDecimal total(PriceList priceList) {
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<ProductType, Integer> e : order.entrySet()) {
            BigDecimal unit = priceList.priceOf(e.getKey());
            total = total.add(unit.multiply(new BigDecimal(e.getValue())));
        }
        return total;
    }

    public boolean isEmpty() {
        return order.isEmpty();
    }
}