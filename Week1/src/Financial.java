import java.util.*;
public class Financial {
    static class Transaction {
        int id;
        int amount;
        String merchant;
        String account;
        long time; // epoch ms

        Transaction(int id, int amount, String merchant, String account, long time) {
            this.id = id;
            this.amount = amount;
            this.merchant = merchant;
            this.account = account;
            this.time = time;
        }
    }

    private List<Transaction> transactions = new ArrayList<>();

    public void addTransaction(Transaction t) {
        transactions.add(t);
    }

    public List<int[]> findTwoSum(int target) {
        Map<Integer, Transaction> map = new HashMap<>();
        List<int[]> result = new ArrayList<>();
        for (Transaction t : transactions) {
            int complement = target - t.amount;
            if (map.containsKey(complement)) {
                result.add(new int[]{map.get(complement).id, t.id});
            }
            map.put(t.amount, t);
        }
        return result;
    }

    public List<int[]> findTwoSumWithinHour(int target) {
        List<int[]> result = new ArrayList<>();
        Map<Integer, List<Transaction>> map = new HashMap<>();
        for (Transaction t : transactions) {
            int complement = target - t.amount;
            if (map.containsKey(complement)) {
                for (Transaction other : map.get(complement)) {
                    if (Math.abs(t.time - other.time) <= 3600_000) {
                        result.add(new int[]{other.id, t.id});
                    }
                }
            }
            map.computeIfAbsent(t.amount, k -> new ArrayList<>()).add(t);
        }
        return result;
    }

    public List<List<Integer>> findKSum(int k, int target) {
        List<List<Integer>> result = new ArrayList<>();
        backtrack(transactions, k, target, 0, new ArrayList<>(), result);
        return result;
    }

    private void backtrack(List<Transaction> list, int k, int target, int start,
                           List<Integer> current, List<List<Integer>> result) {
        if (k == 0 && target == 0) {
            result.add(new ArrayList<>(current));
            return;
        }
        if (k == 0 || target < 0) return;
        for (int i = start; i < list.size(); i++) {
            current.add(list.get(i).id);
            backtrack(list, k - 1, target - list.get(i).amount, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    public List<Map<String, Object>> detectDuplicates() {
        Map<String, Map<Integer, Set<String>>> map = new HashMap<>();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Transaction t : transactions) {
            map.computeIfAbsent(t.merchant, m -> new HashMap<>())
                    .computeIfAbsent(t.amount, a -> new HashSet<>()).add(t.account);
        }
        for (String merchant : map.keySet()) {
            for (int amount : map.get(merchant).keySet()) {
                Set<String> accounts = map.get(merchant).get(amount);
                if (accounts.size() > 1) {
                    Map<String, Object> dup = new HashMap<>();
                    dup.put("amount", amount);
                    dup.put("merchant", merchant);
                    dup.put("accounts", accounts);
                    result.add(dup);
                }
            }
        }
        return result;
    }

    public static void main(String[] args) {
        Financial q = new Financial();
        q.addTransaction(new Transaction(1, 500, "Store A", "acc1", 10_000));
        q.addTransaction(new Transaction(2, 300, "Store B", "acc2", 10_900));
        q.addTransaction(new Transaction(3, 200, "Store C", "acc3", 11_800));
        q.addTransaction(new Transaction(4, 500, "Store A", "acc2", 12_000));

        System.out.println("findTwoSum(500) →");
        for (int[] pair : q.findTwoSum(500)) {
            System.out.println(Arrays.toString(pair));
        }

        System.out.println("findTwoSumWithinHour(500) →");
        for (int[] pair : q.findTwoSumWithinHour(500)) {
            System.out.println(Arrays.toString(pair));
        }

        System.out.println("findKSum(3, 1000) →");
        for (List<Integer> combo : q.findKSum(3, 1000)) {
            System.out.println(combo);
        }

        System.out.println("detectDuplicates() →");
        for (Map<String, Object> dup : q.detectDuplicates()) {
            System.out.println(dup);
        }
    }
}