import java.util.*;

public class PlagiarismDetector {
    private Map<String, Set<String>> index = new HashMap<>();
    private int n = 5;

    private List<String> nGrams(String text) {
        String[] words = text.split("\\s+");
        List<String> list = new ArrayList<>();
        for (int i = 0; i <= words.length - n; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < n; j++) sb.append(words[i + j]).append(" ");
            list.add(sb.toString().trim());
        }
        return list;
    }

    public void addDoc(String id, String text) {
        for (String g : nGrams(text)) {
            index.computeIfAbsent(g, k -> new HashSet<>()).add(id);
        }
    }

    public void checkDoc(String id, String text) {
        List<String> grams = nGrams(text);
        Map<String, Integer> matches = new HashMap<>();
        for (String g : grams) {
            if (index.containsKey(g)) {
                for (String d : index.get(g)) {
                    if (!d.equals(id)) {
                        matches.put(d, matches.getOrDefault(d, 0) + 1);
                    }
                }
            }
        }
        System.out.println("→ Extracted " + grams.size() + " n-grams");
        for (Map.Entry<String, Integer> e : matches.entrySet()) {
            double sim = (e.getValue() * 100.0) / grams.size();
            System.out.printf("→ Found %d matching n-grams with \"%s\"%n", e.getValue(), e.getKey());
            System.out.printf("→ Similarity: %.2f%% %s%n", sim,
                    sim > 50 ? "(PLAGIARISM DETECTED)" : (sim > 10 ? "(suspicious)" : ""));
        }
    }

    public static void main(String[] args) {
        PlagiarismDetector q = new PlagiarismDetector();

        String essay089 = "This is a sample essay with some repeated content for testing plagiarism detection system.";
        String essay092 = "This essay contains a large portion of text that is similar to another essay for plagiarism detection.";
        String essay123 = "This essay contains a large portion of text that is similar to another essay for plagiarism detection. It also has some unique sentences.";

        q.addDoc("essay_089.txt", essay089);
        q.addDoc("essay_092.txt", essay092);
        q.checkDoc("essay_123.txt", essay123);
    }
}