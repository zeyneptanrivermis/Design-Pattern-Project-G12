// Somut Builder: Product nesnesini inşa eder.
class ConcreteProductBuilder implements ProductBuilder {
    private String name;
    private double basePrice;
    private int stock;

    // Ürün Builder'ı başlangıçta boş olabilir
    public ConcreteProductBuilder() {
        this.name = "Bilinmeyen Ürün";
        this.basePrice = 0.0;
        this.stock = 0;
    }

    @Override
    public ProductBuilder setName(String name) {
        this.name = name;
        return this; // Zincirleme çağırmayı sağlamak için builder'ı geri döndürür
    }

    @Override
    public ProductBuilder setBasePrice(double basePrice) {
        this.basePrice = basePrice;
        return this;
    }

    @Override
    public ProductBuilder setStock(int stock) {
        this.stock = stock;
        return this;
    }

    @Override
    public Product build() {
        // Tüm gerekli parçalar toplandıktan sonra nihai Product nesnesini oluşturur.
        return new Product(this.name, this.basePrice, this.stock);
    }
}