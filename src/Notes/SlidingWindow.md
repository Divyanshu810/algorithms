# Sliding Window Techniques for Google Interviews

## 1. Fixed Size Sliding Window

### When to Use
- Problem mentions a specific window size `k`
- Keywords: "subarray of size k", "every k elements", "window of length k"
- Need to process all subarrays of fixed length

### Example Questions
1. **Maximum Sum Subarray of Size K**
   - Given array `[2, 1, 5, 1, 3, 2]` and `k=3`, find maximum sum of subarray of size 3
   - Answer: `[5, 1, 3]` with sum = 9

2. **Average of All Subarrays of Size K** 
   - Given array `[1, 12, -5, -6, 50, 3]` and `k=4`, find average of each subarray
   - Windows: `[1,12,-5,-6]`, `[12,-5,-6,50]`, `[-5,-6,50,3]`
   - Averages: `[0.5, 12.75, 10.5]`

### Pattern Recognition
```
"Find maximum/minimum/average of all subarrays of size k"
"Given window size k, process each window"
"Sliding window of fixed length k"
```

### Template
```java
public int fixedSizeWindow(int[] arr, int k) {
    int windowSum = 0;
    
    // Calculate sum of first window
    for (int i = 0; i < k; i++) {
        windowSum += arr[i];
    }
    
    int maxSum = windowSum;
    
    // Slide the window
    for (int i = k; i < arr.length; i++) {
        windowSum = windowSum - arr[i - k] + arr[i];
        maxSum = Math.max(maxSum, windowSum);
    }
    
    return maxSum;
}
```

### Visualization
```
Array: [1, 4, 2, 10, 23, 3, 1, 0, 20]  k=4

Window 1: [1, 4, 2, 10]    sum = 17
          �---------�

Window 2:    [4, 2, 10, 23] sum = 39
             �---------�

Window 3:       [2, 10, 23, 3] sum = 38
                �---------�
```

### Google Interview Problems
1. **Maximum Sum Subarray of Size K**
2. **Average of All Subarrays of Size K**
3. **First Negative Integer in Every Window of Size K**

---

## 2. Variable Size Sliding Window

### When to Use
- Window size is not fixed and depends on a condition
- Keywords: "longest", "smallest", "minimum length", "at most", "at least"
- Need to find optimal subarray satisfying some constraint

### Example Questions
1. **Longest Substring Without Repeating Characters**
   - Given string `"abcabcbb"`, find length of longest substring without repeating characters
   - Answer: `"abc"` with length = 3

2. **Minimum Window Substring**
   - Given string `s="ADOBECODEBANC"` and `t="ABC"`, find minimum window containing all characters of t
   - Answer: `"BANC"` with length = 4

3. **Longest Subarray with Sum ≤ K**
   - Given array `[1, 2, 3, 4, 5]` and `k=8`, find longest subarray with sum ≤ 8
   - Answer: `[1, 2, 3]` or `[3, 4]` both have length = 3

### Pattern Recognition
```
"Longest substring/subarray satisfying condition X"
"Minimum length subarray with property Y"
"Find subarray with sum at most/least K"
"Maximum length window with constraint Z"
```

### Template
```java
public int variableSizeWindow(int[] arr, int target) {
    int left = 0, right = 0;
    int windowSum = 0;
    int maxLength = 0;
    
    while (right < arr.length) {
        // Expand window
        windowSum += arr[right];
        
        // Shrink window if condition violated
        while (windowSum > target) {
            windowSum -= arr[left];
            left++;
        }
        
        // Update result
        maxLength = Math.max(maxLength, right - left + 1);
        right++;
    }
    
    return maxLength;
}
```

### Visualization
```
Array: [2, 1, 2, 3, 1, 1]  target = 4

Step 1: [2]           sum=2 d 4  
        �
        L,R

Step 2: [2, 1]        sum=3 d 4  
        �----�
        L    R

Step 3: [2, 1, 2]     sum=5 > 4   � shrink
        �-------�
        L       R

After shrinking: [1, 2] sum=3 d 4  
                 �----�
                 L    R
```

### Google Interview Problems
1. **Longest Substring Without Repeating Characters**
2. **Minimum Window Substring**
3. **Longest Subarray with Sum d K**

---

## 3. Sliding Window Maximum/Minimum

### When to Use
- Need to find maximum/minimum element in each window efficiently
- Keywords: "maximum in window", "minimum in window", "sliding window extrema"
- O(n) solution required instead of O(nk) brute force

### Example Questions
1. **Sliding Window Maximum (LeetCode 239)**
   - Given array `[1,3,-1,-3,5,3,6,7]` and `k=3`, find maximum in each window
   - Windows: `[1,3,-1]`, `[3,-1,-3]`, `[-1,-3,5]`, `[-3,5,3]`, `[5,3,6]`, `[3,6,7]`
   - Answer: `[3, 3, 5, 5, 6, 7]`

2. **Sliding Window Minimum**
   - Given array `[1,3,-1,-3,5,3,6,7]` and `k=3`, find minimum in each window
   - Answer: `[-1, -3, -3, -3, 3, 3]`

