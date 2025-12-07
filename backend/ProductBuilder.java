interface ProductBuilder {
    // Ürünün adını ayarlar
    ProductBuilder setName(String name);
    
    // Ürünün temel fiyatını ayarlar
    ProductBuilder setBasePrice(double basePrice);
    
    // Ürünün başlangıç stoğunu ayarlar
    ProductBuilder setStock(int stock);

    // Yapılandırılan ürünü döndürür
    Product build();
}