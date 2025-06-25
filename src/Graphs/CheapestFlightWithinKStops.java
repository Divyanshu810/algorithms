package Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

class Pairo {
    int no;
    int co;
    Pairo(int no, int co) {
        this.no = no;
        this.co = co;
    }
}
class Tuplee {
    int st;
    int no;
    int dis;

    Tuplee(int st, int no, int dis) {
        this.st = st;
        this.no = no;
        this.dis = dis;
    }
}
public class CheapestFlightWithinKStops {

    public static void main(String[] args) {

        int[][] fl = {{0,1,100},{1,2,100},{2,0,100},{1,3,600},{2,3,200}};
        System.out.println(cheapestFlight(4, fl, 0, 3, 1));

    }

    private static int cheapestFlight(int n, int[][] flights, int src, int dst, int k) {
        ArrayList<ArrayList<Pairo>> adj = new ArrayList<>();
        for(int i = 0; i<n; i++) {
            adj.add(new ArrayList<>());
        }

        for(int i = 0; i<flights.length; i++) {
            adj.get(flights[i][0]).add(new Pairo(flights[i][1], flights[i][2]));
        }

        int[] cost = new int[n];
        Arrays.fill(cost, (int)1e9);
        cost[src] = 0;
        Queue<Tuplee> q = new LinkedList<>();
        q.add(new Tuplee(0, src, 0));

        while(!q.isEmpty()) {
            Tuplee t = q.poll();
            int st = t.st, no = t.no, dis = t.dis;
            if(st > k) continue;
            for(Pairo p : adj.get(no)) {
                if(st <= k && dis + p.co < cost[p.no]) {
                    cost[p.no] = dis + p.co;
                    q.add(new Tuplee(st+1, p.no, cost[p.no]));
                }
            }
        }
        return cost[dst];

    }
}