3. **Constrained Subsequence Sum**
   - Find maximum sum of subsequence where no two elements are more than k distance apart

### Pattern Recognition
```
"Maximum/minimum element in every window of size k"
"Sliding window extrema problems"
"Efficient range max/min queries"
```

### Template
```java
public int[] slidingWindowMaximum(int[] nums, int k) {
    Deque<Integer> deque = new ArrayDeque<>();
    int[] result = new int[nums.length - k + 1];
    
    for (int i = 0; i < nums.length; i++) {
        // Remove elements outside window
        while (!deque.isEmpty() && deque.peekFirst() < i - k + 1) {
            deque.pollFirst();
        }
        
        // Remove smaller elements (they'll never be max)
        while (!deque.isEmpty() && nums[deque.peekLast()] < nums[i]) {
            deque.pollLast();
        }
        
        deque.offerLast(i);
        
        // Store result for current window
        if (i >= k - 1) {
            result[i - k + 1] = nums[deque.peekFirst()];
        }
    }
    
    return result;
}
```

### Visualization
```
Array: [1, 3, -1, -3, 5, 3, 6, 7]  k=3

Window [1, 3, -1]:   deque=[1(3)]     max=3
Window [3, -1, -3]:  deque=[1(3)]     max=3  
Window [-1, -3, 5]:  deque=[4(5)]     max=5
Window [-3, 5, 3]:   deque=[4(5), 5(3)]  max=5
Window [5, 3, 6]:    deque=[6(6)]     max=6
Window [3, 6, 7]:    deque=[7(7)]     max=7

Note: deque stores indices, values shown in parentheses
```

---

## 4. Two Pointers + Sliding Window

### When to Use
- Array is sorted and you need to find pairs/triplets with specific sum
- Need to compare elements from both ends of array/string
- Optimize area/volume calculations with constraints
- Find optimal solutions by moving pointers based on conditions

### Example Questions

1. **Container With Most Water (LeetCode 11)**
   - Given array `[1,8,6,2,5,4,8,3,7]` representing heights, find two lines that form container with most water
   - Use two pointers at start and end, move pointer with smaller height
   - Answer: Between heights 8 and 7 at distance 7, area = 7×7 = 49

2. **Trapping Rain Water (LeetCode 42)**
   - Given `[0,1,0,2,1,0,1,3,2,1,2,1]`, calculate trapped rainwater
   - Use two pointers tracking left_max and right_max
   - Answer: 6 units of water can be trapped

3. **3Sum (LeetCode 15)**
   - Given array `[-1,0,1,2,-1,-4]`, find all unique triplets that sum to 0
   - Sort array, fix first element, use two pointers for remaining two
   - Answer: `[[-1,-1,2], [-1,0,1]]`

4. **Two Sum II - Input Array Is Sorted (LeetCode 167)**
   - Given sorted array `[2,7,11,15]` and target `9`, find two numbers that sum to target
   - Use two pointers from start and end
   - Answer: indices `[1,2]` (1-indexed) for numbers `[2,7]`

5. **Valid Palindrome (LeetCode 125)**
   - Given string `"A man, a plan, a canal: Panama"`, check if it's a palindrome
   - Use two pointers from both ends, skip non-alphanumeric characters
   - Answer: true

6. **Minimum Size Subarray Sum (LeetCode 209)**
   - Given array `[2,3,1,2,4,3]` and target `7`, find minimum length subarray with sum ≥ target
   - Use sliding window with two pointers
   - Answer: `[4,3]` with length = 2

7. **Longest Palindromic Substring (LeetCode 5)**
   - Given string `"babad"`, find longest palindromic substring
   - For each center, expand with two pointers
   - Answer: `"bab"` or `"aba"`

8. **Remove Duplicates from Sorted Array (LeetCode 26)**
   - Given sorted array `[1,1,2]`, remove duplicates in-place
   - Use two pointers: one for reading, one for writing
   - Answer: length = 2, array becomes `[1,2,_]`

### Pattern Recognition
```
"Find pair/triplet with sum X in sorted array"
"Maximum area/volume with constraints"
"Palindrome checking problems"
"Two Sum variants with sorted input"
"Optimize by moving pointers from both ends"
"Remove duplicates from sorted array"
```

### Template
```java
public boolean hasValidSubarray(int[] arr, int target) {
    int left = 0, right = arr.length - 1;
    
    while (left < right) {
        int sum = arr[left] + arr[right];
        
        if (sum == target) {
            return true;
        } else if (sum < target) {
            left++;
        } else {
            right--;
        }
    }
    
    return false;
}
```

### Google Interview Problems
1. **Container With Most Water**
2. **Trapping Rain Water**
3. **3Sum / 4Sum Problems**

---

## 5. Sliding Window with HashMap/Frequency Counter

### When to Use
- Need to track frequency/count of elements in window
- Keywords: "anagram", "permutation", "substring with characters", "frequency", "count"
- Problems involving character matching or pattern finding

### Example Questions

