import java.util.*;
import java.util.concurrent.*;
public class Traffic {
    private Map<String, Integer> pageViews = new HashMap<>();
    private Map<String, Set<String>> uniqueVisitors = new HashMap<>();
    private Map<String, Integer> sources = new HashMap<>();
    public void processEvent(String url, String userId, String source) {
        pageViews.put(url, pageViews.getOrDefault(url, 0) + 1);
        uniqueVisitors.computeIfAbsent(url, k -> new HashSet<>()).add(userId);
        sources.put(source, sources.getOrDefault(source, 0) + 1);
    }
    public void getDashboard() {
        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>((a, b) -> b.getValue().compareTo(a.getValue()));
        pq.addAll(pageViews.entrySet());
        System.out.println("Top Pages:");
        int rank = 1;
        while (!pq.isEmpty() && rank <= 10) {
            Map.Entry<String, Integer> e = pq.poll();
            int unique = uniqueVisitors.getOrDefault(e.getKey(), Collections.emptySet()).size();
            System.out.printf("%d. %s - %d views (%d unique)%n", rank, e.getKey(), e.getValue(), unique);
            rank++;
        }
        System.out.println("\nTraffic Sources:");
        for (Map.Entry<String, Integer> e : sources.entrySet()) {
            System.out.printf("%s: %d%n", e.getKey(), e.getValue());
        }
        System.out.println("-----");
    }
    public static void main(String[] args) {
        Traffic dashboard = new Traffic();
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(dashboard::getDashboard, 5, 5, TimeUnit.SECONDS);
        String[] urls = {"/article/breaking-news", "/sports/championship", "/tech/ai-trends"};
        String[] users = {"user_123", "user_456", "user_789", "user_321", "user_654"};
        String[] sources = {"google", "facebook", "direct", "twitter"};
        Random rand = new Random();
        while (true) {
            String url = urls[rand.nextInt(urls.length)];
            String user = users[rand.nextInt(users.length)];
            String src = sources[rand.nextInt(sources.length)];
            dashboard.processEvent(url, user, src);
            try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
        }
    }
}