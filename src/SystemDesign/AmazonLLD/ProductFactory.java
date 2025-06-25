abstract class ProductFactory {
    public abstract Product createProduct(String productId, String name, String description, double price, int quantity);
    
    public static ProductFactory getFactory(ProductType type) {
        switch (type) {
            case ELECTRONICS:
                return new ElectronicsFactory();
            case CLOTHING:
                return new ClothingFactory();
            case BOOKS:
                return new BooksFactory();
            default:
                return new GenericProductFactory();
        }
    }
}

enum ProductType {
    ELECTRONICS, CLOTHING, BOOKS, GENERIC
}

class ElectronicsProduct extends Product {
    private String warranty;
    private String brand;
    
    public ElectronicsProduct(String productId, String name, String description, double price, int quantity) {
        super(productId, name, description, price, quantity);
    }
    
    public void setWarranty(String warranty) { this.warranty = warranty; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getWarranty() { return warranty; }
    public String getBrand() { return brand; }
}

class ClothingProduct extends Product {
    private String size;
    private String material;
    
    public ClothingProduct(String productId, String name, String description, double price, int quantity) {
        super(productId, name, description, price, quantity);
    }
    
    public void setSize(String size) { this.size = size; }
    public void setMaterial(String material) { this.material = material; }
    public String getSize() { return size; }
    public String getMaterial() { return material; }
}

class BookProduct extends Product {
    private String author;
    private String isbn;
    
    public BookProduct(String productId, String name, String description, double price, int quantity) {
        super(productId, name, description, price, quantity);
    }
    
    public void setAuthor(String author) { this.author = author; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
}

class ElectronicsFactory extends ProductFactory {
    @Override
    public Product createProduct(String productId, String name, String description, double price, int quantity) {
        return new ElectronicsProduct(productId, name, description, price, quantity);
    }
}

class ClothingFactory extends ProductFactory {
    @Override
    public Product createProduct(String productId, String name, String description, double price, int quantity) {
        return new ClothingProduct(productId, name, description, price, quantity);
    }
}

class BooksFactory extends ProductFactory {
    @Override
    public Product createProduct(String productId, String name, String description, double price, int quantity) {
        return new BookProduct(productId, name, description, price, quantity);
    }
}

class GenericProductFactory extends ProductFactory {
    @Override
    public Product createProduct(String productId, String name, String description, double price, int quantity) {
        return new Product(productId, name, description, price, quantity);
    }
}