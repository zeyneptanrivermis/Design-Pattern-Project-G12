class StockChangeObserver implements StockObserver {
    @Override
    public void update(String productName, int newStock, int oldStock) {
        System.out.println("📋 STOCK UPDATE: " + productName +
                " stock changed from " + oldStock +
                " to " + newStock);
    }
}