public class ConfigurationManager {
    // 1. Singleton örneğini tutacak statik değişken
    private static ConfigurationManager instance;

    // Ayar değişkenleri
    private double taxRate = 0.18; // Varsayılan %18 KDV
    private int lowStockThreshold = 5; // Düşük stok uyarısı eşiği

    // 2. Yapıcı metodu dışarıdan erişimi engellemek için private yap
    private ConfigurationManager() {
        // İlk kurulum veya loglama burada yapılabilir
        System.out.println("⚙️ ConfigurationManager başlatıldı (Singleton).");
    }

    // 3. Örneği döndüren statik erişim metodu
    public static ConfigurationManager getInstance() {
        if (instance == null) {
            instance = new ConfigurationManager();
        }
        return instance;
    }

    // Getter ve Setter metotları
    public double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
        System.out.println("✅ Vergi oranı %" + (taxRate * 100) + " olarak güncellendi.");
    }

    public int getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(int lowStockThreshold) {
        this.lowStockThreshold = lowStockThreshold;
    }
}