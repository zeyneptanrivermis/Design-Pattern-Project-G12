
import java.util.ArrayList;
import java.util.List;

class CategoryComposite implements Component {
    private String name;
    private List<Component> components = new ArrayList<>();
    private List<StockObserver> observers = new ArrayList<>();

    public CategoryComposite(String name) {
        this.name = name;
    }

    public void addComponent(Component component) {
        components.add(component);
    }

    public void removeComponent(Component component) {
        components.remove(component);
    }

    public void removeComponentByIndex(int index) {
        if (index >= 0 && index < components.size()) {
            components.remove(index);
        } else {
            throw new IndexOutOfBoundsException("Invalid component index");
        }
    }

    public Component getComponent(int index) {
        if (index >= 0 && index < components.size()) {
            return components.get(index);
        }
        throw new IndexOutOfBoundsException("Invalid component index");
    }

    public List<Component> getComponents() {
        return components;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public double getPrice() {
        return 0; // Categories don't have prices
    }

    @Override
    public void display(String indent) {
        System.out.println(indent + "+ " + name.toUpperCase());

        // Display all components that aren't categories
        for (Component component : components) {
            if (!(component instanceof CategoryComposite)) {
                component.display(indent + "  ");
            }
        }

        // Then display subcategories
        for (Component component : components) {
            if (component instanceof CategoryComposite) {
                component.display(indent + "  ");
            }
        }
    }

    public void displayForSelection(String indent) {
        System.out.println(indent + "+ " + name.toUpperCase());
        for (int i = 0; i < components.size(); i++) {
            Component component = components.get(i);
            if (component instanceof CategoryComposite) {
                System.out.print(indent + "  " + (i + 1) + ". ");
                component.displayForSelection("");
            } else {
                component.display(indent + "  ");
            }
        }
    }

    @Override
    public int getStock() {
        // Option 1: Return 0 since categories don't have stock
        return 0;

        // Option 2: Return sum of all products' stock in this category
        /*
         * return components.stream()
         * .mapToInt(Component::getStock)
         * .sum();
         */
    }

    @Override
    public void addObserver(StockObserver observer) {
        observers.add(observer);
        // Propagate to all components
        for (Component component : components) {
            component.addObserver(observer);
        }
    }

    @Override
    public void removeObserver(StockObserver observer) {
        observers.remove(observer);
        // Propagate to all components
        for (Component component : components) {
            component.removeObserver(observer);
        }
    }

    @Override
    public void notifyObservers(int oldStock) {
        // Categories don't have stock, so they don't notify directly
        // But they could propagate notifications to their components if needed
    }

    @Override
    public void setPricingStrategy(PricingStrategy strategy) {
        // Strategy'yi alttaki tüm bileşenlere (ürünler ve alt kategoriler) yay
        for (Component component : components) {
            component.setPricingStrategy(strategy);
        }
    }
}