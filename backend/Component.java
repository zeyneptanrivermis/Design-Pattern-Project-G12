// Bütün desenleri destekleyen ana arayüz
interface Component {
    String getName();

    void display(String indent);

    void displayForSelection(String indent);

    // Nihai fiyatı Strategy ve Decorator uygulandıktan sonra döndürür.
    double getPrice();

    int getStock();

    // Observer pattern methods
    void addObserver(StockObserver observer);

    void removeObserver(StockObserver observer);

    void notifyObservers(int oldStock);

    // STRATEGY: Fiyatlandırma stratejisini ayarlama
void setPricingStrategy(PricingStrategy strategy);}