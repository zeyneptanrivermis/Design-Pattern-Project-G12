class PriceIncreaseDecorator extends ProductDecorator {
    private double increasePercentage;

    public PriceIncreaseDecorator(Product product, double increasePercentage) {
        super(product);
        this.increasePercentage = increasePercentage;
    }

    @Override
    public double getPrice() {
        return product.getPrice() * (1 + increasePercentage);
    }
}