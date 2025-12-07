class LowStockObserver implements StockObserver {
    private int threshold;

    public LowStockObserver(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public void update(String productName, int newStock, int oldStock) {
        if (newStock <= threshold) {
            System.out.println("⚠️ LOW STOCK ALERT: " + productName + " has only " + newStock + " items left!");
        }
    }
}