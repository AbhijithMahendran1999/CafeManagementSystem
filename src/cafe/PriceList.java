package cafe;

import java.math.*;
import java.util.*;

public class PriceList {
    private final Map<ProductType, BigDecimal> base = new EnumMap<>(ProductType.class);
    private BigDecimal comboDiscount = new BigDecimal("1.00"); // $1 off

    public PriceList(BigDecimal muffin, BigDecimal shake, BigDecimal coffee) {
        base.put(ProductType.MUFFIN, muffin);
        base.put(ProductType.SHAKE,  shake);
        base.put(ProductType.COFFEE, coffee);
    }

    public BigDecimal getBase(ProductType p) {
        return base.get(p);
    }

    public void setBase(ProductType p, BigDecimal price) {
        if (p == ProductType.COFFEE || p == ProductType.MUFFIN || p == ProductType.SHAKE) {
            base.put(p, price);
        }
    }

    public BigDecimal getComboDiscount() {
        return comboDiscount;
    }

    public void setComboDiscount(BigDecimal d) {
        if (d != null && d.compareTo(BigDecimal.ZERO) >= 0) {
            this.comboDiscount = d;
        }
    }

    /** Dynamic unit price for any product, including combos */
    public BigDecimal priceOf(ProductType p) {
        switch (p) {
            case MUFFIN:
            case SHAKE:
            case COFFEE:
                return base.get(p);
            case COFFEE_MUFFIN_COMBO:
                return base.get(ProductType.COFFEE)
                        .add(base.get(ProductType.MUFFIN))
                        .subtract(comboDiscount);
            case SHAKE_MUFFIN_COMBO:
                return base.get(ProductType.SHAKE)
                        .add(base.get(ProductType.MUFFIN))
                        .subtract(comboDiscount);
            default:
                return BigDecimal.ZERO;
        }
    }
}