interface StockObserver {

    void update(String productName, int newStock, int oldStock);
}
