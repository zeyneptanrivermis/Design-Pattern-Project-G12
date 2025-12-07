class TaxIncludedPricing implements PricingStrategy {
    
    private final double TAX_RATE = ConfigurationManager.getInstance().getTaxRate();

    @Override
    public double calculatePrice(double basePrice) {
        return basePrice * (1 + TAX_RATE);
    }
}