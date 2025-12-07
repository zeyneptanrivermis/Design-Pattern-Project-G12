// Director: Belirli bir yapım sırasını tanımlar ve Builder'ı yönetir.
class ProductBuildDirector {
    private ProductBuilder builder;

    public ProductBuildDirector(ProductBuilder builder) {
        this.builder = builder;
    }
    
    // ... (buildSimpleItem ve buildComplexLaptop metotları aynı kalmıştır) ...
    // Basit bir ürünü hızlıca inşa etmek için bir metod (Örn: Hazır T-Shirt)
    public Product buildSimpleItem(String name, double price, int stock) {
        return builder.setName(name)
                      .setBasePrice(price)
                      .setStock(stock)
                      .build();
    }
    
    // Karmaşık bir ürünü (Örn: Özelleştirilmiş Laptop) inşa etmek için bir metod
    public Product buildComplexLaptop(String modelName, double basePrice, int initialStock, String cpu, int ramGB) {
        System.out.println("🔧 Karmaşık Ürün İnşa Ediliyor: " + modelName);
        System.out.println("  -> CPU: " + cpu + ", RAM: " + ramGB + "GB");
        
        String finalName = modelName + " (" + cpu + "/" + ramGB + "GB)";
        
        return builder.setName(finalName)
                      .setBasePrice(basePrice * (1 + 0.15)) 
                      .setStock(initialStock)
                      .build();
    }
}