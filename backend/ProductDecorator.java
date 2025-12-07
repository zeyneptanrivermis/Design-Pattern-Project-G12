// Decorator Base Class
abstract class ProductDecorator implements Component {
    // Decorator'ın Product'ın temel özelliklerine erişimi için protected/public kalması gerekir
    protected Product product; 

    public ProductDecorator(Product product) {
        this.product = product;
    }

    @Override
    public String getName() {
        return product.getName();
    }

    @Override
    public void display(String indent) {
        // Ürünün Base Price'ını ve Decorator uygulanmış Final Price'ını gösteriyoruz
        System.out.printf("%s- %s (Base: $%.2f, Final: $%.2f, Stock: %d) [DECORATED]%n",
                indent, getName().toLowerCase(), product.basePrice, getPrice(), getStock());
    }

    @Override
    public void displayForSelection(String indent) {
        display(indent);
    }

    // Dekorasyonlu fiyatı hesaplamak için soyut metot
    public abstract double getPrice();

    public int getStock() {
        return product.getStock();
    }

    // Observer ve Strategy metotlarını sarmallanan ürüne delege et
    @Override
    public void addObserver(StockObserver observer) {
        product.addObserver(observer);
    }

    @Override
    public void removeObserver(StockObserver observer) {
        product.removeObserver(observer);
    }

    @Override
    public void notifyObservers(int oldStock) {
        product.notifyObservers(oldStock);
    }

    @Override
    public void setPricingStrategy(PricingStrategy strategy) {
        // Strategy'yi sarmallanan ürüne uygula
        product.setPricingStrategy(strategy);
    }
}