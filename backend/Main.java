import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

// DİKKAT: Bu kodun çalışması için tüm diğer sınıfların (ConfigurationManager, Product, CategoryComposite, 
// ProductDecorator, PricingStrategy, vb.) aynı pakette veya uygun şekilde import edilmiş olması gerekir.

public class Main {
    // SINGLETON: ConfigurationManager nesnesini başlat
    private static final ConfigurationManager CONFIG = ConfigurationManager.getInstance();

    private static List<CategoryComposite> categories = new ArrayList<>();
    
    // OBSERVER: Eşikleri Singleton'dan al
    private static StockObserver lowStockObserver = new LowStockObserver(CONFIG.getLowStockThreshold());
    private static StockObserver StockChangeObserver = new StockChangeObserver();

    // STRATEGY: Fiyatlandırma stratejisi nesneleri
    private static final PricingStrategy normalPricing = new NormalPricing();
    private static final PricingStrategy taxIncludedPricing = new TaxIncludedPricing();
    
    // BUILDER/FACTORY: Builder ve Director nesneleri
    private static final ProductBuilder productBuilder = new ConcreteProductBuilder();
    private static final ProductBuildDirector director = new ProductBuildDirector(productBuilder);


    public static void main(String[] args) {
        initializeSampleData();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                System.out.println("\n✨=== ENVANTER YÖNETİM SİSTEMİ (6 Desen Entegre) ===✨");
                System.out.println("1. Yeni Kategori Ekle");
                System.out.println("2. Yeni Ürün Ekle (Builder/Factory)");
                System.out.println("3. Ürün/Stok/Fiyat Güncelle (Decorator ve Observer'ı test et)");
                System.out.println("4. Tüm Katalogu Görüntüle (Composite)");
                System.out.println("5. Fiyatlandırma Stratejisini Değiştir (Strategy)");
                System.out.println("6. Düşük Stok Eşiğini Güncelle (Singleton)");
                System.out.println("7. Remove product");
                System.out.println("8. Remove category");
                System.out.println("9. Exit");
                System.out.print("Choose an option: ");

                if (!scanner.hasNextInt()) {
                    System.out.println("Invalid input! Please enter a number.");
                    scanner.nextLine();
                    continue;
                }

                int mainChoice = scanner.nextInt();
                scanner.nextLine();

                if (mainChoice == 9) {
                    System.out.println("Thank you for using Inventory Management System!");
                    break;
                }

                switch (mainChoice) {
                    case 1:
                        addNewCategory(scanner);
                        break;
                    case 2:
                        if (categories.isEmpty()) {
                            System.out.println("Please add a category first!");
                        } else {
                            addNewProduct(scanner); // Builder kullanacak
                        }
                        break;
                    case 3:
                        modifyProduct(scanner);
                        break;
                    case 4:
                        displayCatalog();
                        break;
                    case 5:
                        changePricingStrategy(scanner); // Strategy Metodu
                        break;
                    case 6:
                        updateLowStockThreshold(scanner); // Singleton Metodu
                        break;
                    case 7:
                        removeProduct(scanner);
                        break;
                    case 8:
                        removeCategory(scanner);
                        break;
                    default:
                        System.out.println("Invalid option! Please enter a number between 1 and 9.");
                }
            } catch (Exception e) {
                System.out.println("An unexpected error occurred: " + e.getMessage());
                scanner.nextLine();
            }
        }
        scanner.close();
    }

    // Builder ve Factory'yi kullanan ürün ekleme metodu
    private static void addNewProduct(Scanner scanner) {
        CategoryComposite selectedCategory = getCategoryByPath(scanner, "Ürün eklenecek kategori yolunu girin: ");
        if (selectedCategory == null || hasSubcategories(selectedCategory)) return;

        System.out.println("\nÜrün Tipi Seçimi:");
        System.out.println("1. Basit Ürün (T-Shirt, Cep Telefonu vb.)");
        System.out.println("2. Karmaşık Ürün (Laptop, PC vb. - Builder ile)");
        System.out.print("Seçim: ");
        int typeChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        System.out.print("Ürün adını girin: ");
        String productName = scanner.nextLine();

        try {
            System.out.print("Ürün temel fiyatını girin: $");
            double productPrice = scanner.nextDouble();
            if (productPrice < 0) {
                System.out.println("Price cannot be negative!");
                return;
            }

            System.out.print("Başlangıç stoğunu girin: ");
            int productStock = scanner.nextInt();
            scanner.nextLine(); 
            
            if (productStock < 0) {
                System.out.println("Stock cannot be negative!");
                return;
            }

            Product newProduct;

            if (typeChoice == 2) {
                // BUILDER/DIRECTOR KULLANIMI (Karmaşık Ürün)
                System.out.print("CPU Modelini girin: ");
                String cpu = scanner.nextLine();
                System.out.print("RAM (GB) girin: ");
                int ram = scanner.nextInt();
                scanner.nextLine();
                
                newProduct = director.buildComplexLaptop(productName, productPrice, productStock, cpu, ram);
                System.out.println("✅ Ürün başarıyla eklendi (Builder/Director kullanıldı)!");
            } else {
                // FACTORY KULLANIMI (Basit Ürün - Factory Method'u basit tuttuğumuz için Builder'ın basit metodunu kullanıyoruz)
                // Normalde direkt ProductFactory.createProduct(..) çağrılmalıydı, ancak Builder'ı ön plana çıkarıyoruz.
                newProduct = productBuilder.setName(productName)
                                           .setBasePrice(productPrice)
                                           .setStock(productStock)
                                           .build();
                System.out.println("✅ Ürün başarıyla eklendi (Basit Builder kullanıldı)!");
            }
            
            newProduct.addObserver(lowStockObserver);
            newProduct.addObserver(StockChangeObserver);
            selectedCategory.addComponent(newProduct);

        } catch (InputMismatchException e) {
            System.out.println("Invalid input: Please enter valid numbers!");
            scanner.nextLine();
        }
    }
    
    // Strategy deseni için menü metodu
    private static void changePricingStrategy(Scanner scanner) {
        CategoryComposite selectedCategory = getCategoryByPath(scanner, "Strateji uygulanacak kategori yolunu girin: ");
        if (selectedCategory == null) return;

        System.out.println("\n🌐 Fiyatlandırma Stratejisi Seçimi:");
        System.out.println("1. Normal Fiyatlandırma");
        System.out.println("2. Vergi Dahil Fiyatlandırma (%" + (CONFIG.getTaxRate() * 100) + " KDV)");
        System.out.print("Seçim: ");

        int choice = scanner.nextInt();
        scanner.nextLine();

        PricingStrategy selectedStrategy;
        String strategyName;

        switch (choice) {
            case 1:
                selectedStrategy = normalPricing;
                strategyName = "Normal Fiyatlandırma";
                break;
            case 2:
                selectedStrategy = taxIncludedPricing;
                strategyName = "Vergi Dahil Fiyatlandırma";
                break;
            default:
                System.out.println("❌ Geçersiz seçenek.");
                return;
        }

        selectedCategory.setPricingStrategy(selectedStrategy);
        System.out.println("✅ **" + selectedCategory.getName().toUpperCase() + "** kategorisindeki tüm ürünlere **" + strategyName + "** uygulandı!");
    }
    
    // Singleton deseni için menü metodu
    private static void updateLowStockThreshold(Scanner scanner) {
        System.out.println("\n📉 Mevcut Düşük Stok Eşiği: " + CONFIG.getLowStockThreshold());
        System.out.print("Yeni düşük stok eşiğini girin: ");
        
        try {
            int newThreshold = scanner.nextInt();
            scanner.nextLine();
            CONFIG.setLowStockThreshold(newThreshold);
            System.out.println("✅ Düşük stok eşiği başarıyla güncellendi!");
        } catch (InputMismatchException e) {
            System.out.println("❌ Geçersiz giriş. Lütfen bir tam sayı girin.");
            scanner.nextLine();
        }
    }

    private static void modifyProduct(Scanner scanner) {
        Product product = findProduct(scanner);
        if (product == null) return;
        
        // Product'ın CategoryComposite'ini bul (Decorator değiştirmek için gerekli)
        CategoryComposite parentCategory = findProductCategory(product);
        if (parentCategory == null) return;

        while (true) {
            System.out.println("\nCurrent product details:");
            // Base Price ve Strategy uygulanmış Final Price gösterilir
            System.out.printf("Name: %s, Base Price: $%.2f, Final Price: $%.2f, Stock: %d%n",
                    product.getName(), product.basePrice, product.getPrice(), product.getStock());
            System.out.println("1. Change Base Price directly");
            System.out.println("2. Apply 10% price increase (Decorator)");
            System.out.println("3. Apply 20% discount (Decorator)");
            System.out.println("4. Modify stock (Observer)");
            System.out.println("5. Back to main menu");

            int choice = scanner.nextInt();
            scanner.nextLine(); 
            
            if (choice == 5) break;

            switch (choice) {
                case 1:
                    System.out.print("Enter new Base Price: $");
                    double newPrice = scanner.nextDouble();
                    if (newPrice < 0) {
                        System.out.println("Price cannot be negative!");
                        continue;
                    }
                    product.setBasePrice(newPrice); 
                    System.out.println("Base Price updated successfully!");
                    break;
                case 2: // DECORATOR: Zam Uygula
                    CategoryComposite category = findProductCategory(product);
                    if (category != null) {
                        // Product nesnesini, Decorator ile sarmalla ve Category'deki eski Component ile değiştir
                        Component increasedProduct = new PriceIncreaseDecorator(product, 0.10); 
                        category.removeComponent(product); 
                        category.addComponent(increasedProduct);
                        System.out.println("✅ Price increase decorator applied successfully! Check final price.");
                        return;
                    }
                    break;
                case 3: // DECORATOR: İndirim Uygula
                    CategoryComposite category2 = findProductCategory(product);
                    if (category2 != null) {
                        Component discountedProduct = new DiscountDecorator(product, 0.20);
                        category2.removeComponent(product);
                        category2.addComponent(discountedProduct);
                        System.out.println("✅ Discount decorator applied successfully! Check final price.");
                        return;
                    }
                    break;
                case 4: // OBSERVER: Stok Güncelle
                    System.out.print("Enter new stock quantity: ");
                    int newStock = scanner.nextInt();
                    if (newStock < 0) {
                        System.out.println("Stock cannot be negative!");
                        continue;
                    }
                    product.setStock(newStock); // Observer tetiklenir
                    System.out.println("✅ Stock updated successfully! Check observers alerts.");
                    break;
            }
        }
    }
    
    // --- YARDIMCI METOTLAR ---

    // Kategori yoluna göre CategoryComposite nesnesini bulur
    private static CategoryComposite getCategoryByPath(Scanner scanner, String prompt) {
        if (categories.isEmpty()) {
            System.out.println("Hiç kategori yok.");
            return null;
        }

        System.out.println("\n➡️ Kategori Seçimi:");
        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i).getName());
            displaySubCategories(categories.get(i), "  ", String.valueOf(i + 1));
        }

        System.out.print(prompt);
        String categoryPath = scanner.nextLine();
        String[] indices = categoryPath.split("\\.");

        try {
            int firstIndex = Integer.parseInt(indices[0]) - 1;
            if (firstIndex < 0 || firstIndex >= categories.size()) {
                System.out.println("❌ Geçersiz ana kategori numarası!");
                return null;
            }

            CategoryComposite selectedCategory = categories.get(firstIndex);
            List<Component> currentLevel = selectedCategory.getComponents();

            for (int i = 1; i < indices.length; i++) {
                int index = Integer.parseInt(indices[i]) - 1;
                if (index < 0 || index >= currentLevel.size()) {
                    System.out.println("❌ Geçersiz alt kategori numarası!");
                    return null;
                }
                Component component = currentLevel.get(index);
                if (component instanceof CategoryComposite) {
                    selectedCategory = (CategoryComposite) component;
                    currentLevel = selectedCategory.getComponents();
                } else {
                    System.out.println("❌ Hata: Ürün üzerinden kategoriye geçilemez!");
                    return null;
                }
            }
            return selectedCategory;
        } catch (NumberFormatException e) {
            System.out.println("❌ Hata: Lütfen sayıları nokta ile ayırarak girin (örn: 1.2)");
            return null;
        } catch (Exception e) {
            System.out.println("❌ Geçersiz kategori yolu!");
            return null;
        }
    }

    private static void addNewCategory(Scanner scanner) {
        System.out.println("\nAvailable categories:");
        System.out.println("0. Add as main category");
        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i).getName().toUpperCase());
            displaySubCategoriesForAdd(categories.get(i), "  ", String.valueOf(i + 1));
        }

        System.out.print("Select category path (0 for main, or use path like 1.2): ");
        String input = scanner.nextLine().trim();

        System.out.print("Enter category name: ");
        String categoryName = scanner.nextLine();
        CategoryComposite newCategory = new CategoryComposite(categoryName);

        if (input.equals("0")) {
            categories.add(newCategory);
        } else {
            String[] indices = input.split("\\.");
            try {
                // Navigate to the selected category
                CategoryComposite selectedCategory = categories.get(Integer.parseInt(indices[0]) - 1);
                List<Component> currentLevel = selectedCategory.getComponents();

                // Navigate through subcategories if specified
                for (int i = 1; i < indices.length; i++) {
                    Component component = currentLevel.get(Integer.parseInt(indices[i]) - 1);
                    if (component instanceof CategoryComposite) {
                        selectedCategory = (CategoryComposite) component;
                        currentLevel = selectedCategory.getComponents();
                    } else {
                        System.out.println("Invalid category path!");
                        return;
                    }
                }
                selectedCategory.addComponent(newCategory);
            } catch (Exception e) {
                System.out.println("Invalid category path!");
                return;
            }
        }
        System.out.println("Category added successfully!");
    }

    private static void displaySubCategoriesForAdd(CategoryComposite category, String indent, String prefix) {
        List<Component> components = category.getComponents();
        for (int i = 0; i < components.size(); i++) {
            Component component = components.get(i);
            if (component instanceof CategoryComposite) {
                System.out.println(indent + prefix + "." + (i + 1) + ". " +
                        component.getName().toUpperCase());
                displaySubCategoriesForAdd((CategoryComposite) component, indent + "  ",
                        prefix + "." + (i + 1));
            }
        }
    }

    private static void displaySubCategories(CategoryComposite category, String indent, String prefix) {
        List<Component> components = category.getComponents();
        for (int i = 0; i < components.size(); i++) {
            Component component = components.get(i);
            if (component instanceof CategoryComposite) {
                System.out.println(indent + prefix + "." + (i + 1) + ". " +
                        component.getName().toUpperCase());
                displaySubCategories((CategoryComposite) component, indent + "  ",
                        prefix + "." + (i + 1));
            }
        }
    }

    // Ürün arama/seçme (Decorator'ı atlayıp temel Product'ı döndürür)
    private static Product findProduct(Scanner scanner) {
        if (categories.isEmpty()) {
            System.out.println("No categories available.");
            return null;
        }

        System.out.println("\nSelect category path:");
        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i).getName().toUpperCase());
            displayCategoryContents(categories.get(i), "  ", String.valueOf(i + 1));
        }

        System.out.print("Enter category path: ");
        String categoryPath = scanner.nextLine();
        String[] indices = categoryPath.split("\\.");

        try {
            int firstIndex = Integer.parseInt(indices[0]) - 1;
            if (firstIndex < 0 || firstIndex >= categories.size()) {
                System.out.println("Invalid main category number!");
                return null;
            }

            CategoryComposite selectedCategory = categories.get(firstIndex);
            List<Component> currentLevel = selectedCategory.getComponents();

            for (int i = 1; i < indices.length; i++) {
                int index = Integer.parseInt(indices[i]) - 1;
                if (index < 0 || index >= currentLevel.size()) {
                    System.out.println("Invalid subcategory number!");
                    return null;
                }
                Component component = currentLevel.get(index);
                if (component instanceof CategoryComposite) {
                    selectedCategory = (CategoryComposite) component;
                    currentLevel = selectedCategory.getComponents();
                } else {
                    System.out.println("Invalid category path: trying to navigate through a product!");
                    return null;
                }
            }

            List<Component> selectableProducts = new ArrayList<>();
            for (Component component : selectedCategory.getComponents()) {
                if (!(component instanceof CategoryComposite)) {
                    selectableProducts.add(component);
                }
            }

            if (selectableProducts.isEmpty()) {
                System.out.println("No products in this category!");
                return null;
            }

            System.out.println("\nProducts in selected category (Final Price):");
            for (int i = 0; i < selectableProducts.size(); i++) {
                Component product = selectableProducts.get(i);
                System.out.printf("%d. %s (Price: $%.2f, Stock: %d)%n",
                        i + 1,
                        product.getName(),
                        product.getPrice(),
                        product.getStock());
            }

            System.out.print("Enter product number: ");
            int productIndex = scanner.nextInt() - 1;
            scanner.nextLine();

            if (productIndex < 0 || productIndex >= selectableProducts.size()) {
                System.out.println("Invalid product number!");
                return null;
            }

            Component selectedComponent = selectableProducts.get(productIndex);
            
            // Decorator sarmalı varsa içindeki Product'ı döndürür
            if (selectedComponent instanceof Product) {
                return (Product) selectedComponent;
            } else if (selectedComponent instanceof ProductDecorator) {
                return ((ProductDecorator) selectedComponent).product;
            } else {
                return null;
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input: please enter numbers separated by dots (e.g., 1.2.1)");
            return null;
        } catch (Exception e) {
            System.out.println("Invalid category path!");
            return null;
        }
    }

    private static void displayCategoryContents(CategoryComposite category, String indent, String prefix) {
        List<Component> components = category.getComponents();
        for (int i = 0; i < components.size(); i++) {
            Component component = components.get(i);
            if (component instanceof CategoryComposite) {
                System.out.println(indent + prefix + "." + (i + 1) + ". " +
                        component.getName().toUpperCase());
                displayCategoryContents((CategoryComposite) component, indent + "  ",
                        prefix + "." + (i + 1));
            } else {
                System.out.printf("%s- %s (Price: $%.2f, Stock: %d)%n",
                        indent,
                        component.getName().toLowerCase(),
                        component.getPrice(),
                        component.getStock());
            }
        }
    }

    private static void displayCatalog() {
        if (categories.isEmpty()) {
            System.out.println("No categories available.");
            return;
        }

        System.out.println("\nProduct Catalog:");
        System.out.println("---------------");
        for (CategoryComposite category : categories) {
            category.display("");
        }
    }
    
    // Ürünün Category'sini bulma (Product veya Decorator sarmalı içindeki Product)
    private static CategoryComposite findProductCategory(Product product) {
        for (CategoryComposite category : categories) {
            CategoryComposite result = findProductCategoryRecursive(category, product);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    private static CategoryComposite findProductCategoryRecursive(CategoryComposite category, Product product) {
        for (Component component : category.getComponents()) {
            if (component == product) { // Doğrudan Product
                return category;
            }
            if (component instanceof ProductDecorator) { // Decorator sarmalı içindeki Product
                if (((ProductDecorator) component).product == product) {
                    return category;
                }
            }
            if (component instanceof CategoryComposite) {
                CategoryComposite result = findProductCategoryRecursive((CategoryComposite) component, product);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }
    
    private static boolean hasSubcategories(CategoryComposite category) {
        return category.getComponents().stream()
                .anyMatch(component -> component instanceof CategoryComposite);
    }
    

    private static void removeProduct(Scanner scanner) {
        if (categories.isEmpty()) {
            System.out.println("No categories available.");
            return;
        }

        // Display categories with numbers and products with dashes
        System.out.println("\nSelect category path:");
        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i).getName().toUpperCase());
            displayCategoryContents(categories.get(i), "  ", String.valueOf(i + 1));
        }

        System.out.print("Enter category path: ");
        String categoryPath = scanner.nextLine();
        String[] indices = categoryPath.split("\\.");

        try {
            // Navigate to the selected category
            CategoryComposite selectedCategory = categories.get(Integer.parseInt(indices[0]) - 1);
            List<Component> currentLevel = selectedCategory.getComponents();

            for (int i = 1; i < indices.length; i++) {
                Component component = currentLevel.get(Integer.parseInt(indices[i]) - 1);
                if (component instanceof CategoryComposite) {
                    selectedCategory = (CategoryComposite) component;
                    currentLevel = selectedCategory.getComponents();
                } else {
                    System.out.println("Invalid category path!");
                    return;
                }
            }

            // Display products in the selected category
            List<Component> removableProducts = new ArrayList<>();
            for (Component component : selectedCategory.getComponents()) {
                if (!(component instanceof CategoryComposite)) {
                    removableProducts.add(component);
                }
            }

            if (removableProducts.isEmpty()) {
                System.out.println("No products in this category!");
                return;
            }

            System.out.println("\nProducts in selected category:");
            for (int i = 0; i < removableProducts.size(); i++) {
                System.out.printf("%d. %s (Price: $%.2f, Stock: %d)%n",
                        i + 1,
                        removableProducts.get(i).getName(),
                        removableProducts.get(i).getPrice(),
                        removableProducts.get(i).getStock());
            }

            System.out.print("Enter product number to remove: ");
            int productIndex = scanner.nextInt() - 1;
            scanner.nextLine();

            if (productIndex >= 0 && productIndex < removableProducts.size()) {
                Component productToRemove = removableProducts.get(productIndex);
                selectedCategory.removeComponent(productToRemove);
                System.out.println("Product removed successfully!");
            } else {
                System.out.println("Invalid product number!");
            }
        } catch (Exception e) {
            System.out.println("Invalid input!");
        }
    }

    private static void removeCategory(Scanner scanner) {
        if (categories.isEmpty()) {
            System.out.println("No categories available.");
            return;
        }

        System.out.println("\nAvailable categories:");
        for (int i = 0; i < categories.size(); i++) {
            System.out.println((i + 1) + ". " + categories.get(i).getName().toUpperCase());
            displaySubCategoriesForAdd(categories.get(i), "  ", String.valueOf(i + 1));
        }

        System.out.print("Enter category path to remove: ");
        String input = scanner.nextLine().trim();
        String[] indices = input.split("\\.");

        try {
            if (indices.length == 1) {
                // Remove main category
                int index = Integer.parseInt(indices[0]) - 1;
                if (index >= 0 && index < categories.size()) {
                    categories.remove(index);
                    System.out.println("Category removed successfully!");
                } else {
                    System.out.println("Invalid category number!");
                }
            } else {
                // Remove subcategory
                CategoryComposite parentCategory = categories.get(Integer.parseInt(indices[0]) - 1);
                List<Component> currentLevel = parentCategory.getComponents();

                // Navigate to the parent of the category to remove
                for (int i = 1; i < indices.length - 1; i++) {
                    Component component = currentLevel.get(Integer.parseInt(indices[i]) - 1);
                    if (component instanceof CategoryComposite) {
                        parentCategory = (CategoryComposite) component;
                        currentLevel = parentCategory.getComponents();
                    } else {
                        System.out.println("Invalid category path!");
                        return;
                    }
                }

                int lastIndex = Integer.parseInt(indices[indices.length - 1]) - 1;
                if (lastIndex >= 0 && lastIndex < currentLevel.size() &&
                        currentLevel.get(lastIndex) instanceof CategoryComposite) {
                    parentCategory.removeComponentByIndex(lastIndex);
                    System.out.println("Category removed successfully!");
                } else {
                    System.out.println("Invalid category index!");
                }
            }
        } catch (Exception e) {
            System.out.println("Invalid input!");
        }
    }


    private static void initializeSampleData() {
        // BUILDER ve DIRECTOR kullanımı
        
        // Create main categories
        CategoryComposite electronics = new CategoryComposite("Electronics");
        CategoryComposite clothing = new CategoryComposite("Clothing");
        categories.add(electronics);
        categories.add(clothing);

        // Create subcategories for Electronics
        CategoryComposite computers = new CategoryComposite("Computers");
        CategoryComposite phones = new CategoryComposite("Phones");
        electronics.addComponent(computers);
        electronics.addComponent(phones);

        // Create subcategories for Computers
        CategoryComposite laptops = new CategoryComposite("Laptops");
        CategoryComposite desktops = new CategoryComposite("Desktops");
        computers.addComponent(laptops);
        computers.addComponent(desktops);

        // BUILDER/DIRECTOR KULLANIMI: Karmaşık Laptop
        Product gamingLaptop = director.buildComplexLaptop("Gamer Pro X", 1499.99, 5, "i9-14900K", 32);
        
        // BUILDER/DIRECTOR KULLANIMI: Basit Ürünler
        Product businessLaptop = director.buildSimpleItem("Business Ultra", 999.99, 8);
        Product smartphone = director.buildSimpleItem("Smartphone", 799.99, 15);
        Product basicPhone = director.buildSimpleItem("Basic Phone", 99.99, 20);
        Product tshirt = director.buildSimpleItem("T-Shirt", 29.99, 50);
        Product jeans = director.buildSimpleItem("Jeans", 59.99, 30);
        
        laptops.addComponent(gamingLaptop);
        laptops.addComponent(businessLaptop);
        phones.addComponent(smartphone);
        phones.addComponent(basicPhone);
        clothing.addComponent(tshirt);
        clothing.addComponent(jeans);

        // OBSERVER: Gözlemcileri ürünlere ekle
        Product[] allProducts = {gamingLaptop, businessLaptop, smartphone, basicPhone, tshirt, jeans};
        for (Product product : allProducts) {
            product.addObserver(lowStockObserver);
            product.addObserver(StockChangeObserver);
        }
        
        // DECORATOR: Dekorasyonları uygula
        // 20% discount on gaming laptop
        Component discountedLaptop = new DiscountDecorator(gamingLaptop, 0.20);
        laptops.removeComponent(gamingLaptop);
        laptops.addComponent(discountedLaptop);

        // 10% price increase on smartphone due to high demand
        Component increasedPhone = new PriceIncreaseDecorator(smartphone, 0.10);
        phones.removeComponent(smartphone);
        phones.addComponent(increasedPhone);
    }
}