1. **Minimum Window Substring (LeetCode 76)**
   - Given `s = "ADOBECODEBANC"` and `t = "ABC"`, find minimum window containing all characters of t
   - Track frequency of characters in t, expand window until all characters found, then shrink
   - Answer: `"BANC"`

2. **Find All Anagrams in a String (LeetCode 438)**
   - Given `s = "abab"` and `p = "ab"`, find all anagrams of p in s
   - Use fixed size window equal to p.length(), compare frequencies
   - Answer: `[0, 2]` for substrings `"ab"` and `"ab"`

3. **Longest Substring with At Most K Distinct Characters**
   - Given `s = "eceba"` and `k = 2`, find longest substring with at most 2 distinct characters
   - Track character frequencies, shrink when distinct count > k
   - Answer: `"ece"` with length = 3

4. **Substring with Concatenation of All Words (LeetCode 30)**
   - Given `s = "barfoothefoobarman"` and `words = ["foo","bar"]`, find all starting indices
   - Track word frequencies in sliding window of size = total words length
   - Answer: `[0, 9]`

5. **Permutation in String (LeetCode 567)**
   - Given `s1 = "ab"` and `s2 = "eidbaooo"`, check if s2 contains permutation of s1
   - Use fixed window size equal to s1.length(), compare character frequencies
   - Answer: `true` (substring `"ba"` is permutation of `"ab"`)

6. **Longest Repeating Character Replacement (LeetCode 424)**
   - Given `s = "ABAB"` and `k = 2`, find longest substring with same character after at most k replacements
   - Track character frequencies, check if window_size - max_frequency <= k
   - Answer: 4 (replace all to `"AAAA"`)

7. **Frequency of the Most Frequent Element (LeetCode 1838)**
   - Given `nums = [1,2,4]` and `k = 5`, make elements equal using at most k operations
   - Sort array, use sliding window to find longest subarray that can be made equal
   - Answer: 3 (make all elements = 4)

### Pattern Recognition
```
"Find substring/subarray with specific character frequency"
"Anagram/permutation problems"
"At most/exactly K distinct elements"
"Minimum window containing pattern"
"Character replacement with K operations"
```

### Template
```java
public int slidingWindowWithMap(String s, String pattern) {
    Map<Character, Integer> patternMap = new HashMap<>();
    Map<Character, Integer> windowMap = new HashMap<>();
    
    // Build pattern frequency map
    for (char c : pattern.toCharArray()) {
        patternMap.put(c, patternMap.getOrDefault(c, 0) + 1);
    }
    
    int left = 0, right = 0;
    int matched = 0;
    int minLength = Integer.MAX_VALUE;
    
    while (right < s.length()) {
        char rightChar = s.charAt(right);
        windowMap.put(rightChar, windowMap.getOrDefault(rightChar, 0) + 1);
        
        if (patternMap.containsKey(rightChar) && 
            windowMap.get(rightChar).equals(patternMap.get(rightChar))) {
            matched++;
        }
        
        while (matched == patternMap.size()) {
            minLength = Math.min(minLength, right - left + 1);
            
            char leftChar = s.charAt(left);
            windowMap.put(leftChar, windowMap.get(leftChar) - 1);
            
            if (patternMap.containsKey(leftChar) && 
                windowMap.get(leftChar) < patternMap.get(leftChar)) {
                matched--;
            }
            left++;
        }
        right++;
    }
    
    return minLength;
}
```

---

## 6. Common Google Interview Problems by Category

### Easy Level
1. **Maximum Average Subarray I** (Fixed Size)
2. **Contains Duplicate II** (Fixed Size)

### Medium Level
1. **Longest Substring Without Repeating Characters** (Variable Size)
2. **Fruit Into Baskets** (Variable Size)
3. **Sliding Window Maximum** (Deque)
4. **Minimum Window Substring** (HashMap)

### Hard Level
1. **Sliding Window Median** (Two Heaps)
2. **Minimum Number of K Consecutive Bit Flips**
3. **Subarrays with K Different Integers**

---

## 7. Time & Space Complexity

| Technique | Time Complexity | Space Complexity |
|-----------|----------------|------------------|
| Fixed Size | O(n) | O(1) |
| Variable Size | O(n) | O(1) |
| With Deque | O(n) | O(k) |
| With HashMap | O(n) | O(k) |

---

## 8. Key Tips for Google Interviews

1. **Identify the pattern**: Fixed vs Variable size
2. **Edge cases**: Empty array, single element, k > array length
3. **Optimization**: Can you solve in one pass?
4. **Follow-up questions**: What if negative numbers? What if k is very large?
5. **Code clarity**: Use meaningful variable names, add comments for complex logic

### Common Mistakes to Avoid
- Forgetting to handle window boundaries
- Not updating window state correctly when shrinking
- Index out of bounds errors
- Not considering edge cases

### Interview Strategy
1. Clarify the problem and constraints
2. Identify if it's a sliding window problem
3. Choose the right sliding window pattern
4. Code the solution step by step
5. Test with examples and edge cases
6. Optimize if possible