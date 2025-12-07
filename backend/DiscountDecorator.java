// İndirim Uygulayan Somut Decorator
class DiscountDecorator extends ProductDecorator {
    private double discountPercentage;

    public DiscountDecorator(Product product, double discountPercentage) {
        super(product);
        this.discountPercentage = discountPercentage;
    }

    @Override
    public double getPrice() {
        // Önce Strategy ile fiyatı al, sonra indirimi uygula.
        // Bu, Strategy ve Decorator'ın uyumlu çalışmasını sağlar.
        return product.getPrice() * (1 - discountPercentage); 
    }
}