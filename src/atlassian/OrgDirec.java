package atlassian;
// Simple, readable implementations for each part — no binary lifting, no MVCC.
// Each part is a separate, minimal class with a tiny demo.

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

// ============================ PART (a) ============================
// Tree only (each group has at most one parent). Each employee is in exactly one group.
// LCA implemented with basic parent climbing (depth align, then move up together).
class PartA_SimpleTreeSolver {
    private final Map<String, String> parent = new HashMap<>(); // child -> parent
    private final Map<String, Set<String>> children = new HashMap<>();
    private final Map<String, String> employeeGroup = new HashMap<>();

    public void addGroup(String g){ children.putIfAbsent(g, new HashSet<>()); }
    public void setParent(String parentId, String childId){ addGroup(parentId); addGroup(childId); parent.put(childId, parentId); children.get(parentId).add(childId); }
    public void assignEmployee(String emp, String group){ employeeGroup.put(emp, group); }

    private int depth(String g){ int d=0; while(g!=null && parent.containsKey(g)){ g = parent.get(g); d++; } return d; }

    private String lcaTwo(String a, String b){
        if(a==null||b==null) return null;
        int da = depth(a), db = depth(b);
        while(da>db){ a = parent.get(a); da--; }
        while(db>da){ b = parent.get(b); db--; }
        while(a!=null && b!=null && !a.equals(b)){
            a = parent.get(a); b = parent.get(b);
        }
        return (a!=null && a.equals(b)) ? a : null;
    }

    public Optional<String> closestCommonGroup(Collection<String> employees){
        String cur = null;
        for(String e: employees){
            String g = employeeGroup.get(e);
            if(g==null) return Optional.empty();
            cur = (cur==null) ? g : lcaTwo(cur, g);
            if(cur==null) return Optional.empty();
        }
        return Optional.ofNullable(cur);
    }

    // demo
    public static void main(String[] args){
        PartA_SimpleTreeSolver s = new PartA_SimpleTreeSolver();
        s.addGroup("ROOT"); s.addGroup("ENG"); s.addGroup("DESIGN"); s.addGroup("PLAT");
        s.setParent("ROOT","ENG"); s.setParent("ENG","PLAT"); s.setParent("ROOT","DESIGN");
        s.assignEmployee("alice","PLAT"); s.assignEmployee("bob","PLAT"); s.assignEmployee("cathy","DESIGN");
        System.out.println(s.closestCommonGroup(List.of("alice","bob")).orElse("NONE")); // PLAT
        System.out.println(s.closestCommonGroup(List.of("alice","cathy")).orElse("NONE")); // ROOT
    }
}

//timecomplexity
// ============================ PART (b) ============================
// DAG with shared groups + employees in multiple groups.
// Simple approach: for each employee, gather all ancestors (including self) by DFS up via parent links.
// Intersect those ancestor sets; among candidates, choose the one with maximum depth (longest path from any root),
// where depth is computed by memoized DFS (no topo ceremony). Tie-break lexicographically for determinism.
class PartB_SimpleDAGClosestOne {
    private final Map<String, Set<String>> parents = new HashMap<>();
    private final Map<String, Set<String>> children = new HashMap<>();
    private final Map<String, Set<String>> empGroups = new HashMap<>();

    public void addGroup(String g){ parents.putIfAbsent(g, new HashSet<>()); children.putIfAbsent(g, new HashSet<>()); }
    public void addParentChild(String p, String c){ addGroup(p); addGroup(c); parents.get(c).add(p); children.get(p).add(c); }
    public void addEmployeeToGroup(String e, String g){ addGroup(g); empGroups.computeIfAbsent(e,k->new HashSet<>()).add(g); }

    // memoized longest depth from any root
    private final Map<String,Integer> depthMemo = new HashMap<>();
    private int depth(String g){
        if(depthMemo.containsKey(g)) return depthMemo.get(g);
        int d = 0; // roots have depth 0
        for(String p: parents.getOrDefault(g, Set.of())) d = Math.max(d, 1 + depth(p));
        depthMemo.put(g, d); return d;
    }

    private Set<String> ancestorClosure(String g){
        Set<String> res = new HashSet<>();
        ArrayDeque<String> stack = new ArrayDeque<>();
        stack.push(g); res.add(g);
        while(!stack.isEmpty()){
            String x = stack.pop();
            for(String p: parents.getOrDefault(x, Set.of())) if(res.add(p)) stack.push(p);
        }
        return res;
    }

    public Optional<String> closestCommonGroup(Collection<String> employees){
        Set<String> acc = null;
        for(String e: employees){
            Set<String> gs = empGroups.get(e); if(gs==null||gs.isEmpty()) return Optional.empty();
            Set<String> reach = new HashSet<>();
            for(String g: gs) reach.addAll(ancestorClosure(g));
            if(acc==null) acc = reach; else acc.retainAll(reach);
            if(acc.isEmpty()) return Optional.empty();
        }
        String best=null; int bestDepth=Integer.MIN_VALUE;
        for(String g: acc){
            int d = depth(g);
            if(d>bestDepth || (d==bestDepth && (best==null || g.compareTo(best)<0))) { best=g; bestDepth=d; }
        }
        return Optional.ofNullable(best);
    }

    // demo
    public static void main(String[] args){
        PartB_SimpleDAGClosestOne s=new PartB_SimpleDAGClosestOne();
        s.addParentChild("ENG","PLAT"); s.addParentChild("ROOT","ENG"); s.addParentChild("ROOT","DES");
        s.addEmployeeToGroup("alice","PLAT"); s.addEmployeeToGroup("bob","PLAT"); s.addEmployeeToGroup("cathy","DES");
        System.out.println(s.closestCommonGroup(List.of("alice","bob")).orElse("NONE")); // PLAT
        System.out.println(s.closestCommonGroup(List.of("alice","cathy")).orElse("NONE")); // ROOT or ENG depending on structure (here ROOT since DES under ROOT, ENG under ROOT)
    }
}


