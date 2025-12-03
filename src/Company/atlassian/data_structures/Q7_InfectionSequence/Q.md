Infection Sequences Count
Problem Description
In a town with n houses aligned in a straight line, numbered from 1 to n from left to right. A virus is spreading from an initially infected house. Every day an infected house spreads the virus to its immediate uninfected neighbors.

Specifically, if house number X is infected on day i, then houses X-1 and X+1 will become infected on day i+1 if they are not already infected. Eventually, all houses will become infected. The sequence in which the houses get infected is called the infection sequence.

Given integer n and an integer array infectedHouses representing the initial infected houses, determine the total number of distinct infection sequences possible, modulo (109 + 7).

Examples
Example 1:
Input: n = 5, infectedHouses = [1, 5]
Output: 2
Explanation: Initially, houses 1 and 5 are infected. The infection progresses as follows:

On Day 1, both houses numbers 2 and 4 become infected.
On Day 2, house number 3 is infected. Now all the houses are infected.
There is no way that house number 3 can be infected before houses 2 and 4. The 2 possible infection sequences are [2,4,3] and [4,2,3].

Example 2:
Input: n = 6, infectedHouses = [3, 5]
Output: 6
Explanation: Initially, houses 3 and 5 are infected. The houses look like: [1,2,3,4,5,6].

On Day 1, houses number 2, 4, 6 get infected. The houses look like this: [1,2,3,4,5,6].
On Day 2, house number 1 gets infected. All the houses are infected now.
The 6 possible infection sequences are: [2,4,6,1], [2,6,4,1], [4,2,6,1], [4,6,2,1], [6,2,4,1], [6,4,2,1].

Example 3:
Input: n = 4, infectedHouses = [1]
Output: 1
Explanation: Initially, house 1 is infected. The houses look like: [1,2,3,4].

On Day 1, house number 2 gets infected. The houses look like this: [1,2,3,4].
On Day 2, house number 3 gets infected. The houses look like this: [1,2,3,4].
On Day 3, house number 4 gets infected. All houses are infected now.
The only possible infection sequence is [2,3,4].

Constraints
2 ≤ n ≤ 105
1 ≤ m ≤ n-1, where m is the length of infectedHouses.
1 ≤ infectedHouses[i] ≤ n
All elements of the array are distinct.