package cafe;

import java.math.*;
import java.util.*;

//To handle setting and fetching price of products including combos
public class PriceList {
	
    private Map<ProductType, BigDecimal> basePrice = new HashMap<>();
    
    // $1 off for combos
    private BigDecimal comboDiscount = new BigDecimal("1.00"); 

    public PriceList(BigDecimal muffin, BigDecimal shake, BigDecimal coffee) {
        basePrice.put(ProductType.MUFFIN, muffin);
        basePrice.put(ProductType.SHAKE,  shake);
        basePrice.put(ProductType.COFFEE, coffee);
    }

    public BigDecimal getBase(ProductType p) {
        return basePrice.get(p);
    }

    public void setBase(ProductType p, BigDecimal price) {
    	// Can only set prices for individual products(not combos)
        if (p == ProductType.COFFEE || p == ProductType.MUFFIN || p == ProductType.SHAKE) {
            basePrice.put(p, price);
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

    //Method to fetch the price of a particular product
    public BigDecimal priceOf(ProductType p) {
        switch (p) {
            case MUFFIN:
            case SHAKE:
            case COFFEE:
                return basePrice.get(p);
            case COFFEE_MUFFIN_COMBO:
                return basePrice.get(ProductType.COFFEE).add(basePrice.get(ProductType.MUFFIN)).subtract(comboDiscount);
            case SHAKE_MUFFIN_COMBO:
                return basePrice.get(ProductType.SHAKE).add(basePrice.get(ProductType.MUFFIN)).subtract(comboDiscount);
            default:
                return BigDecimal.ZERO;
        }
    }
}