// ============================ PART (c) ============================
// Dynamic updates + concurrent access using a simple ReentrantReadWriteLock.
// Reads take read-lock; writes take write-lock; readers always see the latest committed state after write unlocks.
class PartC_SimpleConcurrentDirectory {
    private final ReentrantReadWriteLock rw = new ReentrantReadWriteLock();

    // Under the lock, we reuse PartB-style simple DAG logic/data structures
    private final Map<String, Set<String>> parents = new HashMap<>();
    private final Map<String, Set<String>> children = new HashMap<>();
    private final Map<String, Set<String>> empGroups = new HashMap<>();

    public void addGroup(String g){
        rw.writeLock().lock();
        try{ parents.putIfAbsent(g, new HashSet<>()); children.putIfAbsent(g, new HashSet<>());} finally { rw.writeLock().unlock(); }
    }
    public void addParentChild(String p,String c){
        rw.writeLock().lock();
        try{ parents.putIfAbsent(c,new HashSet<>()); children.putIfAbsent(p,new HashSet<>()); parents.get(c).add(p); children.get(p).add(c);} finally { rw.writeLock().unlock(); }
    }
    public void addEmployeeToGroup(String e,String g){
        rw.writeLock().lock();
        try{ parents.putIfAbsent(g,new HashSet<>()); children.putIfAbsent(g,new HashSet<>()); empGroups.computeIfAbsent(e,k->new HashSet<>()).add(g);} finally { rw.writeLock().unlock(); }
    }

    // helpers (only used under read lock)
    private int depth(Map<String,Integer> memo, String g){ if(memo.containsKey(g)) return memo.get(g); int d=0; for(String p: parents.getOrDefault(g, Set.of())) d=Math.max(d,1+depth(memo,p)); memo.put(g,d); return d; }
    private Set<String> closure(String g){ Set<String> res=new HashSet<>(); ArrayDeque<String> st=new ArrayDeque<>(); st.push(g); res.add(g); while(!st.isEmpty()){ String x=st.pop(); for(String p: parents.getOrDefault(x, Set.of())) if(res.add(p)) st.push(p);} return res; }

    public Optional<String> getCommonGroupForEmployees(Collection<String> employees){
        rw.readLock().lock();
        try{
            Set<String> acc=null;
            for(String e: employees){
                Set<String> gs=empGroups.get(e); if(gs==null||gs.isEmpty()) return Optional.empty();
                Set<String> r=new HashSet<>(); for(String g: gs) r.addAll(closure(g));
                if(acc==null) acc=r; else acc.retainAll(r);
                if(acc.isEmpty()) return Optional.empty();
            }
            Map<String,Integer> memo=new HashMap<>(); String best=null; int bestDepth=Integer.MIN_VALUE;
            for(String g: acc){ int d=depth(memo,g); if(d>bestDepth || (d==bestDepth && (best==null || g.compareTo(best)<0))){ best=g; bestDepth=d; } }
            return Optional.ofNullable(best);
        } finally { rw.readLock().unlock(); }
    }

    // demo
    public static void main(String[] args){
        PartC_SimpleConcurrentDirectory dir=new PartC_SimpleConcurrentDirectory();
        dir.addParentChild("ENG","PLAT"); dir.addEmployeeToGroup("alice","PLAT"); dir.addEmployeeToGroup("bob","PLAT");
        System.out.println(dir.getCommonGroupForEmployees(List.of("alice","bob")).orElse("NONE")); // PLAT
        dir.addParentChild("ROOT","ENG"); dir.addParentChild("ROOT","DES"); dir.addEmployeeToGroup("cathy","DES");
        System.out.println(dir.getCommonGroupForEmployees(List.of("alice","cathy")).orElse("NONE")); // ROOT
    }
}


// ============================ PART (d) ============================
// Flat org: no subgroups. Each group has employees. Simple set check.
class PartD_SimpleFlatSolver {
    private final Map<String, Set<String>> groupMembers = new HashMap<>();

    public void addGroup(String g){ groupMembers.putIfAbsent(g, new HashSet<>()); }
    public void addEmployeeToGroup(String e, String g){ groupMembers.computeIfAbsent(g,k->new HashSet<>()).add(e); }

    public Optional<String> closestCommonGroup(Collection<String> employees){
        if (employees==null || employees.isEmpty()) return Optional.empty();
        String best=null;
        for (var entry : groupMembers.entrySet()) {
            Set<String> members = entry.getValue();
            boolean ok=true; for(String e: employees){ if(!members.contains(e)){ ok=false; break; } }
            if (ok && (best==null || entry.getKey().compareTo(best)<0)) best=entry.getKey();
        }
        return Optional.ofNullable(best);
    }

    // demo
    public static void main(String[] args){
        PartD_SimpleFlatSolver s = new PartD_SimpleFlatSolver();
        s.addGroup("A"); s.addGroup("B");
        s.addEmployeeToGroup("alice","A"); s.addEmployeeToGroup("bob","A"); s.addEmployeeToGroup("alice","B");
        System.out.println(s.closestCommonGroup(List.of("alice","bob")).orElse("NONE")); // A
        System.out.println(s.closestCommonGroup(List.of("alice","cathy")).orElse("NONE")); // NONE
    }
}
