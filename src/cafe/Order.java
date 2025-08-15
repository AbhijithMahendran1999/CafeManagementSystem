package cafe;

import java.math.*;
import java.util.*;

public class Order {
    private final Map<ProductType, Integer> orders = new EnumMap<>(ProductType.class);

    public void add(ProductType p, int qty) {
        orders.put(p, orders.getOrDefault(p, 0) + qty);
    }

    public Map<ProductType, Integer> lines() {
        return Collections.unmodifiableMap(orders);
    }

    /** How many muffins this order would consume (combos count as 1 each) */
    public int muffinsRequired() {
        int muffins = 0;
        muffins += orders.getOrDefault(ProductType.MUFFIN, 0);
        muffins += orders.getOrDefault(ProductType.COFFEE_MUFFIN_COMBO, 0);
        muffins += orders.getOrDefault(ProductType.SHAKE_MUFFIN_COMBO, 0);
        return muffins;
    }

    /** Total using dynamic prices from PriceList (combos included) */
    public BigDecimal total(PriceList priceList) {
        BigDecimal total = BigDecimal.ZERO;
        for (Map.Entry<ProductType, Integer> e : orders.entrySet()) {
            BigDecimal unit = priceList.priceOf(e.getKey());
            total = total.add(unit.multiply(new BigDecimal(e.getValue())));
        }
        return total;
    }

    public boolean isEmpty() {
        return orders.isEmpty();
    }
}