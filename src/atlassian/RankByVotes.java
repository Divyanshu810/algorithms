package practice.atlassian;

import java.util.*;

/**
 * LeetCode 1366: Rank Teams by Votes
 * OOP design with pluggable RankingStrategy to allow future scale-ups/scale-downs
 * (e.g., weighted ballots, different tie-breaks, partial ballots).
 */
public class RankByVotes {

    // ---------- Public API required by LeetCode ----------
    public String rankTeams(String[] votes) {
        if (votes == null || votes.length == 0) return "";
        Election election = Election.fromVotes(votes);
        RankingStrategy strategy = new PositionalCountsWithLexTiebreak();
        List<Team> ranked = election.rank(strategy);
        StringBuilder sb = new StringBuilder();
        for (Team t : ranked) sb.append(t.id());
        return sb.toString();
    }

    // ---------- Domain Model ----------
    static final class Team {
        private final char id; // single uppercase letter per problem
        Team(char id) { this.id = id; }
        char id() { return id; }
        @Override public String toString() { return String.valueOf(id); }
    }

    static final class Ballot {
        // Immutable: order[i] = team at position i
        private final char[] order;
        Ballot(String ranking) { this.order = ranking.toCharArray(); }
        int size() { return order.length; }
        char teamAt(int pos) { return order[pos]; }
    }

    static final class TeamIndex {
        // Map team char -> dense index [0..m-1], and inverse.
        private final Map<Character, Integer> toIndex;
        private final char[] toTeam;
        TeamIndex(Set<Character> teams) {
            List<Character> sorted = new ArrayList<>(teams);
            // Important: build in lexicographic order so tie-breaks can use fixed positions quickly
            Collections.sort(sorted);
            toIndex = new HashMap<>(sorted.size() * 2);
            toTeam = new char[sorted.size()];
            for (int i = 0; i < sorted.size(); i++) {
                char c = sorted.get(i);
                toIndex.put(c, i);
                toTeam[i] = c;
            }
        }
        int size() { return toTeam.length; }
        int idx(char team) { return toIndex.get(team); }
        char team(int idx) { return toTeam[idx]; }
        List<Team> allTeams() {
            List<Team> list = new ArrayList<>(toTeam.length);
            for (char c : toTeam) list.add(new Team(c));
            return list;
        }
    }

    static final class Election {
        private final List<Ballot> ballots;
        private final TeamIndex index;

        private Election(List<Ballot> ballots, TeamIndex index) {
            this.ballots = ballots;
            this.index = index;
        }

        static Election fromVotes(String[] votes) {
            List<Ballot> bs = new ArrayList<>(votes.length);
            Set<Character> teams = new HashSet<>();
            for (String v : votes) {
                bs.add(new Ballot(v));
                for (int i = 0; i < v.length(); i++) teams.add(v.charAt(i));
            }
            TeamIndex idx = new TeamIndex(teams);
            return new Election(bs, idx);
        }

        List<Team> rank(RankingStrategy strategy) {
            return strategy.rank(ballots, index);
        }
    }

    // ---------- Strategy SPI ----------
    interface RankingStrategy {
        List<Team> rank(List<Ballot> ballots, TeamIndex index);
    }

    /**
     * Implements the exact LeetCode rule:
     * - For each rank position r, count how many ballots place team T at r.
     * - Sort teams by higher counts at r=0,1,2,...; tie-break by lexicographically smaller team id.
     *
     * Extensible notes:
     * - Can plug in weighted ballots by replacing the increment with weight.
     * - Can support partial ballots by guarding index lookups.
     */
    static final class PositionalCountsWithLexTiebreak implements RankingStrategy {
        @Override
        public List<Team> rank(List<Ballot> ballots, TeamIndex index) {
            final int m = index.size();
            // counts[t][r] = # of ballots with team t at rank r
            int[][] counts = new int[m][m]; // m ranks max because each ballot ranks all teams

            // Build counts in O(N*M)
            for (Ballot b : ballots) {
                int len = b.size();
                for (int r = 0; r < len; r++) {
                    int tIdx = index.idx(b.teamAt(r));
                    counts[tIdx][r]++;
                }
            }

            // Prepare list of teams to sort
            List<Integer> teamIdx = new ArrayList<>(m);
            for (int i = 0; i < m; i++) teamIdx.add(i);

            // Comparator: higher counts at rank 0, then 1, ..., then lexicographic id
            teamIdx.sort((a, b) -> {
                for (int r = 0; r < m; r++) {
                    int diff = counts[b][r] - counts[a][r];
                    if (diff != 0) return diff; // higher first
                }
                // lexicographic by team id
                return Character.compare(index.team(a), index.team(b));
            });

            // Materialize result
            List<Team> out = new ArrayList<>(m);
            for (int id : teamIdx) out.add(new Team(index.team(id)));
            return out;
        }
    }
}
//leetcode solution
//class Solution {
//    public String rankTeams(String[] votes) {
//        int len = votes[0].length();
//        int[][] map = new int[26][len + 1];
//        for(int i = 0; i < 26; i++) map[i][len] = i;
//        // System.out.println(Arrays.deepToString(map));
//        for(int i = 0; i < votes.length; i++){
//            String s = votes[i];
//            for(int j = 0; j < len; j++){
//                map[s.charAt(j) - 'A'][j]++;
//            }
//        }
//        Arrays.sort(map, (a, b) ->{
//            for(int i = 0; i < len; i++){
//                if(a[i] < b[i]) return 1;
//                if(a[i] > b[i]) return -1;
//            }
//            return 0;
//        });
//        StringBuilder sb = new StringBuilder();
//        for(int i = 0; i < len; i++){
//            sb.append((char)('A' + map[i][len]));
//        }
//        return sb.toString();
//    }
//}
