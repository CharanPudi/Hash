import java.util.*;
public class Parking {
    private static class Spot {
        String plate;
        long entryTime;
        boolean occupied;
    }
    private Spot[] lot;
    private int size;
    private int probes;
    private int totalParked;
    private Map<Integer, Integer> hourlyOccupancy = new HashMap<>();
    public Parking(int capacity) {
        lot = new Spot[capacity];
        for (int i = 0; i < capacity; i++) lot[i] = new Spot();
    }
    private int hash(String plate) {
        return Math.abs(plate.hashCode()) % lot.length;
    }
    public String parkVehicle(String plate) {
        int idx = hash(plate);
        int probeCount = 0;
        while (lot[idx].occupied) {
            idx = (idx + 1) % lot.length;
            probeCount++;
        }
        lot[idx].plate = plate;
        lot[idx].entryTime = System.currentTimeMillis();
        lot[idx].occupied = true;
        size++;
        probes += probeCount;
        totalParked++;
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        hourlyOccupancy.put(hour, hourlyOccupancy.getOrDefault(hour, 0) + 1);
        return "Assigned spot #" + idx + " (" + probeCount + " probes)";
    }
    public String exitVehicle(String plate) {
        int idx = hash(plate);
        while (lot[idx].occupied && !lot[idx].plate.equals(plate)) {
            idx = (idx + 1) % lot.length;
        }
        if (!lot[idx].occupied) return "Vehicle not found";
        long duration = System.currentTimeMillis() - lot[idx].entryTime;
        lot[idx].occupied = false;
        size--;
        double hours = duration / 3600000.0;
        double fee = hours * 5.0;
        return "Spot #" + idx + " freed, Duration: " +
                String.format("%.2f", hours) + "h, Fee: $" + String.format("%.2f", fee);
    }
    public String getStatistics() {
        double occupancy = (size * 100.0) / lot.length;
        double avgProbes = totalParked == 0 ? 0 : (probes * 1.0 / totalParked);
        int peakHour = hourlyOccupancy.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey).orElse(-1);
        return "Occupancy: " + String.format("%.1f", occupancy) + "%, Avg Probes: " +
                String.format("%.2f", avgProbes) + ", Peak Hour: " + peakHour + ":00";
    }
    public static void main(String[] args) throws InterruptedException {
        Parking parking = new Parking(500);
        System.out.println(parking.parkVehicle("ABC-1234"));
        System.out.println(parking.parkVehicle("ABC-1235"));
        System.out.println(parking.parkVehicle("XYZ-9999"));
        Thread.sleep(2000);
        System.out.println(parking.exitVehicle("ABC-1234"));
        System.out.println(parking.getStatistics());
    }
}