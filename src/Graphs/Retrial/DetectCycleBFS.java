package Graphs.Retrial;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

class Pairz {
    int n;
    int p;
    public Pairz(int n, int p) {
        this.n = n;
        this.p = p;
    }
}

public class DetectCycleBFS {

    public static void main(String[] args) {

        ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            adj.add(new ArrayList<>());
        }
        adj.get(0).add(1);
        adj.get(1).add(0);
        adj.get(1).add(2);
        adj.get(1).add(4);
        adj.get(2).add(1);
        adj.get(2).add(3);
        adj.get(3).add(2);
        adj.get(3).add(4);
        adj.get(4).add(1);
        adj.get(4).add(3);

        System.out.println(detectCycleBFS(adj));
    }

    private static boolean detectCycleBFS(ArrayList<ArrayList<Integer>> adj) {
        Queue<Pairz> q = new LinkedList<>();

        boolean[] v = new boolean[adj.size()];

        for(int i = 0; i< adj.size(); i++) {
            if(!v[i]){
                if(bfs(i, q, v, adj)) return true;
            }
        }
        return false;
    }

    private static boolean bfs(int no, Queue<Pairz> q, boolean[] v, ArrayList<ArrayList<Integer>> adj) {
        v[no] = true;
        q.add(new Pairz(no, -1));

        while(!q.isEmpty()) {
            Pairz p = q.poll();
            int node = p.n;
            int parent = p.p;

            for(int i : adj.get(no)) {
                if(!v[i]) {
                    v[i] = true;
                    q.add(new Pairz(i,node));
                } else if(parent != node)
                    return true;
            }
        }
        return false;
    }
}
