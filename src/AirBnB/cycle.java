package airbnb;

import java.util.*;

class LRUCache {
    class Node {
        int key, value;
        Node prev, next;
        
        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }
    
    protected int capacity;
    protected Map<Integer, Node> cache;
    protected Node head, tail;
    
    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.head = new Node(0, 0);
        this.tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
    }
    
    public int get(int key) {
        Node node = cache.get(key);
        if (node == null) {
            return -1;
        }
        moveToHead(node);
        return node.value;
    }
    
    public void put(int key, int value) {
        Node node = cache.get(key);
        if (node == null) {
            Node newNode = new Node(key, value);
            cache.put(key, newNode);
            addNode(newNode);
            
            if (cache.size() > capacity) {
                Node tail = popTail();
                cache.remove(tail.key);
            }
        } else {
            node.value = value;
            moveToHead(node);
        }
    }
    
    private void addNode(Node node) {
        node.prev = head;
        node.next = head.next;
        head.next.prev = node;
        head.next = node;
    }
    
    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
    
    private void moveToHead(Node node) {
        removeNode(node);
        addNode(node);
    }
    
    private Node popTail() {
        Node lastNode = tail.prev;
        removeNode(lastNode);
        return lastNode;
    }
}

class LFUCache {
    // Steps to use LRU cache within LFU cache:
    // 1. Create frequency groups, each containing an LRU cache instance
    // 2. Map each frequency level to its own LRU cache for ordering within same frequency
    // 3. Use LRU cache's get() method to access elements and maintain recency order
    // 4. Use LRU cache's put() method to add/update elements in frequency buckets
    // 5. When frequency increases, remove from old LRU cache and add to new frequency's LRU cache
    // 6. For eviction, find LRU element in minimum frequency bucket using LRU cache's tail
    
    class FrequencyGroup {
        int frequency;
        LRUCache lruCache; // Each frequency level has its own LRU cache
        
        FrequencyGroup(int freq, int capacity) {
            this.frequency = freq;
            this.lruCache = new LRUCache(capacity); // Step 1: Create LRU cache for this frequency
        }
    }
    
    private int capacity;
    private int minFreq;
    private Map<Integer, Integer> keyToFreq;
    private Map<Integer, FrequencyGroup> freqGroups;
    
    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.minFreq = 0;
        this.keyToFreq = new HashMap<>();
        this.freqGroups = new HashMap<>();
    }
    
    public int get(int key) {
        if (!keyToFreq.containsKey(key)) {
            return -1;
        }
        
        int freq = keyToFreq.get(key);
        FrequencyGroup currentGroup = freqGroups.get(freq);
        int value = currentGroup.lruCache.get(key); // Step 3: Use LRU cache's get() method
        
        updateFrequency(key, value, freq); // Step 5: Move to higher frequency LRU cache
        return value;
    }
    
    public void put(int key, int value) {
        if (capacity <= 0) return;
        
        if (keyToFreq.containsKey(key)) {
            int freq = keyToFreq.get(key);
            updateFrequency(key, value, freq); // Step 5: Update frequency and move between LRU caches
        } else {
            if (keyToFreq.size() >= capacity) {
                evictLFU(); // Step 6: Evict LRU element from minimum frequency bucket
            }
            
            keyToFreq.put(key, 1);
            // Step 4: Use LRU cache's put() method to add element to frequency 1 bucket
            freqGroups.computeIfAbsent(1, k -> new FrequencyGroup(1, capacity)).lruCache.put(key, value);
            minFreq = 1;
        }
    }
    
    private void updateFrequency(int key, int value, int oldFreq) {
        FrequencyGroup oldGroup = freqGroups.get(oldFreq);
        removeFromGroup(key, oldGroup); // Remove from old frequency's LRU cache
        
        if (oldFreq == minFreq && isEmpty(oldGroup)) {
            minFreq++;
        }
        
        int newFreq = oldFreq + 1;
        keyToFreq.put(key, newFreq);
        // Step 4: Use LRU cache's put() method to add element to new frequency bucket
        freqGroups.computeIfAbsent(newFreq, k -> new FrequencyGroup(newFreq, capacity)).lruCache.put(key, value);
    }
    
    private void evictLFU() {
        FrequencyGroup minGroup = freqGroups.get(minFreq);
        int keyToEvict = getOldestKeyFromGroup(minGroup); // Step 6: Find LRU element using LRU cache's tail
        
        keyToFreq.remove(keyToEvict);
        removeFromGroup(keyToEvict, minGroup);
    }
    
    private void removeFromGroup(int key, FrequencyGroup group) {
        group.lruCache.cache.remove(key); // Step 2: Remove from LRU cache's internal map
    }
    
    private boolean isEmpty(FrequencyGroup group) {
        return group.lruCache.cache.isEmpty(); // Step 2: Check if LRU cache is empty
    }
    
    private int getOldestKeyFromGroup(FrequencyGroup group) {
        // Step 6: Access LRU cache's tail to find least recently used element
        return group.lruCache.tail.prev.key;
    }
}

class Solution {
    public static void main(String[] args) {
        System.out.println("LFU Cache built on LRU Cache principles");
        
        LFUCache lfu = new LFUCache(2);
        lfu.put(1, 1);
        lfu.put(2, 2);
        System.out.println(lfu.get(1));
        lfu.put(3, 3);
        System.out.println(lfu.get(2));
        System.out.println(lfu.get(3));
        lfu.put(4, 4);
        System.out.println(lfu.get(1));
        System.out.println(lfu.get(3));
        System.out.println(lfu.get(4));
    }
}