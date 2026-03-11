import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Inventory {
    private Map<String, AtomicInteger> stockMap;
    private Map<String, LinkedList<Integer>> waitingList;

    public Inventory() {
        stockMap = new ConcurrentHashMap<>();
        waitingList = new ConcurrentHashMap<>();
    }

    public void addProduct(String productId, int stockCount) {
        stockMap.put(productId, new AtomicInteger(stockCount));
        waitingList.put(productId, new LinkedList<>());
    }

    public int checkStock(String productId) {
        AtomicInteger stock = stockMap.get(productId);
        return (stock != null) ? stock.get() : 0;
    }

    public synchronized String purchaseItem(String productId, int userId) {
        AtomicInteger stock = stockMap.get(productId);
        if (stock == null) {
            return "Product not found";
        }

        if (stock.get() > 0) {
            int remaining = stock.decrementAndGet();
            return "Success, " + remaining + " units remaining";
        } else {
            LinkedList<Integer> queue = waitingList.get(productId);
            queue.add(userId);
            return "Added to waiting list, position #" + queue.size();
        }
    }

    public List<Integer> getWaitingList(String productId) {
        return waitingList.getOrDefault(productId, new LinkedList<>());
    }

    public static void main(String[] args) {
        Inventory inv = new Inventory();

        inv.addProduct("IPHONE15_256GB", 100);

        System.out.println("checkStock(\"IPHONE15_256GB\") → " + inv.checkStock("IPHONE15_256GB"));

        System.out.println(inv.purchaseItem("IPHONE15_256GB", 12345));
        System.out.println(inv.purchaseItem("IPHONE15_256GB", 67890));

        for (int i = 0; i < 98; i++) {
            inv.purchaseItem("IPHONE15_256GB", i);
        }

        System.out.println(inv.purchaseItem("IPHONE15_256GB", 99999));
    }
}