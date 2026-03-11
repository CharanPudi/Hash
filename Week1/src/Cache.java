import java.util.*;
public class Cache {
    static class Video {
        String id;
        String data;
        Video(String id, String data) { this.id = id; this.data = data; }
    }
    static class LRUCache<K,V> extends LinkedHashMap<K,V> {
        private final int capacity;
        LRUCache(int capacity) {
            super(capacity, 0.75f, true);
            this.capacity = capacity;
        }
        protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
            return size() > capacity;
        }
    }
    private LRUCache<String, Video> L1 = new LRUCache<>(10000);
    private LRUCache<String, Video> L2 = new LRUCache<>(100000);
    private Map<String, Video> L3 = new HashMap<>();
    private int hitsL1=0, hitsL2=0, hitsL3=0, misses=0;
    private long timeL1=0, timeL2=0, timeL3=0;
    public void addToDatabase(Video v) {
        L3.put(v.id, v);
    }
    public Video getVideo(String id) {
        long start = System.nanoTime();
        if (L1.containsKey(id)) {
            hitsL1++;
            timeL1 += (System.nanoTime()-start)/1_000_000;
            return L1.get(id);
        }
        timeL1 += (System.nanoTime()-start)/1_000_000;
        start = System.nanoTime();
        if (L2.containsKey(id)) {
            hitsL2++;
            timeL2 += (System.nanoTime()-start)/1_000_000;
            Video v = L2.get(id);
            L1.put(id, v);
            return v;
        }
        timeL2 += (System.nanoTime()-start)/1_000_000;
        start = System.nanoTime();
        if (L3.containsKey(id)) {
            hitsL3++;
            timeL3 += (System.nanoTime()-start)/1_000_000;
            Video v = L3.get(id);
            L2.put(id, v);
            return v;
        }
        timeL3 += (System.nanoTime()-start)/1_000_000;
        misses++;
        return null;
    }
    public String getStatistics() {
        int totalHits = hitsL1+hitsL2+hitsL3;
        int totalRequests = totalHits+misses;
        double hitRate = totalRequests==0?0:(totalHits*100.0/totalRequests);
        double avgL1 = hitsL1==0?0:(timeL1*1.0/hitsL1);
        double avgL2 = hitsL2==0?0:(timeL2*1.0/hitsL2);
        double avgL3 = hitsL3==0?0:(timeL3*1.0/hitsL3);
        double avgOverall = totalHits==0?0:((timeL1+timeL2+timeL3)*1.0/totalHits);
        return "L1: Hit Rate " + String.format("%.1f", hitsL1*100.0/totalRequests) + "%, Avg Time: " + avgL1 + "ms\n" +
                "L2: Hit Rate " + String.format("%.1f", hitsL2*100.0/totalRequests) + "%, Avg Time: " + avgL2 + "ms\n" +
                "L3: Hit Rate " + String.format("%.1f", hitsL3*100.0/totalRequests) + "%, Avg Time: " + avgL3 + "ms\n" +
                "Overall: Hit Rate " + String.format("%.1f", hitRate) + "%, Avg Time: " + avgOverall + "ms";
    }
    public static void main(String[] args) {
        Cache cache = new Cache();
        cache.addToDatabase(new Video("video_123","Breaking News Clip"));
        cache.addToDatabase(new Video("video_999","Movie Trailer"));
        System.out.println("getVideo(\"video_123\") → " + cache.getVideo("video_123").data);
        System.out.println("getVideo(\"video_123\") → " + cache.getVideo("video_123").data);
        System.out.println("getVideo(\"video_999\") → " + cache.getVideo("video_999").data);
        System.out.println("getStatistics() →\n" + cache.getStatistics());
    }
}