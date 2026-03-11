import java.util.*;
public class SearchEngine {
    private static class TrieNode {
        Map<Character, TrieNode> children = new HashMap<>();
        boolean isEnd;
        String query;
    }
    private TrieNode root = new TrieNode();
    private Map<String, Integer> frequency = new HashMap<>();

    public void insert(String query, int freq) {
        TrieNode node = root;
        for (char c : query.toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
        }
        node.isEnd = true;
        node.query = query;
        frequency.put(query, frequency.getOrDefault(query, 0) + freq);
    }
    public void updateFrequency(String query) {
        insert(query, 1);
        System.out.println("Frequency of \"" + query + "\": " + frequency.get(query));
    }
    public List<String> search(String prefix) {
        TrieNode node = root;
        for (char c : prefix.toCharArray()) {
            if (!node.children.containsKey(c)) return Collections.emptyList();
            node = node.children.get(c);
        }
        List<String> results = new ArrayList<>();
        collect(node, results);
        PriorityQueue<String> pq = new PriorityQueue<>(
                (a, b) -> frequency.get(b) - frequency.get(a));
        pq.addAll(results);
        List<String> top = new ArrayList<>();
        int count = 0;
        while (!pq.isEmpty() && count < 10) {
            String q = pq.poll();
            top.add(q + " (" + frequency.get(q) + " searches)");
            count++;
        }
        return top;
    }
    private void collect(TrieNode node, List<String> results) {
        if (node.isEnd) results.add(node.query);
        for (TrieNode child : node.children.values()) {
            collect(child, results);
        }
    }
    public static void main(String[] args) {
        SearchEngine auto = new SearchEngine();
        auto.insert("java tutorial", 1234567);
        auto.insert("javascript", 987654);
        auto.insert("java download", 456789);
        auto.insert("java 21 features", 1);
        System.out.println("search(\"jav\") →");
        for (String s : auto.search("jav")) {
            System.out.println(s);
        }
        auto.updateFrequency("java 21 features");
        auto.updateFrequency("java 21 features");
    }
}