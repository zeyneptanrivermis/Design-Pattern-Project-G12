class ProductFactory {
    public static Product createProduct(String name, double price, int stock) {

        Product newProduct = new Product(name, price, stock);
        
        return newProduct;
    }
}