package org.example;

import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

class Main{
    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();
        OrderManager manager = new OrderManager(lock);

        Thread th1 = new Thread(new Customer("GOGOL", manager));
        Thread th2 = new Thread(new Customer("PUSHKIN", manager));
        Thread th3 = new Thread(new Customer("DOSTOEVSKIY", manager));

        th1.start();
        th2.start();
        th3.start();

        try {
            th1.join();
            th2.join();
            th3.join();
        }catch (Exception e){}

        System.out.println("Общая стоимость электроники = "+ manager.getTotalElectricCost());
    }
}

class Product {
    private String name;
    private double price;

    Product(String name, double price){
        this.name = name;
        this.price = price;
    }

    public double getPrice(){
        return price;
    }

    public String getName(){
        return this.name;
    }
}

class ElectronicProduct extends Product{
    private int warrantyMonths;

    ElectronicProduct(String name, double price, int warrantyMonths){
        super(name,price);
        this.warrantyMonths = warrantyMonths;
    }
}

class Order{
    private List<Product> products;
    private boolean isProcessed;

    Order(List<Product> products){
        this.products = products;
        this.isProcessed = false;
    }

    public List<Product> getProducts (){
        return products;
    }

    public void setProcessed(boolean processed){
        this.isProcessed = processed;
    }

    public boolean isProcessed(){
        return this.isProcessed;
    }
}

class OrderManager{
    private final List<Order> orders = new ArrayList<>();
    private ReentrantLock reentrantLock;

    OrderManager(ReentrantLock reentrantLock){
        this.reentrantLock = reentrantLock;
    }

    public double getTotalElectricCost(){
        reentrantLock.lock();
        try {
            return orders.stream()
                    .flatMap(o -> o.getProducts().stream())
                    .filter(product -> product instanceof ElectronicProduct)
                    .mapToDouble(product -> product.getPrice())
                    .sum();
        }finally {
            reentrantLock.unlock();
        }
    }

    public void addOrder(Order order){
        reentrantLock.lock();
        try{
            orders.add(order);
            Thread.sleep(5000);
        }catch(Exception e){}
        finally {
            reentrantLock.unlock();
        }
    }
}

class Customer implements Runnable{
    private String name;
    private OrderManager manager;

    Customer(String name,OrderManager manager){
        this.name = name;
        this.manager = manager;
    }

    @Override
    public void run(){
        List<Product> products= new ArrayList<>();

        products.add(new Product("Книга", 67.67));
        products.add(new ElectronicProduct("Tesla",78.78,12));

        Order order = new Order(products);

        System.out.println("Заказ сформирован");

        manager.addOrder(order);

        System.out.println("Заказ добавлен!!");
    }

}