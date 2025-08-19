package cafe;

// To manage muffins inventory
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

    // Returns true if there are enough muffins in stock to fullfill an order
    public boolean canFulfillMuffins(int muffinsNeeded) {
        return muffinsNeeded <= muffinsInStock;
    }

    //Method to consume muffins
    public void consumeMuffins(int qty) {
        muffinsInStock -= qty;
        if (muffinsInStock < 0) muffinsInStock = 0;
    }
}