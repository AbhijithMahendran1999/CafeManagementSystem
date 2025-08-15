package cafe;

public class Inventory {
    private int muffinsInStock;

    public Inventory(int startingMuffins) {
        this.muffinsInStock = startingMuffins;
    }

    public int getMuffinsInStock() {
        return muffinsInStock;
    }

    public void bakeMuffins(int qty) {
        muffinsInStock += Math.max(0, qty);
    }

    /** Returns true if we have enough muffins right now for an added amount */
    public boolean canFulfillMuffins(int muffinsNeeded) {
        return muffinsNeeded <= muffinsInStock;
    }

    /** Deduct muffins after a successful order */
    public void consumeMuffins(int qty) {
        muffinsInStock -= qty;
        if (muffinsInStock < 0) muffinsInStock = 0;
    }
}