import java.util.*;

public class UsernameChecker {
    private Map<String, Integer> userMap;
    private Map<String, Integer> attemptMap;

    public UsernameChecker() {
        userMap = new HashMap<>();
        attemptMap = new HashMap<>();
    }

    public boolean registerUser(String username, int userId) {
        if (userMap.containsKey(username)) {
            return false;
        }
        userMap.put(username, userId);
        return true;
    }

    public boolean checkAvailability(String username) {

        attemptMap.put(username, attemptMap.getOrDefault(username, 0) + 1);
        return !userMap.containsKey(username);
    }

    public List<String> suggestAlternatives(String username, int maxSuggestions) {
        List<String> suggestions = new ArrayList<>();
        for (int i = 1; i < 100 && suggestions.size() < maxSuggestions; i++) {
            String alt = username + i;
            if (!userMap.containsKey(alt)) {
                suggestions.add(alt);
            }
        }
        if (suggestions.size() < maxSuggestions) {
            String alt = username.replace("_", ".");
            if (!userMap.containsKey(alt)) {
                suggestions.add(alt);
            }
        }
        return suggestions;
    }

    public String getMostAttempted() {
        if (attemptMap.isEmpty()) return null;
        return Collections.max(attemptMap.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    public static void main(String[] args) {
        UsernameChecker checker = new UsernameChecker();

        checker.registerUser("john_doe", 12345);
        checker.registerUser("jane_smith", 67890);

        System.out.println(checker.checkAvailability("john_doe"));
        System.out.println(checker.checkAvailability("jane_smith"));
        System.out.println(checker.checkAvailability("new_user"));

        System.out.println(checker.suggestAlternatives("john_doe", 3));
        for (int i = 0; i < 10543; i++) {
            checker.checkAvailability("admin");
        }

        System.out.println(checker.getMostAttempted());
    }
}