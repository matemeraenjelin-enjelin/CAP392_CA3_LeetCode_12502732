import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

class LFUCache {

    private int capacity;
    private int minFreq;

    // Stores key -> value
    private Map<Integer, Integer> values;

    // Stores key -> frequency
    private Map<Integer, Integer> frequencies;

    // Stores frequency -> keys
    // LinkedHashSet maintains insertion order
    private Map<Integer, LinkedHashSet<Integer>> frequencyMap;

    public LFUCache(int capacity) {

        this.capacity = capacity;
        this.minFreq = 0;

        values = new HashMap<>();
        frequencies = new HashMap<>();
        frequencyMap = new HashMap<>();
    }

    public int get(int key) {

        if (!values.containsKey(key)) {
            return -1;
        }

        // Increase the frequency because the key was accessed
        updateFrequency(key);

        return values.get(key);
    }

    public void put(int key, int value) {

        if (capacity == 0) {
            return;
        }

        // If key already exists, update its value and frequency
        if (values.containsKey(key)) {

            values.put(key, value);
            updateFrequency(key);

            return;
        }

        // If cache is full, remove the LFU key
        if (values.size() == capacity) {

            LinkedHashSet<Integer> keys =
                frequencyMap.get(minFreq);

            // Remove the least recently used key
            int keyToRemove = keys.iterator().next();

            keys.remove(keyToRemove);

            values.remove(keyToRemove);
            frequencies.remove(keyToRemove);
        }

        // Insert the new key
        values.put(key, value);

        // Every new key starts with frequency 1
        frequencies.put(key, 1);

        frequencyMap
            .computeIfAbsent(1, k -> new LinkedHashSet<>())
            .add(key);

        minFreq = 1;
    }

    private void updateFrequency(int key) {

        int freq = frequencies.get(key);

        // Remove key from its current frequency group
        LinkedHashSet<Integer> oldSet = frequencyMap.get(freq);

        oldSet.remove(key);

        // If this was the last key at the minimum frequency,
        // increase minFreq
        if (freq == minFreq && oldSet.isEmpty()) {
            minFreq++;
        }

        // Increase the key's frequency
        frequencies.put(key, freq + 1);

        // Add key to the next frequency group
        frequencyMap
            .computeIfAbsent(freq + 1, k -> new LinkedHashSet<>())
            .add(key);
    }
}